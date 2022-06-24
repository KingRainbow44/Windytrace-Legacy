package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import lol.magix.windtrace.game.GameManager;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.List;

@Command(label = "join", description = "Join a game of Windtrace.",
        usage = "/join <host UID>", targetRequirement = Command.TargetRequirement.NONE,
        permission = "windtrace.join", aliases = {"joingame", "jg"})
public final class JoinCommand implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        // Check if sender is in-game.
        if(!(sender instanceof WindtracePlayer player)) {
            CommandHandler.sendMessage(null, "You must be in-game to use this command."); return;
        }
        
        // Check if player is in an existing game.
        if(player.isInGame()) {
            CommandHandler.sendMessage(sender, "You are already in a game."); return;
        }

        // Check for arguments.
        if(args.size() < 1) {
            CommandHandler.sendMessage(sender, "Usage: /windtrace <create> [mode]"); return;
        }
        
        // Get the host's UID.
        var hostUid = args.get(0);
        // Get the game from the host's UID.
        var game = GameManager.getGameByHost(Integer.parseInt(hostUid));
        
        // Check if the game exists.
        if(game == null) {
            CommandHandler.sendMessage(sender, "The game does not exist."); return;
        }
        
        // Add the player to the game.
        game.addPlayer(player);
    }
}
