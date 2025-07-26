package me.fami6xx.rpuwarehouses;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuwarehouses.commands.WarehouseCommand;
import me.fami6xx.rpuwarehouses.data.JobPageLimits;
import me.fami6xx.rpuwarehouses.data.Warehouse;
import me.fami6xx.rpuwarehouses.data.WarehouseManager;
import me.fami6xx.rpuwarehouses.listeners.WarehouseListener;
import me.fami6xx.rpuwarehouses.other.RPULanguageAddon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPU_Warehouses extends JavaPlugin {
    private static RPU_Warehouses instance;
    private WarehouseManager warehouseManager;
    private JobPageLimits jobPageLimits;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("RPUniverse")) {
            getLogger().warning("RPUniverse is not enabled or installed! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize language addon
        RPULanguageAddon.getInstance();

        // Initialize warehouse manager
        warehouseManager = WarehouseManager.getInstance(this);

        // Initialize job page limits
        jobPageLimits = JobPageLimits.getInstance(this);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new WarehouseListener(), this);

        // Register commands
        getCommand("warehouse").setExecutor(new WarehouseCommand(this));

        getLogger().info("RPU-Warehouses has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save warehouses on disable
        if (warehouseManager != null) {
            warehouseManager.saveWarehouses();
        }

        // Save job page limits on disable
        if (jobPageLimits != null) {
            jobPageLimits.savePageLimits();
        }

        getLogger().info("RPU-Warehouses has been disabled!");
    }

    /**
     * Checks if the player has permission to access the warehouse.
     *
     * @param warehouse The warehouse to check
     * @param player The player to check
     * @return true if the player has permission, false otherwise
     */
    public static boolean hasWarehousePermission(Warehouse warehouse, Player player) {
        if (warehouse == null || player == null) {
            return false;
        }

        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
        if (data == null) {
            return false;
        }

        String jobName = warehouse.getJobName();
        if (jobName == null || jobName.isEmpty()) {
            return false;
        }
        if (data.getSelectedPlayerJob() == null || !data.getSelectedPlayerJob().getName().equalsIgnoreCase(jobName)) {
            return false;
        }

        return true;
    }

    /**
     * Gets the singleton instance of the plugin.
     *
     * @return The plugin instance
     */
    public static RPU_Warehouses getInstance() {
        return instance;
    }

    /**
     * Gets the warehouse manager.
     *
     * @return The warehouse manager
     */
    public WarehouseManager getWarehouseManager() {
        return warehouseManager;
    }

    /**
     * Gets the job page limits manager.
     *
     * @return The job page limits manager
     */
    public JobPageLimits getJobPageLimits() {
        return jobPageLimits;
    }
}
