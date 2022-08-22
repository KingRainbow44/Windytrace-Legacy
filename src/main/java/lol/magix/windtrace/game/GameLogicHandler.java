package lol.magix.windtrace.game;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import lol.magix.windtrace.game.GameInstance.Role;
import lol.magix.windtrace.player.WindtracePlayer;

/**
 * This interface should be implemented by logic handlers.
 * Logic handlers are responsible for handling the functionality of the game.
 * They implement specific methods which are required for game logic.
 */
public interface GameLogicHandler {
    /**
     * Called when the game is initialized.
     * Setup logic should be performed here.
     */
    void setup();

    /**
     * Called when the game instance is told to start.
     * Initialization logic should be performed here.
     */
    void start();

    /**
     * Called when the game instance is told to update.
     * Re-occurring logic should be performed here.
     */
    void tick();

    /**
     * Called when the game instance is told to stop.
     * Cleanup logic should be performed here.
     */
    void stop(boolean force);

    /**
     * Called when all players have finished loading.
     */
    void doneLoading();

    /**
     * Called when the game instance assigns a role to a player.
     * @param player The player that a role was assigned to.
     * @param role The role that was assigned to the player.
     */
    void assignRole(WindtracePlayer player, Role role);

    /**
     * Called when the game instance receives a packet.
     * @param player The player who sent the packet.
     * @param packetName The name of the packet.
     * @param event The event which was received.
     */
    void processPacket(WindtracePlayer player, String packetName, ReceivePacketEvent event);

    /**
     * Called when a player in this game instance teleports.
     * @param player The player that teleported.
     * @param event The event which was received.
     */
    void processTeleport(WindtracePlayer player, PlayerTeleportEvent event);

    /**
     * Called when a player in this game instance moves.
     * @param player The player that moved.
     * @param event The event which was received.
     */
    void processMove(WindtracePlayer player, PlayerMoveEvent event);
}