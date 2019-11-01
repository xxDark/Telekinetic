package ru.xdark.telekinetic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NamedSide implements Side {
    @Getter
    private final String id;

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Side && id.equals(((Side) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
