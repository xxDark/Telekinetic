package ru.xdark.telekinetic.event;

import lombok.val;
import org.junit.jupiter.api.Test;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.mod.ModDescription;
import ru.xdark.telekinetic.resources.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEventBusTest {

    @Test
    public void testPriorities() {
        val bus = new SimpleEventBus(SimpleEventBusTest.class.getClassLoader());
        bus.registerListener(this, StringEvent.class, event -> {
            event.value = "Hello";
        }, 50);
        bus.registerListener(this, StringEvent.class, event -> {
            event.value = "World!";
        }, 100);
        val event = bus.dispatch(new StringEvent(), Runnable::run).join();
        assertEquals("World!", event.value);
    }

    @Test
    public void testCompilation() {
        val bus = new SimpleEventBus(SimpleEventBusTest.class.getClassLoader());
        bus.registerListeners(new ModContainer() {
            @Override
            public ModDescription getModDescription() {
                return null;
            }

            @Override
            public Object getInstance() {
                return null;
            }

            @Override
            public List<Resource> findResources(String path) {
                return null;
            }

            @Override
            public boolean hasResource(String path) {
                return false;
            }
        }, new TestListener());
        val event = bus.dispatch(new StringEvent(), Runnable::run).join();
        assertEquals("Hello, World!", event.value);
    }

    @Test
    public void testUnregister() {
        val bus = new SimpleEventBus(SimpleEventBusTest.class.getClassLoader());
        val listener = bus.registerListener(this, StringEvent.class, event -> {
            event.value = "Modified";
        }, 50);
        val firstEvent = bus.dispatch(new StringEvent(), Runnable::run).join();
        assertEquals("Modified", firstEvent.value);
        assertTrue(listener.unregister());
        val secondEvent = bus.dispatch(new StringEvent(), Runnable::run).join();
        assertNull(secondEvent.value);
    }

    private static class TestListener {
        @Listener
        void firstMethod(StringEvent e) {
            e.value = "Hello, World!";
        }
    }

    private static class StringEvent implements Event {

        String value;
    }
}
