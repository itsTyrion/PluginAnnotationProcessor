package de.itsTyrion.pluginAnnotation.bukkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface BukkitPlugin {
    String name();
    String[] depend() default {};
    String description() default "";
    String apiVersion() default "1.20";
    String[] authors() default {};
    String[] softDepend() default {};
    String website() default "";
    String[] contributors() default {};
    String[] loadBefore() default {};
    String logPrefix() default "";
    String[] provides() default {};
    LoadAt load() default LoadAt.POSTWORLD;

    @SuppressWarnings("unused")
    enum LoadAt {STARTUP, POSTWORLD}
}
