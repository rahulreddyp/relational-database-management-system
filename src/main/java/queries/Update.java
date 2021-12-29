package queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Update {

  public String[] preprocess_validate_query(String inputQuery, String dbPath) {
    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll("=", " = ").replaceAll(">", " > ").replaceAll("<", " < ").replaceAll("&", " & ").replaceAll(",", " ").split("\\s+");

    /*UPDATE table_name
     * SET column1 = value1, column2 = value2, ...
     * WHERE condition;
     */
    if (queryArray[0].equalsIgnoreCase("update") && queryArray[2].equalsIgnoreCase("set")) {
      File file = new File(dbPath + "/" + queryArray[1].replaceAll(";", "") + ".txt");
      if (!file.exists()) {
        System.out.println("Table doesn't exist");
        return null;
      }
      return queryArray;
    }
    return null;
  }

  public boolean execute_update(String inputQuery, String dbPath) {
    String[] query = this.preprocess_validate_query(inputQuery, dbPath);
    int i = 0;
    Map<String, ArrayList<String>> metadataDictionary = new HashMap<>();
    //Column to update
    while (i < query.length && !query[i].equalsIgnoreCase("set")) {
      i += 1;
    }
    i += 1;

    ArrayList<ArrayList<String>> file_list = new ArrayList<>();
    File file = new File(dbPath + "/" + query[1] + "_metadata.txt");
    File file1 = new File(dbPath + "/" + query[1] + ".txt");

    try {
      //file to read metadata
      Scanner metaDataFileReader = new Scanner(file);
      Scanner FileReader = new Scanner(file1);
      //Storing it in the dictionary
      while (metaDataFileReader.hasNextLine()) {
        String[] metaDataLine = metaDataFileReader.nextLine().split("~");
        ArrayList<String> x = new ArrayList<>();
        x.add(metaDataLine[0]);
        x.add(metaDataLine[2]);
        metadataDictionary.put(metaDataLine[1], x);
      }

      //Getting the column number we want to update
      int index_to_update = Integer.parseInt(metadataDictionary.get(query[i]).get(0));
      i += 2;

      //Storing the value we want to set
      String updateValue = query[i];

      //Reading the data from the file and storing it in  a 2 D arrayList.
      while (FileReader.hasNextLine()) {
        ArrayList<String> lineInTheFile = new ArrayList<>();
        String[] cells = FileReader.nextLine().split("~");
        for (String cell : cells) {
          lineInTheFile.add(cell);
        }
        file_list.add(lineInTheFile);
      }
      while (!query[i].equals("where")) {
        i += 1;
      }
      i += 1;

      int columnOnWhichConditionIsSet = Integer.parseInt(metadataDictionary.get(query[i]).get(0));
      String DataTypeOfColumn = metadataDictionary.get(query[i]).get(1);
      i += 1;
      String operator = query[i];
      i += 1;
      String constrainValue = query[i];

      switch (operator) {
        case "=":
          if (DataTypeOfColumn.equalsIgnoreCase("integer")) {
            for (int a = 1; a < file_list.size(); a++) {
              if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) == Integer.parseInt(constrainValue)) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }

          } else {
            for (int a = 1; a < file_list.size(); a++) {

              if (file_list.get(a).get(columnOnWhichConditionIsSet).equals(constrainValue)) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }
          }
          break;
        case ">":
          if (DataTypeOfColumn.equalsIgnoreCase("integer")) {

            for (int a = 1; a < file_list.size(); a++) {
              if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) > Integer.parseInt(constrainValue)) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }
          } else {
            for (int a = 1; a < file_list.size(); a++) {
              if (file_list.get(a).get(columnOnWhichConditionIsSet).compareTo(constrainValue) > 0) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }
          }
          break;
        case "<":
          if (DataTypeOfColumn.equalsIgnoreCase("integer")) {

            for (int a = 1; a < file_list.size(); a++) {

              if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) < Integer.parseInt(constrainValue)) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }

          } else {
            for (int a = 1; a < file_list.size(); a++) {

              if (file_list.get(a).get(columnOnWhichConditionIsSet).compareTo(constrainValue) < 0) {
                ArrayList x = file_list.get(a);
                x.set(index_to_update, updateValue);
                file_list.set(a, x);
              }
            }
          }
          break;
      }

      String source = "";
      for (ArrayList<String> row : file_list) {
        for (int a = 0; a < row.size(); a++) {
          if (a == row.size() - 1) {
            source = source + row.get(a) + "\n";
          } else {
            source = source + row.get(a) + "~";
          }
        }
      }
      File file2 = file1;
      file1.delete();

      try {
        FileWriter f2 = new FileWriter(file2, false);
        f2.write(source);
        f2.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return true;
  }
}
