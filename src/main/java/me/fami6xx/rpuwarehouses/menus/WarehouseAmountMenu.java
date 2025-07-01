package me.fami6xx.rpuwarehouses.menus;

import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuwarehouses.other.RPULanguageAddon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarehouseAmountMenu extends Menu {
    private final WarehouseJobMenu parentMenu;
    private final ItemStack item;
    private final String jobName;
    private int currentAmount = 1;
    private final int maxAmount;

    /**
     * Creates a new amount selection menu.
     *
     * @param parentMenu The parent menu
     * @param item The item to take
     * @param jobName The job name
     */
    public WarehouseAmountMenu(WarehouseJobMenu parentMenu, ItemStack item, String jobName) {
        this.parentMenu = parentMenu;
        this.item = item.clone();
        this.jobName = jobName;
        this.maxAmount = item.getAmount();
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPULanguageAddon.WarehouseAmountMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 10: // -10
                changeAmount(-10);
                setMenuItems();
                break;
            case 11: // -5
                changeAmount(-5);
                setMenuItems();
                break;
            case 12: // -1
                changeAmount(-1);
                setMenuItems();
                break;
            case 14: // +1
                changeAmount(1);
                setMenuItems();
                break;
            case 15: // +5
                changeAmount(5);
                setMenuItems();
                break;
            case 16: // +10
                changeAmount(10);
                setMenuItems();
                break;
            case 18: // Cancel
                player.closeInventory();
                parentMenu.openMenu(player);
                break;
            case 26: // Confirm
                player.closeInventory();
                parentMenu.giveItemToPlayer(player, item, currentAmount);
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        // Create amount display item
        ItemStack displayItem = item.clone();
        displayItem.setAmount(Math.min(currentAmount, 64));
        inventory.setItem(13, displayItem);

        // Create decrease buttons
        createAmountButton(10, -10);
        createAmountButton(11, -5);
        createAmountButton(12, -1);

        // Create increase buttons
        createAmountButton(14, 1);
        createAmountButton(15, 5);
        createAmountButton(16, 10);

        // Create cancel button
        HashMap<String, String> placeholders = new HashMap<>();
        inventory.setItem(18, createButton(Material.RED_WOOL, 
                RPULanguageAddon.WarehouseAmountCancelDisplayName, 
                RPULanguageAddon.WarehouseAmountCancelLore, 
                placeholders));

        // Create confirm button
        placeholders.clear();
        placeholders.put("amount", String.valueOf(currentAmount));

        // Use item's display name if available, otherwise use formatted type name
        String itemName;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name().toLowerCase().replace("_", " ");
        }
        placeholders.put("item", itemName);

        inventory.setItem(26, createButton(Material.GREEN_WOOL, 
                RPULanguageAddon.WarehouseAmountConfirmDisplayName, 
                RPULanguageAddon.WarehouseAmountConfirmLore, 
                placeholders));
    }

    @Override
    public List<MenuTag> getMenuTags() { // Don't change this
        return new ArrayList<>();
    }

    /**
     * Changes the current amount by the specified delta.
     *
     * @param delta The amount to change by
     */
    private void changeAmount(int delta) {
        currentAmount += delta;
        if (currentAmount < 1) {
            currentAmount = 1;
        } else if (currentAmount > maxAmount) {
            currentAmount = maxAmount;
        }
    }

    /**
     * Creates an amount button.
     *
     * @param slot The slot to place the button in
     * @param amount The amount to change by
     */
    private void createAmountButton(int slot, int amount) {
        Material material = amount < 0 ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
        String displayName = amount < 0 ? RPULanguageAddon.WarehouseAmountDecreaseDisplayName : RPULanguageAddon.WarehouseAmountIncreaseDisplayName;
        String lore = amount < 0 ? RPULanguageAddon.WarehouseAmountDecreaseLore : RPULanguageAddon.WarehouseAmountIncreaseLore;

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("amount", String.valueOf(Math.abs(amount)));

        inventory.setItem(slot, createButton(material, displayName, lore, placeholders));
    }

    /**
     * Creates a button with the specified properties.
     *
     * @param material The button material
     * @param displayName The button display name
     * @param lore The button lore
     * @param placeholders Placeholders to replace in the display name and lore
     * @return The created button
     */
    private ItemStack createButton(Material material, String displayName, String lore, HashMap<String, String> placeholders) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(FamiUtils.replaceAndFormat(displayName, placeholders));

            List<String> loreList = new ArrayList<>();
            for (String line : FamiUtils.replaceAndFormat(lore, placeholders).split("\n")) {
                loreList.add(line);
            }
            meta.setLore(loreList);

            button.setItemMeta(meta);
        }

        return button;
    }
}
