package lol.magix.windtrace.listeners;

import emu.grasscutter.net.packet.PacketOpcodesUtils;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerQuitEvent;
import lol.magix.windtrace.game.GameInstance;
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

    /**
     * Called when the player leaves the server.
     * Removes the player from the game instance.
     */
    public static void onQuit(PlayerQuitEvent event) {
        // Get the player.
        var player = event.getPlayer();
        // Check if the player is a WindtracePlayer.
        if(!(player instanceof WindtracePlayer windtracePlayer))
            return;

        // Check if the player is in a game.
        if(!windtracePlayer.isInGame())
            return;
        // Remove the player from the game.
        windtracePlayer.getGameInstance().removePlayer(windtracePlayer);
    }

    /**
     * Called when the player sends a packet.
     * Changes how certain packets are handled.
     */
    public static void onPacketReceive(ReceivePacketEvent event) {
        // Fetch the packet name and player.
        var packetName = PacketOpcodesUtils.getOpcodeName(event.getPacketId());
        var player = event.getGameSession().getPlayer();

        // Check if the player is a Windtrace player.
        if(!(player instanceof WindtracePlayer windtracePlayer))
            return;

        // Get the player's game instance.
        var gameInstance = windtracePlayer.getGameInstance();

        // Check if the player is in a game.
        if(gameInstance == null)
            return;
        // Check if the game is in progress.
        if(gameInstance.getGameState() != GameInstance.State.IN_PROGRESS)
            return;

        // Call the game's logic handler to process the packet.
        gameInstance.processPacket(windtracePlayer, packetName, event);
    }
}
