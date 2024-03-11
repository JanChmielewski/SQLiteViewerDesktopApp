package viewer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase implements AutoCloseable {

    public static final String ALL_ROWS_QUERY = "SELECT * FROM %s;";
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public DataBase(String filename) throws SQLException {
        if (!filename.matches(".*\\.db")) {
            throw new IllegalArgumentException("It's not a .db file");
        }
        String dbPath = System.getProperty("user.dir") + "/" + filename;
        connect(dbPath);
    }

    private void connect(String fileName) throws SQLException {
        final String url = "jdbc:sqlite:%s";
        connection = DriverManager.getConnection(String.format(url, fileName));
        if (connection == null) {
            throw new SQLException("Failed to create a database connection.");
        }
    }

    public List<String> getTables() throws SQLException {
        List<String> list = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_NAME");
            list.add(name);
        }
        return list;
    }

    public DisplayTable executeQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columns[i] = metaData.getColumnName(i + 1);
            }

            Map<Integer, Object[]> data = new HashMap<>();
            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    row[j] = resultSet.getObject(j + 1);
                }
                data.put(i++, row);
            }
            return new DisplayTable(columns, data);
        }

    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}