package org.example.db;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class Repository implements Closeable {
    {
        properties = new Properties();
        FileInputStream fis;
        try {
            File file = new File(Objects.requireNonNull(Repository.class.getResource("/properties/db.properties")).toURI());
            fis = new FileInputStream(file);
            this.properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private Connection connection;
    private Statement statement;
    private final Properties properties;

    private void openSession() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("name"),
                properties.getProperty("password"));
        statement = connection.createStatement();
    }

    public ResultSet getResultSet(String query) throws SQLException, ClassNotFoundException {
        openSession();
        return statement.executeQuery(query);
    }

    public void addValue(String query) throws SQLException, ClassNotFoundException {
        openSession();
        System.out.println(statement.execute(query));

    }

    @Override
    public void close() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
