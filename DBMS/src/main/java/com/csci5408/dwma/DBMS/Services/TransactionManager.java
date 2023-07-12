package com.csci5408.dwma.DBMS.Services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private boolean inTransaction;
    private List<String> transactionQueries;
    private List<String> backupQueries;
    private String databaseFilePath;
    private String databaseBackupPath;

    
    public TransactionManager(String databaseFilePath) {
        this.inTransaction = false;
        this.transactionQueries = new ArrayList<>();
        this.backupQueries = new ArrayList<>();
        this.databaseFilePath = databaseFilePath;
        this.databaseBackupPath = databaseFilePath + ".bak";
    }

    private String getBackupFilePath(String filePath) {
        String fileName = Path.of(filePath).getFileName().toString();
        return "./database/" + fileName + ".bak";
    }
    /**
     * Begins a new transaction.
     */
    public void beginTransaction() {
        if (!inTransaction) {
            inTransaction = true;
            transactionQueries.clear();
            backupQueries.clear();
            createBackup(databaseFilePath); // Create backup file
            System.out.println("Transaction started.");
        } else {
            System.out.println("Transaction already active.");
        }
    }
    
        private void createBackup(String filePath) {
            try {
                // Create a backup of the database file before making any changes
                String backupFilePath = getBackupFilePath(filePath);
                Files.copy(Path.of(filePath), Path.of(backupFilePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Failed to create backup: " + e.getMessage());
            }
        }
    /**
     * Ends the current transaction.
     */
    public void endTransaction() {
        if (inTransaction) {
            inTransaction = false;
            transactionQueries.clear();
            backupQueries.clear();
            System.out.println("Transaction ended.");
        } else {
            System.out.println("No active transaction.");
        }
    }

    /**
     * Commits the current transaction by executing the stored queries.
     */
    public void commitTransaction() {
        if (inTransaction) {
            for (String query : transactionQueries) {
                // Execute the query on the database or update the intermediate data structure
                executeQuery(query);
            }
            transactionQueries.clear();
            backupQueries.clear();
            System.out.println("Transaction committed.");
        } else {
            System.out.println("No active transaction.");
        }
    }

    /**
     * Rolls back the current transaction by discarding the stored queries and restoring the backup queries.
     */
    public void rollbackTransaction() {
        if (inTransaction) {
            transactionQueries.clear();
            backupQueries.clear();
            restoreBackup(databaseFilePath); // Restore the backup file
            System.out.println("Transaction rolled back. Database file restored.");
        } else {
            System.out.println("No active transaction.");
        }
    }

    private void restoreBackup(String filePath) {
        try {
            String backupFilePath = getBackupFilePath(filePath);
            // Revert changes made to the database file by copying the backup file
            Files.copy(Path.of(backupFilePath), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(Path.of(backupFilePath));
        } catch (IOException e) {
            System.out.println("Failed to restore backup: " + e.getMessage());
        }
    }

    /**
     * Adds a query to the current transaction.
     *
     * @param query the query to add
     */
    public void addQuery(String query) {
        if (inTransaction) {
            transactionQueries.add(query);
        } else {
            System.out.println("No active transaction. Query ignored.");
        }
    }

    /**
     * Checks if a transaction is currently active.
     *
     * @return true if a transaction is active, false otherwise
     */
    public boolean isInTransaction() {
        return inTransaction;
    }

    /**
     * Executes a query on the database.
     *
     * @param query the query to execute
     */
    private void executeQuery(String query) {
        // Your logic to execute the query on the database or update the intermediate data structure
    	try {
            // Create a backup of the database file before making any changes
            Files.copy(Path.of(databaseFilePath), Path.of(databaseBackupPath), StandardCopyOption.REPLACE_EXISTING);

            // Execute the query and update the database file

            // For example, you can append the query to the database file
            FileWriter fw = new FileWriter(databaseFilePath, true);
            fw.write(query + '\n');
            fw.close();

        } catch (IOException e) {
            System.out.println("Failed to execute query: " + e.getMessage());
        }
    }
}