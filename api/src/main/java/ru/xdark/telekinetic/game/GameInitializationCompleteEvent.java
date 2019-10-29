package ru.xdark.telekinetic.game;

public final class GameInitializationCompleteEvent extends GameStateEvent {
    public GameInitializationCompleteEvent() {
        super(State.COMPLETE);
    }
}
