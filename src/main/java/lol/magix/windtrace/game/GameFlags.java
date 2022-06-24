package lol.magix.windtrace.game;

/**
 * Settings/flags for a game instance.
 */
public final class GameFlags {
    /* The game mode for the game. */
    public Mode gameMode = Mode.WINDTRACE;
    
    /* The max amount of players in this game. */
    public int maxPlayers = 4;
    /* The amount of hunters to have. */
    public short hunterCount = 1;
    
    /*
     * Enums
     */
    
    public enum Mode {
        WINDTRACE, HIDE_AND_SEEK, MANHUNT
    }
}