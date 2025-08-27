package user.nicolaidybro.databaseCore.examples;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import user.nicolaidybro.databaseCore.api.DatabaseAPI;
import user.nicolaidybro.databaseCore.api.QueryResult;

import java.util.Map;

/**
 * Example class showing how other plugins can use the DatabaseCore API
 * This is just an example - other plugins would implement similar logic
 */
public class DatabaseExample {

    /**
     * Example of how to get the DatabaseAPI from another plugin
     */
    public static DatabaseAPI getDatabaseAPI(JavaPlugin plugin) {
        RegisteredServiceProvider<DatabaseAPI> provider =
            plugin.getServer().getServicesManager().getRegistration(DatabaseAPI.class);

        if (provider != null) {
            return provider.getProvider();
        }

        plugin.getLogger().warning("DatabaseCore API not found! Make sure DatabaseCore plugin is installed and enabled.");
        return null;
    }

    /**
     * Example of creating a table
     */
    public static void createPlayerTable(DatabaseAPI api) {
        String sql = """
            CREATE TABLE IF NOT EXISTS players (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid VARCHAR(36) UNIQUE NOT NULL,
                name VARCHAR(16) NOT NULL,
                coins INT DEFAULT 0,
                last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        api.update(sql).thenAccept(result -> {
            System.out.println("Players table created/verified");
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * Example of inserting a player
     */
    public static void insertPlayer(DatabaseAPI api, String uuid, String name) {
        String sql = "INSERT INTO players (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name)";

        api.update(sql, uuid, name).thenAccept(result -> {
            System.out.println("Player " + name + " saved to database");
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * Example of getting player data
     */
    public static void getPlayer(DatabaseAPI api, String uuid) {
        String sql = "SELECT * FROM players WHERE uuid = ?";

        api.query(sql, uuid).thenAccept(result -> {
            if (!result.isEmpty()) {
                Map<String, Object> playerData = result.getFirstRow();
                System.out.println("Player found: " + playerData.get("name") +
                                 " with " + playerData.get("coins") + " coins");
            } else {
                System.out.println("Player not found");
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * Example of updating player coins
     */
    public static void updatePlayerCoins(DatabaseAPI api, String uuid, int coins) {
        String sql = "UPDATE players SET coins = ? WHERE uuid = ?";

        api.update(sql, coins, uuid).thenAccept(result -> {
            if (result > 0) {
                System.out.println("Updated coins for player");
            } else {
                System.out.println("Player not found");
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }
}
