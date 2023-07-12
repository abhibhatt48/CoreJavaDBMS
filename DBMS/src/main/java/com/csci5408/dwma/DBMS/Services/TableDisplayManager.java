package com.csci5408.dwma.DBMS.Services;

import java.util.List;

public class TableDisplayManager {
    /**
     * Prints the list data in the tabular format in console
     * @param list - list of data
     */
    public void printTable(List<List<String>> list){
        if (list == null || list.isEmpty()) {
            System.out.println("No data to display");
            return;
        }

        int[] maxLengths = new int[list.get(0).size()];
        for (List<String> row : list) {
            for (int i = 0; i < row.size(); i++) {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        printDivider(maxLengths);
        printRow(list.get(0), maxLengths);
        printDivider(maxLengths);

        for (int i = 1; i < list.size(); i++) {
            printRow(list.get(i), maxLengths);
        }

        printDivider(maxLengths);
    }

    private void printDivider(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            System.out.print(new String(new char[width + 2]).replace("\0", "-"));
        }
        System.out.println("+");
    }

    private void printRow(List<String> row, int[] columnWidths) {
        for (int i = 0; i < row.size(); i++) {
            System.out.format("| %" + columnWidths[i] + "s ", row.get(i));
        }
        System.out.println("|");
    }
}
