package ru.xdark.telekinetic.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Objects;

@RequiredArgsConstructor
public final class Version implements Comparable<Version> {

    private final int[] digits;
    @Getter
    private final VersionType type;

    public static Version parse(String input) {
        val index = input.indexOf('-');
        val versionType = (index == -1) ? DefaultVersionTypes.RELEASE : DefaultVersionTypes.getByName(input.substring(index + 1));
        val parts = (index == -1) ? input.split("\\.") : (input.substring(0, index).split("\\."));
        int length = parts.length;
        val digits = new int[length];
        while (length-- > 0) {
            digits[length] = Integer.parseInt(parts[length], 10);
        }
        return new Version(digits, versionType);
    }

    public int getMajorVersion() {
        return this.digits[0];
    }

    public int getMinorVersion() {
        return getDigitAt(1);
    }

    public int getBuildVersion() {
        return getDigitAt(2);
    }

    public int getPatchVersion() {
        return getDigitAt(3);
    }

    public boolean isGreaterThan(Version o) {
        return compareTo(o) > 0;
    }

    public boolean isGreaterOrEqualTo(Version o) {
        return compareTo(o) >= 0;
    }

    public boolean isLessThan(Version o) {
        return compareTo(o) < 0;
    }

    public boolean isLessThanOrEqualTo(Version o) {
        return compareTo(o) <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Version && compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digits, type);
    }

    @Override
    public String toString() {
        val builder = new StringBuilder();
        val digits = this.digits;
        for (int i = 0, j = digits.length; i < j; i++) {
            builder.append(digits[i]).append('.');
        }
        return builder.deleteCharAt(builder.length() - 1).append('-').append(this.type.getId()).toString();
    }

    @Override
    public int compareTo(Version o) {
        val ourDigits = this.digits;
        for (int i = 0, j = ourDigits.length; i < j; i++) {
            val result = Integer.compare(ourDigits[i], o.getDigitAt(i));
            if (result != 0) return result;
        }
        return this.type.compareTo(o.type);
    }

    private int getDigitAt(int index) {
        val digits = this.digits;
        return digits.length > index ? digits[index] : 0;
    }
}
