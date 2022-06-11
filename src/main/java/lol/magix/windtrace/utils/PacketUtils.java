package lol.magix.windtrace.utils;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.PacketOpcodes;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.packet.response.WindSeed;

import java.util.Collection;
import java.util.HashSet;

public final class PacketUtils {
    private PacketUtils() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Disables the banned packet protection for Grasscutter.
     * THIS SHOULD NOT BE DONE UNDER NORMAL CIRCUMSTANCES.
     */
    public static void windy() {
        try {
            var field = PacketOpcodes.class.getDeclaredField("BANNED_PACKETS"); // Get the 'BANNED_PACKETS' field.
            field.setAccessible(true); // Make the field accessible.
            field.set(null, new HashSet<>()); // Set the field to an empty set.
            field.setAccessible(false); // Make the field inaccessible.
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().warn("Failed to disable packet protection.", exception);
        }
    }

    /**
     * Executes Lua code on a list of clients.
     * @param lua The name of the Lua script to execute.
     * @param clients The list of clients to execute the script on.
     */
    public static void windy(String lua, Collection<Player> clients) {
        var luaFile = Windtrace.getInstance().getResource("lua/" + lua + ".luac"); // Get the Lua script.
        if (luaFile == null) {
            Windtrace.getInstance().getLogger().warn("Failed to find Lua script '" + lua + "'.");
            return;
        }
        
        var script = new byte[Byte.MAX_VALUE]; // Read the compiled Lua into a buffer array.
        try {
            Windtrace.getInstance().getLogger().info("Read " + luaFile.read(script) + " bytes from Lua script '" + lua + "'.");
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().warn("Failed to read Lua script '" + lua + "'.", exception);
            return;
        }
        
        var encoded = StringUtils.base64Encode(script); // Encode the Lua script.
        
        // Broadcast the packet to the specified clients.
        clients.forEach(client -> client.sendPacket(new WindSeed(encoded)));
    }
}
