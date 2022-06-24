package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.List;

@Command(label = "leave", description = "Leave a game of Windtrace.",
        usage = "/leave", targetRequirement = Command.TargetRequirement.ONLINE,
        permission = "windtrace.leave", aliases = {"leavegame", "lg"})
public final class LeaveCommand implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        if(sender == null) {
            CommandHandler.sendMessage(null, "You must be in-game to use this command."); return;
        }

        /* Cast the sender to a Windtrace player. */
        WindtracePlayer player = (WindtracePlayer) sender;
        
        // Check if the player is in a game.
        if(!player.isInGame()) {
            CommandHandler.sendMessage(sender, "You are not in a game."); return;
        }
        
        // Leave the game.
        player.getGameInstance().removePlayer(player);
        // Send a message to the player.
        CommandHandler.sendMessage(sender, "You have left the game.");
    }
}
