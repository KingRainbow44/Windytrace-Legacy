package lol.magix.windtrace.utils;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.LifeState;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketLifeStateChangeNotify;
import emu.grasscutter.utils.Position;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Windtrace;
import lol.magix.windtrace.player.WindtracePlayer;

import java.util.Collection;
import java.util.List;

/**
 * Houses quick utilities related to the player.
 */
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
            Windblade.executeLuaAsync("CS.PAKJGCBJMOL.CEKENHBHGAF(\"" + message + "\")", WindtracePlayer.toPlayerCollection(players));
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
            Windblade.executeLuaAsync("CS.UnityEngine.GameObject.Find(\"/BetaWatermarkCanvas(Clone)/Panel/TxtUID\"):GetComponent(\"Text\").text = \"UID: " + player.getUid() + "\"", List.of(player));
        } catch (Exception exception) {
            Windtrace.getInstance().getLogger().debug("Unable to change UID text.", exception);
            player.dropMessage("Unable to change UID text.");
        }
    }

    /**
     * Kills the player's currently selected avatar.
     * @param player The player to kill the avatar of.
     */
    public static void killAvatar(WindtracePlayer player) {
        // Get the current avatar.
        var avatar = player.getTeamManager().getCurrentAvatarEntity();
        // Kill the avatar.
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, 0f);
        avatar.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(avatar, FightProperty.FIGHT_PROP_CUR_HP));
        avatar.getWorld().broadcastPacket(new PacketLifeStateChangeNotify(0, avatar, LifeState.LIFE_DEAD));
        // Remove the avatar from the scene.
        player.getScene().removeEntity(avatar);
        // Call death method.
        avatar.onDeath(player.getTeamManager().getCurrentAvatarEntity().getId());
    }

    /**
     * Attempts to safely teleport the player.
     * @param player The player to teleport.
     * @param position The position to teleport to.
     */
    public static void safeTeleport(Player player, Position position) {
        // Invoke PlayerUtils#safeTeleport(Player, Position).
        PlayerUtils.safeTeleport(player, player.getSceneId(), position);
    }

    /**
     * Attempts to safely teleport the player.
     * @param player The player to teleport.
     * @param sceneId The scene ID to teleport to.
     * @param position The position to teleport to.
     */
    public static void safeTeleport(Player player, int sceneId, Position position) {
        // Set the player's scene load state.
        player.setSceneLoadState(Player.SceneLoadState.LOADING);
        // Teleport the player using transferPlayerToScene.
        player.getWorld().transferPlayerToScene(player, sceneId, position);
    }
}