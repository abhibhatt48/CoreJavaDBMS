package com.csci5408.dwma.DBMS;


import com.csci5408.dwma.DBMS.Controllers.AppUserController;
import com.csci5408.dwma.DBMS.Controllers.SqlQueryController;
import com.csci5408.dwma.DBMS.Controllers.UserLoginController;
import com.csci5408.dwma.DBMS.Interface.UserAuthenticationInterface;
import com.csci5408.dwma.DBMS.Services.UserAuthentication;
import com.csci5408.dwma.DBMS.Services.UserInteractionOptions;


public class Main {
	public static void main(String[] args) {
        AppUserController appUserController = new AppUserController();
        UserLoginController loginController = new UserLoginController();
        UserAuthenticationInterface userAuthentication = new UserAuthentication();
        SqlQueryController userQueryController = new SqlQueryController();
        UserInteractionOptions userOptions = new UserInteractionOptions();
        int selectedOption = userOptions.getSelectedOption();

        switch (selectedOption) {
            case 1:
                loginController.handleLogin(userAuthentication, userQueryController);
                break;
            case 2:
                appUserController.handleRegistration();
                break;
            case 3:
                System.out.println("Thank-you for using our application.");
                break;
            default:
                System.out.println("Invalid selection. Please choose a correct option.");
                break;
        }
    }
}