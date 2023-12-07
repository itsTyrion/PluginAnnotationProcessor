package de.itsTyrion.pluginAnnotation;

public @interface CommandInfo {
    String name();
    String permission() default "";
    String[] aliases() default {};
    String usage() default "";
    String description() default "";
    String permissionMessage() default "";
}
