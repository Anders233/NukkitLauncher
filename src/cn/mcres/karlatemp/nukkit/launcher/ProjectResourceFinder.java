/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 14:55:42
 *
 * NukkitLauncher/NukkitLauncher/ProjectResourceFinder.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.function.Function;

public class ProjectResourceFinder implements ResourceFinder {
    private final Collection<File> resources;
    private final Collection<File> sources;
    private final Collection<File> classes;

    public ProjectResourceFinder(
            @NotNull Collection<File> sourceDirs,
            @NotNull Collection<File> classOutputDirs,
            @NotNull Collection<File> resourceDirs) {
        this.sources = sourceDirs;
        this.classes = classOutputDirs;
        this.resources = resourceDirs;
    }

    @Override
    public URL find(String s) throws IOException {
        for (File c : classes) {
            File file = new File(c, s);
            if (file.isFile())
                return file.toURI().toURL();
        }
        for (File res : resources) {
            File f = new File(res, s);
            if (f.isFile())
                return f.toURI().toURL();
        }
        for (File source : sources) {
            File f = new File(source, s);
            if (f.isFile())
                return f.toURI().toURL();
        }
        return null;
    }
}
