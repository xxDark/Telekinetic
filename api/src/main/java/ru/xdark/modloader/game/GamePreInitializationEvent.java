package ru.xdark.modloader.game;

public final class GamePreInitializationEvent extends GameStateEvent {
    public GamePreInitializationEvent() {
        super(State.PRE_INITIALIZATION);
    }
}
