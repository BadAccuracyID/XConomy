/*
 *  This file (CacheSemiOnline.java) is a part of project XConomy
 *  Copyright (C) YiC and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.yic.xconomy.data.caches;

import me.yic.xconomy.XConomy;
import me.yic.xconomy.utils.ServerINFO;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CacheSemiOnline {
    public static File cacheSubUUIDFile;
    public static FileConfiguration cacheSubUUIDConfig;

    public static boolean createFile() {
        if (ServerINFO.IsSemiOnlineMode) {
            File dataFolder = new File(XConomy.getInstance().getDataFolder(), "cache");
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                XConomy.getInstance().logger("文件夹创建异常", null);
                return false;
            }
            cacheSubUUIDFile = new File(dataFolder, "cache_subuuid.yml");
            cacheSubUUIDConfig = YamlConfiguration.loadConfiguration(cacheSubUUIDFile);
            if (!cacheSubUUIDFile.exists()) {
                try {
                    cacheSubUUIDConfig.save(cacheSubUUIDFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    XConomy.getInstance().logger("缓存文件创建异常", null);
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static void CacheSubUUID_checkUser(String mainu, Player pp) {
        if (cacheSubUUIDConfig.contains(mainu)) {
            if (!cacheSubUUIDConfig.get(mainu + ".SubUUID").equals(pp.getUniqueId().toString())) {
                if (pp.isOnline()) {
                    Bukkit.getScheduler().runTask(XConomy.getInstance(), () ->
                            pp.kickPlayer("[XConomy] The player with the same name exists on the server (Three times)"));
                }
            }
        } else {
            cacheSubUUIDConfig.createSection(mainu + ".SubUUID");
            cacheSubUUIDConfig.set(mainu + ".SubUUID", pp.getUniqueId().toString());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static UUID CacheSubUUID_getSubUUID(String name) {
        if (cacheSubUUIDConfig.contains(name)) {
            return UUID.fromString(cacheSubUUIDConfig.getString(name + ".SubUUID"));
        } else {
            return null;
        }
    }

    public static void save() {
        try {
            if (XConomy.config.getBoolean("Settings.semi-online-mode")) {
                cacheSubUUIDConfig.save(cacheSubUUIDFile);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}