package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.game.GameFlags;
import lol.magix.windtrace.game.GameManager;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.List;

@Command(label = "windtrace", /* description = "The windtrace management command.", */
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
            CommandHandler.sendMessage(sender, "Usage: /windtrace <create|start|end> [mode]"); return;
        }

        // Execute sub-command.
        switch(args.get(0)) {
            default -> CommandHandler.sendMessage(sender, "Unknown sub-command.");

            case "reload" -> {
                // Reload the configuration file.
                Windtrace.getInstance().reloadConfig();
                // Send success message.
                CommandHandler.sendMessage(sender, "Configuration reloaded.");
            }

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
                var flags = player.getGameFlags();

                try {
                    // Set mode from argument.
                    flags.gameMode = GameFlags.Mode.valueOf(mode.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    CommandHandler.sendMessage(sender, "Unknown mode."); return;
                }

                // Create game.
                GameManager.createGame(flags, player);

                // Send command feedback.
                CommandHandler.sendMessage(sender, "Successfully created a game.");
                CommandHandler.sendMessage(sender, "Have other players join with '/join @" + sender.getUid() + "'.");
            }

            case "start" -> {
                // Check if player is in an existing game.
                if(!player.isInGame()) {
                    CommandHandler.sendMessage(sender, "You are not in a game."); return;
                }

                // Get the game.
                var game = player.getGameInstance();
                // Check if the player is the host.
                if(game.getHost().getUid() != player.getUid()) {
                    CommandHandler.sendMessage(sender, "You are not the host of this game."); return;
                }

                try {
                    // Start game.
                    game.start();
                } catch (Exception ignored) {
                    CommandHandler.sendMessage(sender, "Failed to start game."); return;
                }

                // Send command feedback.
                CommandHandler.sendMessage(sender, "Successfully started game.");
            }

            case "end" -> {
                // Check if player is in an existing game.
                if(!player.isInGame()) {
                    CommandHandler.sendMessage(sender, "You are not in a game."); return;
                }

                try {
                    // End game.
                    player.getGameInstance().stop(true);
                } catch (Exception ignored) {
                    CommandHandler.sendMessage(sender, "The game is not running."); return;
                }

                // Send command feedback.
                CommandHandler.sendMessage(sender, "Successfully ended the game.");
            }
        }
    }
}