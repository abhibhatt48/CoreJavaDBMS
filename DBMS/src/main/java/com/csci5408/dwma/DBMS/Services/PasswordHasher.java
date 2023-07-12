package com.csci5408.dwma.DBMS.Services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    /**
     * Generates the hash of a password using MD5.
     * @param password - The plain text password.
     * @return String - The hashed password.
     * @throws NoSuchAlgorithmException - If the MD5 algorithm is not found in the environment.
     */
    public String generateHash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no.toString(16);
    }
}
