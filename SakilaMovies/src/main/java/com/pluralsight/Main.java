package com.pluralsight;

import com.pluralsight.Color.ColorCodes;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static BasicDataSource dataSource;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(ColorCodes.BRIGHT_RED + "❌ Application needs three arguments to run:" + ColorCodes.RESET);
            System.out.println(ColorCodes.YELLOW + "   java com.pluralsight.SakilaMovies <username> <password> <database_url>" + ColorCodes.RESET);
            System.exit(1);
        }

        // Initialize the data source
        dataSource = createDataSource(args[0], args[1], args[2]);

        System.out.println(ColorCodes.BRIGHT_CYAN + ColorCodes.BOLD + "🎬 Welcome to Sakila Movies Database Explorer!" + ColorCodes.RESET);
        System.out.println(ColorCodes.BRIGHT_BLUE + "================================================" + ColorCodes.RESET);

        try (Scanner scanner = new Scanner(System.in)) {
            // Step 1: Get actors by last name
            System.out.print(ColorCodes.BRIGHT_GREEN + "\n🔍 Enter the last name of an actor you like: " + ColorCodes.RESET
            + ColorCodes.BRIGHT_GREEN + "(e.g 'STALLONE', 'CRONYN', 'TEMPLE', 'PINKETT',MIRANDA):\n" + ColorCodes.RESET);
            String lastName = scanner.nextLine().trim();

            if (lastName.isEmpty()) {
                System.out.println(ColorCodes.BRIGHT_RED + "❌ Last name cannot be empty!" + ColorCodes.RESET);
                return;
            }

            displayActorsByLastName(lastName);

            // Step 2: Get movies by actor's full name
            System.out.print(ColorCodes.BRIGHT_PURPLE + "\n🎭 Enter the first name of the actor: " + ColorCodes.RESET);
            String firstName = scanner.nextLine().trim();

            System.out.print(ColorCodes.BRIGHT_PURPLE + "🎭 Enter the last name of the actor: " + ColorCodes.RESET);
            String actorLastName = scanner.nextLine().trim();

            if (firstName.isEmpty() || actorLastName.isEmpty()) {
                System.out.println(ColorCodes.BRIGHT_RED + "❌ Both first name and last name are required!" + ColorCodes.RESET);
                return;
            }

            displayMoviesByActor(firstName, actorLastName);

        } catch (Exception e) {
            System.out.println(ColorCodes.BRIGHT_RED + "💥 An error occurred: " + e.getMessage() + ColorCodes.RESET);
            e.printStackTrace();
        } finally {
            // Close the data source when done
            if (dataSource != null) {
                try {
                    dataSource.close();
                    System.out.println(ColorCodes.BRIGHT_GREEN + "\n👋 Database connection closed. Goodbye!" + ColorCodes.RESET);
                } catch (SQLException e) {
                    System.out.println(ColorCodes.ORANGE + "⚠️  Error closing data source: " + e.getMessage() + ColorCodes.RESET);
                }
            }
        }
    }

    /**
     * Creates and configures a BasicDataSource with the provided connection details
     */
    private static BasicDataSource createDataSource(String username, String password, String url) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    /**
     * Displays all actors with the specified last name
     */
    private static void displayActorsByLastName(String lastName) {
        String sql = "SELECT actor_id, first_name, last_name FROM actor WHERE last_name = ? ORDER BY first_name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                boolean foundActors = false;

                System.out.println(ColorCodes.GOLD + ColorCodes.BOLD + "\n🌟 Actors with last name '" + lastName + "':" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_BLUE + "----------------------------------------" + ColorCodes.RESET);

                while (rs.next()) {
                    foundActors = true;
                    int actorId = rs.getInt("actor_id");
                    String firstName = rs.getString("first_name");
                    String actorLastName = rs.getString("last_name");

                    System.out.printf(ColorCodes.CYAN + "ID: " + ColorCodes.BRIGHT_WHITE + "%-3d" + ColorCodes.CYAN + " | " +
                                    ColorCodes.BRIGHT_YELLOW + "%s %s" + ColorCodes.RESET + "%n",
                            actorId, firstName, actorLastName);
                }

                if (!foundActors) {
                    System.out.println(ColorCodes.BRIGHT_RED + "❌ No actors found with last name '" + lastName + "'" + ColorCodes.RESET);
                    System.out.println(ColorCodes.YELLOW + "   Please try a different last name (e.g., 'Johansson', 'Wahlberg', 'Davis')" + ColorCodes.RESET);
                }
            }

        } catch (SQLException e) {
            System.out.println(ColorCodes.BRIGHT_RED + "💥 Database error while searching for actors: " + e.getMessage() + ColorCodes.RESET);
            e.printStackTrace();
        }
    }

    /**
     * Displays all movies featuring the specified actor
     */
    private static void displayMoviesByActor(String firstName, String lastName) {
        String sql = """
            SELECT DISTINCT f.film_id, f.title, f.description, f.release_year, f.length
            FROM film f
            JOIN film_actor fa ON f.film_id = fa.film_id
            JOIN actor a ON fa.actor_id = a.actor_id
            WHERE a.first_name = ? AND a.last_name = ?
            ORDER BY f.title
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                boolean foundMovies = false;

                System.out.println(ColorCodes.BRIGHT_CYAN + ColorCodes.BOLD + "\n🎬 Movies starring " + firstName + " " + lastName + ":" + ColorCodes.RESET);
                System.out.println(ColorCodes.BRIGHT_BLUE + "=".repeat(50) + ColorCodes.RESET);

                while (rs.next()) {
                    foundMovies = true;
                    int filmId = rs.getInt("film_id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    int releaseYear = rs.getInt("release_year");
                    int length = rs.getInt("length");

                    System.out.println(ColorCodes.BRIGHT_GREEN + "\n🎥 " + ColorCodes.BRIGHT_WHITE + ColorCodes.BOLD + title +
                            ColorCodes.RESET + ColorCodes.BRIGHT_YELLOW + " (" + releaseYear + ")" + ColorCodes.RESET);
                    System.out.println(ColorCodes.CYAN + "   ID: " + ColorCodes.SNOW + filmId +
                            ColorCodes.CYAN + " | Length: " + ColorCodes.SNOW + length + " minutes" + ColorCodes.RESET);
                    System.out.println(ColorCodes.LAVENDER + "   Description: " + ColorCodes.BRIGHT_WHITE + description + ColorCodes.RESET);
                    System.out.println(ColorCodes.BRIGHT_BLUE + "-".repeat(50) + ColorCodes.RESET);
                }

                if (!foundMovies) {
                    System.out.println(ColorCodes.BRIGHT_RED + "❌ No movies found for actor '" + firstName + " " + lastName + "'" + ColorCodes.RESET);
                    System.out.println(ColorCodes.YELLOW + "   Please check the spelling or try a different actor name." + ColorCodes.RESET);
                    System.out.println(ColorCodes.BRIGHT_CYAN + "   Tip: Search by last name first to see available actors!" + ColorCodes.RESET);
                }
            }

        } catch (SQLException e) {
            System.out.println(ColorCodes.BRIGHT_RED + "💥 Database error while searching for movies: " + e.getMessage() + ColorCodes.RESET);
            e.printStackTrace();
        }
    }
}