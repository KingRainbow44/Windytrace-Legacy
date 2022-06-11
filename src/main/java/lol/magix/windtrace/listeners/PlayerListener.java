package lol.magix.windtrace.listeners;

import emu.grasscutter.server.event.game.PlayerCreationEvent;
import lol.magix.windtrace.player.WindtracePlayer;

public final class PlayerListener {
    private PlayerListener() {
        // No construction.
    }

    /**
     * Called when the player is created.
     * Sets the player class to {@link WindtracePlayer}
     */
    public static void onPlayerCreation(PlayerCreationEvent event) {
        event.setPlayerClass(WindtracePlayer.class);
    }
}
