package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Constants;
import lol.magix.windtrace.game.GameFlags;
import lol.magix.windtrace.game.GameManager;
import lol.magix.windtrace.player.WindtracePlayer;
import lol.magix.windtrace.utils.PlayerUtils;

import java.util.List;

@Command(label = "windtrace", description = "The windtrace management command.",
    usage = "/windtrace <create> [mode]", targetRequirement = Command.TargetRequirement.PLAYER, 
    permission = "windtrace.manage", aliases = {"wt", "hideandseek", "hs"})
public final class WindtraceCommand implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        // Check if sender is in-game.
        if(!(sender instanceof WindtracePlayer player)) {
            CommandHandler.sendMessage(sender, "You must be in-game to use this command."); return;
        }
        
        // Check for arguments.
        if(args.size() < 1) {
            CommandHandler.sendMessage(sender, "Usage: /windtrace <create> [mode]"); return;
        }
        
        // Execute sub-command.
        switch(args.get(0)) {
            default -> CommandHandler.sendMessage(sender, "Unknown sub-command.");
            
            case "create" -> {
                // Check if player is in an existing game.
                if(player.isInGame()) {
                    CommandHandler.sendMessage(sender, "You are already in a game."); return;
                }
                
                // Check for mode argument.
                if(args.size() < 2) {
                    CommandHandler.sendMessage(sender, "Usage: /windtrace create [mode]"); return;
                }
                
                // Get mode.
                var mode = args.get(1);
                // Create game flags.
                var flags = new GameFlags();
                // Set mode from argument.
                try {
                    flags.gameMode = GameFlags.Mode.valueOf(mode.toUpperCase());
                } catch (IllegalArgumentException e) {
                    CommandHandler.sendMessage(sender, "Unknown mode."); return;
                }
                
                // Create game.
                var game = GameManager.createGame(flags, player);
                // Add the host to the game.
                game.addPlayer(player);
                
                // Send command feedback.
                CommandHandler.sendMessage(sender, "Successfully created a game.");
                CommandHandler.sendMessage(sender, "Have other players join with '/join " + sender.getUid() + "'.");
            }
            
            case "test" -> {
                Windblade.executeLua(Constants.RUNNER_UID_SCRIPT, List.of(player));
                CommandHandler.sendMessage(sender, "Test message sent.");
            }
        }
    }
}