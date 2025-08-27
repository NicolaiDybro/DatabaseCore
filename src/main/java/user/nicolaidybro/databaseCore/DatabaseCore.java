package user.nicolaidybro.databaseCore;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import user.nicolaidybro.databaseCore.api.DatabaseAPI;
import user.nicolaidybro.databaseCore.api.DatabaseAPIImpl;
import user.nicolaidybro.databaseCore.database.DatabaseManager;

public final class DatabaseCore extends JavaPlugin {

    private DatabaseManager databaseManager;
    private DatabaseAPI databaseAPI;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize database manager
        databaseManager = new DatabaseManager(this);

        // Initialize database connection
        if (!databaseManager.initialize(getConfig())) {
            getLogger().severe("Failed to initialize database connection! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create API implementation
        databaseAPI = new DatabaseAPIImpl(databaseManager, this);

        // Register API as a service for other plugins to use
        getServer().getServicesManager().register(DatabaseAPI.class, databaseAPI, this, ServicePriority.High);

        getLogger().info("DatabaseCore has been enabled successfully!");
        getLogger().info("Other plugins can now access the database API through the ServicesManager");
    }

    @Override
    public void onDisable() {
        // Unregister API service
        if (databaseAPI != null) {
            getServer().getServicesManager().unregister(DatabaseAPI.class, databaseAPI);
        }

        // Close database connections
        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("DatabaseCore has been disabled");
    }

    /**
     * Get the database API instance
     * @return DatabaseAPI instance
     */
    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }
}
