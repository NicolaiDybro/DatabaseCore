package user.nicolaidybro.databaseCore.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * API interface for DatabaseCore plugin
 * Other plugins can use this to access database functionality
 */
public interface DatabaseAPI {

    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    Connection getConnection() throws SQLException;

    /**
     * Execute a database operation asynchronously
     * @param operation The operation to execute
     * @return CompletableFuture with the result
     */
    <T> CompletableFuture<T> executeAsync(Function<Connection, T> operation);

    /**
     * Execute a database operation synchronously
     * @param operation The operation to execute
     * @return The result of the operation
     */
    <T> T execute(Function<Connection, T> operation) throws SQLException;

    /**
     * Check if the database is connected
     * @return true if connected, false otherwise
     */
    boolean isConnected();

    /**
     * Execute a query that returns a result
     * @param sql SQL query string
     * @param parameters Query parameters
     * @return QueryResult object
     */
    CompletableFuture<QueryResult> query(String sql, Object... parameters);

    /**
     * Execute an update/insert/delete operation
     * @param sql SQL statement
     * @param parameters Statement parameters
     * @return Number of affected rows
     */
    CompletableFuture<Integer> update(String sql, Object... parameters);
}
