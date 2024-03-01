package de.itsTyrion.pluginAnnotation.bungee;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface BungeePlugin {
    String name();
    String[] depends() default {};
    String description() default "";
    String author() default "";
    String[] softDepends() default {};
}
