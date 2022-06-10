package lol.magix.windtrace;

import emu.grasscutter.plugin.Plugin;

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
        this.getLogger().info("Windtrace was enabled.");
    }
    
    @Override
    public void onDisable() {
        this.getLogger().info("Windtrace was disabled.");
    }
}