package lol.magix.windtrace.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all currently running games.
 */
public final class GameManager {
    private static final Map<UUID, GameInstance> games = new ConcurrentHashMap<>();
}