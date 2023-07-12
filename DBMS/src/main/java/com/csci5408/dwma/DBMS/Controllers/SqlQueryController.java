package com.csci5408.dwma.DBMS.Controllers;

import java.util.Scanner;

import com.csci5408.dwma.DBMS.Services.QueryProcessor;
import com.csci5408.dwma.DBMS.Services.TransactionManager;

public class SqlQueryController {

	public static void handleQuery() {
        Scanner scanner = new Scanner(System.in);
        String databaseFilePath = "./database";
        TransactionManager transactionManager = new TransactionManager(databaseFilePath);
        QueryProcessor q = new QueryProcessor();
        
        q.setTransactionManager(transactionManager);

        // Providing instructions to the user
        System.out.println("Enter your queries below. Enter 'EXIT' to quit the query interface.");

        while (true) {
            System.out.print("> "); // Prompt for input
            String line = scanner.nextLine().trim();

            // Check if the user input is not empty
            if (line.isEmpty()) {
                System.out.println("The input can't be empty. Please enter a valid query or 'EXIT' to quit.");
                continue;
            }

            // Check if the user wants to exit
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the query interface...");
                break;
            }

            // Parse and execute the query
            try {
                q.executeQuery(line);
            } catch (Exception e) {
                System.out.println("Error while processing the query: " + e.getMessage());
            }
        }
    }
}
