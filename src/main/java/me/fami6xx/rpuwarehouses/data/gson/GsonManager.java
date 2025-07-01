package me.fami6xx.rpuwarehouses.data.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for managing Gson instances with custom type adapters.
 */
public class GsonManager {
    private static Gson gson;
    
    /**
     * Gets a configured Gson instance with all necessary type adapters.
     * 
     * @return A configured Gson instance
     */
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Location.class, new LocationAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
            
            gson = gsonBuilder.create();
        }
        
        return gson;
    }
}