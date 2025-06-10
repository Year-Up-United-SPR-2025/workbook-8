package com.pluralsight;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import javax.sql.DataSource;

public class Main {

    private static sqlConnectionInfo sqlConnectionInfo;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println(
                    "Application needs three arguments to run: " +
                            "java com.pluralsight.Main <username> <password> <sqlUrl>");
            System.exit(1);
        }

        sqlConnectionInfo = getSqlConnectionInfoFromArgs(args);

        try {
            displayCities(103);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static sqlConnectionInfo getSqlConnectionInfoFromArgs(String[] args) {
        // get the username and password from the command line args
        String username = args[0];
        String password = args[1];

        String connectionString = args[2];

        return new sqlConnectionInfo(connectionString, username, password);
    }


    public static void displayCities(int countryId) throws SQLException {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet results = null;

        try {

            // load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
// 1. open a connection to the database
// use the database URL to point to the correct database


            connection = DriverManager.getConnection(
                    sqlConnectionInfo.getConnectionString(),
                    sqlConnectionInfo.getUsername(),
                    sqlConnectionInfo.getPassword());


            // define your query
            String query = "SELECT city FROM city " +
                    "WHERE country_id = ?";

            // create statement
// the statement is tied to the open connection

            ps = connection.prepareStatement(query);
            ps.setInt(1, countryId);


// 2. Execute your query
            results = ps.executeQuery();

// process the results
            while (results.next()) {
                String city = results.getString("city");
                System.out.println(city);
            }
// 3. Close the connection


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (results != null) results.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();


        }
    }
}