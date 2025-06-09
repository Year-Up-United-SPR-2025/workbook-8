package com.pluralsight;


import java.sql.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String connectionString = "jdbc:mysql://localhost:3306/world";
        String username = "user_1";
        String password = "password1234";


        // load the MySQL Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
// 1. open a connection to the database
// use the database URL to point to the correct database
        Connection connection;
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/world",
                username,
                password);
// create statement
// the statement is tied to the open connection
        Statement statement = connection.createStatement();
// define your query
        String query = "SELECT Name FROM city " +
                "WHERE CountryCode = 'USA'";
// 2. Execute your query
        ResultSet results = statement.executeQuery(query);
// process the results
        while (results.next()) {
            String city = results.getString("Name");
            System.out.println(city);
        }
// 3. Close the connection
        connection.close();

    }
}
