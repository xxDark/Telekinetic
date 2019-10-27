package ru.xdark.modloader.game;

public final class GameInitializationCompleteEvent extends GameStateEvent {
    public GameInitializationCompleteEvent() {
        super(State.COMPLETE);
    }
}
