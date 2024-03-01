[![](https://jitpack.io/v/de.itsTyrion/PluginAnnotationProcessor.svg)](https://jitpack.io/#de.itsTyrion/PluginAnnotationProcessor)
# Plugin Annotation Processor

A toolkit that simplifies the generation of Minecraft server plugin meta files using annotations.

## Overview

This tool provides a convenient way to generate the `plugin.yml`/`bungee.yml`/`velocity-plugin.json` file for your plugin.  
It utilizes annotations to define the required information directly in your code and fill in the version automatically.

## Prerequisites
- Java 8 or higher
- Bukkit/Spigot/Paper/Pufferfish/Purpur - latest version recommended but should down to 1.8 and further
- BungeeCord/Waterfall latest - it supports down to MC 1.8 as of 2024
- Velocity latest (but any 3.x should work) - it supports down to MC 1.8 as of 2024

## Features
- **Annotation-based Configuration**: Use the `@BukkitPlugin`/`@BungeePlugin`/`@VelocityPlugin` annotations to define plugin information directly in your Java/Kotlin code.
- **Cross-Compatibility**: Works with Bukkit, Spigot, Paper, Pufferfish, Purpur, BungeeCord, Waterfall and Velocity.
- **Versioning**: Version number is derived from the Gradle/Maven project version. (Optional)

---
## Usage
Moved to [the wiki](https://github.com/itsTyrion/PluginAnnotationProcessor/wiki) 


## Contributing
Feel free to contribute to the development of this plugin by opening issues or submitting pull requests.

## API Reference
Just add it to the plugin's main class
Parameter names match plugin.yml/bungee.yml and Velocity's own `@Plugin` annotation 

## License
This project is licensed under the MIT License.