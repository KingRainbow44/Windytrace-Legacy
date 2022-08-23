package lol.magix.windtrace.listeners;

import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.player.WindtracePlayer;

public final class GameListener {
    private GameListener() {
        // No construction.
    }

    /**
     * Called when a player moves.
     * Handles positional tracking in-game.
     */
    public static void onMove(PlayerMoveEvent event) {
        var player = (WindtracePlayer) event.getPlayer();
        if(player.isInGame() && player.getGameInstance().getGameState() == GameInstance.State.IN_PROGRESS)
            player.getGameInstance().processMove(player, event);
    }

    /**
     * Called when a player teleports.
     * @param event The event.
     */
    public static void onTeleport(PlayerTeleportEvent event) {
        var player = (WindtracePlayer) event.getPlayer();
        if(player.isInGame() && player.getGameInstance().getGameState() == GameInstance.State.IN_PROGRESS)
            player.getGameInstance().processTeleport(player, event);
    }
}
