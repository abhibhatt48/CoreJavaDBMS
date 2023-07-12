package com.csci5408.dwma.DBMS.Services;

import java.util.Scanner;

public class UserInteractionOptions {
    /**
     * Prompts the user to select an option from the following: Login, Register, Exit.
     * It keeps asking until a correct option is selected.
     * @return int - Code for the option selected by the user.
     */
    public int getSelectedOption() {
        Scanner inputScanner = new Scanner(System.in);
        int selectedOption = -1;
        while (selectedOption > 3 || selectedOption < 1) {
            System.out.println("Welcome to our application. Please choose from the following options:");
            System.out.println("1: Login - If you have already registered and want to use the application.");
            System.out.println("2: Register - If you are new and want to create an account.");
            System.out.println("3: Exit - If you want to close the application.");
            System.out.println("Enter the number corresponding to your choice:");
            selectedOption = inputScanner.nextInt();
            if (selectedOption > 3 || selectedOption < 1) {
                System.out.println("You've selected an invalid option. Please enter a number between 1 and 3.");
            }
        }
        return selectedOption;
    }
}