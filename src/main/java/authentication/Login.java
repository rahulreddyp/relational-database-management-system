package authentication;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.Scanner;


public class Login {

  private final Scanner scanner;
  private String user = null;

  public Login() {
    scanner = new Scanner(System.in);
  }

  public static String encryptToSha256(final String base) {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] hash = digest.digest(base.getBytes("UTF-8"));
      final StringBuilder hexString = new StringBuilder();
      for (int i = 0; i < hash.length; i++) {
        final String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception ex) {
      System.out.println("Couldn't create SHA256 Hash");
      System.exit(0);
      return "";
    }
  }

  public boolean multiFactorAuthentication(String userName) {
    while (true) {
      System.out.println("Please select a security Question");
      System.out.println("1. Favourite city");
      System.out.println("2  Favourite food");
      System.out.println("3 Place where you were born");
      Scanner sc = new Scanner(System.in);
      int option = sc.nextInt();
      String ans = null;
      System.out.println("Enter your answer");
      ans = sc.next();
      File myObj = new File("./bin/securityQuestion.txt");
      Scanner myReader = null;
      String[] credentials = new String[4];
      try {
        myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
          credentials = data.split("~");
          if (credentials[0].equals(userName)) {
            break;
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      switch (option) {
        case 1:
          if (credentials[1].equalsIgnoreCase(ans)) {
            return true;
          } else {
            System.out.println("Answer is wrong please try again");
            continue;
          }
        case 2:
          if (credentials[2].equalsIgnoreCase(ans)) {
            return true;
          } else {
            System.out.println("Answer is wrong please try again");
            continue;
          }
        case 3:
          if (credentials[3].equalsIgnoreCase(ans)) {
            return true;
          } else {
            System.out.println("Answer is wrong please try again");
            continue;
          }
        default:
          System.out.println("Wrong option try again");
      }
    }
  }

  public boolean authenticateUser() {
    while (true) {
      System.out.println("Login Page: ");
      System.out.print("Enter UserName:");
      String userName = scanner.nextLine();
      System.out.print("Password:");
      String password = scanner.nextLine();
      MessageDigest digest = null;
      String encoded_password = " ";
      encoded_password = Login.encryptToSha256(password);

      File myObj = new File("./bin/LoginInfo.txt");

      encoded_password = Login.encryptToSha256(password);
      this.user = userName;


      Scanner myReader;
      try {
        boolean flag = false;
        myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
          String[] credentials = data.split("~");
          if (credentials[0].equals(userName)) {
            flag = true;

            if (credentials[1].equals(encoded_password)) {

              return multiFactorAuthentication(userName);
            } else {
              System.out.println("Wrong password try again");
              break;
            }
          }
        }
        if (!flag) {
          System.out.println("UserName doesn't exist Please try again");
          continue;
        }

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public String getUser() {
    return user;
  }
}