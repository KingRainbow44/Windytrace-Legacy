package lol.magix.windtrace.game;

import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.player.WindtracePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all currently running games.
 */
public final class GameManager {
    private static final Map<UUID, GameInstance> games = new ConcurrentHashMap<>();

    /**
     * Generates a new game instance with the chosen flags.
     * @param flags The flags to use for the game.
     * @return The generated game instance.
     */
    public static GameInstance createGame(GameFlags flags, WindtracePlayer host) {
        var uuid = UUID.randomUUID(); // Generate a UUID.
        var game = new GameInstance(uuid, flags, host); // Create a new game instance.

        games.put(uuid, game); // Add the game to the map.
        return game; // Return the game instance.
    }

    /**
     * Returns an existing game instance by UUID.
     * @param uuid The UUID of the game instance.
     * @return The game instance, or null if it doesn't exist.
     */
    @Nullable public static GameInstance getGame(UUID uuid) {
        return games.get(uuid);
    }

    /**
     * Returns an existing game instance by host.
     * @param hostUid The UID of the host.
     * @return The game instance, or null if it doesn't exist.
     */
    @Nullable public static GameInstance getGameByHost(int hostUid) {
        for(var game : games.values()) {
            if(game.getHost().getUid() == hostUid)
                return game;
        } return null;
    }

    /**
     * Stops an existing game instance.
     * @param uuid The unique identifier of the game instance to stop.
     */
    public static void stopGame(UUID uuid) {
        if(!games.containsKey(uuid)) {
            return; // If the game doesn't exist, return.
        }

        games.remove(uuid); // Remove the game from the map.
    }

    /**
     * Called once per server tick.
     * Updates all running game instances.
     */
    public static void tickGames() {
        games.forEach((uuid, game) -> {
            if(game.getGameState() != GameInstance.State.IN_PROGRESS)
                return;

            try {
                // Tick the game.
                game.tick();
            } catch (IllegalAccessException exception) {
                Windtrace.getInstance().getLogger().debug("Unable to tick game instance " + uuid + "!", exception);
            }
        });
    }
}