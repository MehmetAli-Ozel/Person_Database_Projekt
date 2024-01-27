package academy.mischok.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

    public final static String SERVER = "hattie.db.elephantsql.com";
    public final static String USER_AND_DATABASE = "ottuqznl";
    public final static String PASSWORD = "aMaeNqxzcR1BHuBt60HmvwRcvCCcyFfY";

    public static void main(String[] args) {
        try (Connection connection = ConnectionHelper.getConnection()) {
            performOperations(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performOperations(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Options:");
            System.out.println("L - Show all persons");
            System.out.println("A - Add a new person");
            System.out.println("E - Edit a person");
            System.out.println("D - Delete a person");
            System.out.println("Q - Quit");

            System.out.print("Select an option: ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "L":
                    showAllPersons(connection);
                    break;
                case "A":
                    addNewPerson(connection);
                    break;
                case "E":
                    editPerson(connection);
                    break;
                case "D":
                    deletePerson(connection);
                    break;
                case "Q":
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showAllPersons(Connection connection) {
        try {
            String query = "SELECT * FROM person";
            try (PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

                System.out.printf("%-5s %-20s %-20s %-5s%n", "ID", "First Name", "Last Name", "Birthday", "Email", "Country", "Salary");
                System.out.println("--------------------------------------------------");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String country = resultSet.getString("country");
                    int salary = resultSet.getInt("salary");
                    Date birthday = resultSet.getDate("birthday");

                    System.out.printf("%-5s %-20s %-20s %-5s%n", id, firstName, lastName, birthday, email, country, salary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addNewPerson(Connection connection) {
        try {
            System.out.print("Enter first name: ");
            String firstName = new Scanner(System.in).nextLine();

            System.out.print("Enter last name: ");
            String lastName = new Scanner(System.in).nextLine();

            System.out.print("Enter birthday: ");
            String birthday = new Scanner(System.in).next();

            System.out.print("Enter email: ");
            String email = new Scanner(System.in).next();

            System.out.print("Enter country: ");
            String country = new Scanner(System.in).next();

            System.out.print("Enter salary: ");
            int salary = new Scanner(System.in).nextInt();

            String query = "INSERT INTO person (first_name, last_name, birthday,email,country,salary) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setDate(3, convertToSqlDate(birthday));
                statement.setString(4, email);
                statement.setString(5, country);
                statement.setInt(6, salary);
                statement.executeUpdate();
                System.out.println("Person added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private static void editPerson(Connection connection) {
        try {
            System.out.print("Enter person ID to edit: ");
            int personId = new Scanner(System.in).nextInt();

            String query = "SELECT * FROM person WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, personId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Current details:");
                        System.out.printf("First Name: %s%n", resultSet.getString("first_name"));
                        System.out.printf("Last Name: %s%n", resultSet.getString("last_name"));
                        System.out.printf("birthday: %s%n", resultSet.getInt("birthday"));

                        System.out.print("Enter new first name: ");
                        String newFirstName = new Scanner(System.in).nextLine();

                        System.out.print("Enter new last name: ");
                        String newLastName = new Scanner(System.in).nextLine();

                        System.out.print("Enter new birthday: ");
                        String newBirthday = new Scanner(System.in).next();

                        System.out.print("Enter new email: ");
                        String email = new Scanner(System.in).next();

                        System.out.print("Enter new country: ");
                        String country = new Scanner(System.in).next();

                        System.out.print("Enter new salary: ");
                        int salary = new Scanner(System.in).nextInt();

                        String updateQuery = "UPDATE person SET first_name = ?, last_name = ?, birthday = ? , email = ?, country = ?, salary= ? WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newFirstName);
                            updateStatement.setString(2, newLastName);
                            updateStatement.setDate(3, convertToSqlDate(newBirthday));
                            updateStatement.setInt(4, personId);
                            updateStatement.setString(5, email);
                            updateStatement.setString(6, country);
                            updateStatement.setInt(7, salary);
                            updateStatement.executeUpdate();
                            System.out.println("Person updated successfully.");
                        }
                    } else {
                        System.out.println("Person with ID " + personId + " not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deletePerson(Connection connection) {
        try {
            System.out.print("Enter person ID to delete: ");
            int personId = new Scanner(System.in).nextInt();

            String query = "SELECT * FROM person WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, personId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Details:");
                        System.out.printf("First Name: %s%n", resultSet.getString("first_name"));
                        System.out.printf("Last Name: %s%n", resultSet.getString("last_name"));
                        System.out.printf("birthday: %s%n", resultSet.getDate("birthday"));
                        System.out.printf("email: %s%n", resultSet.getString("email"));
                        System.out.printf("country: %s%n", resultSet.getString("country"));
                        System.out.printf("salary: %s%n", resultSet.getInt("salary"));

                        System.out.print("Are you sure you want to delete this person? (Y/N): ");
                        String confirmation = new Scanner(System.in).nextLine().toUpperCase();
                        if (confirmation.equals("Y")) {
                            String deleteQuery = "DELETE FROM person WHERE id = ?";
                            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                                deleteStatement.setInt(1, personId);
                                deleteStatement.executeUpdate();
                                System.out.println("Person deleted successfully.");
                            }
                        } else {
                            System.out.println("Deletion canceled.");
                        }
                    } else {
                        System.out.println("Person with ID " + personId + " not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static java.sql.Date convertToSqlDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            java.util.Date utilDate = dateFormat.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}