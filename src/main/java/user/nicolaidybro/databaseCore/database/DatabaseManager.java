package user.nicolaidybro.databaseCore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManager {
    private HikariDataSource dataSource;
    private final Plugin plugin;

    public DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean initialize(FileConfiguration config) {
        try {
            HikariConfig hikariConfig = new HikariConfig();

            String host = config.getString("database.host", "localhost");
            int port = config.getInt("database.port", 3306);
            String database = config.getString("database.name", "minecraft");
            String username = config.getString("database.username", "root");
            String password = config.getString("database.password", "password");

            String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8",
                    host, port, database);

            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Connection pool settings
            hikariConfig.setMaximumPoolSize(config.getInt("connection-pool.maximum-pool-size", 10));
            hikariConfig.setMinimumIdle(config.getInt("connection-pool.minimum-idle", 5));
            hikariConfig.setConnectionTimeout(config.getLong("connection-pool.connection-timeout", 30000));
            hikariConfig.setIdleTimeout(config.getLong("connection-pool.idle-timeout", 600000));
            hikariConfig.setMaxLifetime(config.getLong("connection-pool.max-lifetime", 1800000));

            // Additional settings for better performance
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            this.dataSource = new HikariDataSource(hikariConfig);

            // Test connection
            try (Connection connection = getConnection()) {
                plugin.getLogger().info("Successfully connected to MySQL database!");
                return true;
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database connection", e);
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection closed");
        }
    }

    public boolean isConnected() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
