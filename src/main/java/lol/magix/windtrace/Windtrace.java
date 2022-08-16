package lol.magix.windtrace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emu.grasscutter.server.event.*;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.plugin.Plugin;

import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.game.GameFlags;
import lol.magix.windtrace.listeners.*;
import lol.magix.windtrace.commands.*;
import lol.magix.windtrace.tasks.WindtraceUpdateTask;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import lombok.Getter;

public final class Windtrace extends Plugin {
    @Getter private static final Gson gson =
        new GsonBuilder().setPrettyPrinting().create();

    @Getter private static Windtrace instance;
    @Getter private static Configuration config;

    /* All plugin tasks. */
    private final Collection<Integer> tasks =
            new LinkedList<>();

    /**
     * Reloads the configuration from the file.
     */
    public void reloadConfig() {
        try {
            // Get the config file.
            var configFile = new File(this.getDataFolder(), "config.json");
            // Read the config file.
            if(!configFile.exists()) {
                Files.write(configFile.toPath(), new Configuration().serialize());
            }

            // Load the configuration.
            config = gson.fromJson(new FileReader(configFile), Configuration.class);
        } catch (IOException ignored) {
            this.getLogger().warn("Unable to reload configuration.");
        }

        // Reload the scene enum.
        GameFlags.Scene.CUSTOM.reload();
    }

    @Override
    public void onLoad() {
        instance = this; // Set the instance to this plugin.

        // Ensure Windblade is loaded.
        Windblade.setLogger(this.getLogger());
        // Load constants.
        Constants.load();

        // Load configuration.
        try {
            var configFile = new File(this.getDataFolder(), "config.json");
            if(!configFile.exists()) {
                Files.write(configFile.toPath(), new Configuration().serialize());
            }

            config = gson.fromJson(new FileReader(configFile), Configuration.class);
        } catch (IOException ignored) {
            this.getLogger().warn("Failed to load configuration.");
        }

        this.getLogger().info("Windtrace was loaded.");
    }

    @Override
    public void onEnable() {
        // Register all commands.
        this.getHandle().registerCommand(new WindtraceCommand());
        this.getHandle().registerCommand(new JoinCommand());
        this.getHandle().registerCommand(new LeaveCommand());
        this.getHandle().registerCommand(new FlagsCommand());
        this.getHandle().registerCommand(new SyncPositionCommand());

        // Register event listeners.
        new EventHandler<>(PlayerCreationEvent.class)
                .listener(PlayerListener::onPlayerCreation)
                .priority(HandlerPriority.LOW).register(this);

        new EventHandler<>(ReceivePacketEvent.class)
                .listener(PlayerListener::onPacketReceive)
                .priority(HandlerPriority.HIGH).register(this);

        new EventHandler<>(PlayerMoveEvent.class)
            .listener(GameListener::onMove)
            .priority(HandlerPriority.HIGH).register(this);

        new EventHandler<>(PlayerTeleportEvent.class)
            .listener(GameListener::onTeleport)
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
        this.getServer().getCommandMap().unregisterCommand("flags");
        this.getServer().getCommandMap().unregisterCommand("syncposition");

        // Cancel all tasks.
        var scheduler = this.getServer().getScheduler();
        this.tasks.forEach(scheduler::cancelTask);

        this.getLogger().info("Windtrace was disabled.");
    }
}