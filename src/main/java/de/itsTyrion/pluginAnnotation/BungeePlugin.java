package de.itsTyrion.pluginAnnotation;

public @interface BungeePlugin {
    String name();
    String[] depends() default {};
    String description() default "";
    String author() default "";
    String[] softDepends() default {};
    String version() default "%mcPluginVersion%";
}
