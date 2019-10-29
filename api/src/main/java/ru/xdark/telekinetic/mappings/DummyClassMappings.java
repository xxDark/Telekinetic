package ru.xdark.telekinetic.mappings;

public final class DummyClassMappings extends DummyMappings implements ClassMappings {
    @Override
    public Mappings fieldMappings() {
        return this;
    }

    @Override
    public Mappings methodMappings() {
        return this;
    }
}
