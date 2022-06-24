package lol.magix.windtrace.listeners;

import emu.grasscutter.server.event.entity.EntityMoveEvent;

public final class GameListener {
    private GameListener() {
        // No construction.
    }

    /**
     * Called when an entity moves.
     * Handles positional tracking in-game.
     */
    public static void onMove(EntityMoveEvent event) {
        var entity = event.getEntity();
    }
}
