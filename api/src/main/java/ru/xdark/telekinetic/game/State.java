package ru.xdark.telekinetic.game;

public enum State {

    BOOTSTRAPPING,
    PRE_INITIALIZATION,
    INITIALIZATION,
    POST_INITIALIZATION,
    COMPLETE,

    SERVER_INITIALIZATION,
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_STOPPED
}
