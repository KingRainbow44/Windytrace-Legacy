package lol.magix.windtrace.game.logic;

import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.game.GameLogicHandler;

public final class HideAndSeekLogic implements GameLogicHandler {
    private final GameInstance instance;

    public HideAndSeekLogic(GameInstance game) {
        this.instance = game;
    }
}
