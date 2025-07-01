package me.fami6xx.rpuwarehouses.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.JobPageLimits;
import me.fami6xx.rpuwarehouses.data.Warehouse;
import me.fami6xx.rpuwarehouses.data.WarehouseManager;
import me.fami6xx.rpuwarehouses.other.RPULanguageAddon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseJobMenu extends EasyPaginatedMenu {
    private final String jobName;
    private final List<ItemStack> warehouseItems = new ArrayList<>();

    public WarehouseJobMenu(PlayerMenu menu, String jobName) {
        super(menu);
        this.jobName = jobName;

        // Load items from all warehouses with this job name
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        List<Warehouse> warehouses = manager.getWarehousesByJob(jobName);

        for (Warehouse warehouse : warehouses) {
            for (Map.Entry<String, ItemStack> entry : warehouse.getItems().entrySet()) {
                ItemStack item = entry.getValue().clone();

                // Add lore to the item
                List<String> lore = new ArrayList<>();
                if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
                    lore.addAll(item.getItemMeta().getLore());
                }

                lore.add("");
                lore.add(FamiUtils.format(RPULanguageAddon.WarehouseItemLore));

                item = setLore(item, lore);

                warehouseItems.add(item);
            }
        }

        // Limit the number of items based on the job's page limit
        JobPageLimits jobPageLimits = RPU_Warehouses.getInstance().getJobPageLimits();
        int pageLimit = jobPageLimits.getPageLimit(jobName);
        int maxItems = pageLimit * 45; // 45 items per page

        if (warehouseItems.size() > maxItems) {
            warehouseItems.subList(maxItems, warehouseItems.size()).clear();
        }
    }

    @Override
    public ItemStack getItemFromIndex(int i) {
        if (i >= 0 && i < warehouseItems.size()) {
            return warehouseItems.get(i);
        }
        return null;
    }

    @Override
    public int getCollectionSize() {
        return warehouseItems.size();
    }

    public int getMaxPage() {
        // Get the page limit for this job
        JobPageLimits jobPageLimits = RPU_Warehouses.getInstance().getJobPageLimits();
        int pageLimit = jobPageLimits.getPageLimit(jobName);

        // Calculate the max page based on the collection size
        int calculatedMaxPage = (int) Math.ceil((double) getCollectionSize() / 45.0);

        // Return the smaller of the two values
        return Math.min(calculatedMaxPage, pageLimit);
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // Check if the clicked slot is in the paginated area
        if (slot < 45) {
            int index = getSlotIndex(slot);
            if (index >= 0 && index < warehouseItems.size()) {
                ItemStack clickedItem = warehouseItems.get(index).clone();
                // Remove the lore we added earlier
                if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getLore() != null) {
                    List<String> lore = new ArrayList<>(clickedItem.getItemMeta().getLore());
                    lore.remove(lore.size() - 1); // Remove the last line which is the lore we added
                    clickedItem = setLore(clickedItem, lore);
                }

                // If there's more than one of this item, open the amount menu
                if (clickedItem.getAmount() > 1) {
                    PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                    new WarehouseAmountMenu(this, clickedItem, jobName, playerMenu).open();
                } else {
                    // Otherwise, just give the player the item directly
                    giveItemToPlayer(player, clickedItem, 1);
                }
            }
        } else if (slot == 53) {
            // Add item button was clicked
            // This is handled by the WarehouseListener when shift-clicking a sign
            player.closeInventory();
        }
    }

    @Override
    public void addAdditionalItems() {
        inventory.setItem(53, FamiUtils.makeItem(Material.EMERALD_BLOCK, RPULanguageAddon.WarehouseAddItemDisplayName, RPULanguageAddon.WarehouseAddItemLore));
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", jobName);
        return FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseOpenMenuName, placeholders);
    }

    @Override
    public List<MenuTag> getMenuTags() { // Don't change this
        return new ArrayList<>();
    }

    /**
     * Gives an item to a player from the warehouse.
     *
     * @param player The player
     * @param item The item to give
     * @param amount The amount to give
     */
    public void giveItemToPlayer(Player player, ItemStack item, int amount) {
        // Check if the player has enough inventory space
        if (player.getInventory().firstEmpty() == -1) {
            HashMap<String, String> placeholders = new HashMap<>();
            player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseInventoryFull, placeholders));
            return;
        }

        // Find a warehouse with this item
        WarehouseManager manager = RPU_Warehouses.getInstance().getWarehouseManager();
        List<Warehouse> warehouses = manager.getWarehousesByJob(jobName);

        for (Warehouse warehouse : warehouses) {
            if (warehouse.hasItem(item, amount)) {
                // Remove the item from the warehouse
                if (manager.removeItemFromWarehouse(warehouse, item, amount)) {
                    // Give the item to the player
                    ItemStack giveItem = item.clone();
                    giveItem.setAmount(amount);
                    player.getInventory().addItem(giveItem);

                    // Send confirmation message
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("amount", String.valueOf(amount));

                    // Use item's display name if available, otherwise use formatted type name
                    String itemName;
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        itemName = item.getItemMeta().getDisplayName();
                    } else {
                        itemName = item.getType().name().toLowerCase().replace("_", " ");
                    }
                    placeholders.put("item", itemName);

                    player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseItemTaken, placeholders));

                    // Close the menu and update the warehouse
                    player.closeInventory();
                    return;
                }
            }
        }

        // If we get here, no warehouse had enough of the item
        HashMap<String, String> placeholders = new HashMap<>();
        player.sendMessage(FamiUtils.replaceAndFormat(RPULanguageAddon.WarehouseNotEnoughItems, placeholders));
    }

    private ItemStack setLore(ItemStack item, List<String> lore) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
