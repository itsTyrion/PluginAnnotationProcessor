/*
 * Copyright (C) 2018-2021 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package de.itsTyrion.pluginAnnotation.velocity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface VelocityPlugin {

    /**
     * The plugin's ID. This ID should be unique as to not conflict with other plugins. The plugin ID
     * may contain alphanumeric characters, dashes, and underscores, and be a maximum of 64 characters long.
     */
    String id();

    /**
     * The human-readable name of the plugin as to be used in descriptions and similar things.
     */
    String name() default "";

    /**
     * The plugin description, briefly explaining its use.
     */
    String description() default "";

    /**
     * The plugin's website/URL, or an empty string if unknown
     */
    String url() default "";

    /**
     * The plugin's author, or empty if unknown
     */
    String[] authors() default {};

    /**
     * The dependencies required to load before this plugin.
     */
    Dependency[] dependencies() default {};
}
