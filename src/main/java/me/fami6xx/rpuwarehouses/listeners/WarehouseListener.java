package me.fami6xx.rpuwarehouses.listeners;

import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.Warehouse;
import me.fami6xx.rpuwarehouses.data.WarehouseManager;
import me.fami6xx.rpuwarehouses.other.RPULanguageAddon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WarehouseListener implements Listener {
    private static final Pattern WAREHOUSE_PATTERN = Pattern.compile("(?i)\\[warehouse]");

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();

        // Check if the first line contains [Warehouse]
        if (WAREHOUSE_PATTERN.matcher(event.getLine(0)).matches()) {
            // Check if the player has permission
            if (!player.hasPermission("rpuwarehouses.create")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to create warehouses!");
                event.setCancelled(true);
                return;
            }

            // Get the job name from the second line
            String jobName = event.getLine(1);
            if (jobName == null || jobName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must specify a job name on the second line!");
                event.setCancelled(true);
                return;
            }

            // Check if a warehouse already exists at this location
            WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
            if (manager.getWarehouseByLocation(event.getBlock().getLocation()) != null) {
                player.sendMessage(FamiUtils.format(RPULanguageAddon.WarehouseAlreadyExists));
                event.setCancelled(true);
                return;
            }

            // Create the warehouse
            Warehouse warehouse = manager.createWarehouse(jobName, event.getBlock().getLocation());

            // Format the sign
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("jobName", jobName);
            placeholders.put("itemCount", "0");

            String[] lines = FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseSignFormat, placeholders).split("\n");
            for (int i = 0; i < Math.min(lines.length, 4); i++) {
                event.setLine(i, lines[i]);
            }

            // Send confirmation message
            placeholders.clear();
            placeholders.put("jobName", jobName);
            player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseCreated, placeholders));
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        // Check if the player right-clicked a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        Block block = event.getClickedBlock();

        // Check if the clicked block is a sign
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        // Get the warehouse at this location
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        Warehouse warehouse = manager.getWarehouseByLocation(block.getLocation());

        // If this is a warehouse sign, open the warehouse menu
        if (warehouse != null) {
            event.setCancelled(true);

            Player player = event.getPlayer();

            // Check if the player has permission
            if (!player.hasPermission("rpuwarehouses.use")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use warehouses!");
                return;
            }

            // Show the player what items are in the warehouse
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("jobName", warehouse.getJobName());
            player.sendMessage(FamiUtils.replaceAndFormat("&a&lWarehouse for job &f{jobName}", placeholders));

            // Get all warehouses for this job
            List<Warehouse> warehouses = manager.getWarehousesByJob(warehouse.getJobName());

            // Show items in the warehouses
            boolean hasItems = false;
            for (Warehouse w : warehouses) {
                Map<String, ItemStack> warehouseItems = w.getItems();
                for (String key : warehouseItems.keySet()) {
                    ItemStack item = warehouseItems.get(key);
                    placeholders.clear();
                    placeholders.put("amount", String.valueOf(item.getAmount()));
                    placeholders.put("item", item.getType().name().toLowerCase().replace("_", " "));
                    player.sendMessage(FamiUtils.replaceAndFormat("&7- &f{amount}x {item}", placeholders));
                    hasItems = true;
                }
            }

            if (!hasItems) {
                player.sendMessage(ChatColor.GRAY + "This warehouse is empty.");
            } else {
                player.sendMessage(ChatColor.GRAY + "Shift + right-click with an item to add it to the warehouse.");
                player.sendMessage(ChatColor.GRAY + "Shift + left-click to take the first item from the warehouse.");
            }
        }
    }

    @EventHandler
    public void onWarehouseTakeItem(PlayerInteractEvent event) {
        // Check if the player left-clicked a block
        if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();

        // Check if the player is sneaking (shift-clicking)
        if (!player.isSneaking()) {
            return;
        }

        Block block = event.getClickedBlock();

        // Check if the clicked block is a sign
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        // Get the warehouse at this location
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        Warehouse warehouse = manager.getWarehouseByLocation(block.getLocation());

        // If this is a warehouse sign, take an item
        if (warehouse != null) {
            event.setCancelled(true);

            // Check if the player has permission
            if (!player.hasPermission("rpuwarehouses.take")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to take items from warehouses!");
                return;
            }

            // Check if the player has enough inventory space
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(FamiUtils.format(RPULanguageAddon.WarehouseInventoryFull));
                return;
            }

            // Get all warehouses for this job
            List<Warehouse> warehouses = manager.getWarehousesByJob(warehouse.getJobName());

            // Try to take the first item from any warehouse
            for (Warehouse w : warehouses) {
                Map<String, ItemStack> warehouseItems = w.getItems();
                if (!warehouseItems.isEmpty()) {
                    // Get the first item
                    String firstKey = warehouseItems.keySet().iterator().next();
                    ItemStack item = warehouseItems.get(firstKey);

                    // Take one of the item
                    int amount = Math.min(item.getAmount(), 1);
                    if (manager.removeItemFromWarehouse(w, item, amount)) {
                        // Give the item to the player
                        ItemStack giveItem = item.clone();
                        giveItem.setAmount(amount);
                        player.getInventory().addItem(giveItem);

                        // Send confirmation message
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("amount", String.valueOf(amount));
                        placeholders.put("item", item.getType().name().toLowerCase().replace("_", " "));
                        player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseItemTaken, placeholders));

                        // Update the sign
                        updateWarehouseSign(block, w);
                        return;
                    }
                }
            }

            // If we get here, no warehouse had any items
            player.sendMessage(ChatColor.GRAY + "This warehouse is empty.");
        }
    }

    @EventHandler
    public void onWarehouseAddItem(PlayerInteractEvent event) {
        // Check if the player right-clicked a block with an item
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getItem() == null) {
            return;
        }

        Player player = event.getPlayer();

        // Check if the player is sneaking (shift-clicking)
        if (!player.isSneaking()) {
            return;
        }

        // Check if the player is holding a book (for page limit setting)
        if (event.getItem().getType() == Material.BOOK) {
            handlePageLimitSetting(event);
            return;
        }

        Block block = event.getClickedBlock();

        // Check if the clicked block is a sign
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        // Get the warehouse at this location
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        Warehouse warehouse = manager.getWarehouseByLocation(block.getLocation());

        // If this is a warehouse sign, add the item
        if (warehouse != null) {
            event.setCancelled(true);

            // Check if the player has permission
            if (!player.hasPermission("rpuwarehouses.add")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to add items to warehouses!");
                return;
            }

            // Get the item and amount
            ItemStack item = event.getItem().clone();
            int amount = item.getAmount();
            item.setAmount(1); // Set to 1 for storage key

            // Add the item to the warehouse
            manager.addItemToWarehouse(warehouse, item, amount);

            // Remove the item from the player's hand
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                player.getInventory().getItemInMainHand().setAmount(0);
            }

            // Send confirmation message
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("amount", String.valueOf(amount));
            placeholders.put("item", item.getType().name().toLowerCase().replace("_", " "));
            player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseItemAdded, placeholders));

            // Update the sign
            updateWarehouseSign(block, warehouse);
        }
    }

    /**
     * Handles setting the page limit for a job's warehouse.
     *
     * @param event The player interact event
     */
    private void handlePageLimitSetting(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Check if the clicked block is a sign
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        // Get the warehouse at this location
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        Warehouse warehouse = manager.getWarehouseByLocation(block.getLocation());

        // If this is a warehouse sign, open the page limit menu
        if (warehouse != null) {
            event.setCancelled(true);

            // Check if the player has permission
            if (!player.hasPermission("rpuwarehouses.admin")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to set page limits!");
                return;
            }

            // Tell the player how to set the page limit
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("jobName", warehouse.getJobName());
            player.sendMessage(FamiUtils.replaceAndFormat("&aTo set the page limit for job &f{jobName}&a, use the command:", placeholders));
            player.sendMessage(ChatColor.YELLOW + "/warehouse pagelimit " + warehouse.getJobName() + " <number>");
        }
    }

    /**
     * Updates a warehouse sign with the current item count.
     *
     * @param block The sign block
     * @param warehouse The warehouse
     */
    private void updateWarehouseSign(Block block, Warehouse warehouse) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();

            // Format the sign
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("jobName", warehouse.getJobName());
            placeholders.put("itemCount", String.valueOf(warehouse.getItems().size()));

            String[] lines = FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseSignFormat, placeholders).split("\n");
            for (int i = 0; i < Math.min(lines.length, 4); i++) {
                sign.setLine(i, lines[i]);
            }

            sign.update();
        }
    }
}
