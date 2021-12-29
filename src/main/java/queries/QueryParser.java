package queries;

import java.sql.Timestamp;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class QueryParser {

  private static Logger loggerObject;
  private final FileHandler fileHandler;
  private String dbPath = null;

  public QueryParser(FileHandler fh) {
    loggerObject = Logger.getLogger(Create.class.getName());
    this.fileHandler = fh;
    loggerObject.addHandler(fileHandler);
    SimpleFormatter sFormatter = new SimpleFormatter();
    fileHandler.setFormatter(sFormatter);
  }

  public void createMenuOptions(String userName) {
    while (true) {
      System.out.print("RDBMSGROUP13 ->");
      Scanner scanner = new Scanner(System.in);
      String inputQuery = scanner.nextLine();
      if (!inputQuery.isEmpty()) {
        if (inputQuery.equalsIgnoreCase("exit")) {
          break;
        }
        parseQueryOperation(inputQuery, userName);
      }
    }
  }

  private boolean parseQueryOperation(String inputQuery, String userName) {
    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll("=", " = ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll(",", " ").split("\\s+");
    String queryType = queryArray[0].toUpperCase();
    UseDBParser useDB;
    Timestamp timestamp;
    long startTime = System.nanoTime();

    switch (queryType) {
      case "SHOW":
        if (queryArray[1].equalsIgnoreCase("DATABASES")) {
          useDB = new UseDBParser();
          useDB.showDatabases(userName);
        }
        break;
      case "USE":
        // use database
        useDB = new UseDBParser();
        if (useDB.executeQuery(queryArray[1], userName)) {
          dbPath = useDB.getDBPath();
        }
        break;
      case "CREATE":
        // create query
        Create createParse = new Create(fileHandler);
        if (queryArray[1].equalsIgnoreCase("DATABASE")) {
          String dbName = createParse.validateCreateDBQuery(inputQuery);
          if (dbName == null) {
            System.out.println("You have an error in your SQL syntax, Please check again");
          } else {
            createParse.createDatabase(dbName, userName);
          }
        } else if (queryArray[1].equalsIgnoreCase("TABLE")) {
          if (dbPath == null) {
            System.out.println("Please select the database");
            return false;
          }
          createParse.createTable(inputQuery, dbPath.replaceAll(";", ""));
        } else {
          System.out.println("Enter valid create query");
          return false;
        }
        break;
      case "INSERT":
        // insert
        if (dbPath == null) {
          System.out.println("No database Selected");
          break;
        }
        Insert insert = new Insert();
        boolean result = insert.insertIntoTables(inputQuery, dbPath);
        if (result) {
          System.out.println("value inserted successfully");
        } else {
          System.out.println("Error Occurred: Failed to insert into table");
        }
        break;
      case "SELECT":
        // select
        if (dbPath == null) {
          System.out.println("No database Selected");
          break;
        } else {
          Select select = new Select(fileHandler);
          select.parseQuery(dbPath, inputQuery);
        }
        break;
      case "UPDATE":
        if (dbPath == null) {
          System.out.println("No database Selected");
          break;
        } else {
          Update update = new Update();
          update.execute_update(inputQuery, dbPath);
        }
        break;
      // update

      case "DROP":
        if (dbPath == null) {
          System.out.println("No database Selected");
        } else {
          Drop dropQuery = new Drop(fileHandler);
          if (queryArray[1].equalsIgnoreCase("DATABASE")) {
            dropQuery.dropDatabase(dbPath, queryArray[2]);
          } else if (queryArray[1].equalsIgnoreCase("TABLE")) {
            dropQuery.dropTable(dbPath, queryArray[2]);
          } else {
            System.out.println("Invalid drop syntax, please check again");
          }
        }
        break;
      case "DELETE":
        // delete
        if (dbPath == null) {
          System.out.println("No database Selected");
        } else {
          Delete deleteCommand = new Delete();
          deleteCommand.execute_delete(inputQuery, dbPath);
        }
        break;
      case "TRUNCATE":
        // truncate
        if (dbPath == null) {
          System.out.println("No database Selected");
        } else {
          Truncate truncateQuery = new Truncate();
          result = truncateQuery.truncateTable(queryArray, dbPath);
          if (result) {
            loggerObject.info("Table " + queryArray[2] + " truncated successfully");
          } else {
            loggerObject.info("Failed to truncate the table" + queryArray[2]);
          }
        }
        break;
    }

    long endTime = System.nanoTime();
    timestamp = new Timestamp(System.currentTimeMillis());
    loggerObject.info("Action: " + inputQuery + " : Time: " + timestamp + " Duration: Executed in " + (endTime - startTime) / 1000 + " milli seconds");
    return false;
  }
}
