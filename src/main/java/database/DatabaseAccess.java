package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class DatabaseAccess {

  public boolean checkAccess(String DBName, String userName) {
    String DB_Access_Ino_PATH = "./bin/Databases" + "/DatabaseAccessInfo" + ".txt";
    File file = new File(DB_Access_Ino_PATH);
    Scanner myReader = null;
    try {
      myReader = new Scanner(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (myReader.hasNextLine()) {
      String[] line = myReader.nextLine().split("~");
      if (line[0].equals(DBName) && line[1].equals(userName)) {
        return true;
      }
    }
    return false;
  }

  public boolean giveAccess(String DBName, String userName) {
    String DB_Access_Ino_PATH = "./bin/Databases" + "/DatabaseAccessInfo" + ".txt";
    Path p = Paths.get(DB_Access_Ino_PATH);

    String s = System.lineSeparator() + DBName + "~" + userName;
    try {
      Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
