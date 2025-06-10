package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BasicDataSource {

    private String connectionString;
    private String username;
    private String password;

    public BasicDataSource(String connectionString, String username, String password) {
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            // load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(this.getConnectionString(),
                    this.getUsername(), this.getPassword());


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}