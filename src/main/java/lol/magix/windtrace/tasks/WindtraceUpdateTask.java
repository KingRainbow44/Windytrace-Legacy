package lol.magix.windtrace.tasks;

import lol.magix.windtrace.game.GameManager;

public final class WindtraceUpdateTask implements Runnable {
    @Override public void run() {
        // Tick all running games.
        GameManager.tickGames();
    }
}
