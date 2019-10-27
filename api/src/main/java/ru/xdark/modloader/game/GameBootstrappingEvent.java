package ru.xdark.modloader.game;

public final class GameBootstrappingEvent extends GameStateEvent {
    public GameBootstrappingEvent() {
        super(State.BOOTSTRAPPING);
    }
}
