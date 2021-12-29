package queries;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Drop {

  private static Logger loggerObject;
  private Timestamp timestamp;

  public Drop(FileHandler fh) {
    loggerObject = Logger.getLogger(Create.class.getName());
    try {
      loggerObject.addHandler(fh);
      SimpleFormatter sFormatter = new SimpleFormatter();
      fh.setFormatter(sFormatter);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void dropDatabase(String dbPath, String dbName) {
    File file = new File(dbPath);
    try {
      FileUtils.deleteDirectory(new File(String.valueOf(file)));
      System.out.println("Drop Database Success");
      timestamp = new Timestamp(System.currentTimeMillis());
      loggerObject.info("Action: Successfully dropped database " + dbName + " Time: " + timestamp);
    } catch (IOException e) {
      timestamp = new Timestamp(System.currentTimeMillis());
      loggerObject.info("Action: Error occurred  while dropping database " + dbName + " Time: " + timestamp);
    }
  }

  public void dropTable(String dbPath, String tableName) {
    boolean deleteFile;
    File file = new File(dbPath + "/" + tableName + ".txt");
    if (file.exists()) {
      deleteFile = file.delete();
      file = new File(dbPath + "/" + tableName + "_metadata.txt");
      file.delete();
      timestamp = new Timestamp(System.currentTimeMillis());
      if (deleteFile) {
        System.out.println("Table successfully dropped");
        loggerObject.info("Action: Successfully dropped table " + tableName + " Time: " + timestamp);
      } else {
        System.out.println("Failed to delete the table, please try again.");
        loggerObject.info("Action: Error while dropping table " + tableName + " Time: " + timestamp);
      }
    } else {
      System.out.println("The table " + tableName + " does not exists, please try again!");
      loggerObject.info("Action: The table " + tableName + " does not exists Time: " + timestamp);
    }
  }
}
