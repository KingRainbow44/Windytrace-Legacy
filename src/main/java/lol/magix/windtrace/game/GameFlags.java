package lol.magix.windtrace.game;

/**
 * Settings/flags for a game instance.
 */
public final class GameFlags {
    /* The game mode for the game. */
    public Mode gameMode = Mode.WINDTRACE;
    
    /*
     * Enums.
     */
    
    public enum Mode {
        WINDTRACE, HIDE_AND_SEEK
    }
}