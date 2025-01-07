package me.fami6xx.rpuwarehouses;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPU_Warehouses extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("RPUniverse")) {
            getLogger().warning("RPUniverse is not enabled or installed! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
