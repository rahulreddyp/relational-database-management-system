package ERD;

import queries.TableGenerator;
import queries.UseDBParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EntityRelation {

  public void generateERD(String userName) {

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


    FileWriter erdFile;
    try {
      erdFile = new FileWriter("bin/ERD/ERDiagram.txt");
      erdFile.flush();

      erdFile = new FileWriter("bin/ERD/ERDiagram.txt", true);
      File[] listOfFiles = folder.listFiles();
      if (listOfFiles != null) {
        for (File currentFile : listOfFiles) {
          if (currentFile.isFile() && currentFile.getName().contains("_metadata")) {
            BufferedReader bReader;
            bReader = new BufferedReader(new FileReader(currentFile));

            String line;
            String tableName = currentFile.getName().replace("_metadata.txt", "");

            List<String> columnNames = new ArrayList<>();
            columnNames.add(tableName);
            List<List<String>> valuesList = new ArrayList<>();

            while ((line = bReader.readLine()) != null) {
              String[] words = line.split("~");

              List<String> currentValues = new ArrayList<>();
              if (words.length == 4) {
                currentValues.add(words[1] + " | " + words[2] + " " + words[3]);
              } else {
                for (int i = 1; i < words.length - 1; i++) {
                  currentValues.add(words[i] + " | " + words[i + 1]);
                }
              }

              valuesList.add(currentValues);
            }

            TableGenerator tableGenerator = new TableGenerator();
            System.out.print(tableGenerator.generateTable(columnNames, valuesList));
            erdFile.write(tableGenerator.generateTable(columnNames, valuesList) + "\n");
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
