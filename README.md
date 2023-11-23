[![](https://jitpack.io/v/de.itsTyrion/PluginAnnotationProcessor.svg)](https://jitpack.io/#de.itsTyrion/PluginAnnotationProcessor)
# Plugin Annotation Processor


A Bukkit/Spigot or BungeeCord plugin that simplifies the generation of `plugin.yml` file using annotations.

## Overview

This tool provides a convenient way to generate the `plugin.yml` file for your Spigot/Paper/BungeeCord/Waterfall plugin.  
It utilizes annotations to define essential information directly in your code.

## Prerequisites
- Java 8 or higher
- Bukkit/Spigot/Paper latest recommended but should work down to 1.7
- BungeeCord/Waterfall latest - it supports down to MC 1.8 as of 2023

## Features
- **Annotation-based Configuration**: Use the `@Plugin` and `@BungeePlugin` annotations to define plugin information directly in your Java/Kotlin code.
- **Cross-Compatibility**: Works with Bukkit, Spigot, Paper, BungeeCord, and Waterfall.
- **Versioning**: Version number is derived from the Gradle/Maven project version. (Optional)

---
## Usage

### Bukkit/Spigot/Paper
(This example does not show all properties)
```java
import org.bukkit.plugin.java.JavaPlugin;

import de.itsTyrion.pluginAnnotation.Plugin;

@Plugin(
        name = "MyPlugin",
        description = "Awesome Bukkit plugin",
        authors = {"Your Name"},
        depend = {"aDependency"}
)
public class MyPlugin extends JavaPlugin {
    // Your plugin code here
}
```

### BungeeCord/Waterfall
```java
import net.md_5.bungee.api.plugin.Plugin;

import de.itsTyrion.pluginAnnotation.BungeePlugin;

@Plugin(
        name = "MyPlugin",
        description = "Awesome Bungee/Waterfall plugin",
        author = "Your Name",
        depends = {"aDependency"}
)
public class MyBungeePlugin extends Plugin {
    // Your plugin code here
}
```

## Gradle
#### Add the Jitpack repo if you haven't already:
```groovy
repositories {
    // Add your other repositories here
    maven { url 'https://jitpack.io' }
}
```
#### For Java sources:
```groovy
dependencies {
    // Add your other dependencies here
    compileOnly 'de.itsTyrion:PluginAnnotationProcessor:1.0'
    annotationProcessor 'de.itsTyrion:PluginAnnotationProcessor:1.0'
}

tasks.withType(JavaCompile).configureEach {
    // Add your other options here
    options.compilerArgs += ('-Aproject.version=' + project.version)
}
```
#### For Kotlin sources:
```groovy
plugins {
    // Add your other gradle plugins here
    id 'org.jetbrains.kotlin.kapt' version 'current.kotlin.version'
}

dependencies {
    // Add your other dependencies here
    compileOnly 'de.itsTyrion:PluginAnnotationProcessor:1.0'
    kapt 'de.itsTyrion:PluginAnnotationProcessor:1.0'
}

kapt {
    arguments {
        // Add other annotation processor arguments if needed
        arg('project.version', project.version)
    }
}
```

## Maven
#### Add the Jitpack repo if you haven't already:
```xml
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
```
#### For Java sources:
```xml
<dependency>
    <groupId>de.itsTyrion</groupId>
    <artifactId>PluginAnnotationProcessor</artifactId>
    <version>1.0</version>
</dependency>
```
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <!-- Pass the project version as an argument to the compiler -->
                <compilerArgs>
                    <arg>-Aproject.version=${project.version}</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```
#### For Kotlin sources (I recommend you use Gradle):  
(Copied from [the docs](https://kotlinlang.org/docs/kapt.html#use-in-maven))  
Add an execution of the kapt goal from kotlin-maven-plugin before compile:
```xml
<execution>
    <id>kapt</id>
    <goals>
        <goal>kapt</goal> <!-- You can skip the <goals> element
if you enable extensions for the plugin -->
    </goals>
    <configuration>
        <sourceDirs>
            <sourceDir>src/main/kotlin</sourceDir>
            <sourceDir>src/main/java</sourceDir>
        </sourceDirs>
        <annotationProcessorPaths>
            <annotationProcessorPath>
                <groupId>de.itsTyrion</groupId>
                <artifactId>PluginAnnotationProcessor</artifactId>
                <version>1.0</version>
            </annotationProcessorPath>
        </annotationProcessorPaths>
    </configuration>
</execution>
```

## Contributing
Feel free to contribute to the development of this plugin by opening issues or submitting pull requests.

## API Reference
Just add it to the plugin's main class
Parameter names match plugin.yml/bungee.yml

## License
This project is licensed under the MIT License.