package lol.magix.windtrace;

import emu.grasscutter.server.event.*;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.plugin.Plugin;

import lol.magix.windtrace.listeners.*;
import lol.magix.windtrace.packet.handler.*;
import lol.magix.windtrace.commands.*;

import lol.magix.windtrace.utils.PacketUtils;

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
        
        PacketUtils.windy(); // w i n d y .  Disable Grasscutter's packet protection.
        
        this.getLogger().info("Windtrace was loaded.");
    }   

    @Override
    public void onEnable() {
        // Register all commands.
        this.getHandle().registerCommand(new WindyCommand());
        
        // Register packet handlers.
        this.getHandle().registerPacket(SelectSkillRequest.class);
        
        // Register event listeners.
        new EventHandler<>(PlayerCreationEvent.class)
                .listener(PlayerListener::onPlayerCreation)
                .priority(HandlerPriority.LOW).register();
        
        this.getLogger().info("Windtrace was enabled.");
    }
    
    @Override
    public void onDisable() {
        // Un-register all commands.
        this.getServer().getCommandMap().unregisterCommand("windy");
        
        this.getLogger().info("Windtrace was disabled.");
    }
}