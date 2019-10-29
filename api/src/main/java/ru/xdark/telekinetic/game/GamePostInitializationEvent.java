package ru.xdark.telekinetic.game;

public final class GamePostInitializationEvent extends GameStateEvent {
    public GamePostInitializationEvent() {
        super(State.POST_INITIALIZATION);
    }
}
