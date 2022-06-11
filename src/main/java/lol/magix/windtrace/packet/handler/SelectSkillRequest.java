package lol.magix.windtrace.packet.handler;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.server.game.GameSession;

import static lol.magix.windtrace.proto.HideAndSeekSelectSkillReqOuterClass.*;

@Opcodes(PacketOpcodes.HideAndSeekSelectSkillReq)
public final class SelectSkillRequest extends PacketHandler {
    @Override public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        HideAndSeekSelectSkillReq request = HideAndSeekSelectSkillReq.parseFrom(payload);
    }
}