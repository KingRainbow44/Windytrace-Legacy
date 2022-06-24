package lol.magix.windtrace.player;

import dev.morphia.annotations.Transient;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.game.GameSession;
import lol.magix.windtrace.game.GameInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;

public final class WindtracePlayer extends Player {
    /**
     * Converts a collection of {@link WindtracePlayer}s to a collection of {@link Player}s.
     * @param players The collection of {@link WindtracePlayer}s.
     * @return The collection of {@link Player}s.
     */
    public static Collection<Player> toCollection(Collection<WindtracePlayer> players) {
        return new LinkedList<>(players);
    }
    
    /* Game instance for a specific player. */
    @Transient @Getter @Setter 
    private GameInstance gameInstance = null;
    
    @SuppressWarnings("deprecation")
    public WindtracePlayer() {
        super();
    }
    
    public WindtracePlayer(GameSession session) {
        super(session);
    }

    /**
     * Returns whether the player is in a game.
     * @return True if the player has a valid game instance, false otherwise.
     */
    public boolean isInGame() {
        return this.gameInstance != null;
    }
}