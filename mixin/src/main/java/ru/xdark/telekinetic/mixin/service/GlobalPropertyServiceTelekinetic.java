package ru.xdark.telekinetic.mixin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;
import ru.xdark.launcher.RootLauncher;

public final class GlobalPropertyServiceTelekinetic implements IGlobalPropertyService {

    @Override
    public IPropertyKey resolveKey(String name) {
        return new Key(name);
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) RootLauncher.get().getProperties().get(((Key) key).key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        RootLauncher.get().getProperties().put(((Key)key).key, value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) RootLauncher.get().getProperties().getOrDefault(((Key)key).key, defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return getPropertyString(key, defaultValue);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Key implements IPropertyKey {

        private final String key;

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof Key && key.equals(((Key) obj).key);
        }
    }
}
