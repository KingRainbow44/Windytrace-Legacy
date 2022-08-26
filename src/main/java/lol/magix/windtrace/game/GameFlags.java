package lol.magix.windtrace.game;

import emu.grasscutter.utils.Position;
import lol.magix.windtrace.Constants;
import lol.magix.windtrace.Windtrace;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * Settings/flags for a game instance.
 */
public final class GameFlags {
    /* The game mode for the game. */
    public Mode gameMode = Mode.WINDTRACE;
    /* The scene to play in. */
    public Scene scene = Scene.SPAWN;

    /* The max amount of players in this game. */
    public int maxPlayers = 4;
    /* The amount of hunters to have. */
    public int hunterCount = 1;
    /* The maximum distance for the capture system. */
    public float captureDistance = Constants.DEFAULT_CAPTURE_DISTANCE;
    /* The amount of time for the runners to start running. */
    public int headStartTime = 10;
    /* The amount of time runners need to survive for. */
    public long survivalTime = TimeUnit.MINUTES.toSeconds(5);

    /* Should the game kick players when removing? */
    public boolean kickOnRemove = true;
    /* Should the game kill players when eliminating? */
    // WARNING: This flag is **experimental** and is buggy.
    public boolean murderOnEliminate = false;
    /* Should the game teleport all players to the start after ending? */
    // WARNING: This flag is **experimental** and is buggy.
    public boolean startTeleportAfterEnd = true;
    /* Should players instantly teleport to waypoints? */
    // WARNING: This flag is **experimental** and is buggy.
    public boolean instantWaypointTeleport = false;

    /*
     * Enums
     */

    public enum Mode {
        WINDTRACE, HIDE_AND_SEEK, MANHUNT, TAG
    }

    public enum Scene {
        MONDSTADT(3,
            2309.4597f, 249.98993f, -775.8998f,
            2347.104f, 259.9756f, -701.0056f),
        INAZUMA(3,
            -2569.9875f, 201.6807f, -3741.613f,
            -2522.6592f, 202.02132f, -3642.496f),
        LIYUE(3,
            -557.74176f, 225.0867f, 356.23678f,
            -454.90802f, 209.15298f, 345.19638f),
        SUMERU(3,
            -486.10565f, 223.8297f, 2806.2083f,
            -446.81128f, 238.12457f, 2746.1897f),
        ENKANOMIYA(5,
            277.19528f, 336.9558f, 280.86972f,
            284.50165f, 313.7694f, 350.44156f),
        CHASM(6,
            454.37927f, 378.07373f, 525.56287f,
            546.11633f, 387.87122f, 547.27545f),

        SPAWN(3,
            2735.5945f, 195.01889f, -1698.4061f,
            2816.5303f, 195.01242f, -1807.813f),
        SUMERU_2(3,
            -207.37392f, 226.32776f, 3520.6533f,
            -149.64601f, 238.30852f, 3455.5862f),
        CUSTOM();

        @Getter int sceneId;
        @Getter Position spawnPosition;
        @Getter Position hunterSpawnPosition;

        boolean canReload = false;

        Scene(int sceneId,
              float spawnX, float spawnY, float spawnZ,
              float hSpawnX, float hSpawnY, float hSpawnZ) {
            this.sceneId = sceneId;
            this.spawnPosition = new Position(spawnX, spawnY, spawnZ);
            this.hunterSpawnPosition = new Position(hSpawnX, hSpawnY, hSpawnZ);
        }

        Scene() {
            // Set values.
            this.canReload = true;
            this.reload();
        }

        /**
         * Attempts to reload properties from the config.
         */
        public void reload() {
            if(!this.canReload)
                return;

            // Get the plugin configuration.
            var config = Windtrace.getConfig().customMap;

            // Set values.
            this.sceneId = config.sceneId;
            this.spawnPosition = config.runnerSpawn.to();
            this.hunterSpawnPosition = config.hunterSpawn.to();
        }
    }
}