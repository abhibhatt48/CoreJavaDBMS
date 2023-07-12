package com.csci5408.dwma.DBMS.Controllers;

import java.util.Scanner;

import com.csci5408.dwma.DBMS.Interface.UserAuthenticationInterface;

public class UserLoginController {

	private static final int MAX_ATTEMPTS = 3;  // Maximum failed attempts before locking the system

    public static void handleLogin(UserAuthenticationInterface authentication, SqlQueryController queryController) {
        int attempts = 0;
        boolean userAuthenticated = false;
        Scanner scanner = new Scanner(System.in);

        while (!userAuthenticated && attempts < MAX_ATTEMPTS) {
            System.out.println("Enter the userID: ");
            String userID = scanner.nextLine();
            System.out.println("Enter the password: ");
            String password = scanner.nextLine();

            // Option to exit the application
            if ("exit".equalsIgnoreCase(userID) || "exit".equalsIgnoreCase(password)) {
                System.out.println("Exiting the application...");
                System.exit(0);
            }

            // Check if input is valid
            if (isInputValid(userID, password)) {
                // Attempt to authenticate user
                if (authentication.authenticateUser(userID, password)) {
                    System.out.println("Welcome to the DBMS");
                    userAuthenticated = true;
                } else {
                    System.out.println("Invalid credentials! Please try again.");
                    attempts++;
                    if(attempts == MAX_ATTEMPTS) {
                        System.out.println("Too many failed attempts. Please try again later.");
                        System.exit(0);
                    }
                }
            } else {
                System.out.println("Invalid input! UserID and password cannot be empty. Try again.");
            }
        }
        if (userAuthenticated) {
            queryController.handleQuery();
        }
    }

    private static boolean isInputValid(String userID, String password) {
        return userID != null && !userID.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }
}
