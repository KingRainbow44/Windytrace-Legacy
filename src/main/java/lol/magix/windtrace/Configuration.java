package lol.magix.windtrace;

import lol.magix.windtrace.objects.Vector3;

import java.nio.charset.StandardCharsets;

/**
 * The configuration class for Windtrace.
 * Comes with default values for writing.
 */
public final class Configuration {
    /* Custom map configuration. */
    public Map customMap = new Map();

    public static class Map {
        public int sceneId = 3;
        public Vector3 runnerSpawn = Vector3.ZERO;
        public Vector3 hunterSpawn = Vector3.ZERO;
    }

    /**
     * Serializes the configuration to a string.
     * @return The serialized configuration.
     */
    public byte[] serialize() {
        // Serialize the configuration.
        return Windtrace.getGson().toJson(this)
            .getBytes(StandardCharsets.UTF_8);
    }
}