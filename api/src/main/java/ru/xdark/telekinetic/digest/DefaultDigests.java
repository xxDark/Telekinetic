package ru.xdark.telekinetic.digest;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class DefaultDigests {
    public final Supplier<Digest> MD4 = () -> new MessageDigest("MD4");
    public final Supplier<Digest> MD5 = () -> new MessageDigest("MD5");
    public final Supplier<Digest> SHA256 = () -> new MessageDigest("SHA-256");
    public final Supplier<Digest> SHA512 = () -> new MessageDigest("SHA-512");
}
