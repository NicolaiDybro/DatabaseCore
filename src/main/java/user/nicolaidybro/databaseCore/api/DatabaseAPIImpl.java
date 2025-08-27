package user.nicolaidybro.databaseCore.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import user.nicolaidybro.databaseCore.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Implementation of the DatabaseAPI interface
 */
public class DatabaseAPIImpl implements DatabaseAPI {
    private final DatabaseManager databaseManager;
    private final Plugin plugin;

    public DatabaseAPIImpl(DatabaseManager databaseManager, Plugin plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return databaseManager.getConnection();
    }

    @Override
    public <T> CompletableFuture<T> executeAsync(Function<Connection, T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                return operation.apply(connection);
            } catch (SQLException e) {
                plugin.getLogger().severe("Database operation failed: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> T execute(Function<Connection, T> operation) throws SQLException {
        try (Connection connection = getConnection()) {
            return operation.apply(connection);
        }
    }

    @Override
    public boolean isConnected() {
        return databaseManager.isConnected();
    }

    @Override
    public CompletableFuture<QueryResult> query(String sql, Object... parameters) {
        return executeAsync(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, parameters);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return new QueryResult(resultSet);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Query failed: " + sql + " - " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(String sql, Object... parameters) {
        return executeAsync(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, parameters);
                return statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Update failed: " + sql + " - " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    private void setParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }
}
