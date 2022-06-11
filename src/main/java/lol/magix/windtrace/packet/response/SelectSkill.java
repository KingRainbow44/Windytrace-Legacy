package lol.magix.windtrace.packet.response;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;

public final class SelectSkill extends BasePacket {
    public SelectSkill() {
        super(PacketOpcodes.HideAndSeekSelectSkillRsp);
    }
}
