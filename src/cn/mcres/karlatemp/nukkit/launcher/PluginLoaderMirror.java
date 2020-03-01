/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 15:50:15
 *
 * NukkitLauncher/NukkitLauncher/PluginLoaderMirror.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;

import java.io.File;
import java.util.regex.Pattern;

public class PluginLoaderMirror implements PluginLoader {
    public static PluginLoader mirror;

    public PluginLoaderMirror(Server server) {
    }

    @Override
    public Plugin loadPlugin(String s) throws Exception {
        return mirror.loadPlugin(s);
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        return mirror.loadPlugin(file);
    }

    @Override
    public PluginDescription getPluginDescription(String s) {
        return mirror.getPluginDescription(s);
    }

    @Override
    public PluginDescription getPluginDescription(File file) {
        return mirror.getPluginDescription(file);
    }

    @Override
    public Pattern[] getPluginFilters() {
        return mirror.getPluginFilters();
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        mirror.enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        mirror.disablePlugin(plugin);
    }
}
