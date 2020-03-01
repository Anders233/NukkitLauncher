/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 16:05:58
 *
 * NukkitLauncher/NukkitLauncher/ExampleStartup.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import cn.nukkit.Nukkit;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class ExampleStartup {
    public static void main(String[] args) throws IOException, NoSuchFieldException {
        String project = "<your project root here>";
        String working = project + "/server";
        System.setProperty("user.dir", working);

        ResourceFinder finder = new ProjectResourceFinder(
                Collections.singleton(new File(project, "src")),
                Collections.singleton(new File(project, "out/production/PlaceholderAPI-Nukkit")),
                Collections.emptyList()
        );
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        NukkitBootstrap.bootstrap(finder, loader, loader,
                new File(working, "plugins/PlaceholderAPI-Nukkit.jar"));
        Nukkit.main(args);
    }
}
