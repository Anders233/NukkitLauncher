/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 14:53:20
 *
 * NukkitLauncher/NukkitLauncher/NukkitLauncher.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import cn.nukkit.Server;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.plugin.*;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NukkitLauncher implements PluginLoader {
    private final ResourceFinder finder;
    private final ClassLoader loader;
    public static final Pattern matcher =
            Pattern.compile("^jrt\\$(.+)$");
    public final PluginDescription pluginDescription;
    private final File pluginFile;

    public NukkitLauncher(
            ResourceFinder finder,
            ClassLoader loader,
            File pluginFile) {
        this.finder = finder;
        this.loader = loader;
        this.pluginDescription = initPluginDescription();
        this.pluginFile = pluginFile;
    }

    @Override
    public synchronized Plugin loadPlugin(String s) throws Exception {
        try {
            return loadPlugin0(s);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized Plugin loadPlugin0(String s) throws Exception {
        final Matcher matcher = NukkitLauncher.matcher.matcher(s);
        if (matcher.find()) {
            PluginDescription description = pluginDescription;
            Server server = Server.getInstance();
            server.getLogger().info(server.getLanguage().translateString("nukkit.plugin.load", description.getFullName()));
            File dataFolder = new File("plugins", description.getName());
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw new IllegalStateException("Projected dataFolder '" + dataFolder.toString() + "' for " + description.getName() + " exists and is not a directory");
            }

            String className = description.getMain();
            // PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(), file);
            // this.classLoaders.put(description.getName(), classLoader);

            try {
                Class<?> javaClass = loader.loadClass(className);
                if (!PluginBase.class.isAssignableFrom(javaClass)) {
                    throw new PluginException("Main class `" + description.getMain() + "' does not extend PluginBase");
                }

                try {
                    Class<? extends PluginBase> pluginClass = javaClass.asSubclass(PluginBase.class);
                    PluginBase plugin = pluginClass.newInstance();
                    plugin.init(this, server, description, dataFolder, pluginFile);
                    plugin.onLoad();
                    return plugin;
                } catch (ClassCastException var9) {
                    throw new PluginException("Error whilst initializing main class `" + description.getMain() + "'", var9);
                } catch (IllegalAccessException | InstantiationException var10) {
                    Server.getInstance().getLogger().logException(var10);
                }
            } catch (ClassNotFoundException var11) {
                throw new PluginException("Couldn't load plugin " + description.getName() + ": main class \"" + className + "\" not found");
            }
        }
        return null;
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        return loadPlugin(file.getPath());
    }

    public PluginDescription initPluginDescription() {
        URL url = null;
        try {
            url = finder.find("nukkit.yml");
        } catch (IOException ignore) {
        }
        if (url == null) {
            try {
                url = finder.find("plugin.yml");
            } catch (IOException ignore) {
            }
        }
        if (url != null) {
            final InputStream stream;
            try {
                stream = url.openStream();

                if (stream == null) return null;
                try (InputStream st = stream) {
                    return new PluginDescription(Utils.readFile(st));
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    @Override
    public PluginDescription getPluginDescription(String s) {
        return pluginDescription;
    }

    @Override
    public PluginDescription getPluginDescription(File file) {
        return getPluginDescription((String) null);
    }

    @Override
    public Pattern[] getPluginFilters() {
        return new Pattern[]{matcher};
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && !plugin.isEnabled()) {
            Server server = Server.getInstance();
            server.getLogger().info(server.getLanguage().translateString("nukkit.plugin.enable", plugin.getDescription().getFullName()));
            ((PluginBase) plugin).setEnabled(true);
            server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && plugin.isEnabled()) {
            Server server = Server.getInstance();
            server.getLogger().info(server.getLanguage().translateString("nukkit.plugin.disable", plugin.getDescription().getFullName()));
            server.getServiceManager().cancel(plugin);
            server.getPluginManager().callEvent(new PluginDisableEvent(plugin));
            ((PluginBase) plugin).setEnabled(false);
        }
    }
}
