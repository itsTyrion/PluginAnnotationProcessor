package de.itsTyrion.pluginAnnotation;

import de.itsTyrion.pluginAnnotation.bukkit.BukkitPlugin;
import de.itsTyrion.pluginAnnotation.bukkit.CommandInfo;
import de.itsTyrion.pluginAnnotation.bungee.BungeePlugin;
import de.itsTyrion.pluginAnnotation.util.Generator;
import de.itsTyrion.pluginAnnotation.velocity.VelocityPlugin;
import lombok.val;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedOptions(value = {"mcPluginVersion", "spigotLibraries"})
@SupportedAnnotationTypes({
    "de.itsTyrion.pluginAnnotation.bukkit.BukkitPlugin",
    "de.itsTyrion.pluginAnnotation.bungee.BungeePlugin",
    "de.itsTyrion.pluginAnnotation.velocity.VelocityPlugin"
})
public class PluginAnnotationProcessor extends AbstractProcessor {

    private String pluginMainClassFound = null;
    private String bungeePluginMainClassFound = null;
    private String velocityPluginMainClassFound = null;

    @Override
    public SourceVersion getSupportedSourceVersion() {return SourceVersion.latestSupported();}

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Retrieve the project version from the processor options, required for plugin.yml `version` property
        val projectVersion = processingEnv.getOptions().get("mcPluginVersion");
        if (projectVersion == null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "`mcPluginVersion` unset, check the docs.");
            return false;
        }

        for (val element : roundEnv.getElementsAnnotatedWith(BukkitPlugin.class)) {
            // fully qualified name, required for plugin.yml `main` property
            val fqName = ((TypeElement) element).getQualifiedName().toString();

            if (pluginMainClassFound != null && !pluginMainClassFound.equals(fqName)) {
                processingEnv.getMessager()
                    .printMessage(Kind.ERROR, "Multiple plugin main classes are unsupported! Using `" + fqName + "`.");
            }
            pluginMainClassFound = fqName;

            // `libraries` section of plugin.yml, used by Spigot-based servers to DL dependencies as needed
            val librariesString = processingEnv.getOptions().get("spigotLibraries");
            val libraries = librariesString != null ? librariesString.split(";") : new String[0];


            val commandInfos = roundEnv.getElementsAnnotatedWith(CommandInfo.class).stream()
                .map(element1 -> element1.getAnnotation(CommandInfo.class)).toArray(CommandInfo[]::new);

            val pluginAnnotation = element.getAnnotation(BukkitPlugin.class);
            val content = Generator.pluginYML(pluginAnnotation, fqName, projectVersion, libraries, commandInfos);
            writeResource("plugin.yml", content, fqName);
        }

        for (val element : roundEnv.getElementsAnnotatedWith(BungeePlugin.class)) {
            // fully qualified name, required for bungee.yml `main` property
            val fqName = ((TypeElement) element).getQualifiedName().toString();

            if (bungeePluginMainClassFound != null && !bungeePluginMainClassFound.equals(fqName)) {
                processingEnv.getMessager()
                    .printMessage(Kind.ERROR, "Multiple plugin main classes are unsupported! Using `" + fqName + "`.");
                return false;
            }
            bungeePluginMainClassFound = fqName;

            val pluginAnnotation = element.getAnnotation(BungeePlugin.class);
            val content = Generator.bungeeYML(pluginAnnotation, fqName, projectVersion);
            writeResource("bungee.yml", content, fqName);
        }

        for (val element : roundEnv.getElementsAnnotatedWith(VelocityPlugin.class)) {
            // fully qualified name, required for velocity-plugin.json `main` property
            val fqName = ((TypeElement) element).getQualifiedName().toString();

            if (velocityPluginMainClassFound != null && !velocityPluginMainClassFound.equals(fqName)) {
                processingEnv.getMessager()
                    .printMessage(Kind.ERROR, "Multiple plugin main classes are unsupported! Using `" + fqName + "`.");
                return false;
            }
            velocityPluginMainClassFound = fqName;

            val plugin = element.getAnnotation(VelocityPlugin.class);
            if (!Generator.VELOCITY_ID_PATTERN.matcher(plugin.id()).matches()) {
                processingEnv.getMessager().printMessage(Kind.ERROR,
                    "Invalid ID for plugin " + fqName + ". IDs must start alphabetically," +
                    "have lowercase alphanumeric characters, and can contain dashes or underscores.");
                return false;
            }
            val content = Generator.velocityPluginJSON(plugin, fqName, projectVersion);
            writeResource("velocity-plugin.json", content, fqName);
        }

        return true;
    }

    private void writeResource(String name, String content, String fqName) {
        processingEnv.getMessager()
            .printMessage(Kind.NOTE, "Processed plugin annotation on `" + fqName + '`');

        val filer = processingEnv.getFiler();
        // Write to the resources directory
        try (val writer = new PrintWriter(filer.createResource(StandardLocation.CLASS_OUTPUT, "", name).openWriter())) {
            writer.print(content);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Error while writing " + name + ':' + e);
        }
    }
}
