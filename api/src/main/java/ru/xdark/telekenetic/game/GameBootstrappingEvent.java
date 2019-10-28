package ru.xdark.telekenetic.game;

public final class GameBootstrappingEvent extends GameStateEvent {
    public GameBootstrappingEvent() {
        super(State.BOOTSTRAPPING);
    }
}
