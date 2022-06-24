package lol.magix.windtrace.game;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Constants;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.game.logic.*;
import lol.magix.windtrace.player.WindtracePlayer;
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
    
    /* The amount of ticks since the game instance started. */
    private long ticks = 0; 
    
    public GameInstance(UUID uuid, GameFlags flags, WindtracePlayer host) {
        this.gameId = uuid; this.flags = flags; this.host = host;
        
        try {
            this.initialize();
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().warn("Failed to initialize a game instance!", exception);
        }
    }

    /**
     * Attempts to initialize the game.
     * @throws IllegalArgumentException if the game flags failed validation.
     */
    private void initialize() throws IllegalArgumentException {
        // Check player counts.
        if(this.flags.maxPlayers <= this.flags.hunterCount) {
            throw new IllegalArgumentException("The max player count must be greater than the hunter count!");
        }
        
        // Determine the appropriate game logic handler.
        this.logicHandler = switch(this.flags.gameMode) {
            case WINDTRACE -> new WindtraceLogic(this);
            case HIDE_AND_SEEK -> new HideAndSeekLogic(this);
            case MANHUNT -> new ManhuntLogic(this);
        };
        
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
        
        // Assign players roles.
        this.assignRoles();
        
        // Call the logic handler's start method.
        this.logicHandler.start();
        
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
        
        // Remove remaining players.
        this.players.values().forEach(this::removePlayer);
        
        this.gameState = State.FINISHED; // Set the game state after completing.
    }

    /**
     * Ticks the game and updates its state.
     * Requires the game to be in the 'IN_PROGRESS' state.
     * @throws IllegalStateException if the game is not in the 'IN_PROGRESS' state.
     */
    public void tick() throws IllegalAccessException {
        if(this.gameState != State.IN_PROGRESS)
            throw new IllegalAccessException("Game " + this.gameId + " is not in progress.");
        
        // Call the logic handler's tick method.
        this.logicHandler.tick();
        
        // Update the tick counter.
        this.ticks++;
    }

    /**
     * Adds a player to the game.
     * Requires the game to be in the 'WAITING' state.
     * @param player The player to add.
     * @throws IllegalStateException if the game is not in the 'WAITING' state.
     * @throws IllegalStateException if the game is full.
     */
    @SneakyThrows
    public void addPlayer(WindtracePlayer player) {
        // Check game state.
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        // Check player count.
        if(this.players.size() >= this.flags.maxPlayers)
            throw new IllegalAccessException("Game " + this.gameId + " is full.");
        
        // Add the player to the collection.
        this.players.put(player.getUid(), player);
        // Set the player's game instance.
        player.setGameInstance(this);
        
        // Add the player to the host's world.
        this.host.getWorld().addPlayer(player);
    }

    /**
     * Adds a group of players to the game.
     * Requires the game to be in the 'WAITING' state.
     * @param players The players to add.
     * @throws IllegalAccessException if the game is not in the 'WAITING' state.
     */
    public void addPlayers(Collection<WindtracePlayer> players) throws IllegalAccessException {
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        players.forEach(this::addPlayer);
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
        // Broadcast the player's removal.
        PlayerUtils.broadcastMemo(player.getNickname() + " has left the game.", this.players.values());
        
        // Remove the player from the host's world.
        this.host.getWorld().removePlayer(player);
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
            }
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
        Collections.shuffle(players);
        
        // Pick out hunters.
        var hunters = new HashMap<Integer, WindtracePlayer>();
        for(int i = 0; i <= this.flags.hunterCount; i++) {
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
    
    /*
     * Data
     */

    /**
     * Returns the amount of ticks since the game has started.
     * @return The amount of ticks in seconds.
     */
    public long getGameTicks() {
        return this.ticks;
    }

    /**
     * Returns the game's current state.
     * @return A {@link State} enum value.
     */
    public State getGameState() {
        return this.gameState;
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
        CONVERT_TO_HUNTER
    }
}