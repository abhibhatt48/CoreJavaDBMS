package com.csci5408.dwma.DBMS.Services;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import com.csci5408.dwma.DBMS.Interface.UserAuthenticationInterface;

public class UserAuthentication implements UserAuthenticationInterface {

	 /**
     * Authenticates the user based upon the credentials and matches the hashed passwords.
     * @param username - User's login username
     * @param userPassword - User's login password
     * @return boolean - true if user is authenticated, false otherwise
     */
    @Override
    public boolean authenticateUser(String username, String userPassword) {
        UserCredentials userCredentials = new UserCredentials();
        HashMap<String, String> users = userCredentials.getAllRegisteredUsers();
        Scanner inputScanner = new Scanner(System.in);
        String userInfo = users.get(username);
        if (userInfo == null) {
            return false;
        }
        try {
            String hashedPassword = userInfo.split("\\$\\$")[0];
            String securityQuestion=userInfo.split("\\$\\$")[1].split("&&")[0];
            String securityAnswer = userInfo.split("\\$\\$")[1].split("&&")[1];
            PasswordHasher passwordHasher = new PasswordHasher();
            boolean isPasswordCorrect = passwordHasher.generateHash(userPassword).equals(hashedPassword);
            System.out.println(securityQuestion);
            boolean isAnswerCorrect = securityAnswer.equals(inputScanner.nextLine());
            return isPasswordCorrect && isAnswerCorrect;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
