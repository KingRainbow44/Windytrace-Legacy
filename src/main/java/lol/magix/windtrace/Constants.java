package lol.magix.windtrace;

import io.grasscutter.windblade.api.Windblade;

public final class Constants {
    private Constants() {
        // No construction.
    }

    /**
     * Loads the constants.
     * This method does nothing.
     * Its logic is instead implemented by the static constructor.
     */
    public static void load() {
        PLUGIN.getLogger().debug("Loaded Windtrace constants.");
    }

    /* Property for constants. */
    private static final Windtrace PLUGIN = Windtrace.getInstance();

    /* Properties for Manhunt. */
    public static final float DEFAULT_CAPTURE_DISTANCE = 12.0f;

    /* Properties for Windtrace. */
    public static final int WINDTRACE_SUPER_PRISON = 44000108;

    /* Pre-compiled Lua scripts. */
    public static final byte[] HUNTER_UID_SCRIPT;
    public static final byte[] RUNNER_UID_SCRIPT;

    static {
        try {
            // Compile Lua snippets.
            HUNTER_UID_SCRIPT = Windblade.compileLua("CS.UnityEngine.GameObject.Find(\"/BetaWatermarkCanvas(Clone)/Panel/TxtUID\"):GetComponent(\"Text\").text = \"Role: Hunter\"");
            RUNNER_UID_SCRIPT = Windblade.compileLua("CS.UnityEngine.GameObject.Find(\"/BetaWatermarkCanvas(Clone)/Panel/TxtUID\"):GetComponent(\"Text\").text = \"Role: Runner\"");
        } catch (Exception exception) {
            PLUGIN.getLogger().error("Unable to read a Lua script.", exception);
            throw new RuntimeException("Unable to read a Lua script.");
        }
    }
}