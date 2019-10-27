package ru.xdark.modloader.version;

import lombok.val;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VersionTest {

    @Test
    public void testParsing() {
        val first = Version.parse("1.0.1.5");
        assertEquals(1, first.getMajorVersion());
        assertEquals(0, first.getMinorVersion());
        assertEquals(1, first.getBuildVersion());
        assertEquals(5, first.getPatchVersion());
        assertEquals(Version.VersionType.RELEASE, first.getType());
    }

    @Test
    public void testCompare() {
        val first = Version.parse("1.1");
        val second = Version.parse("1.0");
        assertTrue(first.isGreaterThan(second));
        assertTrue(second.isLessThan(first));
    }

    @Test
    public void testCompareTypes() {
        val first = Version.parse("1.1");
        val second = Version.parse("1.1-BETA");
        assertTrue(first.isGreaterThan(second));
        assertTrue(second.isLessThan(first));
    }
}
