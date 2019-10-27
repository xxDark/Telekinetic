package ru.xdark.modloader.game;

public final class GameInitializationEvent extends GameStateEvent {
    public GameInitializationEvent() {
        super(State.INITIALIZATION);
    }
}
