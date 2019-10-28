package ru.xdark.telekenetic.event;

public interface EventPriorities {
    int LOWEST = -32768;
    int LOW = -16384;
    int DEFAULT = 0;
    int NORMAL = 16384;
    int HIGH = 32768;
    int POST = 65536;
}
