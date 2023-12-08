package de.itsTyrion.pluginAnnotation;

import lombok.val;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;

@SuppressWarnings("Since15") // This is only about the SupportedSourceVersion annotation
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions(value = {"mcPluginVersion", "spigotLibraries"})
@SupportedAnnotationTypes({"de.itsTyrion.pluginAnnotation.Plugin", "de.itsTyrion.pluginAnnotation.BungeePlugin"})
public class PluginAnnotationProcessor extends AbstractProcessor {

    private String pluginMainClassFound = null;
    private String bungeePluginMainClassFound = null;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Retrieve the project version from the processor options, required for plugin.yml `version` property
        val projectVersion = processingEnv.getOptions().get("mcPluginVersion");
        if (projectVersion == null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "`mcPluginVersion` unset, check the docs.");
            return false;
        }

        for (val element : roundEnv.getElementsAnnotatedWith(Plugin.class)) {
            val pluginAnnotation = element.getAnnotation(Plugin.class);

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

            val content = generatePluginYmlContent(pluginAnnotation, fqName, projectVersion, libraries, commandInfos);
            writeYml("plugin.yml", content, fqName);
        }
        for (val element : roundEnv.getElementsAnnotatedWith(BungeePlugin.class)) {
            val pluginAnnotation = element.getAnnotation(BungeePlugin.class);

            // fully qualified name, required for bungee.yml `main` property
            val fqName = ((TypeElement) element).getQualifiedName().toString();

            if (bungeePluginMainClassFound != null && !bungeePluginMainClassFound.equals(fqName)) {
                processingEnv.getMessager()
                    .printMessage(Kind.ERROR, "Multiple plugin main classes are unsupported! Using `" + fqName + "`.");
                return false;
            }
            bungeePluginMainClassFound = fqName;

            val content = generateBungeeYmlContent(pluginAnnotation, fqName, projectVersion);
            writeYml("bungee.yml", content, fqName);
        }

        return true;
    }

    private void writeYml(String name, String content, String fqName) {
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

    private String generatePluginYmlContent(Plugin plugin, String fqName, String version, String[] libraries,
                                            CommandInfo[] commands) {
        val builder = new StringBuilder()
            .append("name: ").append(plugin.name()).append('\n')
            .append("version: ").append(plugin.version().replace("%mcPluginVersion%", version)).append('\n')
            .append("main: ").append(fqName).append('\n')
            .append("api-version: ").append(plugin.apiVersion()).append('\n')
            .append("load: ").append(plugin.load().name()).append('\n');

        appendIfPresent(builder, "depend", plugin.depend());
        appendIfPresent(builder, "authors", plugin.authors());
        appendIfPresent(builder, "contributors", plugin.contributors());
        appendIfPresent(builder, "loadbefore", plugin.loadBefore());
        appendIfPresent(builder, "provides", plugin.provides());
        appendIfPresent(builder, "softdepend", plugin.softDepend());
        appendIfPresent(builder, "libraries", libraries);

        appendIfPresent(builder, "website", plugin.website());
        if (notBlank(plugin.description()))
            appendIfPresent(builder, "description", '"' + plugin.description().replace("\n", "\\n") + '"');
        appendIfPresent(builder, "prefix", plugin.logPrefix());

        builder.append('\n');

        if (commands.length != 0) {
            val sb = new StringBuilder("commands:\n");
            for (CommandInfo ci : commands) {
                sb.append("  ").append(ci.name()).append(": ").append('\n');
                sb.append("    aliases: ").append(Arrays.toString(ci.aliases())).append('\n');

                BiConsumer<String, String> append = (k, v) -> {if (notBlank(v)) sb.append(k).append(v).append('\n');};

                append.accept("    description: ", ci.description());
                append.accept("    usage: ", ci.usage());
                append.accept("    permission: ", ci.permission());
                append.accept("    permission-message: ", ci.permissionMessage());
            }
            builder.append(sb);
        }

        return builder.toString();
    }


    private String generateBungeeYmlContent(BungeePlugin plugin, String fqName, String version) {
        val builder = new StringBuilder()
            .append("name: ").append(plugin.name()).append('\n')
            .append("version: ").append(plugin.version().replace("%mcPluginVersion%", version)).append('\n')
            .append("main: ").append(fqName).append('\n');

        appendIfPresent(builder, "depends", plugin.depends());
        appendIfPresent(builder, "softdepends", plugin.softDepends());

        appendIfPresent(builder, "author", plugin.author());
        appendIfPresent(builder, "description", plugin.description());

        return builder.toString();
    }

    private void appendIfPresent(StringBuilder builder, String key, String[] value) {
        if (value.length > 0) // The format of Arrays.toString is a valid YAML list - how convenient.
            builder.append(key).append(": ").append(Arrays.toString(value)).append('\n');
    }

    private void appendIfPresent(StringBuilder builder, String key, String value) {
        if (notBlank(value))
            builder.append(key).append(": ").append(value).append('\n');
    }

    private boolean notBlank(String str) {return !str.isEmpty() && !str.chars().allMatch(Character::isWhitespace);}
}
