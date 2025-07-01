package me.fami6xx.rpuwarehouses.menus;

import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.JobPageLimits;
import me.fami6xx.rpuwarehouses.other.RPULanguageAddon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Menu for setting the page limit for a job's warehouse.
 */
public class WarehousePageLimitMenu extends Menu {
    private final String jobName;
    private int currentPageLimit;

    /**
     * Creates a new page limit menu.
     *
     * @param menu The parent menu
     * @param jobName The job name
     */
    public WarehousePageLimitMenu(PlayerMenu menu, String jobName) {
        super(menu);
        this.jobName = jobName;
        this.currentPageLimit = RPU_Warehouses.getInstance().getJobPageLimits().getPageLimit(jobName);
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("jobName", jobName);
        return FamiUtils.replaceAndFormat("&8Page Limit: &f{jobName}", placeholders);
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
            case 10: // -5
                changePageLimit(-5);
                setMenuItems();
                break;
            case 11: // -1
                changePageLimit(-1);
                setMenuItems();
                break;
            case 15: // +1
                changePageLimit(1);
                setMenuItems();
                break;
            case 16: // +5
                changePageLimit(5);
                setMenuItems();
                break;
            case 18: // Cancel
                player.closeInventory();
                break;
            case 26: // Confirm
                // Save the page limit
                RPU_Warehouses.getInstance().getJobPageLimits().setPageLimit(jobName, currentPageLimit);

                // Send confirmation message
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("jobName", jobName);
                placeholders.put("pageLimit", String.valueOf(currentPageLimit));
                player.sendMessage(FamiUtils.replaceAndFormat("&aPage limit for job &f{jobName} &aset to &f{pageLimit}", placeholders));

                player.closeInventory();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        // Create page limit display item
        ItemStack displayItem = new ItemStack(Material.BOOK);
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("pageLimit", String.valueOf(currentPageLimit));
            meta.setDisplayName(FamiUtils.replaceAndFormat("&aPage Limit: &f{pageLimit}", placeholders));

            List<String> lore = new ArrayList<>();
            lore.add(FamiUtils.format("&7This is how many pages of items"));
            lore.add(FamiUtils.format("&7this job can have in the warehouse."));
            meta.setLore(lore);

            displayItem.setItemMeta(meta);
        }
        inventory.setItem(13, displayItem);

        // Create decrease buttons
        createPageLimitButton(10, -5);
        createPageLimitButton(11, -1);

        // Create increase buttons
        createPageLimitButton(15, 1);
        createPageLimitButton(16, 5);

        // Create cancel button
        HashMap<String, String> placeholders = new HashMap<>();
        inventory.setItem(18, createButton(Material.RED_WOOL, 
                "&cCancel", 
                "&7Click to cancel", 
                placeholders));

        // Create confirm button
        placeholders.clear();
        placeholders.put("pageLimit", String.valueOf(currentPageLimit));
        inventory.setItem(26, createButton(Material.GREEN_WOOL, 
                "&aConfirm", 
                "&7Click to set page limit to &f{pageLimit}", 
                placeholders));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }

    /**
     * Changes the current page limit by the specified delta.
     *
     * @param delta The amount to change by
     */
    private void changePageLimit(int delta) {
        currentPageLimit += delta;
        if (currentPageLimit < 1) {
            currentPageLimit = 1;
        }
    }

    /**
     * Creates a page limit button.
     *
     * @param slot The slot to place the button in
     * @param amount The amount to change by
     */
    private void createPageLimitButton(int slot, int amount) {
        Material material = amount < 0 ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
        String displayName = amount < 0 ? "&cDecrease by &f{amount}" : "&aIncrease by &f{amount}";
        String lore = amount < 0 ? "&7Click to decrease page limit by &f{amount}" : "&7Click to increase page limit by &f{amount}";

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
