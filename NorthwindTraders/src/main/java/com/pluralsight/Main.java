package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class Main {

    // Static field to hold database connection information throughout the application
    private static sqlConnectionInfo sqlConnectionInfo;

    public static void main(String[] args) {

        // Validate command line arguments - exactly 3 are required (username, password, SQL URL)
        if (args.length != 3) {
            System.out.println("Application needs three arguments to run: " +
                    "java com.pluralsight.Main <username> <password> <sqlUrl>");
            System.exit(1); // Exit with error code if wrong number of arguments
        }

        // Initialize database connection info from command line arguments
        sqlConnectionInfo = getSqlConnectionInfoFromArgs(args);

        // Use try-with-resources to automatically close Scanner when done
        try (Scanner scanner = new Scanner(System.in)) {
            // Main application loop - continues until user chooses to exit
            while (true) {
                // Display the main menu options to the user
                System.out.println("What do you want to do?");
                System.out.println("1) Display all products");
                System.out.println("2) Display all customers");
                System.out.println("3) Display all categories");
                System.out.println("0) Exit");
                System.out.print("Select an option: ");

                // Read user's menu choice
                int choice = scanner.nextInt();

                // Process user's selection using if-else chain
                if (choice == 0) {
                    // User wants to exit the application
                    System.out.println("Exiting...");
                    break; // Exit the while loop
                } else if (choice == 1) {
                    // User wants to see all products
                    displayProducts();
                } else if (choice == 2) {
                    // User wants to see all customers
                    displayCustomers();
                } else if (choice == 3) {
                    // User wants to see all categories and then products by category
                    displayCategoriesAndProducts(scanner);
                } else {
                    // Invalid menu option selected
                    System.out.println("Invalid selection. Try again.");
                }
            }
        } catch (Exception e) {
            // Catch and print any exceptions that occur during execution
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create sqlConnectionInfo object from command line arguments
     *
     * @param args Command line arguments array [username, password, sqlUrl]
     * @return sqlConnectionInfo object with connection details
     */
    public static sqlConnectionInfo getSqlConnectionInfoFromArgs(String[] args) {
        // Create connection info object: args[2] = URL, args[0] = username, args[1] = password
        return new sqlConnectionInfo(args[2], args[0], args[1]);
    }

    /**
     * Method to retrieve and display all products from the database
     * Uses try-with-resources to ensure proper cleanup of database resources
     */
    public static void displayProducts() throws SQLException {
        // Try-with-resources automatically closes Connection, Statement, and ResultSet
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products")) {

            // Iterate through all rows returned by the query
            while (results.next()) {
                // Extract and display each product's information
                System.out.println("Product Id: " + results.getInt("ProductID"));
                System.out.println("Name:");
                System.out.println(results.getString("ProductName"));
                System.out.println("Price:");
                System.out.printf("%.2f%n", results.getDouble("UnitPrice"));
                System.out.println("Stock:");
                System.out.println(results.getInt("UnitsInStock"));
                System.out.println("------------------");
            }
        }
        // Resources are automatically closed here due to try-with-resources
    }

    /**
     * Method to retrieve and display all customers from the database
     * Results are ordered by country for better organization
     */
    public static void displayCustomers() throws SQLException {
        // Try-with-resources for automatic resource management
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country")) {

            // Loop through each customer record returned
            while (results.next()) {
                // Extract and display each customer's information
                System.out.println("Contact Name: " + results.getString("ContactName"));
                System.out.println("Company Name: " + results.getString("CompanyName"));
                System.out.println("City: " + results.getString("City"));
                System.out.println("Country: " + results.getString("Country"));
                System.out.println("Phone: " + results.getString("Phone"));
                System.out.println("------------------");
            }
        }
        // Database resources automatically closed here
    }

    /**
     * Method to display all categories and then prompt user to select a category
     * to view products within that category
     */
    public static void displayCategoriesAndProducts(Scanner scanner) throws SQLException {
        // First, display all categories
        displayCategories();

        // Prompt user to select a category
        System.out.print("Enter a category ID to view products in that category: ");
        int categoryId = scanner.nextInt();

        // Display products in the selected category
        displayProductsByCategory(categoryId);
    }

    /**
     * Method to retrieve and display all categories from the database
     * Results are ordered by category ID
     */
    public static void displayCategories() throws SQLException {
        // Try-with-resources for automatic resource management
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID")) {

            System.out.println("Categories:");
            System.out.println("------------------");

            // Loop through each category record returned
            while (results.next()) {
                // Extract and display each category's information
                System.out.println("Category ID: " + results.getInt("CategoryID"));
                System.out.println("Category Name: " + results.getString("CategoryName"));
                System.out.println("------------------");
            }
        }
        // Database resources automatically closed here
    }

    /**
     * Method to retrieve and display products for a specific category
     * Uses PreparedStatement to safely handle user input
     */
    public static void displayProductsByCategory(int categoryId) throws SQLException {
        // Try-with-resources for automatic resource management
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE CategoryID = ?")) {

            // Set the category ID parameter in the prepared statement
            preparedStatement.setInt(1, categoryId);

            try (ResultSet results = preparedStatement.executeQuery()) {
                System.out.println("Products in Category " + categoryId + ":");
                System.out.println("------------------");

                boolean hasProducts = false;

                // Loop through each product record returned
                while (results.next()) {
                    hasProducts = true;
                    // Extract and display each product's information
                    System.out.println("Product ID: " + results.getInt("ProductID"));
                    System.out.println("Product Name: " + results.getString("ProductName"));
                    System.out.println("Unit Price: " + String.format("%.2f", results.getDouble("UnitPrice")));
                    System.out.println("Units in Stock: " + results.getInt("UnitsInStock"));
                    System.out.println("------------------");
                }

                // If no products found in the category
                if (!hasProducts) {
                    System.out.println("No products found in category " + categoryId);
                    System.out.println("------------------");
                }
            }
        }
        // Database resources automatically closed here
    }
}