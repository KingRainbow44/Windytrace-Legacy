package lol.magix.windtrace.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import lol.magix.windtrace.game.GameFlags;
import lol.magix.windtrace.player.WindtracePlayer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Command(label = "flags", /* description = "Change your next game's flags.", */
        usage = "/flags <flag> <setting>", targetRequirement = Command.TargetRequirement.NONE,
        permission = "windtrace.join", aliases = {"joingame", "jg"})
public final class FlagsCommand implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        // Check if sender is in-game.
        if(!(sender instanceof WindtracePlayer player)) {
            CommandHandler.sendMessage(null, "You must be in-game to use this command."); return;
        }

        // Check for arguments.
        if(args.size() < 2) {
            CommandHandler.sendMessage(sender, "Usage: /flags <flag> <setting>"); return;
        }

        // Get the arguments.
        var property = args.get(0);
        var setting = args.get(1);

        // Attempt to set property.
        try {
            // Get the flags and it's class.
            var flagsClass = GameFlags.class;
            var flags = player.getGameFlags();

            // Get the field.
            var field = flagsClass.getField(property);
            // Check if the field is accessible.
            if(!field.canAccess(flags))
                field.setAccessible(true);
            // Get the field's type.
            var fieldType = field.getType();

            if(fieldType == boolean.class) {
                // Set the field.
                field.setBoolean(flags, Boolean.parseBoolean(setting));
            } else if(fieldType == int.class) {
                // Set the field.
                field.setInt(flags, Integer.parseInt(setting));
            } else if(fieldType == long.class) {
                // Set the field.
                field.setLong(flags, Long.parseLong(setting));
            } else if(fieldType == float.class) {
                // Set the field.
                field.setFloat(flags, Float.parseFloat(setting));
            } else if(fieldType == String.class) {
                // Set the field.
                field.set(flags, setting);
            } else if(field.get(flags) instanceof Enum<?>) {
                // Try to parse a value.
                var valueMethod = fieldType.getMethod("valueOf", String.class);
                var value = valueMethod.invoke(null, setting.toUpperCase());
                // Set the field.
                field.set(flags, value);
            } else {
                // Unknown type.
                CommandHandler.sendMessage(sender, "Unknown type."); return;
            }

            // Send command feedback.
            CommandHandler.sendMessage(sender, "Successfully set " + property + " to " + setting + ".");
        } catch (NoSuchFieldException ignored) {
            // Send command feedback.
            CommandHandler.sendMessage(sender, "Unknown flag.");
        } catch (IllegalAccessException ignored) {
            // Send command feedback.
            CommandHandler.sendMessage(sender, "Unable to set flag.");
        } catch (NoSuchMethodException | InvocationTargetException ignored) {
            // Send command feedback.
            CommandHandler.sendMessage(sender, "Unable to parse value.");
        } catch (NumberFormatException ignored) {
            // Send command feedback.
            CommandHandler.sendMessage(sender, "Invalid value.");
        }
    }
}