package me.fami6xx.rpuwarehouses.commands;

import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.JobPageLimits;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Command handler for warehouse-related commands.
 */
public class WarehouseCommand implements CommandExecutor {
    private final RPU_Warehouses plugin;

    /**
     * Creates a new warehouse command handler.
     *
     * @param plugin The plugin instance
     */
    public WarehouseCommand(RPU_Warehouses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("pagelimit")) {
            handlePageLimitCommand(player, args);
            return true;
        }

        sendHelp(player);
        return true;
    }

    /**
     * Handles the pagelimit subcommand.
     *
     * @param player The player
     * @param args The command arguments
     */
    private void handlePageLimitCommand(Player player, String[] args) {
        // Check if the player has permission
        if (!player.hasPermission("rpuwarehouses.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set page limits!");
            return;
        }

        // Check if the command has the correct number of arguments
        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Usage: /warehouse pagelimit <jobName> <pageLimit>");
            return;
        }

        String jobName = args[1];
        int pageLimit;

        // Parse the page limit
        try {
            pageLimit = Integer.parseInt(args[2]);
            if (pageLimit < 1) {
                player.sendMessage(ChatColor.RED + "Page limit must be at least 1.");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Page limit must be a number.");
            return;
        }

        // Set the page limit
        JobPageLimits jobPageLimits = plugin.getJobPageLimits();
        jobPageLimits.setPageLimit(jobName, pageLimit);

        // Send confirmation message
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("jobName", jobName);
        placeholders.put("pageLimit", String.valueOf(pageLimit));
        player.sendMessage(FamiUtils.replaceAndFormat("&aPage limit for job &f{jobName} &aset to &f{pageLimit}", placeholders));
    }

    /**
     * Sends help information to a player.
     *
     * @param player The player
     */
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== Warehouse Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/warehouse pagelimit <jobName> <pageLimit>" + ChatColor.WHITE + " - Set the page limit for a job");
    }
}