/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 14:58:12
 *
 * NukkitLauncher/NukkitLauncher/ResourceFinder.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import java.io.IOException;
import java.net.URL;

public interface ResourceFinder {
    URL find(String path) throws IOException;
}
