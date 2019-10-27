package ru.xdark.modloader.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {

    String name();

    String id();

    String version();

    String url() default "";

    String[] authors() default {};
}
