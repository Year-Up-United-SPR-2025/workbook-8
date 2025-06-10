package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class Option_1 {

    private static sqlConnectionInfo sqlConnectionInfo;

    public static void main(String[] args) {

        // Ensure three arguments are provided
        if (args.length != 3) {
            System.out.println("Application needs three arguments to run: " +
                    "java com.pluralsight.Main <username> <password> <sqlUrl>");
            System.exit(1);
        }

        sqlConnectionInfo = getSqlConnectionInfoFromArgs(args);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                // Display menu
                System.out.println("What do you want to do?");
                System.out.println("1) Display all products");
                System.out.println("2) Display all customers");
                System.out.println("0) Exit");
                System.out.print("Select an option: ");
                int choice = scanner.nextInt();

                // Handle user selection
                if (choice == 0) {
                    System.out.println("Exiting...");
                    break;
                } else if (choice == 1) {
                    displayProducts();
                } else if (choice == 2) {
                    displayCustomers();
                } else {
                    System.out.println("Invalid selection. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static sqlConnectionInfo getSqlConnectionInfoFromArgs(String[] args) {
        return new sqlConnectionInfo(args[2], args[0], args[1]);
    }

    public static void displayProducts() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery("SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products")) {

            //Option 1
            while (results.next()) {
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
    }

    public static void displayCustomers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                sqlConnectionInfo.getConnectionString(),
                sqlConnectionInfo.getUsername(),
                sqlConnectionInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country")) {

            while (results.next()) {
                System.out.println("Contact Name: " + results.getString("ContactName"));
                System.out.println("Company Name: " + results.getString("CompanyName"));
                System.out.println("City: " + results.getString("City"));
                System.out.println("Country: " + results.getString("Country"));
                System.out.println("Phone: " + results.getString("Phone"));
                System.out.println("------------------");
            }
        }
    }
}