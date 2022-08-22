package lol.magix.windtrace.game.logic;

import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import io.grasscutter.windblade.api.Windblade;
import lol.magix.windtrace.Constants;
import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.game.GameInstance.Role;
import lol.magix.windtrace.game.GameInstance.Strategy;
import lol.magix.windtrace.game.GameLogicHandler;
import lol.magix.windtrace.player.WindtracePlayer;
import lol.magix.windtrace.objects.Vector3;
import lol.magix.windtrace.utils.GadgetUtils;
import lol.magix.windtrace.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The concept of manhunt, implemented in Genshin Impact.
 */
public final class ManhuntLogic implements GameLogicHandler {
    private final GameInstance instance;

    /* These are box gadgets that should be removed after headstart. */
    private final List<EntityVehicle> hunterBoxes = new ArrayList<>();
    private final List<EntityVehicle> runnerBoxes = new ArrayList<>();

    public ManhuntLogic(GameInstance game) {
        this.instance = game;
    }

    @Override
    public void setup() {

    }

    @Override
    public void start() {
        var scene = this.instance.getFlags().scene;

        // Teleport all runners to the starting area.
        this.instance.getRunners().forEach(player -> {
            PlayerUtils.safeTeleport(player, scene.getSceneId(), scene.getSpawnPosition());

            // Spawn a box at the runner's spawn position.
            var box = GadgetUtils.createGadget(player, Constants.WINDTRACE_SUPER_PRISON,
                player.getPosition(), player.getRotation());
            // Add the box to the list of boxes to be removed after loading.
            this.runnerBoxes.add(box);
        });

        // Teleport all hunters to the starting area.
        this.instance.getHunters().forEach(player -> {
            PlayerUtils.safeTeleport(player, scene.getSceneId(), scene.getHunterSpawnPosition());

            // Spawn a box at the hunter's spawn position.
            var box = GadgetUtils.createGadget(player, Constants.WINDTRACE_SUPER_PRISON,
                player.getPosition(), player.getRotation());
            // Add the box to the list of boxes.
            this.hunterBoxes.add(box);
        });
    }

    @Override
    public void tick() {
        // Check if boxes should be removed.
        if(this.instance.getGameTicks() == this.instance.getFlags().headStartTime) {
            this.hunterBoxes.forEach(GadgetUtils::removeGadget);
            this.hunterBoxes.clear();
        }
    }

    @Override
    public void stop(boolean force) {
        // Remove all boxes.
        this.hunterBoxes.forEach(GadgetUtils::removeGadget);
        this.hunterBoxes.clear();
    }

    @Override
    public void doneLoading() {
        // Remove all boxes.
        this.runnerBoxes.forEach(GadgetUtils::removeGadget);
        this.runnerBoxes.clear();
    }

    @Override
    public void assignRole(WindtracePlayer player, Role role) {
        switch(role) {
            case RUNNER -> {
                // Set UID.
                Windblade.executeLua(Constants.RUNNER_UID_SCRIPT, player);
                // Send feedback.
                player.dropMessage("You have been assigned to the runners team.");
            }

            case HUNTER -> {
                // Set UID.
                Windblade.executeLua(Constants.HUNTER_UID_SCRIPT, player);
                // Send feedback.
                player.dropMessage("You have been assigned to the hunters team.");
            }
        }
    }

    @Override
    public void processPacket(WindtracePlayer player, String packetName, ReceivePacketEvent event) {
        switch(packetName) {
            case "SceneTransToPointReq" -> {
                if(!this.isHunter(player))
                    event.cancel();
                else if(this.instance.getGameTicks() < this.instance.getFlags().headStartTime)
                    event.cancel();
            }

            case "EvtDoSkillSuccNotify" -> {
                if(this.isHunter(player))
                    this.capture(player, this.instance.getFlags().captureDistance);
            }
        }
    }

    @Override
    public void processTeleport(WindtracePlayer player, PlayerTeleportEvent event) {
        if(!this.isHunter(player))
            event.cancel();
        else if(this.instance.getGameTicks() < this.instance.getFlags().headStartTime)
            event.cancel();
    }

    @Override
    public void processMove(WindtracePlayer player, PlayerMoveEvent event) {
        if(!this.isHunter(player))
            return;
        this.capture(player, 3.0f);
    }

    /* Logic checking methods. */

    /**
     * Checks if the player is permitted to teleport.
     * @param player The player to check.
     * @return True if the player is permitted to teleport, false otherwise.
     */
    private boolean isHunter(WindtracePlayer player) {
        return this.instance.getRole(player) == Role.HUNTER;
    }

    /**
     * Attempts to capture nearby players.
     * @param hunter The player to center around.
     */
    private void capture(WindtracePlayer hunter, float captureDistance) {
        // Get the position of the hunter.
        var hunterPosition = Vector3.from(hunter.getPosition());

        // For-each every runner.
        this.instance.getRunners().forEach(runner -> {
            // Get the position of the runner.
            var runnerPosition = Vector3.from(runner.getPosition());

            // Check if the runner is within range.
            if(runnerPosition.distance(hunterPosition) <= captureDistance) {
                // Capture the runner.
                this.instance.eliminatePlayer(runner, Strategy.CONVERT_TO_HUNTER);
            }
        });
    }
}
