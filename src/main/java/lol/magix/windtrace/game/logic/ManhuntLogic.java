package lol.magix.windtrace.game.logic;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Constants;
import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.game.GameInstance.Role;
import lol.magix.windtrace.game.GameLogicHandler;
import lol.magix.windtrace.player.WindtracePlayer;

public final class ManhuntLogic implements GameLogicHandler {
    private final GameInstance instance;

    public ManhuntLogic(GameInstance game) {
        this.instance = game;
    }

    @Override
    public void setup() {

    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stop(boolean force) {

    }

    @Override
    public void assignRole(WindtracePlayer player, Role role) {
        switch(role) {
            case RUNNER -> {
                // Set UID.
                Windblade.executeLua(Constants.RUNNER_UID_SCRIPT, player);
            }
            
            case HUNTER -> {
                // Set UID.
                Windblade.executeLua(Constants.HUNTER_UID_SCRIPT, player);
            }
        }
    }

    @Override
    public void processPacket(WindtracePlayer player, String packetName, ReceivePacketEvent event) {
        switch(packetName) {
            case "SceneTransToPointReq" -> {
                if(!this.canTeleport(player))
                    event.cancel();
            }
        }
    }
    
    /* Logic checking methods. */

    /**
     * Checks if the player is permitted to teleport.
     * @param player The player to check.
     * @return True if the player is permitted to teleport, false otherwise.
     */
    private boolean canTeleport(WindtracePlayer player) {
        return this.instance.getRole(player) == Role.HUNTER;
    }
}
