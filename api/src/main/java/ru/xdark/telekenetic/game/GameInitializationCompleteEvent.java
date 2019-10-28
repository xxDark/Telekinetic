package ru.xdark.telekenetic.game;

public final class GameInitializationCompleteEvent extends GameStateEvent {
    public GameInitializationCompleteEvent() {
        super(State.COMPLETE);
    }
}
