package me.fami6xx.rpuwarehouses.data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a warehouse that stores items for a specific job.
 */
public class Warehouse {
    private final UUID id;
    private final String jobName;
    private final Location signLocation;
    private final Map<String, ItemStack> items;

    /**
     * Creates a new warehouse.
     *
     * @param jobName The name of the job this warehouse is for
     * @param signLocation The location of the sign for this warehouse
     */
    public Warehouse(String jobName, Location signLocation) {
        this.id = UUID.randomUUID();
        this.jobName = jobName;
        this.signLocation = signLocation;
        this.items = new HashMap<>();
    }

    /**
     * Creates a warehouse from serialized data.
     *
     * @param map The serialized data
     */
    @SuppressWarnings("unchecked")
    public Warehouse(Map<String, Object> map) {
        this.id = UUID.fromString((String) map.get("id"));
        this.jobName = (String) map.get("jobName");
        this.signLocation = (Location) map.get("signLocation");
        this.items = (Map<String, ItemStack>) map.get("items");
    }

    /**
     * Gets the unique ID of this warehouse.
     *
     * @return The warehouse ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the job name for this warehouse.
     *
     * @return The job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Gets the location of the sign for this warehouse.
     *
     * @return The sign location
     */
    public Location getSignLocation() {
        return signLocation;
    }

    /**
     * Gets all items in this warehouse.
     *
     * @return A map of item keys to ItemStacks
     */
    public Map<String, ItemStack> getItems() {
        return items;
    }

    /**
     * Adds an item to the warehouse.
     *
     * @param item The item to add
     * @param amount The amount to add
     */
    public void addItem(ItemStack item, int amount) {
        String key = getItemKey(item);
        ItemStack existingItem = items.get(key);

        if (existingItem == null) {
            ItemStack newItem = item.clone();
            newItem.setAmount(amount);
            items.put(key, newItem);
        } else {
            existingItem.setAmount(existingItem.getAmount() + amount);
        }
    }

    /**
     * Removes an item from the warehouse.
     *
     * @param item The item to remove
     * @param amount The amount to remove
     * @return True if the item was removed, false if there wasn't enough
     */
    public boolean removeItem(ItemStack item, int amount) {
        String key = getItemKey(item);
        ItemStack existingItem = items.get(key);

        if (existingItem == null || existingItem.getAmount() < amount) {
            return false;
        }

        if (existingItem.getAmount() == amount) {
            items.remove(key);
        } else {
            existingItem.setAmount(existingItem.getAmount() - amount);
        }

        return true;
    }

    /**
     * Checks if the warehouse has enough of an item.
     *
     * @param item The item to check
     * @param amount The amount to check for
     * @return True if the warehouse has enough of the item
     */
    public boolean hasItem(ItemStack item, int amount) {
        String key = getItemKey(item);
        ItemStack existingItem = items.get(key);
        return existingItem != null && existingItem.getAmount() >= amount;
    }

    /**
     * Gets a unique key for an item based on its type and metadata.
     *
     * @param item The item to get a key for
     * @return A unique key for the item
     */
    private String getItemKey(ItemStack item) {
        StringBuilder key = new StringBuilder(item.getType().name());

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                key.append("_name:").append(item.getItemMeta().getDisplayName());
            }

            if (item.getItemMeta().hasLore()) {
                key.append("_lore:");
                for (String loreLine : item.getItemMeta().getLore()) {
                    key.append(loreLine).append(";");
                }
            }

            if (item.getItemMeta().hasEnchants()) {
                key.append("_enchants:");
                item.getItemMeta().getEnchants().forEach((enchant, level) -> 
                    key.append(enchant.getName()).append(":").append(level).append(";")
                );
            }
        }

        return key.toString();
    }

    /**
     * Serializes this warehouse to a map for JSON serialization.
     *
     * @return A map representation of this warehouse
     */
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("jobName", jobName);
        map.put("signLocation", signLocation);
        map.put("items", items);
        return map;
    }
}
