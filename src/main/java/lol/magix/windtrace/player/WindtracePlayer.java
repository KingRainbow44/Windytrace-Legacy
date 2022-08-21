package lol.magix.windtrace.player;

import dev.morphia.annotations.Transient;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.plugin.api.PlayerHook;
import emu.grasscutter.server.game.GameSession;
import lol.magix.windtrace.game.GameFlags;
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
    public static Collection<Player> toPlayerCollection(Collection<WindtracePlayer> players) {
        return new LinkedList<>(players);
    }

    /**
     * Converts a collection of {@link Player}s to a collection of {@link WindtracePlayer}s.
     * @param players The collection of {@link Player}s.
     * @return The collection of {@link WindtracePlayer}s.
     */
    public static Collection<WindtracePlayer> toWindtracePlayerCollection(Collection<Player> players) {
        var list = new LinkedList<WindtracePlayer>();
        players.forEach(player -> list.add((WindtracePlayer) player));
        return list;
    }
    
    /* The hook for this player. */
    @Transient @Getter
    private final PlayerHook hook;
    /* The player's preferred game settings. */
    @Transient @Getter
    private final GameFlags gameFlags = new GameFlags();
    
    /* Game instance for a specific player. */
    @Transient @Getter @Setter 
    private GameInstance gameInstance = null;
    
    @SuppressWarnings("deprecation")
    public WindtracePlayer() {
        super();

        this.hook = new PlayerHook(this);
    }
    
    public WindtracePlayer(GameSession session) {
        super(session);
        
        this.hook = new PlayerHook(this);
    }

    /**
     * Returns whether the player is in a game.
     * @return True if the player has a valid game instance, false otherwise.
     */
    public boolean isInGame() {
        return this.gameInstance != null;
    }
}