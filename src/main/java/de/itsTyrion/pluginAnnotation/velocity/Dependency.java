/*
 * Copyright (C) 2018-2021 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package de.itsTyrion.pluginAnnotation.velocity;

import java.lang.annotation.Target;

@Target({})
public @interface Dependency {

  /**
   * The plugin ID of the dependency.
   * @see VelocityPlugin#id()
   */
  String id();

  /**
   * Whether the dependency is not required to enable this plugin. By default, this is
   * {@code false}, meaning that the dependency is required to enable this plugin.
   */
  boolean optional() default false;
}