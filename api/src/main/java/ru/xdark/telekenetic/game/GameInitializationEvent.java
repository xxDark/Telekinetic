package ru.xdark.telekenetic.game;

public final class GameInitializationEvent extends GameStateEvent {
    public GameInitializationEvent() {
        super(State.INITIALIZATION);
    }
}
