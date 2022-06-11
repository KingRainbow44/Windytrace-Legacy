package lol.magix.windtrace.game.logic;

import lol.magix.windtrace.game.GameInstance;
import lol.magix.windtrace.game.GameLogicHandler;

public final class WindtraceLogic implements GameLogicHandler {
    private final GameInstance instance;
    
    public WindtraceLogic(GameInstance game) {
        this.instance = game;
    }
}
