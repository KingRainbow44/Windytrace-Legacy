package lol.magix.windtrace.packet.handler;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.server.game.GameSession;

import static lol.magix.windtrace.proto.HideAndSeekPlayerReadyNotifyOuterClass.*;

@Opcodes(value = PacketOpcodes.HideAndSeekPlayerReadyNotify)
public final class PlayerReadyNotification extends PacketHandler {
    @Override public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        HideAndSeekPlayerReadyNotify notification = HideAndSeekPlayerReadyNotify.parseFrom(payload);
    }
}
