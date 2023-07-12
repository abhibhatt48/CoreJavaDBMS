package com.csci5408.dwma.DBMS.Services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UserCredentials {
	/**
     * Reads the information related to all registered users in the system.
     * @return HashMap - A key-value pair collection of every user's details.
     */
    public HashMap<String, String> getAllRegisteredUsers() {
        try {
            HashMap<String, String> registeredUsers = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader("AppUser.txt"));
            String line = reader.readLine();
            while (line != null) {
                registeredUsers.put(line.split("\\|")[0], line.split("\\|")[1]+"$$"+line.split("\\|")[2]);
                line = reader.readLine();
            }
            reader.close();
            return registeredUsers;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("User data file not found: ", e);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading user data file: ", e);
        }
    }
}
