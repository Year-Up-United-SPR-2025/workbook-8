package com.pluralsight;

import java.sql.*;

public class Option_2 {
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

        // Define query ProductID, ProductName, UnitPrice, UnitsInStock
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products";

        // Execute the query
        ResultSet results = statement.executeQuery(query);

        // Process the results with stacked output format
        while (results.next()) {
            int productId = results.getInt("ProductID");
            String productName = results.getString("ProductName");
            double unitPrice = results.getDouble("UnitPrice");
            int unitsInStock = results.getInt("UnitsInStock");

            // Display stacked Option 2 format
            System.out.println("Product Id: " + productId);
            System.out.println("Name: " + productName);
            System.out.printf("Price: %.2f%n", unitPrice);
            System.out.println("Stock: " + unitsInStock);
            System.out.println("------------------");
        }

        // Close the connection
        connection.close();
    }
}