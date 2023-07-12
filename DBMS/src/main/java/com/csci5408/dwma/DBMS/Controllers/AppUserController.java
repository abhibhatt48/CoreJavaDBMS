package com.csci5408.dwma.DBMS.Controllers;

import java.io.IOException;
import java.util.Scanner;

import com.csci5408.dwma.DBMS.Interface.UserAuthenticationInterface;
import com.csci5408.dwma.DBMS.Models.AppUser;
import com.csci5408.dwma.DBMS.Services.UserAuthentication;

public class AppUserController {

	/**
     * Initiates the user registration process by asking for user input.
     * After successful registration, it redirects the user to login.
     */
    public void handleRegistration() {
        AppUser appUser = new AppUser();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the registration process.");
        System.out.println("Please enter your preferred user ID:");
        appUser.setId(scanner.nextLine());
        try {
            boolean userExist = appUser.checkUserExist();
            while (userExist) {
                System.out.println("The user ID you entered is already in use. Please enter a unique user ID:");
                appUser.setId(scanner.nextLine());
                userExist = appUser.checkUserExist();
            }
            System.out.println("Enter your desired password:");
            appUser.setPassword(scanner.nextLine());
            System.out.println("Enter a security question that will be used for password recovery:");
            appUser.setSecurityQuestion(scanner.nextLine());
            System.out.println("Enter the answer to your security question:");
            appUser.setSecurityQuestionAnswer(scanner.nextLine());
            System.out.println(appUser.createUser());
            System.out.println("Registration successful! Please proceed to login.");
            UserLoginController loginController = new UserLoginController();
            UserAuthenticationInterface authentication = new UserAuthentication();
            SqlQueryController queryController = new SqlQueryController();
            loginController.handleLogin(authentication,queryController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
