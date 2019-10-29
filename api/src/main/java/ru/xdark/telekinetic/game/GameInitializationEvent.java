package ru.xdark.telekinetic.game;

public final class GameInitializationEvent extends GameStateEvent {
    public GameInitializationEvent() {
        super(State.INITIALIZATION);
    }
}
