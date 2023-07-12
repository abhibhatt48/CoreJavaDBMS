package com.csci5408.dwma.DBMS.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProcessor {
	
	private ERDGenerator erdGenerator;
	private TransactionManager transactionManager;
	
	public QueryProcessor() {
        this.erdGenerator = new ERDGenerator();
        //this.transactionManager = new TransactionManager();
    }
	
	/**
     * Set the TransactionManager object.
     *
     * @param transactionManager the TransactionManager object to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
	/**
     * This method will parse user queries and route them to the corresponding operation.
     *
     * @param userQuery the SQL query input by the user
     */
    public void executeQuery(String userQuery) {
        String queryOperation = userQuery.trim().split(" ")[0].toUpperCase(Locale.ROOT);

        switch (queryOperation) {
            case "CREATE":
                String creationResponse = createTable(userQuery);
                System.out.println(Objects.requireNonNullElse(creationResponse, "Table already exists."));
                break;
            case "SELECT":
                List<List<String>> queryData = fetchData(userQuery);
                if (queryData != null) {
                    TableDisplayManager tableDisplayManager = new TableDisplayManager();
                    tableDisplayManager.printTable(queryData);
                }
                break;
            case "INSERT":
                String insertionResponse = insertData(userQuery);
                System.out.println(Objects.requireNonNullElse(insertionResponse, "Unable to insert data."));
                break;
            case "UPDATE":
                System.out.println(updateData(userQuery));
                break;
            case "DELETE":
                System.out.println(deleteData(userQuery));
                break;
            case "GENERATEERD":
                erdGenerator.generateERD();
                break;
            case "BEGIN":
                transactionManager.beginTransaction();
                break;
            case "END":
                transactionManager.endTransaction();
                break;
            case "COMMIT":
                transactionManager.commitTransaction();
                break;
            case "ROLLBACK":
                transactionManager.rollbackTransaction();
                break;
            default:
                if (transactionManager.isInTransaction()) {
                    transactionManager.addQuery(userQuery);
                } else {
                    executeQuery(userQuery);
                }
                break;
        }
    }
    
    /**
     * This method creates a new table in the database.
     *
     * @param createQuery the CREATE TABLE query passed by the user
     * @return a success message or an error message
     */
    public String createTable(String query) {
        String tableName = query.trim().split(" ")[2];
        HashMap<String, String> attributesWithTypes = new HashMap<String, String>();
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(query);
        String primaryKey = null;
        HashMap<String, String> foreignKeys = new HashMap<>();
        if (matcher.find()) {
            String columnsString = matcher.group(1);
            String[] columnNames = columnsString.split(",");
            for (String columnName : columnNames) {
            	String[] columnParts = columnName.trim().split(" ");
                if (columnParts[0].equalsIgnoreCase("PRIMARY")) {
                    primaryKey = columnParts[2].replaceAll("\\(|\\)", ""); // remove parentheses
                } else if (columnParts[0].equalsIgnoreCase("FOREIGN")) {
                    String fkColumn = columnParts[2].replaceAll("\\(|\\)", ""); // remove parentheses
                    String refTableAndColumn = columnParts[4] + columnParts[5];
                    foreignKeys.put(fkColumn, refTableAndColumn);
                } else {
                    String columnType = columnParts.length > 2 ? columnParts[1] + " " + columnParts[2] : columnParts[1];
                    attributesWithTypes.put(columnParts[0], columnType);
                }
            }
        }
        FileHandler fileHandler = new FileHandler();
        try {
            String filePath = fileHandler.getFilePath(tableName);
            File folder = new File("./database/");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (fileHandler.fileDoesntExist(filePath)) {
                FileWriter fw = new FileWriter(filePath);
                String tableInfo = "";
                for (String i : attributesWithTypes.keySet()) {
                    tableInfo = tableInfo + i + "$$" + attributesWithTypes.get(i) + "|";
                }

                fw.write(tableInfo + "\n");
                fw.close();
                return  "Created " + tableName;
            }

        } catch (IOException e) {
            return "Something went wrong";
        }
        return null;
    }
    
    /**
     * This method retrieves data from the table based on the user query.
     *
     * @param selectQuery the SELECT query entered by the user
     * @return a list of rows that matches the select condition
     */
    public List<List<String>> fetchData(String selectQuery) {
        Pattern pattern = Pattern.compile("SELECT\\s(.+?)FROM\\s(.+?);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        matcher.find();
        String[] columnNames = matcher.group(1).split(",");
        String tableName = matcher.group(2);
        FileHandler fileHandler = new FileHandler();
        String filePath = fileHandler.getFilePath(tableName);
        if (fileHandler.checkIfFileExist(filePath)) {

        	Map<String, Map<String, String>> columnInformationMap = fileHandler.getFileColumnNames(filePath);
            Map<String,String> columnTypes = new HashMap<>();
            for (Map.Entry<String, Map<String, String>> column : columnInformationMap.entrySet()) {
                columnTypes.put(column.getKey(), column.getValue().get("type"));
            }
            List<String> columnInformation = new ArrayList<>(columnInformationMap.keySet());
            List<String> rows = fileHandler.readFileRows(filePath);
            List<List<String>> columnData = new ArrayList<List<String>>();
            if(columnNames[0].trim().equals("*")){
                columnData.add(columnInformation);
                for(int i = 1 ; i < rows.size(); i++){
                    columnData.add(List.of(rows.get(i).split("\\|")));
                }
                return columnData;
            }
            else{
                List<Integer> columnsSelected = new ArrayList<Integer>();
                for(String columnName: columnNames){
                    if(columnInformation.contains(columnName.trim())){
                        columnsSelected.add(columnInformation.indexOf(columnName.trim()));
                    }
                }
                List<String> head = new ArrayList<>();
                for(int i = 0; i < columnsSelected.size() ; i++){
                    head.add(columnInformation.get(columnsSelected.get(i)));
                }
                columnData.add(head);

                for(int i = 1 ; i < rows.size(); i++){
                    List<String> data = new ArrayList<>();
                    String[] rowColumns = rows.get(i).split("\\|");
                    for(int j = 0; j < columnsSelected.size() ; j++){
                        data.add(rowColumns[columnsSelected.get(j)]);
                    }
                    columnData.add(data);
                }
                return columnData;
            }
        }
        return null;
    }

    
    /**
     * This method deletes a row from the table based on a condition in the WHERE clause.
     *
     * @param deleteQuery the DELETE query enter by the user
     * @return a success message or an error message
     */
    
    public String deleteData(String deleteQuery) {
        Pattern pattern = Pattern.compile("DELETE FROM\\s(.+?)WHERE\\s(.+?);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(deleteQuery);
        matcher.find();
        String tableName = matcher.group(1).trim();
        String whereClause = matcher.group(2);
        String whereClauseField = whereClause.split("=")[0].trim();
        String whereClauseValue = whereClause.split("=")[1].trim();
        FileHandler fileHandler = new FileHandler();
        String filePath = fileHandler.getFilePath(tableName);

        if (fileHandler.checkIfFileExist(filePath)) {
            List<String> rows = fileHandler.readFileRows(filePath);
            List<String> columnNames = new ArrayList<String>();
            String[] columnData = rows.get(0).split("\\|");
            for (String column : columnData) {
                columnNames.add(column.split("\\$\\$")[0]);
            }
            int indexOfColumnForWhereClause = columnNames.indexOf(whereClauseField);
            if (indexOfColumnForWhereClause == -1) {
                return "Specified column name is not valid";
            }
            int matchedRow = -1;
            for (int i = 1; i < rows.size(); i++) {
                String row = rows.get(i);
                String dataOnSelectedWhereClauseColumn = row.split("\\|")[indexOfColumnForWhereClause];
                if (dataOnSelectedWhereClauseColumn.equals(whereClauseValue)) {
                    matchedRow = i;
                }
            }
            if (matchedRow >= 1) {
                rows.remove(matchedRow);
                fileHandler.writeListToFile(filePath, rows);
                return "Deleted the row!";
            }
        }

        return null;
    }

    /**
     * This method inserts data into the table.
     *
     * @param insertQuery the INSERT query from the user
     * @return a success message if the data was inserted successfully
     */
    public String insertData(String insertQuery) {
        String tableName = insertQuery.trim().split(" ")[2];
        FileHandler fileHandler = new FileHandler();
        String filePath = fileHandler.getFilePath(tableName);


        if (fileHandler.checkIfFileExist(filePath)) {
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(insertQuery);
            HashMap<String, String> columnValues = new HashMap<String, String>();

            matcher.find();
            String columnsNames = matcher.group(1);
            matcher.find();
            String values = matcher.group(1);
            String[] columnNamesArray = columnsNames.split(",");
            String[] valuesArray = values.split(",");
            for (int i = 0; i < columnNamesArray.length; i++) {
                columnValues.put(columnNamesArray[i].trim(), valuesArray[i].trim());
            }

            try {
                BufferedReader brTest = new BufferedReader(new FileReader(filePath));
                String[] tableColumnInfo = brTest.readLine().split("\\|");
                List<String> columnOrder = new ArrayList<String>();
                HashMap<String, String> columnData = new HashMap<String, String>();
                for (String columnInfo : tableColumnInfo) {
                    columnOrder.add(columnInfo.split("\\$\\$")[0]);
                    columnData.put(columnInfo.split("\\$\\$")[0], columnInfo.split("\\$\\$")[1]);
                }
                String row = "";
                for (int i = 0; i < columnOrder.size(); i++) {
                    String columnName = columnOrder.get(i);
                    String value = columnValues.get(columnName);
                    if (value == null) {
                        row += "|";
                    } else {
                        row += value + "|";

                    }
                }
                FileWriter fw = new FileWriter(filePath, true);
                fw.write(row + '\n');
                fw.close();
                return  "Data inserted successfully.";

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * This method updates data in the table.
     *
     * @param updateQuery the UPDATE query from the user
     * @return a success message if the data was updated successfully
     */
    public String updateData(String updateQuery) {
        Pattern pattern = Pattern.compile("UPDATE\\s(.+?)SET\\s(.+?)WHERE (.+?);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(updateQuery);
        matcher.find();
        String tableName = matcher.group(1).trim();
        String[] updateParams = matcher.group(2).split(",");

        String whereClause = matcher.group(3);
        String whereClauseField = whereClause.split("=")[0].trim();
        String whereClauseValue = whereClause.split("=")[1].trim();
        FileHandler fileHandler = new FileHandler();
        String filePath = fileHandler.getFilePath(tableName);
        if (fileHandler.checkIfFileExist(filePath)) {
            List<String> rows = fileHandler.readFileRows(filePath);
            List<String> columnNames = new ArrayList<String>();
            String[] columnData = rows.get(0).split("\\|");
            for (String column : columnData) {
                columnNames.add(column.split("\\$\\$")[0]);
            }
            int indexOfColumnForWhereClause = columnNames.indexOf(whereClauseField);
            if (indexOfColumnForWhereClause == -1) {
                return "Specified column name is not valid";
            }
            int matchedRow = -1;
            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                String dataOnSelectedWhereClauseColumn = row.split("\\|")[indexOfColumnForWhereClause];
                if (dataOnSelectedWhereClauseColumn.equals(whereClauseValue)) {
                    matchedRow = i;
                }
            }
            if (matchedRow >= 0) {

                List<String> updateParamsColumnNames = new ArrayList<String>();
                List<String> updateValues = new ArrayList<String>();
                for (String updateParam : updateParams) {
                    updateParamsColumnNames.add(updateParam.split("=")[0].trim());
                    updateValues.add(updateParam.split("=")[1].trim());

                }
                List<Integer> columnIndexForUpdate = new ArrayList<Integer>();
                for (String updateColumns : updateParamsColumnNames) {
                    columnIndexForUpdate.add(columnNames.indexOf(updateColumns));
                }
                String rowToBeUpdated = rows.get(matchedRow);
                String[] rowData = rowToBeUpdated.split("\\|");
                for (int i = 0; i < columnIndexForUpdate.size(); i++) {
                    if(columnIndexForUpdate.get(i)<0){
                        return "Column name dosen't exist";
                    }
                    rowData[columnIndexForUpdate.get(i)] = updateValues.get(i);
                }
                String updatedRow = String.join("|", rowData);
                rows.set(matchedRow, updatedRow);
                fileHandler.writeListToFile(filePath,rows);
                return "Update the row successfully!";
            }

        }
        return null;
    }
}
