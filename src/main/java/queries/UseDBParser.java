package queries;

import database.DatabaseAccess;

import java.io.File;

public class UseDBParser {

  private String DBPath;

  public boolean useQueryValidation(String DBName, String userName) {
    String database_path = "/bin/Databases/";
    File file = new File(database_path + DBName);
    DatabaseAccess databaseAccess = new DatabaseAccess();

    if (!file.exists() && !databaseAccess.checkAccess(DBName, userName)) {
      return false;
    } else {
      return true;
    }
  }

  public boolean executeQuery(String DBName, String userName) {
    if (!useQueryValidation(DBName, userName)) {
      System.out.println("This database does not exist");
    }
    this.setDBPath("bin/Databases/" + DBName);
    return true;
  }

  public void showDatabases(String userName) {
    File databaseDirPath = new File("bin\\Databases");
    String[] dbNamesList = databaseDirPath.list();

    boolean userAccess;
    int flag = 0;
    if (dbNamesList != null) {
      int index = 1;
      for (String dbName : dbNamesList) {
        userAccess = useQueryValidation(dbName, userName);
        if (userAccess) {
          flag = 1;
          System.out.println(index++ + "." + dbName);
        }
      }
    }
    if (flag == 0) {
      System.out.println("No database exists!");
    }
  }

  public String getDBPath() {
    return DBPath;
  }

  public void setDBPath(String DBPath) {
    this.DBPath = DBPath;
  }
}
