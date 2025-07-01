package me.fami6xx.rpuwarehouses.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.fami6xx.rpuwarehouses.RPU_Warehouses;
import me.fami6xx.rpuwarehouses.data.gson.GsonManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages the page limits for each job's warehouse.
 */
public class JobPageLimits {
    private static JobPageLimits instance;
    private final RPU_Warehouses plugin;
    private final Map<String, Integer> pageLimits;
    private final File pageLimitsFile;
    private final int DEFAULT_PAGE_LIMIT = 1;

    /**
     * Creates a new job page limits manager.
     *
     * @param plugin The plugin instance
     */
    private JobPageLimits(RPU_Warehouses plugin) {
        this.plugin = plugin;
        this.pageLimits = new HashMap<>();
        this.pageLimitsFile = new File(plugin.getDataFolder(), "page_limits.json");

        // Load page limits from disk
        loadPageLimits();
    }

    /**
     * Gets the singleton instance of the job page limits manager.
     *
     * @param plugin The plugin instance
     * @return The job page limits manager instance
     */
    public static JobPageLimits getInstance(RPU_Warehouses plugin) {
        if (instance == null) {
            instance = new JobPageLimits(plugin);
        }
        return instance;
    }

    /**
     * Gets the singleton instance of the job page limits manager.
     *
     * @return The job page limits manager instance
     */
    public static JobPageLimits getInstance() {
        if (instance == null) {
            throw new IllegalStateException("JobPageLimits has not been initialized yet");
        }
        return instance;
    }

    /**
     * Loads page limits from disk.
     */
    private void loadPageLimits() {
        // Create the file if it doesn't exist
        if (!pageLimitsFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                pageLimitsFile.createNewFile();
                // Save an empty map if the file is new
                savePageLimits();
                return;
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create page_limits.json", e);
                return;
            }
        }

        // Load page limits from JSON
        try (FileReader reader = new FileReader(pageLimitsFile)) {
            Gson gson = GsonManager.getGson();
            Type pageLimitsType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> loadedLimits = gson.fromJson(reader, pageLimitsType);

            // Handle empty file or null result
            if (loadedLimits != null) {
                pageLimits.putAll(loadedLimits);
            }

            plugin.getLogger().info("Loaded page limits for " + pageLimits.size() + " jobs");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load page_limits.json", e);
        }
    }

    /**
     * Saves page limits to disk.
     */
    public void savePageLimits() {
        try (FileWriter writer = new FileWriter(pageLimitsFile)) {
            Gson gson = GsonManager.getGson();
            gson.toJson(pageLimits, writer);
            plugin.getLogger().info("Saved page limits for " + pageLimits.size() + " jobs");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save page_limits.json", e);
        }
    }

    /**
     * Gets the page limit for a job.
     *
     * @param jobName The job name
     * @return The page limit for the job
     */
    public int getPageLimit(String jobName) {
        return pageLimits.getOrDefault(jobName, DEFAULT_PAGE_LIMIT);
    }

    /**
     * Sets the page limit for a job.
     *
     * @param jobName The job name
     * @param pageLimit The page limit to set
     */
    public void setPageLimit(String jobName, int pageLimit) {
        if (pageLimit < 1) {
            pageLimit = 1;
        }
        pageLimits.put(jobName, pageLimit);
        savePageLimits();
    }

    /**
     * Gets all job page limits.
     *
     * @return A map of job names to page limits
     */
    public Map<String, Integer> getAllPageLimits() {
        return new HashMap<>(pageLimits);
    }
}