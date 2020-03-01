/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 17:23:16
 *
 * NukkitLauncher/NukkitLauncher/ClassLoaderFinder.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class ClassLoaderFinder implements ResourceFinder {
    private final ClassLoader loader;

    public ClassLoaderFinder(@NotNull ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public URL find(String path) throws IOException {
        return loader.getResource(path);
    }
}
