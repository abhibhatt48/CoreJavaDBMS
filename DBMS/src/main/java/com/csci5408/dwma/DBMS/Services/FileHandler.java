package com.csci5408.dwma.DBMS.Services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {

	/**
     * Constructs the file path for a specific database table.
     * @param fileName - Name of the database table.
     * @return String - The generated file path for the table.
     */
    public String getFilePath(String fileName) {
        // Check if fileName is null or empty
        if(fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        return "./database/" + fileName + ".table";
    }

    /**
     * Checks whether a given file exists or not.
     * @param filePath - The file path to check.
     * @return boolean - Returns true if the file exists, otherwise returns false and prints an error message.
     */
    public boolean checkIfFileExist(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            System.out.println("The specified table does not exist in the database.");
            return false;
        }
    }

    /**
     * Creates a new file if it doesn't exist.
     * @param filePath - The file path to check existence for or create a new one.
     */
    public void createFileIfNotExist(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if(fileDoesntExist(filePath)) {
            File file = new File(filePath);
            try {
                if(file.createNewFile()) {
                    System.out.println("File successfully created!");
                }
            } catch (IOException e) {
                System.out.println("Unable to create a new table file due to an IO exception.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checks if a file doesn't exist at the specified path.
     * @param filePath - The file path to check.
     * @return boolean - Returns false if the file exists, otherwise returns true.
     */
    public boolean fileDoesntExist(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        File file = new File(filePath);
        return !file.exists();
    }

    /**
     * Reads each row of a file.
     * @param filePath - The path of the file to read from.
     * @return List<String> - A list containing each row of the file.
     */
    public List<String> readFileRows(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            List<String> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line);
            }
            reader.close();
            return rows;
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Unable to read the file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("An IO exception occurred while trying to read the file");
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a list of rows to a file.
     * @param filePath - The path of the file to write to.
     * @param rows - The data in list format to be written to the file.
     */
    public void writeListToFile(String filePath, List<String> rows) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        // Check if rows is null or empty
        if(rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Rows cannot be null or empty");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (String updatedLine : rows) {
                writer.write(updatedLine + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An IO exception occurred while trying to write to the file");
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the names of columns in a table.
     * @param filePath - The path of the file representing the table.
     * @return List<String> - A list containing the column names.
     */
    public Map<String, Map<String, String>> getFileColumnNames(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            if(line != null) {
                String[] columnHead = line.split("\\|");
                Map<String, Map<String, String>> columnInformation = new HashMap<>();
                for (String name : columnHead) {
                    String[] nameParts = name.split("\\$\\$");
                    Map<String, String> attributes = new HashMap<>();
                    attributes.put("type", nameParts[1]);
                    if(nameParts.length > 2) {
                        if(nameParts[2].equals("PK")) {
                            attributes.put("PK", "true");
                        }
                        if(nameParts[3].startsWith("FK")) {
                            attributes.put("FK", nameParts[3].split(":")[1]); // assuming FKs are stored as FK:otherTable(otherColumn)
                        }
                    }
                    columnInformation.put(nameParts[0], attributes);
                }
                br.close();
                return columnInformation;
            } else {
                br.close();
                throw new RuntimeException("File is empty. No column names to read");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Unable to read the file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("An IO exception occurred while trying to read the file");
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the keys of columns in a table.
     * @param filePath - The path of the file representing the table.
     * @return List<String> - A list containing the column keys.
     */
    public List<String> getFileColumnKeys(String filePath) {
        // Check if filePath is null or empty
        if(filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            if(line != null) {
                String[] columnHead = line.split("\\|");
                List<String> columnKeys = new ArrayList<>();
                for (String name : columnHead) {
                    columnKeys.add(name.split("\\$\\$")[2]); // we fetch the third element which should be the keys
                }
                br.close();
                return columnKeys;
            } else {
                br.close();
                throw new RuntimeException("File is empty. No column keys to read");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Unable to read the file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("An IO exception occurred while trying to read the file");
            throw new RuntimeException(e);
        }
    }

}
