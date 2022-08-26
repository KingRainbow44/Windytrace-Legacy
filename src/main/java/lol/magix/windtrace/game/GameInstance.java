package lol.magix.windtrace.game;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.World;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.game.logic.*;
import lol.magix.windtrace.player.WindtracePlayer;
import lol.magix.windtrace.utils.MultiplayerUtils;
import lol.magix.windtrace.utils.MultiplayerUtils.Reason;
import lol.magix.windtrace.utils.PlayerUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An instance of a currently running Windtrace/Hide and Seek game.
 */
public final class GameInstance {
    /* Game settings. (aka flags) */
    @Getter private final GameFlags flags;
    /* The unique identifier for this instance. */
    @Getter private final UUID gameId;
    /* The game's host. */
    @Getter private final WindtracePlayer host;
    /* The world the game will take place in. */
    @Getter private final World world;

    /* The players partaking in this game. */
    private final Map<Integer, WindtracePlayer> players = new ConcurrentHashMap<>();

    /* A map of hunters. */
    private final Map<Integer, WindtracePlayer> hunters = new ConcurrentHashMap<>();
    /* A map of runners. */
    private final Map<Integer, WindtracePlayer> runners = new ConcurrentHashMap<>();

    /* The game's logic handler. */
    private GameLogicHandler logicHandler;
    /* The current state of this game instance. */
    private State gameState = State.INITIALIZING;

    /* The amount of players left to load. */
    private final List<Player> leftToLoad = new LinkedList<>();

    /* The amount of ticks since the game instance started. */
    private long ticks = 0;
    /* The amount of ticks to stop the game at. */
    private long stopAtTicks = -1;

    public GameInstance(UUID uuid, GameFlags flags, WindtracePlayer host) {
        this.gameId = uuid; this.flags = flags; this.host = host;
        this.world = host.getWorld();

        try {
            this.initialize();
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().error("Failed to initialize a game instance!", exception);
        }
    }

    /**
     * Attempts to initialize the game.
     * @throws IllegalArgumentException if the game flags failed validation.
     */
    private void initialize() throws IllegalArgumentException, IllegalAccessException {
        // Check player counts.
        if(this.flags.maxPlayers <= this.flags.hunterCount) {
            throw new IllegalArgumentException("The max player count must be greater than the hunter count!");
        }

        // Determine the appropriate game logic handler.
        this.logicHandler = switch(this.flags.gameMode) {
            case WINDTRACE -> new WindtraceLogic(this);
            case HIDE_AND_SEEK -> new HideAndSeekLogic(this);
            case MANHUNT -> new ManhuntLogic(this);
            case TAG -> new TagLogic(this);
        };

        // Check if the world is already in Co-Op mode.
        if(!this.world.isMultiplayer()) {
            // Open the world to Co-Op.
            MultiplayerUtils.openForMultiplayer(this.host);

            // Add the player to the collection.
            this.players.put(this.host.getUid(), this.host);
            // Set the player's game instance.
            this.host.setGameInstance(this);
        } else {
            // Add all players to the game.
            this.addPlayers(this.world.getPlayers(), true);
        }

        // Call the logic handler's setup method.
        this.logicHandler.setup();

        this.gameState = State.WAITING; // Set the game state after initialization.
    }

    /**
     * Starts the game.
     * Requires the game to be in the 'WAITING' state.
     * @throws IllegalStateException if the game is not in the 'WAITING' state.
     */
    public void start() throws IllegalAccessException {
        // Check game state.
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " has not finished initializing.");
        // Check player count.
        if(this.players.size() < this.flags.hunterCount + 1)
            throw new IllegalAccessException("Game " + this.gameId + " has not enough players.");
        // Add all players to left to load.
        this.leftToLoad.addAll(this.players.values());

        // Assign players roles.
        this.assignRoles();

        // Call the logic handler's start method.
        this.logicHandler.start();

        // Broadcast message.
        this.getPlayers().forEach(player -> player.dropMessage("The game has started!"));

        this.gameState = State.IN_PROGRESS; // Set the game state after completing.
    }

    /**
     * Stops the game.
     * Requires the game to be in the 'IN_PROGRESS' state.
     * @param force If true, the game will be stopped regardless of its current state.
     * @throws IllegalStateException if the game is not in the 'IN_PROGRESS' state.
     */
    public void stop(boolean force) throws IllegalAccessException {
        if(this.gameState != State.IN_PROGRESS && !force)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");

        // Call the logic handler's stop method.
        this.logicHandler.stop(force);

        // Broadcast a game over message.
        PlayerUtils.broadcastMessage("Game over!", this.players.values());
        // Teleport all players to the start.
        if(!this.flags.kickOnRemove && this.flags.startTeleportAfterEnd)
            this.players.values().forEach(player -> {
                if(player.getSceneLoadState() == Player.SceneLoadState.LOADED)
                    PlayerUtils.safeTeleport(player, this.flags.scene.getSpawnPosition());
            });

        // Remove remaining players.
        this.players.values().forEach(this::removePlayer);

        this.gameState = State.FINISHED; // Set the game state after completing.
        GameManager.stopGame(this.gameId); // Remove the game from the manager.
    }

    /**
     * Ticks the game and updates its state.
     * Requires the game to be in the 'IN_PROGRESS' state.
     * @throws IllegalAccessException if the game is not in the 'IN_PROGRESS' state.
     */
    public void tick() throws IllegalAccessException {
        if(this.gameState != State.IN_PROGRESS)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");

        // Check if all players have loaded.
        if(this.leftToLoad.size() != 0) {
            this.leftToLoad.removeIf(player ->
                player.getSceneLoadState() == Player.SceneLoadState.LOADED);

            // Check if all players have loaded.
            if(this.leftToLoad.size() != 0)
                return;
            else {
                // Call the logic handler's loaded method.
                this.logicHandler.doneLoading();
            }
        }

        // Call the logic handler's tick method.
        this.logicHandler.tick();

        // If the game has reached the stopAtTicks, stop it.
        if(this.stopAtTicks != -1 && this.ticks >= this.stopAtTicks) {
            try {
                // Call the stop method.
                this.stop(true); return;
            } catch (Exception exception) {
                // Log the exception.
                Windtrace.getInstance().getLogger().error("Failed to stop game " + this.gameId + "!", exception);
            }
        }

        // Check if runners should win.
        if(this.stopAtTicks == -1 && this.ticks >= (this.flags.survivalTime + this.flags.headStartTime)) {
            // Check if there are any runners left.
            if(this.runners.size() > 0) {
                // Broadcast a message.
                PlayerUtils.broadcastMessage("The runners have won!", this.getPlayers());
            } else {
                // Broadcast a message.
                PlayerUtils.broadcastMessage("The hunters have won!", this.getPlayers());
            }

            // Stop the game.
            this.doStop();
        }

        // Update the tick counter.
        this.ticks++;
    }

    /**
     * Adds a player to the game.
     * Requires the game to be in the 'WAITING' state.
     *
     * @param player The player to add.
     * @param force If true, the player will be added regardless of its current state.
     * @throws IllegalAccessException if the game is not in the 'WAITING' state.
     * @throws IllegalStateException  if the game is full.
     * @throws IllegalStateException  if the player is already in a multiplayer world.
     */
    public void addPlayer(WindtracePlayer player, boolean force) throws IllegalAccessException {
        // Check game state.
        if(this.gameState != State.WAITING && !force)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        // Check player count.
        if(this.players.size() >= this.flags.maxPlayers)
            throw new IllegalStateException("Game " + this.gameId + " is full.");
        // Check if player is in a multiplayer world already.
        if(player.isInMultiplayer() && !force)
            throw new IllegalStateException("Player " + player.getNickname() + " is already in a multiplayer world.");

        // Add the player to the collection.
        this.players.put(player.getUid(), player);
        // Set the player's game instance.
        player.setGameInstance(this);

        // Check if the player is already in a multiplayer world.
        if(player.isInMultiplayer()) {
            // Return if the player is already in the host's world.
            if(player.getWorld().getHost().getUid() == this.host.getUid())
                return;
            // Kick the player from their existing world.
            MultiplayerUtils.leaveMultiplayerGame(player, Reason.LEAVE);
        }

        // Add the player to the host's world.
        MultiplayerUtils.joinMultiplayerGame(player, this.host);
    }

    /**
     * Adds a group of players to the game.
     * Requires the game to be in the 'WAITING' state.
     * @param players The players to add.
     * @param force If true, the players will be added regardless of their current state.
     * @throws IllegalAccessException if the game is not in the 'WAITING' state.
     */
    public void addPlayers(Collection<Player> players, boolean force) throws IllegalAccessException {
        if(this.gameState != State.WAITING && !force)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        players.forEach(player -> {
            try {
                this.addPlayer((WindtracePlayer) player, force);
            } catch (Exception ignored) {
                player.dropMessage("Unable to join game.");
            }
        });
    }

    /**
     * Removes a player from the game.
     * @param player The player to remove.
     */
    public void removePlayer(WindtracePlayer player) {
        // Remove player from maps.
        this.players.remove(player.getUid());
        this.hunters.remove(player.getUid());
        this.runners.remove(player.getUid());
        // Set the player's game instance to null.
        player.setGameInstance(null);

        // Reload the player's UID.
        PlayerUtils.reloadUid(player);

        // Remove the player from the host's world.
        if(this.flags.kickOnRemove)
            MultiplayerUtils.leaveMultiplayerGame(player, Reason.KICK);
    }

    /**
     * Eliminates a player from the game.
     * This does not have the same effect as {@link #removePlayer(WindtracePlayer)}.
     * @param player The player to eliminate.
     * @param strategy The strategy to use for elimination.
     */
    public void eliminatePlayer(WindtracePlayer player, Strategy strategy) {
        switch(strategy) {
            case CONVERT_TO_HUNTER -> {
                // Remove the player from the runners team.
                this.runners.remove(player.getUid());
                // Add the player to the hunters team.
                this.hunters.put(player.getUid(), player);

                // Call the logic handler's assignRole method.
                this.logicHandler.assignRole(player, Role.HUNTER);

                // Send feedback.
                player.dropMessage("You are now a hunter.");

                // Murder the player.
                if(this.flags.murderOnEliminate)
                    PlayerUtils.killAvatar(player);
            }

            case KICK_FROM_GAME -> {
                // Remove the player from the game.
                this.removePlayer(player);
                // Send feedback.
                player.dropMessage("You have been removed from the game.");
            }
        }

        // Broadcast message.
        PlayerUtils.broadcastMessage(player.getNickname() + " has been caught!", this.getPlayers());

        // Check if the game should end.
        if(this.runners.size() == 0) {
            // Call for the game to be stopped.
            this.doStop();
            // Broadcast message.
            PlayerUtils.broadcastMessage("All players have been caught!", this.getPlayers());
        }
    }

    /**
     * Called when the player receives a packet.
     * @param player The player who sent the packet.
     * @param packetName The name of the packet.
     * @param event The packet event.
     * @throws IllegalStateException if the game is not in the 'IN_PROGRESS' state.
     */
    @SneakyThrows
    public void processPacket(WindtracePlayer player, String packetName, ReceivePacketEvent event) {
        // Check game state.
        if(this.gameState != State.IN_PROGRESS)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");

        // Call the logic handler's process method.
        this.logicHandler.processPacket(player, packetName, event);
    }

    /**
     * Called when the player teleports.
     * @param player The player who teleported.
     * @param event The teleport event.
     * @throws IllegalStateException if the game is not in the 'IN_PROGRESS' state.
     */
    @SneakyThrows
    public void processTeleport(WindtracePlayer player, PlayerTeleportEvent event) {
        // Check game state.
        if(this.gameState != State.IN_PROGRESS)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");

        // Prevent server teleports.
        if(event.getTeleportType() == TeleportType.MAP ||
            event.getTeleportType() == TeleportType.COMMAND) {
            event.cancel(); // Cancel the event.
            return; // Cancel execution.
        }

        // Call the logic handler's process method.
        this.logicHandler.processTeleport(player, event);
    }

    /**
     * Called when the player moves.
     * @param player The player who moved.
     * @param event The move event.
     * @throws IllegalStateException if the game is not in the 'IN_PROGRESS' state.
     */
    @SneakyThrows
    public void processMove(WindtracePlayer player, PlayerMoveEvent event) {
        // Check game state.
        if(this.gameState != State.IN_PROGRESS)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");

        // Call the logic handler's process method.
        this.logicHandler.processMove(player, event);
    }

    /*
     * Utilities
     */

    /**
     * Attempts to assign every player a role.
     * @throws IllegalStateException if the game is not in the 'WAITING' state.
     */
    public void assignRoles() throws IllegalAccessException {
        // Check game state.
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");

        // Shuffle the player list.
        var players = new ArrayList<>(this.players.values());
        players.add(this.host); Collections.shuffle(players);

        // Pick out hunters.
        var hunters = new HashMap<Integer, WindtracePlayer>();
        for(int i = 0; i < this.flags.hunterCount; i++) {
            hunters.put(players.get(i).getUid(), players.get(i));
        } this.hunters.putAll(hunters);

        // Set remaining players as runners.
        var runners = new HashMap<>(this.players);
        this.hunters.forEach((uid, player) -> runners.remove(uid));
        this.runners.putAll(runners);

        // Call the logic handler's assignRole method.
        this.hunters.values().forEach(player -> this.logicHandler.assignRole(player, Role.HUNTER));
        this.runners.values().forEach(player -> this.logicHandler.assignRole(player, Role.RUNNER));
    }

    /**
     * Returns the player's assigned role for this game.
     * The player's role can change throughout the game.
     * @param player The player.
     * @return The player's current role.
     */
    public Role getRole(WindtracePlayer player) {
        return this.runners.containsKey(player.getUid()) ? Role.RUNNER : Role.HUNTER;
    }

    /**
     * Alerts the game to stop in the given amount of ticks.
     */
    public void doStop() {
        // Stop the game in 5 ticks.
        this.stopAtTicks = this.ticks + 5;
    }

    /*
     * Data
     */

    /**
     * Returns the amount of ticks since the game has started.
     * @return The amount of ticks in seconds.
     */
    public long  getGameTicks() {
        return this.ticks;
    }

    /**
     * Returns the game's current state.
     * @return A {@link State} enum value.
     */
    public State getGameState() {
        return this.gameState;
    }

    /**
     * Returns the players currently in this game.
     * @return A collection of {@link WindtracePlayer} objects.
     */
    public Collection<WindtracePlayer> getPlayers() {
        return this.players.values();
    }

    /**
     * Returns the runners currently in this game.
     * @return A collection of {@link WindtracePlayer} objects.
     */
    public Collection<WindtracePlayer> getRunners() {
        return this.runners.values();
    }

    /**
     * Returns the hunters currently in this game.
     * @return A collection of {@link WindtracePlayer} objects.
     */
    public Collection<WindtracePlayer> getHunters() {
        return this.hunters.values();
    }

    /*
     * Enums
     */

    public enum State {
        INITIALIZING, WAITING, IN_PROGRESS, FINISHED
    }

    public enum Role {
        HUNTER, RUNNER
    }

    public enum Strategy {
        /* Elimination strategies. */
        CONVERT_TO_HUNTER, KICK_FROM_GAME
    }
}