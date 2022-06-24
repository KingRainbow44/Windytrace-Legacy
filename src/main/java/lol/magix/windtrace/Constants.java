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
        PLUGIN.getLogger().info("Loaded Windtrace constants.");
    }
    
    /* Property for constants. */
    private static final Windtrace PLUGIN = Windtrace.getInstance();
    
    /* Pre-compiled Lua scripts. */
    public static final byte[] HUNTER_UID_SCRIPT;
    public static final byte[] RUNNER_UID_SCRIPT;
    
    static {
        try {
            // Compile Lua snippets.
            HUNTER_UID_SCRIPT = Windblade.compileLua("CS.UnityEngine.GameObject.Find(\"/BetaWatermarkCanvas(Clone)/Panel/TxtUID\"):GetComponent(\"Text\").text = \"Hunter\"");
            RUNNER_UID_SCRIPT = Windblade.compileLua("CS.UnityEngine.GameObject.Find(\"/BetaWatermarkCanvas(Clone)/Panel/TxtUID\"):GetComponent(\"Text\").text = \"Runner\"");
        } catch (Exception exception) {
            PLUGIN.getLogger().error("Unable to read a Lua script.", exception);
            throw new RuntimeException("Unable to read a Lua script.");
        }
    }
}