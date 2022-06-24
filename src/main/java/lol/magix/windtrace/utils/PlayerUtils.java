package lol.magix.windtrace.utils;

import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.Collection;
import java.util.List;

/**
 * Houses quick utilities related to the player.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class PlayerUtils {
    private PlayerUtils() {
        // No construction.
    }

    /**
     * Broadcasts a message to specified players.
     * @param message The message to broadcast.
     * @param players The players to send the message to.
     */
    public static void broadcastMessage(String message, Collection<WindtracePlayer> players) {
        players.forEach(player -> player.dropMessage(message));
    }

    /**
     * Broadcasts a memo to specified players.
     * @param message The message to broadcast.
     * @param players The players to send the message to.
     */
    public static void broadcastMemo(String message, Collection<WindtracePlayer> players) {
        // Use wind seed to send a memo.
        try {
            Windblade.executeLuaAsync("""
                    CS.PAKJGCBJMOL.CEKENHBHGAF("%s")
                    """.formatted(message), WindtracePlayer.toCollection(players));
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().debug("Unable to send memo.", exception);
        }   
    }

    /**
     * Changes the UID text in the player's client back to default.
     * @param player The player to change the UID text for.
     */
    public static void reloadUid(WindtracePlayer player) {
        // Use wind seed to change the UID text.
        try {
            Windblade.executeLuaAsync("""
                    CS.UnityEngine.GameObject.Find("/BetaWatermarkCanvas(Clone)/Panel/TxtUID"):GetComponent("Text").text = "%s\""""
                    .formatted(player.getUid()), List.of(player));
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().debug("Unable to change UID text.", exception);
            player.dropMessage("Unable to change UID text.");
        }
    }
}