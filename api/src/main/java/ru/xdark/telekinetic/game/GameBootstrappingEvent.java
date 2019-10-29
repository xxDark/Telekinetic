package ru.xdark.telekinetic.game;

public final class GameBootstrappingEvent extends GameStateEvent {
    public GameBootstrappingEvent() {
        super(State.BOOTSTRAPPING);
    }
}
