package lol.magix.windtrace.utils;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.world.World;
import emu.grasscutter.net.proto.EnterTypeOuterClass.EnterType;
import emu.grasscutter.server.packet.send.PacketPlayerEnterSceneNotify;

public final class MultiplayerUtils {
    private MultiplayerUtils() {
        // No construction.
    }

    /**
     * Opens a player's world for multiplayer usage.
     * @param player The player to open the world for.
     */
    public static void openForMultiplayer(Player player) {
        if(player.isInMultiplayer())
            return;

        // Re-create world with multiplayer.
        World world = new World(player, true);
        // Add player to world.
        world.addPlayer(player);

        // Send a packet to reload the world.
        player.sendPacket(new PacketPlayerEnterSceneNotify(
                player, player, EnterType.ENTER_TYPE_SELF,
                EnterReason.HostFromSingleToMp, player.getScene().getId(), player.getPosition()
        ));
    }

    /**
     * Forces a player to join the target's world.
     * @param player The player to force join the world.
     * @param target The player to join the world of.
     */
    public static void joinMultiplayerGame(Player player, Player target) {
        // Check if the target is in a multiplayer world.
        if(!target.isInMultiplayer())
            MultiplayerUtils.openForMultiplayer(target);

        // Get the world to join.
        var world = target.getWorld();

        // Add the player to the world.
        world.addPlayer(player);
        // Set the position to the target's position.
        player.getPosition().set(target.getPosition());
        // Set the rotation to the target's rotation.
        player.getRotation().set(target.getRotation());
        // Set the player's scene to the target's scene.
        player.setScene(target.getScene());

        // Send a packet to join the target's world.
        player.sendPacket(new PacketPlayerEnterSceneNotify(
                player, target, EnterType.ENTER_TYPE_OTHER,
                EnterReason.TeamJoin, target.getSceneId(),
                target.getPosition()
        ));
    }

    /**
     * Forces a player to leave the target's world.
     * @param player The player to force leave the world.
     * @param leaveReason The reason for leaving the world.
     */
    public static void leaveMultiplayerGame(Player player, Reason leaveReason) {
        if(!player.isInMultiplayer())
            return;

        // Check if player has loaded.
        if(player.getSceneLoadState() != Player.SceneLoadState.LOADED)
            return;

        // Convert reason into an EnterReason.
        var reason = switch(leaveReason) {
            case LEAVE -> EnterReason.TeamBack;
            case KICK -> EnterReason.TeamKick;
        };

        // Place the player back into their own world.
        var world = new World(player); world.addPlayer(player);
        // Send a leave packet.
        player.sendPacket(new PacketPlayerEnterSceneNotify(
                player, EnterType.ENTER_TYPE_SELF, reason,
                player.getScene().getId(), player.getPosition()
        ));
    }

    /* Reasons for leaving. */
    public enum Reason {
        LEAVE, KICK
    }
}