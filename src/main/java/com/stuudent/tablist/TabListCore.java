package com.stuudent.tablist;

import com.stuudent.tablist.schedulers.TabListScheduler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class TabListCore extends JavaPlugin {

    public static TabListCore instance;
    public static FileConfiguration cf;

    @Override
    public void onEnable() {
        instance = this;
        if(!dependCheck())
            return;
        saveDefaultConfig();
        cf = getConfig();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new TabListScheduler(), 0, 600);
        Bukkit.getConsoleSender().sendMessage("§6ST§f-§fTabList §ev" + getDescription().getVersion() + " §a플러그인이 활성화 되었습니다. §f(created by STuuDENT, Discord 민제#5894)");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(instance);
        Bukkit.getConsoleSender().sendMessage("§6ST§f-§fTabList §ev" + getDescription().getVersion() + " §c플러그인이 비활성화 되었습니다. §f(created by STuuDENT, Discord 민제#5894)");
    }

    public boolean dependCheck() {
        if(getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getServer().getPluginManager().disablePlugin(instance);
            return false;
        }
        return true;
    }

}
