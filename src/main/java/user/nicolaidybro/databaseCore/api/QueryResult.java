package user.nicolaidybro.databaseCore.api;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class for SQL query results
 */
public class QueryResult {
    private final List<Map<String, Object>> rows;
    private final List<String> columnNames;

    public QueryResult(ResultSet resultSet) throws SQLException {
        this.rows = new ArrayList<>();
        this.columnNames = new ArrayList<>();

        if (resultSet != null) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Get data rows
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                rows.add(row);
            }
        }
    }

    /**
     * Get all rows as a list of maps
     * @return List of rows
     */
    public List<Map<String, Object>> getRows() {
        return new ArrayList<>(rows);
    }

    /**
     * Get a specific row by index
     * @param index Row index
     * @return Row data as map
     */
    public Map<String, Object> getRow(int index) {
        if (index >= 0 && index < rows.size()) {
            return new HashMap<>(rows.get(index));
        }
        return new HashMap<>();
    }

    /**
     * Get the first row
     * @return First row data as map
     */
    public Map<String, Object> getFirstRow() {
        return getRow(0);
    }

    /**
     * Get column names
     * @return List of column names
     */
    public List<String> getColumnNames() {
        return new ArrayList<>(columnNames);
    }

    /**
     * Get number of rows
     * @return Row count
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Check if result set is empty
     * @return true if empty
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    /**
     * Get a value from the first row by column name
     * @param columnName Column name
     * @return Value or null
     */
    public Object getValue(String columnName) {
        if (!rows.isEmpty()) {
            return rows.get(0).get(columnName);
        }
        return null;
    }

    /**
     * Get a string value from the first row by column name
     * @param columnName Column name
     * @return String value or null
     */
    public String getString(String columnName) {
        Object value = getValue(columnName);
        return value != null ? value.toString() : null;
    }

    /**
     * Get an integer value from the first row by column name
     * @param columnName Column name
     * @return Integer value or 0
     */
    public int getInt(String columnName) {
        Object value = getValue(columnName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * Get a long value from the first row by column name
     * @param columnName Column name
     * @return Long value or 0
     */
    public long getLong(String columnName) {
        Object value = getValue(columnName);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    /**
     * Get a boolean value from the first row by column name
     * @param columnName Column name
     * @return Boolean value or false
     */
    public boolean getBoolean(String columnName) {
        Object value = getValue(columnName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return false;
    }
}
