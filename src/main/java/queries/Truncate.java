package queries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Truncate {

  public boolean truncateTable(String[] queryArray, String dbPath) {
    try {
      if (queryArray[1].equalsIgnoreCase("table")) {
        String path = dbPath + "/" + queryArray[2] + ".txt";
        File file = new File(path);
        Scanner scan = new Scanner(file);
        String header;
        if (scan.hasNextLine()) {
          header = scan.nextLine();
          FileWriter fileWriter = new FileWriter(path);
          fileWriter.flush();
          fileWriter.write(header);
          fileWriter.close();
        }
        return true;
      } else {
        System.out.println("There is an error in your SQL syntax, Please try again");
      }
    } catch (IOException e) {
      return false;
    }
    return false;
  }
}
