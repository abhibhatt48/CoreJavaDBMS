package com.csci5408.dwma.DBMS.Services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;

public class ERDGenerator {

    public void generateERD() {
        try {
            File databaseFolder = new File("./database/");
            if (databaseFolder.exists() && databaseFolder.isDirectory()) {
                File[] tableFiles = databaseFolder.listFiles();
                if (tableFiles != null && tableFiles.length > 0) {
                    FileWriter erdWriter = new FileWriter("./erd.txt");
                    for (File tableFile : tableFiles) {
                        if (tableFile.isFile()) {
                            String tableName = tableFile.getName().replace(".table", "");
                            erdWriter.write("Table: " + tableName + "\n");
                            Map<String, Map<String, String>> columnDetails = new FileHandler().getFileColumnNames(tableFile.getPath());
                            erdWriter.write("+-------------------------+\n");
                            for (Map.Entry<String, Map<String, String>> column : columnDetails.entrySet()) {
                                String columnName = column.getKey();
                                Map<String, String> columnProperties = column.getValue();
                                String columnType = columnProperties.get("type");
                                erdWriter.write("| " + columnName + " (" + columnType + ")");
                                if(columnProperties.get("PK") != null) {
                                    erdWriter.write(" PK");
                                }
                                if(columnProperties.get("FK") != null) {
                                    erdWriter.write(" FK REFERENCES " + columnProperties.get("FK"));
                                }
                                erdWriter.write(" |\n");
                            }
                            erdWriter.write("+-------------------------+\n\n");
                        }
                    }
                    erdWriter.close();
                    System.out.println("ERD (Entity-Relationship Diagram) generated successfully.");
                } else {
                    System.out.println("No tables found in the database.");
                }
            } else {
                System.out.println("Database folder does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while generating the ERD.");
            throw new RuntimeException(e);
        }
    }
}
