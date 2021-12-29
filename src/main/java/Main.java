import ERD.EntityRelation;
import authentication.Login;
import queries.Create;
import queries.QueryParser;
import sqldump.GenerateDump;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

  public static void main(String[] args) {
    int userInput;
    Logger loggerObject;
    FileHandler fh = null;

    try {
      loggerObject = Logger.getLogger(Create.class.getName());
      fh = new FileHandler("bin/Logs/TransactionLogs.txt");
      loggerObject.addHandler(fh);
      SimpleFormatter sFormatter = new SimpleFormatter();
      fh.setFormatter(sFormatter);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Scanner scan = new Scanner(System.in);
    Login login = new Login();

    if (login.authenticateUser()) {
      String userName = login.getUser();
      while (true) {
        System.out.println("1. SQL Query\n2. Generate SQL Dump\n3. Generate ERD\n4. Logout");
        System.out.println("Enter your selection:");
        userInput = scan.nextInt();
        switch (userInput) {
          case 1:
            QueryParser qp = new QueryParser(fh);
            qp.createMenuOptions(userName);
            break;
          case 2:
            // SQL Dump
            GenerateDump gd = new GenerateDump();
            gd.makeQueries(userName);
            break;
          case 3:
            // ERD
            EntityRelation erDiagram = new EntityRelation();
            erDiagram.generateERD(userName);
            break;
          case 4:
            // Logout from application
            return;
          default:
            System.out.println("Please enter a valid input");
        }
      }
    }
  }
}
