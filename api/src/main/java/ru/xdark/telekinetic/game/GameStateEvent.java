package ru.xdark.telekinetic.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.telekinetic.event.Event;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameStateEvent implements Event {
    @Getter private final State state;
}
