package com.pluralsight;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String connectionString = "jdbc:mysql://localhost:3306/northwind";
        String username = "user_1";
        String password = "password1234";

        // Load the MySQL Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Open a connection to the database
        Connection connection = DriverManager.getConnection(connectionString, username, password);

        // Create a statement
        Statement statement = connection.createStatement();

        // Define query to select all product names
        String query = "SELECT ProductName FROM Products";

        // Execute the query
        ResultSet results = statement.executeQuery(query);

        // Process the results
        while (results.next()) {
            String productName = results.getString("ProductName");
            System.out.println(productName);
        }

        // Close the connection
        connection.close();
    }
}