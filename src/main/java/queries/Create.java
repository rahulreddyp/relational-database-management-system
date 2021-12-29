package queries;

import database.DatabaseAccess;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Create {

  private static Logger loggerObject;
  private FileHandler fileHandler;

  public Create(FileHandler fh) {
    loggerObject = Logger.getLogger(Create.class.getName());
    try {
      this.fileHandler = fh;
      loggerObject.addHandler(fileHandler);
      SimpleFormatter sFormatter = new SimpleFormatter();
      this.fileHandler.setFormatter(sFormatter);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean createTable(String query, String DATABASE_PATH) {
    FileWriter file;
    if (DATABASE_PATH == null) {
      System.out.println("No Database Selected");
      return false;
    }
    String[] validatedQuery = validateTableCreation(query, DATABASE_PATH);

    if (validatedQuery == null) {
      return false;
    }

    try {
      Writer writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DATABASE_PATH + "/" + validatedQuery[2] + ".txt"), "utf-8"));
      ArrayList<ArrayList<String>> x = new ArrayList<>();
      int i = 3;
      while (i < validatedQuery.length - 1) {
        ArrayList<String> temp = new ArrayList<>();
        if (i == validatedQuery.length - 2) {
          writer1.write(validatedQuery[i]);
        } else {
          writer1.write(validatedQuery[i] + "~");
        }
        temp.add(validatedQuery[i]);
        temp.add(validatedQuery[i + 1]);

        if (validatedQuery.length > i + 2 && validatedQuery[i + 2].equals("primary")) {
          temp.add(validatedQuery[i + 2]);
          i += 3;
        } else {
          i += 2;
        }
        x.add(temp);
      }

      writer1.flush();
      writer1.close();

      // writing to meta data
      file = new FileWriter(DATABASE_PATH + "/" + validatedQuery[2] + "_metadata" + ".txt");
      int index = 0;
      for (ArrayList<String> l : x) {
        if (l.size() == 3) {
          file.write(index + "~" + l.get(0) + "~" + l.get(1) + "~" + l.get(2) + "\n");
        } else {
          file.write(index + "~" + l.get(0) + "~" + l.get(1) + "\n");
        }
        index++;
      }

      file.flush();
      file.close();

      System.out.println("Table created successfully");
      loggerObject.info("Action: " + query + " Output: 0 rows(s) affected");
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  private String[] validateTableCreation(String inputQuery, String DATABASE_PATH) {
    int flag = 0;

    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll(",", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("[^a-zA-Z ]", "").split("\\s+");

    if (queryArray.length > 4) {
      File file = new File(DATABASE_PATH + "/" + queryArray[2] + ".txt");
      if (file.exists()) {
        System.out.println("Table " + queryArray[2] + " already exists");
        loggerObject.info("Error occurred: table " + queryArray[2] + " already exists");
        return null;
      }

      for (int i = 0; i < queryArray[2].length(); i++) {
        if (Character.isWhitespace(queryArray[2].charAt(i)) || queryArray[2].charAt(i) == '@' || queryArray[2].charAt(i) == '!' || queryArray[2].charAt(i) == '#' || queryArray[2].charAt(i) == '$' || queryArray[2].charAt(i) == '%' || queryArray[2].charAt(i) == '^' || queryArray[2].charAt(i) == '&' || queryArray[2].charAt(i) == '*' || queryArray[2].charAt(i) == '(' || queryArray[2].charAt(i) == ')' || queryArray[2].charAt(i) == '-' || queryArray[2].charAt(i) == '+' || queryArray[2].charAt(i) == '=') {
          flag = 1;
          break;
        }
      }
      if (flag == 1) {
        System.out.println("Invalid character use! Please enter valid table name");
        loggerObject.info("Error occurred: Invalid SQL syntax while creating table");
        return null;
      }
    }
    return queryArray;
  }

  public boolean createDatabase(String dbName, String userName) {
    boolean result = false;
    try {
      String directoryPath = "./bin/Databases/" + dbName;

      File newDatabase = new File(directoryPath);
      result = newDatabase.mkdir();
      if (result) {
        DatabaseAccess databaseAccess = new DatabaseAccess();
        databaseAccess.giveAccess(dbName, userName);
        loggerObject.info(dbName + " database created successfully!");
      } else {
        System.out.println("Database already exists");
        loggerObject.info("Database " + dbName + " already exists");
      }
    } catch (Exception exception) {
      loggerObject.info("Failed to create database" + dbName);
    }
    return result;
  }

  public String validateCreateDBQuery(String inputQuery) {
    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll("[^a-zA-Z ]", "").split("\\s+");

    if (queryArray.length == 3) {
      int flag = 0;
      for (int i = 0; i < queryArray[2].length(); i++) {
        if (Character.isWhitespace(queryArray[2].charAt(i)) || queryArray[2].charAt(i) == '@' || queryArray[2].charAt(i) == '!' || queryArray[2].charAt(i) == '#' || queryArray[2].charAt(i) == '$' || queryArray[2].charAt(i) == '%' || queryArray[2].charAt(i) == '^' || queryArray[2].charAt(i) == '&' || queryArray[2].charAt(i) == '*' || queryArray[2].charAt(i) == '(' || queryArray[2].charAt(i) == ')' || queryArray[2].charAt(i) == '-' || queryArray[2].charAt(i) == '+' || queryArray[2].charAt(i) == '=') {
          flag = 1;
          break;
        }
      }

      if (flag == 1) {
        System.out.println("Invalid character use! Please enter valid database name");
        return null;
      }
    }
    return queryArray[2];
  }
}
