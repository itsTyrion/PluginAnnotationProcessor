package de.itsTyrion.pluginAnnotation;

import lombok.val;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

@SupportedOptions(value = "project.version")
@SupportedAnnotationTypes({"de.itsTyrion.pluginAnnotation.Plugin", "de.itsTyrion.pluginAnnotation.BungeePlugin"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class PluginAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Retrieve the project version from the processor options, required for plugin.yml `version` property
        val projectVersion = processingEnv.getOptions().get("project.version");
        if (projectVersion == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "project.version unset!");
            return false;
        }

        for (val element : roundEnv.getElementsAnnotatedWith(Plugin.class)) {
            if (element instanceof TypeElement) {
                val pluginAnnotation = element.getAnnotation(Plugin.class);

                // fully qualified name, required for plugin.yml `main` property
                val fullyQualifiedName = ((TypeElement) element).getQualifiedName().toString();

                val pluginYmlContent = generatePluginYmlContent(pluginAnnotation, fullyQualifiedName, projectVersion);
                writeYml("plugin.yml", pluginYmlContent, fullyQualifiedName);
            }
        }
        for (val element : roundEnv.getElementsAnnotatedWith(BungeePlugin.class)) {
            if (element instanceof TypeElement) {
                val pluginAnnotation = element.getAnnotation(BungeePlugin.class);

                // fully qualified name, required for bungee.yml `main` property
                val fullyQualifiedName = ((TypeElement) element).getQualifiedName().toString();

                val pluginYmlContent = generateBungeeYmlContent(pluginAnnotation, fullyQualifiedName, projectVersion);
                writeYml("bungee.yml", pluginYmlContent, fullyQualifiedName);
            }
        }

        return true;
    }

    private void writeYml(String name, String content, String fqName) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.NOTE, "Processed plugin annotation on `" + fqName + '`');

        val filer = processingEnv.getFiler();
        // Write to the resources directory
        try (val writer = new PrintWriter(filer.createResource(StandardLocation.CLASS_OUTPUT, "", name).openWriter())) {
            writer.print(content);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error while writing " + name + ':' + e);
        }
    }

    private String generatePluginYmlContent(Plugin plugin, String fqName, String version) {
        val builder = new StringBuilder()
            .append("name: ").append(plugin.name()).append('\n')
            .append("version: ").append(plugin.version().replace("{project.version}", version)).append('\n')
            .append("main: ").append(fqName).append('\n')
            .append("api-version: ").append(plugin.apiVersion()).append('\n')
            .append("load: ").append(plugin.load().name()).append('\n');

        appendIfPresent(builder, "depend", plugin.depend());
        appendIfPresent(builder, "authors", plugin.authors());
        appendIfPresent(builder, "contributors", plugin.contributors());
        appendIfPresent(builder, "loadbefore", plugin.loadBefore());
        appendIfPresent(builder, "provides", plugin.provides());
        appendIfPresent(builder, "softdepend", plugin.softDepend());

        appendIfPresent(builder, "website", plugin.website());
        appendIfPresent(builder, "description", plugin.description());
        appendIfPresent(builder, "prefix", plugin.logPrefix());

        return builder.toString();
    }

    private String generateBungeeYmlContent(BungeePlugin plugin, String fqName, String version) {
        val builder = new StringBuilder()
            .append("name: ").append(plugin.name()).append('\n')
            .append("version: ").append(plugin.version().replace("{project.version}", version)).append('\n')
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
        if (!isBlank(value))
            builder.append(key).append(": ").append(value).append('\n');
    }

    private boolean isBlank(String str) {
        return str == null || str.isEmpty() || str.chars().allMatch(Character::isWhitespace);
    }
}
