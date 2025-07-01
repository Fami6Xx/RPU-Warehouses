package me.fami6xx.rpuwarehouses.other;

import me.fami6xx.rpuniverse.core.misc.language.AbstractAddonLanguage;

public class RPULanguageAddon extends AbstractAddonLanguage {
    // Menu titles
    public static final String WarehouseOpenMenuName = "&c&lWAREHOUSE &7| &4{jobName}";
    public static final String WarehouseAmountMenuName = "&c&lSELECT AMOUNT";

    // Menu items
    public static final String WarehouseAddItemDisplayName = "&a&lAdd Item";
    public static final String WarehouseAddItemLore = "&7Shift-right-click to add an item to the warehouse.";
    public static final String WarehouseItemLore = "&7Click to take this item from the warehouse.";
    public static final String WarehouseItemAmount = "&7Amount: &f{itemAmount}";

    // Buttons
    public static final String WarehouseAmountIncreaseDisplayName = "&a&l+{amount}";
    public static final String WarehouseAmountIncreaseLore = "&7Click to increase the amount by {amount}.";
    public static final String WarehouseAmountDecreaseDisplayName = "&c&l-{amount}";
    public static final String WarehouseAmountDecreaseLore = "&7Click to decrease the amount by {amount}.";
    public static final String WarehouseAmountConfirmDisplayName = "&a&lConfirm";
    public static final String WarehouseAmountConfirmLore = "&7Click to confirm taking &f{amount}x {item}&7.";
    public static final String WarehouseAmountCancelDisplayName = "&c&lCancel";
    public static final String WarehouseAmountCancelLore = "&7Click to cancel.";

    // Messages
    public static final String WarehouseCreated = "&aWarehouse created for job &f{jobName}&a!";
    public static final String WarehouseAlreadyExists = "&cA warehouse already exists at this location!";
    public static final String WarehouseItemAdded = "&aAdded &f{amount}x {item} &ato the warehouse!";
    public static final String WarehouseItemTaken = "&aTook &f{amount}x {item} &afrom the warehouse!";
    public static final String WarehouseNotEnoughItems = "&cThe warehouse doesn't have enough of that item!";
    public static final String WarehouseInventoryFull = "&cYour inventory is full!";
    public static final String WarehouseSignFormat = "&c&lWAREHOUSE\n&7Job: &f{jobName}\n&7Items: &f{itemCount}\n&7Click to open";


    // Create a singleton instance
    private static RPULanguageAddon instance;

    public static RPULanguageAddon getInstance() {
        if (instance == null) {
            instance = AbstractAddonLanguage.create(RPULanguageAddon.class);
        }
        return instance;
    }

    // Constructor
    public RPULanguageAddon() {
        // Call initLanguage() to register translations
        initLanguage();
    }
}
