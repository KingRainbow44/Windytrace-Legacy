package lol.magix.windtrace.game.logic;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.game.GameLogicHandler;
import lol.magix.windtrace.player.WindtracePlayer;

/**
 * The generic chase game, with an *explosive* twist.
 */
public final class TagLogic implements GameLogicHandler {
    private final GameInstance instance;

    public TagLogic(GameInstance game) {
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
    public void doneLoading() {

    }

    @Override
    public void assignRole(WindtracePlayer player, GameInstance.Role role) {

    }

    @Override
    public void processPacket(WindtracePlayer player, String packetName, ReceivePacketEvent event) {

    }

    @Override
    public void processTeleport(WindtracePlayer player, PlayerTeleportEvent event) {

    }

    @Override
    public void processMove(WindtracePlayer player, PlayerMoveEvent event) {

    }
}
