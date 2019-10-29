package ru.xdark.telekinetic.version;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionTest {

    @Test
    public void testParsing() {
        val version = Version.parse("1.0.1.5");
        assertEquals(1, version.getMajorVersion());
        assertEquals(0, version.getMinorVersion());
        assertEquals(1, version.getBuildVersion());
        assertEquals(5, version.getPatchVersion());
        assertEquals(DefaultVersionTypes.RELEASE, version.getType());
    }

    @Test
    public void testVersionTypeParsing() {
        val version = Version.parse("1.0.1-BETA");
        assertEquals(DefaultVersionTypes.BETA, version.getType());
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
