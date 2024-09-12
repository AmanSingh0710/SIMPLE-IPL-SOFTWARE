package Auction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BCCI{
    
  private static final String url = "jdbc:mysql://localhost:3306/ipl";
  private static final String username = "root";
  private static final String password = "tendulkar";

  public static void Menu() {
    System.out.println("* ================= ** Welcome To IPL Auction Application ** ================= *");
    System.out.println();
    System.out.println("** ======== ** 1. add Players ** ============ **  2. View Team ** ======== **");
    System.out.println();
    System.out.println("** ======== ** 3. View Players  ** ========  ** 4. Buy Player ** ============ **");
    System.out.println();
    System.out.println("** =============================== 5. EXIT ================================**");
    System.out.println();
    System.out.println("** ================ ** Thanks for using this application ** ================= **");
    System.out.println();
}

  public static void main(String[] args) {
    try{
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("Driver Loaded Successfully");
      System.out.println();

    }catch(ClassNotFoundException e){
     e.printStackTrace();
    }
    Scanner scanner = new Scanner(System.in);
    try{
      String query = "SELECT * FROM players";
      Connection connection = DriverManager.getConnection(url,username,password);
      Players players = new Players(connection, scanner);
      Teams teams = new Teams(connection);

      while (true) {
        Menu();
      System.out.println("Enter your choice");
      int choice = scanner.nextInt();
      switch (choice) {
        case 1:
          // Buy Players
          players.add_players();
          break;
        case 2:
          // Team Details
          teams.view_teams();
          break;
        case 3:
          // Players Details
          players.view_player();
          break;
        case 4:
          // Buy Players
          Buyplyers(players, teams, connection, scanner);
          break;
        case 5:
          // EXIT
          System.out.println("Application Exiting....");
          connection.close();
          scanner.close();
           return;
        default:
            System.out.println("Please Enter Valid Choice:");
            break;
      }
      }
    }catch(SQLException e ){
     e.printStackTrace();
    }
  }
  public static void Buyplyers(Players players, Teams teams ,Connection connection , Scanner scanner){
    System.out.print("Enter Players Id: ");
    int playersId = scanner.nextInt();
    System.out.print("Enter Teams Id: ");
    int teamsId = scanner.nextInt();
    System.out.print("Enter players Name: ");
    String playersName = scanner.next();
      if (players.getPlayersbyid(playersId) && teams.getTeamsbyid(teamsId)) {
        if (cheakPlayersAvailable(playersId, playersName, connection)) {
          String buyplayersquery = "INSERT INTO buyplyers(players_id , teams_id , buy_players) VALUES(?,?,?)";
          try{
              PreparedStatement preparedStatement = connection.prepareStatement(buyplayersquery);
              preparedStatement.setInt(1, playersId);
              preparedStatement.setInt(2, teamsId);
              preparedStatement.setString(3, playersName);
              int rowAffect = preparedStatement.executeUpdate();
              if (rowAffect>0) {
                  System.out.println("Buying Players Succesfull");
              }else{
                  System.out.println("Buying Players Filed");
              }
          }catch(SQLException e){
             e.printStackTrace();
          }
         }
      }
      else{
      System.out.println("Players and Teams doesn't Exits");
      }
  }
  public static boolean cheakPlayersAvailable(int playersId ,String playersName , Connection connection){
    String query ="SELECT COUNT(*) FROM players WHERE id = ? AND Full_Name = ?";
    try{
     PreparedStatement preparedStatement = connection.prepareStatement(query);
     preparedStatement.setInt(1, playersId);
     preparedStatement.setString(2, playersName);
     ResultSet resultSet = preparedStatement.executeQuery();
     if (resultSet.next()) {
         int count = resultSet.getInt(1);
         if (count == 0) {
             return true;
         }else{
             return false;
         } 
     }
     }catch(SQLException e){
     e.printStackTrace();
     }
     return false;
 }
}

