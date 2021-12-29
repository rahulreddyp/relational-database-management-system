package sqldump;

import queries.UseDBParser;

import java.io.*;
import java.util.Scanner;

public class GenerateDump {

  public void makeQueries(String userName) {
    UseDBParser useDB = new UseDBParser();
    useDB.showDatabases(userName);
    File folder;

    do {
      System.out.println("Enter the Database Name:");
      Scanner scan = new Scanner(System.in);
      String dbName = scan.nextLine();

      folder = new File("bin/Databases/" + dbName);
      if (!folder.isDirectory()) {
        System.out.println("Invalid input, please enter valid database name");
      } else {
        break;
      }
    } while (true);

    try {
      FileWriter sqlDumpFile = new FileWriter("bin/SQLDumps/SQlDump.txt");
      sqlDumpFile.flush();
      StringBuilder sb;

      sqlDumpFile = new FileWriter("bin/SQLDumps/SQlDump.txt", true);
      File[] listOfFiles = folder.listFiles();
      if (listOfFiles != null) {
        for (File currentFile : listOfFiles) {
          sb = new StringBuilder();
          if (currentFile.isFile() && currentFile.getName().contains("_metadata")) {
            BufferedReader bReader = new BufferedReader(new FileReader(currentFile));
            String line;

            sb.append("CREATE TABLE ");
            String tableName = null;
            StringBuilder createQuery = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
              String[] words = line.split("~");
              tableName = currentFile.getName().replace("_metadata.txt", "");

              for (int i = 0; i < words.length; i++) {
                if (i != 0) {
                  createQuery.append(words[i]).append(" ");
                }
              }
              if (words.length != 3) {
                createQuery.append(", ");
              }
            }

            if (!tableName.isEmpty()) {
              sb.append(tableName);
              if (!createQuery.isEmpty()) {
                createQuery.deleteCharAt(createQuery.length() - 1);
                sb.append("( ").append(createQuery).append(");\n");
              }
            }

            for (File table : listOfFiles) {
              if (!tableName.isEmpty()) {
                if (table.isFile() && !table.getName().contains("_metadata") && table.getName().contains(tableName)) {
                  String fileLines;
                  BufferedReader tableReader = new BufferedReader(new FileReader(table));
                  StringBuilder insertQuery = new StringBuilder();
                  fileLines = tableReader.readLine();
                  while ((fileLines = tableReader.readLine()) != null) {
                    insertQuery.append("INSERT INTO ").append(tableName).append(" VALUES(");
                    StringBuilder insertWords = new StringBuilder();
                    String[] tableWords = fileLines.split("~");
                    for (int i = 0; i < tableWords.length; i++) {
                      insertWords.append(tableWords[i]).append(", ");
                    }
                    if (!insertWords.isEmpty()) {
                      insertWords.deleteCharAt(insertWords.length() - 1).deleteCharAt(insertWords.length() - 1);
                      insertQuery.append(insertWords).append(");\n");
                    }
                  }
                  sb.append(insertQuery);
                }
              }
            }
            System.out.println(sb);
            sqlDumpFile.write(sb + "\n");
          }
        }
      }

      sqlDumpFile.close();
      System.out.println("SQL Dump File created successfully!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}