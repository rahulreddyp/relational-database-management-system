package queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Select {

  private static Logger loggerObject;

  public Select(FileHandler fh) {
    loggerObject = Logger.getLogger(Create.class.getName());
    try {
      loggerObject.addHandler(fh);
      SimpleFormatter sFormatter = new SimpleFormatter();
      fh.setFormatter(sFormatter);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void parseQuery(String dbPath, String inputQuery) {
    List<List<String>> values_to_Show = new ArrayList<>();
    Map<String, String> queryInfo = validateSelectQuery(inputQuery);
    if (inputQuery.contains("where") || inputQuery.contains("WHERE")) {
      String[] query = this.preprocess_validate_query(inputQuery, dbPath);
      int i = 0;
      Map<String, ArrayList<String>> metadataDictionary = new HashMap<>();

      int j = 0;
      while (j < query.length && !query[j].equalsIgnoreCase("where")) {

        j += 1;
      }
      j -= 1;
      List<List<String>> file_list = new ArrayList<>();
      File file = new File(dbPath + "/" + query[j] + "_metadata.txt");
      File file1 = new File(dbPath + "/" + query[j] + ".txt");
      //file to read metadata
      Scanner metaDataFileReader;
      List<String> column_headers = new ArrayList<>();
      try {
        metaDataFileReader = new Scanner(file);
        Scanner FileReader = new Scanner(file1);

        //Reading the data from the file and storing it in  a 2 D arrayList.
        while (FileReader.hasNextLine()) {
          ArrayList<String> lineInTheFile = new ArrayList<>();
          String[] cells = FileReader.nextLine().split("~");
          for (String cell : cells) {
            if (!cell.isEmpty() || !cell.isBlank()) {
              lineInTheFile.add(cell);
            }
          }
          if (!lineInTheFile.isEmpty() || lineInTheFile.size() < file_list.get(0).size()) {
            file_list.add(lineInTheFile);
          }
        }
        //Storing it in the dictionary
        while (metaDataFileReader.hasNextLine()) {
          String[] metaDataLine = metaDataFileReader.nextLine().split("~");
          ArrayList<String> x = new ArrayList<>();
          x.add(metaDataLine[0]);
          x.add(metaDataLine[2]);
          metadataDictionary.put(metaDataLine[1], x);
        }

        //Column to display
        i += 1;

        ArrayList<Integer> columns_to_display = new ArrayList<>();
        if (query[i].equals("*")) {
          for (Map.Entry<String, ArrayList<String>> e : metadataDictionary.entrySet()) {
            columns_to_display.add(Integer.parseInt(e.getValue().get(0)));
            column_headers.add(e.getKey());
          }
          while (i < query.length && !query[i].equalsIgnoreCase("where")) {
            i += 1;
          }

        } else {
          while (i < query.length && !query[i].equalsIgnoreCase("from")) {
            column_headers.add(query[i]);
            columns_to_display.add(Integer.parseInt(metadataDictionary.get(query[i]).get(0)));
            i += 1;
          }
        }
        while (i < query.length && !query[i].equalsIgnoreCase("where")) {
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
                ArrayList<String> row = new ArrayList<>();
                if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) == Integer.parseInt(constrainValue)) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }
                }
                values_to_Show.add(row);
              }
            } else {
              for (int a = 1; a < file_list.size(); a++) {
                ArrayList<String> row = new ArrayList<>();
                if (file_list.get(a).size() >= columnOnWhichConditionIsSet && file_list.get(a).get(columnOnWhichConditionIsSet).equals(constrainValue)) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }
                }
                values_to_Show.add(row);
              }
            }
            break;
          case ">":
            if (DataTypeOfColumn.equalsIgnoreCase("integer")) {

              for (int a = 1; a < file_list.size(); a++) {
                ArrayList<String> row = new ArrayList<>();
                if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) > Integer.parseInt(constrainValue)) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }
                }
                values_to_Show.add(row);
              }
            } else {
              for (int a = 1; a < file_list.size(); a++) {
                ArrayList<String> row = new ArrayList<>();
                if (file_list.get(a).get(columnOnWhichConditionIsSet).compareTo(constrainValue) > 0) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }
                }
                values_to_Show.add(row);
              }
            }
            break;
          case "<":
            if (DataTypeOfColumn.equalsIgnoreCase("integer")) {

              for (int a = 1; a < file_list.size(); a++) {
                ArrayList<String> row = new ArrayList<>();
                if (Integer.parseInt(file_list.get(a).get(columnOnWhichConditionIsSet)) < Integer.parseInt(constrainValue)) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }
                }
                values_to_Show.add(row);
              }
            } else {
              for (int a = 1; a < file_list.size(); a++) {
                ArrayList<String> row = new ArrayList<>();
                if (file_list.get(a).get(columnOnWhichConditionIsSet).compareTo(constrainValue) < 0) {
                  List<String> x = file_list.get(a);
                  for (int k : columns_to_display) {
                    row.add(x.get(k));
                  }

                }
                values_to_Show.add(row);
              }
            }
            break;
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      TableGenerator tableGenerator = new TableGenerator();
      List<List<String>> y = new ArrayList<>();
      for (List<String> str : values_to_Show) {
        if (str.size() != 0) {
          y.add(str);
        }
      }
      System.out.println(tableGenerator.generateTable(column_headers, y));

    } else {
      if (!queryInfo.isEmpty()) {
        if (queryInfo.containsKey("conditions")) {
          String[] conditionsList = queryInfo.get("conditions").trim().split("and");
          Map<String, String> conditionsMap = new HashMap<>();
          for (String value : conditionsList) {
            String[] conditionArray = value.split("=");
            conditionsMap.put(conditionArray[0].trim(), conditionArray[1].trim().replaceAll("[^0-9a-zA-Z]+", ""));
          }
        } else {
          if (queryInfo.get("columns").trim().contains("*")) {
            getAllTableValues(queryInfo.get("TableName"), dbPath);
          }
        }
      }
    }
  }

  private void getAllTableValues(String tableName, String BASE_PATH) {
    String filePath = BASE_PATH + "/" + tableName + ".txt";
    File file = new File(filePath);
    if (!file.exists()) {
      System.out.println("Table " + tableName + " does not exists");
    } else {
      try {
        Scanner scanner = new Scanner(file);
        int rowCount = 0;

        List<List<String>> table = new ArrayList<>();
        while (scanner.hasNext()) {
          String line = scanner.nextLine();
          String[] rows = line.split("~");
          List<String> row = new ArrayList<>();
          for (int i = 0; i < rows.length; i++) {
            row.add(rows[i]);
          }
          table.add(row);
          rowCount++;
        }

        TableGenerator tableGenerator = new TableGenerator();
        List<List<String>> y;
        y = table;
        List<String> headers = table.get(0);
        y.remove(0);
        System.out.println(tableGenerator.generateTable(headers, y));
        loggerObject.info("Action: select * from " + tableName + " Message: " + rowCount + " row(s) returned");
      } catch (FileNotFoundException e) {
        System.out.println("Error occurred: While fetching values from table " + tableName);
        loggerObject.info("Action: Failed to retrieve records from  " + tableName + " Message: 0 row(s) returned");
        e.printStackTrace();
      }
    }
  }

  public String[] preprocess_validate_query(String inputQuery, String dbPath) {
    String[] queryArray = inputQuery.trim().replaceAll(";", "").replaceAll("=", " = ").replaceAll(">", " > ").replaceAll("<", " < ").replaceAll("&", " & ").replaceAll(",", " ").split("\\s+");

    //UPDATE table_name
    //SET column1 = value1, column2 = value2, ...
    //WHERE condition;
    int i = 0;
    while (!queryArray[i].equalsIgnoreCase("from")) {
      i += 1;
    }
    i += 1;

    File file = new File(dbPath + "/" + queryArray[i].replaceAll(";", "") + ".txt");
    if (!file.exists()) {
      System.out.println("Table doesn't exist");
      return null;
    }
    return queryArray;
  }

  public Map<String, String> validateSelectQuery(String inputQuery) {
    Map<String, String> tableData = new HashMap<>();
    Pattern pattern;
    // select with where clause
    pattern = Pattern.compile("select\\s+(.*?)\\s*from\\s+(.*?);", Pattern.CASE_INSENSITIVE);
    Matcher match = pattern.matcher(inputQuery);
    if (match.find() && match.matches()) {
      tableData.put("columns", match.group(1));
      tableData.put("TableName", match.group(2).trim());
    } else {
      System.out.println("There is an error in your SQL syntax, please check again!");
    }
    return tableData;
  }
}
