package de.itsTyrion.pluginAnnotation;

public @interface BungeePlugin {
    String name();
    String version() default "{project.version}";
    String[] depends() default {};
    String description() default "";
    String author() default "";
    String[] softDepends() default {};
}
