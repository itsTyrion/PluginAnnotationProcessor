package de.itsTyrion.pluginAnnotation.util;

import com.grack.nanojson.JsonWriter;
import de.itsTyrion.pluginAnnotation.BungeePlugin;
import de.itsTyrion.pluginAnnotation.CommandInfo;
import de.itsTyrion.pluginAnnotation.Plugin;
import de.itsTyrion.pluginAnnotation.velocity.VelocityPlugin;
import lombok.val;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Generator {
    private Generator() {}


    public static String pluginYML(Plugin plugin, String fqName, String version, String[] libraries,
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


    public static String bungeeYML(BungeePlugin plugin, String fqName, String version) {
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


    public static final Pattern VELOCITY_ID_PATTERN = Pattern.compile("[a-z][a-z0-9-_]{0,63}");

    public static String velocityPluginJSON(VelocityPlugin vp, String qualifiedName, String version) {
        for (val dependency : vp.dependencies())
            if (!VELOCITY_ID_PATTERN.matcher(dependency.id()).matches())
                throw new IllegalArgumentException("id of dependency " + dependency.id() + " is not valid");
        val json = JsonWriter.string()
            .object()
            .value("id", vp.id());

        if (!vp.name().isEmpty()) json.value("name", vp.name());
        json.value("version", version);
        if (!vp.description().isEmpty()) json.value("description", vp.description());
        if (!vp.url().isEmpty()) json.value("url", vp.url());
        val authors = Arrays.stream(vp.authors()).filter(author -> !author.isEmpty()).collect(Collectors.toList());
        if (!authors.isEmpty()) json.array("authors", authors);
        if (vp.dependencies().length > 0) {
            json.array("dependencies");
            for (val dependency : vp.dependencies())
                json.object().value("id", dependency.id()).value("optional", dependency.optional()).end();
            json.end();
        }
        json.value("main", qualifiedName);
        return json.end().done();
    }

    private static void appendIfPresent(StringBuilder builder, String key, String[] value) {
        if (value.length > 0) // The format of Arrays.toString is a valid YAML list - how convenient.
            builder.append(key).append(": ").append(Arrays.toString(value)).append('\n');
    }

    private static void appendIfPresent(StringBuilder builder, String key, String value) {
        if (notBlank(value))
            builder.append(key).append(": ").append(value).append('\n');
    }

    private static boolean notBlank(String str) {
        return !str.isEmpty() && !str.chars().allMatch(Character::isWhitespace);
    }
}
