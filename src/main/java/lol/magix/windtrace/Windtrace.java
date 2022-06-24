package lol.magix.windtrace;

import emu.grasscutter.server.event.*;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.plugin.Plugin;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.listeners.*;
import lol.magix.windtrace.commands.*;
import lol.magix.windtrace.tasks.WindtraceUpdateTask;

import java.util.Collection;
import java.util.LinkedList;

public final class Windtrace extends Plugin {
    private static Windtrace instance;

    /**
     * Returns the instance of the plugin.
     * @return A {@link Windtrace} singleton instance.
     */
    public static Windtrace getInstance() {
        return Windtrace.instance;
    }
    
    /* All plugin tasks. */
    private final Collection<Integer> tasks =
            new LinkedList<>();
    
    @Override
    public void onLoad() {
        instance = this; // Set the instance to this plugin.
        
        // Ensure Windblade is loaded.
        Windblade.setLogger(this.getLogger());
        // Load constants.
        Constants.load();
        
        this.getLogger().info("Windtrace was loaded.");
    }   

    @Override
    public void onEnable() {
        // Register all commands.
        this.getHandle().registerCommand(new WindtraceCommand());
        this.getHandle().registerCommand(new JoinCommand());
        this.getHandle().registerCommand(new LeaveCommand());
        
        // Register event listeners.
        new EventHandler<>(PlayerCreationEvent.class)
                .listener(PlayerListener::onPlayerCreation)
                .priority(HandlerPriority.LOW).register(this);

        new EventHandler<>(ReceivePacketEvent.class)
                .listener(PlayerListener::onPacketReceive)
                .priority(HandlerPriority.HIGH).register(this);
        
        // Schedule all tasks.
        var scheduler = this.getServer().getScheduler();
        this.tasks.add(scheduler.scheduleRepeatingTask(new WindtraceUpdateTask(), 1));
        
        this.getLogger().info("Windtrace was enabled.");
    }
    
    @Override
    public void onDisable() {
        // Un-register all commands.
        this.getServer().getCommandMap().unregisterCommand("windtrace");
        this.getServer().getCommandMap().unregisterCommand("join");
        this.getServer().getCommandMap().unregisterCommand("leave");
        
        // Cancel all tasks.
        var scheduler = this.getServer().getScheduler();
        this.tasks.forEach(scheduler::cancelTask);
        
        this.getLogger().info("Windtrace was disabled.");
    }
}