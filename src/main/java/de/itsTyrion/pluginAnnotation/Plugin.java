package de.itsTyrion.pluginAnnotation;

public @interface Plugin {
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
    String version() default "%mcPluginVersion%";
    LoadAt load() default LoadAt.POSTWORLD;

    @SuppressWarnings("unused")
    enum LoadAt {STARTUP, POSTWORLD}
}
