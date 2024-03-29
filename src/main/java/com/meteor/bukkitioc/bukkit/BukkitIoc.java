package com.meteor.bukkitioc.bukkit;


import org.bukkit.plugin.java.JavaPlugin;



public final class BukkitIoc extends JavaPlugin{


    public static boolean debug;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        debug = getConfig().getBoolean("debug");
        getServer().getPluginManager().registerEvents(new PluginListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
