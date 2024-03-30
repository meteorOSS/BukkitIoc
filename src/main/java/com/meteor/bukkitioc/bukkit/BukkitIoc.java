package com.meteor.bukkitioc.bukkit;


import org.bukkit.plugin.java.JavaPlugin;



public final class BukkitIoc extends JavaPlugin{



    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
//        debug = getConfig().getBoolean("debug");
        BukkitIocManager.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
