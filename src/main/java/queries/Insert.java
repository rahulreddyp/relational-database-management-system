package queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Insert {

  public String[] InsertValidation(String inputQuery, String dbPath) {
    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll(",", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").split("\\s+");

    if (queryArray[0].equalsIgnoreCase("insert") && queryArray[1].equalsIgnoreCase("into") && queryArray[3].equalsIgnoreCase("values")) {
      File file = new File(dbPath + "/" + queryArray[2].replaceAll(";", "") + ".txt");
      File file1 = new File(dbPath.replaceAll(";", "") + "/" + queryArray[2] + "_metadata" + ".txt");
      if (!file.exists()) {
        System.out.println("Table doesn't exist");
        return null;
      }
      try {
        int i = 0;
        Scanner myReader = new Scanner(file1);
        while (myReader.hasNextLine()) {
          myReader.nextLine();
          i += 1;
        }
        if (i != queryArray.length - 4) {
          return null;
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return queryArray;
  }

  public boolean insertIntoTables(String inputQuery, String dbPath) {

    String[] query = this.InsertValidation(inputQuery, dbPath);
    if (query == null) {
      System.out.println("Error in the SQL query");
      return false;
    }
    try {
      Path p = Paths.get(dbPath.replaceAll(";", "") + "/" + query[2] + ".txt");
      File file = new File(dbPath.replaceAll(";", "") + "/" + query[2] + "_metadata" + ".txt");
      File file1 = new File(dbPath.replaceAll(";", "") + "/" + query[2] + ".txt");
      Scanner FileReader = new Scanner(file1);
      String s = System.lineSeparator();
      File out = new File(s);
      Map<String, ArrayList<String>> metadataDictionary = new HashMap<>();
      Scanner metaDataFileReader = new Scanner(file);
      int primary_index = -1;
      ArrayList<ArrayList<String>> file_list = new ArrayList<>();
      while (metaDataFileReader.hasNextLine()) {
        String[] metaDataLine = metaDataFileReader.nextLine().split("~");
        ArrayList<String> x = new ArrayList<>();
        x.add(metaDataLine[0]);
        x.add(metaDataLine[2]);
        if (metaDataLine.length == 4) {
          primary_index = Integer.parseInt(metaDataLine[0]);
          x.add("primary");
        }
        metadataDictionary.put(metaDataLine[1], x);
      }
      Set<String> primarySet = new HashSet<>();
      while (FileReader.hasNextLine()) {
        ArrayList<String> lineInTheFile = new ArrayList<>();
        String[] cells = FileReader.nextLine().split("~");
        primarySet.add(cells[primary_index]);
        for (String cell : cells) {
          lineInTheFile.add(cell);
        }
        file_list.add(lineInTheFile);
      }
      for (int i = 4; i < query.length; i++) {
        if (i == query.length - 1) {
          if (!primarySet.contains(query[i])) {
            s += query[i].replaceAll("\"", "");
          } else {
            System.out.println("This primary key already exists");
            return false;
          }
        } else {
          if (!primarySet.contains(query[i])) {
            s += query[i].replaceAll("\"", "") + "~";
          } else {
            System.out.println("This primary key already exists");
            return false;
          }

        }
      }
      Files.write(p, s.getBytes(), StandardOpenOption.APPEND);

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
