package ru.xdark.telekinetic.mod;

public interface ModContainer {

    ModDescription getModDescription();

    Object getInstance();
}
