package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static sqlConnectionInfo sqlConnectionInfo;

    public static void main(String[] args) {

        // Validate command line arguments - exactly 3 are required (username, password, SQL URL)
        if (args.length != 3) {
            System.out.println(ColorCodes.BRIGHT_RED + ColorCodes.BOLD +
                    "❌ Application needs three arguments to run: " + ColorCodes.RESET +
                    ColorCodes.YELLOW + "java com.pluralsight.Main <username> <password> <sqlUrl>" + ColorCodes.RESET);
            System.exit(1); // Exit with error code if wrong number of arguments
        }

        // Initialize database connection info from command line arguments
        sqlConnectionInfo = getSqlConnectionInfoFromArgs(args);

        // Welcome message
        System.out.println(ColorCodes.BRIGHT_CYAN + ColorCodes.BOLD +
                "🗄️  Welcome to the Database Explorer!" + ColorCodes.RESET);
        System.out.println(ColorCodes.CYAN + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + ColorCodes.RESET);

        // Use try-with-resources to automatically close Scanner when done
        try (Scanner scanner = new Scanner(System.in)) {
            // Main application loop - continues until user chooses to exit
            while (true) {
                // Display the main menu options to the user
                System.out.println(ColorCodes.BRIGHT_BLUE + ColorCodes.BOLD +
                        "\n📋 What Table do you want to look at?\n" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_GREEN + "1) " + ColorCodes.RESET +
                        ColorCodes.GREEN + "📦 Display all products" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_YELLOW + "2) " + ColorCodes.RESET +
                        ColorCodes.YELLOW + "👥 Display all customers" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_PURPLE + "3) " + ColorCodes.RESET +
                        ColorCodes.PURPLE + "📂 Display all categories" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_RED + "0) " + ColorCodes.RESET +
                        ColorCodes.RED + "🚪 Exit" + ColorCodes.RESET);
                System.out.print(ColorCodes.BRIGHT_CYAN + "\n💬 Select an option: " + ColorCodes.RESET);

                // Read user's menu choice
                int choice = scanner.nextInt();

                // Process user's selection using if-else chain
                if (choice == 0) {
                    // User wants to exit the application
                    System.out.println(ColorCodes.CORAL + ColorCodes.BOLD +
                            "👋 Thanks for using Database Explorer! Goodbye!" + ColorCodes.RESET);
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
                    System.out.println(ColorCodes.BRIGHT_RED + ColorCodes.BOLD +
                            "❌ Invalid selection. Please try again.\n" + ColorCodes.RESET);
                }
            }
        } catch (Exception e) {
            // Catch and print any exceptions that occur during execution
            System.out.println(ColorCodes.BRIGHT_RED + ColorCodes.BOLD +
                    "💥 An error occurred: " + ColorCodes.RESET + ColorCodes.RED + e.getMessage() + ColorCodes.RESET);
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
        System.out.println(ColorCodes.BRIGHT_GREEN + ColorCodes.BOLD +
                "\n📦 ALL PRODUCTS" + ColorCodes.RESET);
        System.out.println(ColorCodes.GREEN + "═══════════════════════════════════════" + ColorCodes.RESET);

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
                System.out.println(ColorCodes.BRIGHT_BLUE + "🆔 Product Id: " + ColorCodes.RESET +
                        ColorCodes.CYAN + results.getInt("ProductID") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_YELLOW + "📝 Name: " + ColorCodes.RESET +
                        ColorCodes.YELLOW + results.getString("ProductName") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_GREEN + "💰 Price: " + ColorCodes.RESET +
                        ColorCodes.GREEN + String.format("$%.2f", results.getDouble("UnitPrice")) + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_PURPLE + "📊 Stock: " + ColorCodes.RESET +
                        ColorCodes.PURPLE + results.getInt("UnitsInStock") + " units" + ColorCodes.RESET);
                System.out.println(ColorCodes.CYAN + "─────────────────────────" + ColorCodes.RESET);
            }
        }
        // Resources are automatically closed here due to try-with-resources
    }

    /**
     * Method to retrieve and display all customers from the database
     * Results are ordered by country for better organization
     */
    public static void displayCustomers() throws SQLException {
        System.out.println(ColorCodes.BRIGHT_YELLOW + ColorCodes.BOLD +
                "\n👥 ALL CUSTOMERS" + ColorCodes.RESET);
        System.out.println(ColorCodes.YELLOW + "═══════════════════════════════════════" + ColorCodes.RESET);

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
                System.out.println(ColorCodes.BRIGHT_BLUE + "👤 Contact: " + ColorCodes.RESET +
                        ColorCodes.CYAN + results.getString("ContactName") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_PURPLE + "🏢 Company: " + ColorCodes.RESET +
                        ColorCodes.PURPLE + results.getString("CompanyName") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_GREEN + "🏙️  City: " + ColorCodes.RESET +
                        ColorCodes.GREEN + results.getString("City") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_YELLOW + "🌍 Country: " + ColorCodes.RESET +
                        ColorCodes.GOLD + results.getString("Country") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_RED + "📞 Phone: " + ColorCodes.RESET +
                        ColorCodes.RED + results.getString("Phone") + ColorCodes.RESET);
                System.out.println(ColorCodes.YELLOW + "─────────────────────────" + ColorCodes.RESET);
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
        System.out.print(ColorCodes.BRIGHT_CYAN +
                "🔍 Enter a category ID to view products in that category: " + ColorCodes.RESET);
        int categoryId = scanner.nextInt();

        // Display products in the selected category
        displayProductsByCategory(categoryId);
    }

    /**
     * Method to retrieve and display all categories from the database
     * Results are ordered by category ID
     */
    public static void displayCategories() throws SQLException {
        System.out.println(ColorCodes.BRIGHT_PURPLE + ColorCodes.BOLD +
                "\n📂 ALL CATEGORIES" + ColorCodes.RESET);
        System.out.println(ColorCodes.PURPLE + "═══════════════════════════════════════" + ColorCodes.RESET);

        // Try-with-resources for automatic resource management
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID")) {

            // Loop through each category record returned
            while (results.next()) {
                // Extract and display each category's information
                System.out.println(ColorCodes.BRIGHT_BLUE + "🆔 Category ID: " + ColorCodes.RESET +
                        ColorCodes.CYAN + results.getInt("CategoryID") + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_GREEN + "📂 Category Name: " + ColorCodes.RESET +
                        ColorCodes.GREEN + results.getString("CategoryName") + ColorCodes.RESET);
                System.out.println(ColorCodes.PURPLE + "─────────────────────────" + ColorCodes.RESET);
            }
        }
        // Database resources automatically closed here
    }

    /**
     * Method to retrieve and display products for a specific category
     * Uses PreparedStatement to safely handle user input
     */
    public static void displayProductsByCategory(int categoryId) throws SQLException {
        System.out.println(ColorCodes.ORANGE + ColorCodes.BOLD +
                "\n🔍 PRODUCTS IN CATEGORY " + categoryId + ColorCodes.RESET);
        System.out.println(ColorCodes.ORANGE + "═══════════════════════════════════════" + ColorCodes.RESET);

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
                boolean hasProducts = false;

                // Loop through each product record returned
                while (results.next()) {
                    hasProducts = true;
                    // Extract and display each product's information
                    System.out.println(ColorCodes.BRIGHT_BLUE + "🆔 Product ID: " + ColorCodes.RESET +
                            ColorCodes.CYAN + results.getInt("ProductID") + ColorCodes.RESET);
                    System.out.println(ColorCodes.BRIGHT_YELLOW + "📝 Product Name: " + ColorCodes.RESET +
                            ColorCodes.YELLOW + results.getString("ProductName") + ColorCodes.RESET);
                    System.out.println(ColorCodes.BRIGHT_GREEN + "💰 Unit Price: " + ColorCodes.RESET +
                            ColorCodes.GREEN + String.format("$%.2f", results.getDouble("UnitPrice")) + ColorCodes.RESET);
                    System.out.println(ColorCodes.BRIGHT_PURPLE + "📊 Units in Stock: " + ColorCodes.RESET +
                            ColorCodes.PURPLE + results.getInt("UnitsInStock") + " units" + ColorCodes.RESET);
                    System.out.println(ColorCodes.ORANGE + "─────────────────────────" + ColorCodes.RESET);
                }

                // If no products found in the category
                if (!hasProducts) {
                    System.out.println(ColorCodes.BRIGHT_RED + ColorCodes.BOLD +
                            "❌ No products found in category " + categoryId + ColorCodes.RESET);
                    System.out.println(ColorCodes.RED + "─────────────────────────" + ColorCodes.RESET);
                }
            }
        }
        // Database resources automatically closed here
    }
}