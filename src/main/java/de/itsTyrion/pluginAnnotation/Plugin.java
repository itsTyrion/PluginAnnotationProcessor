package de.itsTyrion.pluginAnnotation;

public @interface Plugin {
    String name();
    String version() default "{project.version}";
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

    enum LoadAt {STARTUP, POSTWORLD}
}
