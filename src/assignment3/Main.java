package assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class Main {
             private static final String FILE_NAME = "users.txt";

    public static void main(String[] args) {
         Scanner input = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== Password Authentication System =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine();

            switch (choice) {

                case "1":
                    registerUser(input);
                    break;

                case "2":
                    loginUser(input);
                    break;

                case "3":
                    System.out.println("Program terminated.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
     private static void registerUser(Scanner input) {

        try {

            System.out.print("Enter username: ");
            String username = input.nextLine();

            if (userExists(username)) {
                System.out.println("Username already exists.");
                return;
            }

            System.out.print("Enter password: ");
            String password = input.nextLine();

            // Generate random salt
            byte[] saltBytes = new byte[16];

            SecureRandom random = new SecureRandom();
            random.nextBytes(saltBytes);

            String salt =
                    Base64.getEncoder().encodeToString(saltBytes);

            // Hash password + salt
            String hashedPassword =
                    hashPassword(password, salt);

            // Store username, salt and hash
            try (FileWriter writer =
                         new FileWriter(FILE_NAME, true)) {

                writer.write(
                        username + "," +
                        salt + "," +
                        hashedPassword +
                        System.lineSeparator()
                );
            }

            System.out.println("\nRegistration successful.");

            System.out.println("Username: " + username);
            System.out.println("Generated Salt: " + salt);
            System.out.println("Hashed Password: " + hashedPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Login existing user
    private static void loginUser(Scanner input) {

        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No registered users found.");
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] userData = line.split(",");

                if (userData.length == 3) {

                    String storedUsername = userData[0];
                    String storedSalt = userData[1];
                    String storedHash = userData[2];

                    if (storedUsername.equals(username)) {

                        String newHash =
                                hashPassword(password, storedSalt);

                        if (newHash.equals(storedHash)) {

                            System.out.println(
                                    "\nLogin successful."
                            );

                        } else {

                            System.out.println(
                                    "\nInvalid password."
                            );
                        }

                        return;
                    }
                }
            }

            System.out.println("\nUser not found.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SHA-256(password + salt)
    private static String hashPassword(
            String password,
            String salt) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        String passwordWithSalt =
                password + salt;

        byte[] hash =
                digest.digest(
                        passwordWithSalt.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Base64.getEncoder()
                .encodeToString(hash);
    }

    // Check if username already exists
    private static boolean userExists(
            String username) throws IOException {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] userData = line.split(",");

                if (userData.length == 3 &&
                    userData[0].equals(username)) {

                    return true;
                }
            }
        }

        return false;
    }
}
