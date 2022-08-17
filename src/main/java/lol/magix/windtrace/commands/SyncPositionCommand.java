package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketScenePlayerLocationNotify;
import emu.grasscutter.server.packet.send.PacketWorldPlayerLocationNotify;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.List;

@Command(label = "syncposition", /* description = "Syncs your map with player's positions.", */
    usage = "/syncposition", targetRequirement = Command.TargetRequirement.ONLINE,
    permission = "windtrace.sync", aliases = {"syncpos"})
public final class SyncPositionCommand implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        // Check if sender is in-game.
        if(!(sender instanceof WindtracePlayer player)) {
            CommandHandler.sendMessage(null, "You must be in-game to use this command."); return;
        }

        // Check if player is in an existing game.
        if(!player.isInGame()) {
            CommandHandler.sendMessage(sender, "You are not in a game."); return;
        }

        // Update the player's map.
        player.sendPacket(new PacketWorldPlayerLocationNotify(player.getWorld()));
        player.sendPacket(new PacketScenePlayerLocationNotify(player.getScene()));

        // Send a message to the player.
        CommandHandler.sendMessage(sender, "You have synced your map.");
    }
}
