package com.csci5408.dwma.DBMS.Interface;

public interface UserAuthenticationInterface {
	 /**
     * @param username - User's login username
     * @param userPassword - User's login password
     * @return boolean - true if user is authenticated, false otherwise
     */
    boolean authenticateUser(String username, String userPassword);
}
