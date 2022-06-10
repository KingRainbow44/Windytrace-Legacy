package lol.magix.windtrace;

import emu.grasscutter.plugin.Plugin;
import lol.magix.windtrace.commands.WindyCommand;

public final class Windtrace extends Plugin {
    private static Windtrace instance;

    /**
     * Returns the instance of the plugin.
     * @return A {@link Windtrace} singleton instance.
     */
    public static Windtrace getInstance() {
        return Windtrace.instance;
    }
    
    @Override
    public void onLoad() {
        instance = this; // Set the instance to this plugin.
        
        this.getLogger().info("Windtrace was loaded.");
    }

    @Override
    public void onEnable() {
        // Register all commands.
        this.getHandle().registerCommand(new WindyCommand());
        
        this.getLogger().info("Windtrace was enabled.");
    }
    
    @Override
    public void onDisable() {
        // Un-register all commands.
        this.getServer().getCommandMap().unregisterCommand("windy");
        
        this.getLogger().info("Windtrace was disabled.");
    }
}