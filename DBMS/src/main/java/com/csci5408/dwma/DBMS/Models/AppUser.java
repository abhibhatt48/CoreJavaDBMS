package com.csci5408.dwma.DBMS.Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.csci5408.dwma.DBMS.Services.FileHandler;
import com.csci5408.dwma.DBMS.Services.PasswordHasher;

public class AppUser {
	private String id;
    private String password;
    private String securityQuestion;
    private String securityQuestionAnswer;

    /**
     * Assigns the specified user ID to the user registration details.
     * @param id - The user ID to be assigned.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Assigns the specified password to the user registration details.
     * @param password - The password to be assigned.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Assigns the specified security question to the user registration details.
     * @param securityQuestion - The security question to be assigned.
     */
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    /**
     * Assigns the specified answer to the security question to the user registration details.
     * @param securityQuestionAnswer - The answer to the security question to be assigned.
     */
    public void setSecurityQuestionAnswer(String securityQuestionAnswer){
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    /**
     * Saves the user registration details to a file and creates a new user entry in the system.
     * @return String - Message indicating successful registration.
     */
    public String createUser() {
        PasswordHasher passwordHasher = new PasswordHasher();
        try {
            FileWriter fw = new FileWriter("AppUser.txt", true);
            fw.write(id + "|" + passwordHasher.generateHash(password) + "|" + securityQuestion + "&&" + securityQuestionAnswer + "\n");
            fw.close();
            return "User Registration Successful!!";
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user with the specified user ID already exists in the system.
     * @return boolean - True if the user exists, False if it doesn't.
     */
    public boolean checkUserExist() throws IOException {
        FileHandler fileHandler = new FileHandler();
        if (fileHandler.checkIfFileExist("AppUser.txt")) {
            BufferedReader br = new BufferedReader(new FileReader("AppUser.txt"));
            ArrayList<String> users = new ArrayList<String>();
            String line = br.readLine();
            while (line != null) {
                users.add(line.split("\\|")[0]);
                line = br.readLine();
            }
            br.close();
            return users.stream().anyMatch(user -> user.equals(id));
        }
        return false;
    }
}
