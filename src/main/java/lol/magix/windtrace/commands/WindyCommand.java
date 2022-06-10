package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

@Command(label = "windy", description = "The windtrace configuration command.", 
        aliases = {"windtrace"}, usage = "windy <help>", 
        permission = "windtrace.config")
public final class WindyCommand implements CommandHandler {
    
    /*
     * Command usage:
     * /windy = Show message 'windy help'
     */
    
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        
    }
}