package lol.magix.windtrace.game;

import emu.grasscutter.game.player.Player;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.game.logic.*;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An instance of a currently running Windtrace/Hide and Seek game.
 */
public final class GameInstance {
    /* Game settings. (aka flags) */
    private final GameFlags flags;
    /* The unique identifier for this instance. */
    private final UUID gameId;
    
    /* The players partaking in this game. */
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();

    /* The game's logic handler. */
    private GameLogicHandler logicHandler;
    /* The current state of this game instance. */
    private State gameState = State.INITIALIZING;
    
    public GameInstance(UUID uuid, GameFlags flags) {
        this.gameId = uuid; this.flags = flags;
        
        try {
            this.initialize();
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().warn("Failed to initialize a game instance!", exception);
        }
    }

    /**
     * Attempts to initialize the game.
     */
    private void initialize() {
        // Determine the appropriate game logic handler.
        this.logicHandler = switch(this.flags.gameMode) {
            case WINDTRACE -> new WindtraceLogic(this);
            case HIDE_AND_SEEK -> new HideAndSeekLogic(this);
        };
        
        this.gameState = State.WAITING; // Set the game state after initialization.
    }

    /**
     * Starts the game.
     * Requires the game to be in the 'WAITING' state.
     * @throws IllegalStateException if the game is not in the 'WAITING' state.
     */
    public void start() throws IllegalAccessException {
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " has not finished initializing.");
        
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
    }

    /**
     * Adds a player to the game.
     * Requires the game to be in the 'WAITING' state.
     * @param player The player to add.
     * @throws IllegalStateException if the game is not in the 'WAITING' state.
     */
    public void addPlayer(Player player) throws IllegalAccessException {
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        this.players.put(player.getUid(), player);
    }

    /**
     * Adds a group of players to the game.
     * Requires the game to be in the 'WAITING' state.
     * @param players The players to add.
     * @throws IllegalAccessException if the game is not in the 'WAITING' state.
     */
    public void addPlayers(Collection<Player> players) throws IllegalAccessException {
        if(this.gameState != State.WAITING)
            throw new IllegalAccessException("Game " + this.gameId + " is not waiting for players.");
        players.forEach(player -> this.players.put(player.getUid(), player));
    }
    
    /*
     * Enums
     */

    public enum State {
        INITIALIZING, WAITING, IN_PROGRESS, FINISHED
    }
}