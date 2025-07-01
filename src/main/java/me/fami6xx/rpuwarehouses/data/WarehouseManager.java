package me.fami6xx.rpuwarehouses.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.gson.GsonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages warehouses, including loading and saving them to disk.
 */
public class WarehouseManager {
    private static WarehouseManager instance;
    private final RPU_Warehouses plugin;
    private final Map<UUID, Warehouse> warehouses;
    private final File warehouseFile;

    /**
     * Creates a new warehouse manager.
     *
     * @param plugin The plugin instance
     */
    private WarehouseManager(RPU_Warehouses plugin) {
        this.plugin = plugin;
        this.warehouses = new HashMap<>();
        this.warehouseFile = new File(plugin.getDataFolder(), "warehouses.json");

        // Load warehouses from disk
        loadWarehouses();
    }

    /**
     * Gets the singleton instance of the warehouse manager.
     *
     * @param plugin The plugin instance
     * @return The warehouse manager instance
     */
    public static WarehouseManager getInstance(RPU_Warehouses plugin) {
        if (instance == null) {
            instance = new WarehouseManager(plugin);
        }
        return instance;
    }

    /**
     * Gets the singleton instance of the warehouse manager.
     *
     * @return The warehouse manager instance
     */
    public static WarehouseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WarehouseManager has not been initialized yet");
        }
        return instance;
    }

    /**
     * Loads warehouses from disk.
     */
    @SuppressWarnings("unchecked")
    private void loadWarehouses() {
        // Create the file if it doesn't exist
        if (!warehouseFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                warehouseFile.createNewFile();
                // Save an empty list if the file is new
                saveWarehouses();
                return;
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create warehouses.json", e);
                return;
            }
        }

        // Load warehouses from JSON
        try (FileReader reader = new FileReader(warehouseFile)) {
            Gson gson = GsonManager.getGson();
            Type warehouseListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> warehouseList = gson.fromJson(reader, warehouseListType);

            // Handle empty file or null result
            if (warehouseList != null) {
                for (Map<String, Object> map : warehouseList) {
                    Warehouse warehouse = new Warehouse(map);
                    warehouses.put(warehouse.getId(), warehouse);
                }
            }

            plugin.getLogger().info("Loaded " + warehouses.size() + " warehouses");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load warehouses.json", e);
        }
    }

    /**
     * Saves warehouses to disk.
     */
    public void saveWarehouses() {
        // Create a list of serialized warehouses
        List<Map<String, Object>> warehouseList = new ArrayList<>();
        for (Warehouse warehouse : warehouses.values()) {
            warehouseList.add(warehouse.serialize());
        }

        // Save the list to JSON
        try (FileWriter writer = new FileWriter(warehouseFile)) {
            Gson gson = GsonManager.getGson();
            gson.toJson(warehouseList, writer);
            plugin.getLogger().info("Saved " + warehouses.size() + " warehouses");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save warehouses.json", e);
        }
    }

    /**
     * Creates a new warehouse.
     *
     * @param jobName The name of the job
     * @param signLocation The location of the sign
     * @return The created warehouse
     */
    public Warehouse createWarehouse(String jobName, Location signLocation) {
        Warehouse warehouse = new Warehouse(jobName, signLocation);
        warehouses.put(warehouse.getId(), warehouse);
        saveWarehouses();
        return warehouse;
    }

    /**
     * Gets a warehouse by its sign location.
     *
     * @param location The sign location
     * @return The warehouse, or null if none exists at that location
     */
    public Warehouse getWarehouseByLocation(Location location) {
        for (Warehouse warehouse : warehouses.values()) {
            Location signLoc = warehouse.getSignLocation();
            if (signLoc.getWorld().equals(location.getWorld()) &&
                signLoc.getBlockX() == location.getBlockX() &&
                signLoc.getBlockY() == location.getBlockY() &&
                signLoc.getBlockZ() == location.getBlockZ()) {
                return warehouse;
            }
        }
        return null;
    }

    /**
     * Gets all warehouses for a specific job.
     *
     * @param jobName The job name
     * @return A list of warehouses for the job
     */
    public List<Warehouse> getWarehousesByJob(String jobName) {
        List<Warehouse> result = new ArrayList<>();
        for (Warehouse warehouse : warehouses.values()) {
            if (warehouse.getJobName().equalsIgnoreCase(jobName)) {
                result.add(warehouse);
            }
        }
        return result;
    }

    /**
     * Gets all warehouses.
     *
     * @return A list of all warehouses
     */
    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouses.values());
    }

    /**
     * Removes a warehouse.
     *
     * @param warehouse The warehouse to remove
     */
    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse.getId());
        saveWarehouses();
    }

    /**
     * Adds an item to a warehouse.
     *
     * @param warehouse The warehouse
     * @param item The item to add
     * @param amount The amount to add
     */
    public void addItemToWarehouse(Warehouse warehouse, ItemStack item, int amount) {
        warehouse.addItem(item, amount);
        saveWarehouses();
    }

    /**
     * Removes an item from a warehouse.
     *
     * @param warehouse The warehouse
     * @param item The item to remove
     * @param amount The amount to remove
     * @return True if the item was removed, false if there wasn't enough
     */
    public boolean removeItemFromWarehouse(Warehouse warehouse, ItemStack item, int amount) {
        boolean result = warehouse.removeItem(item, amount);
        if (result) {
            saveWarehouses();
        }
        return result;
    }
}
