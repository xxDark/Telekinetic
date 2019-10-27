package ru.xdark.modloader.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.event.Event;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameStateEvent implements Event {
    @Getter private final State state;
}
