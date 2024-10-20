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
// Player Purchasing Function
  public static void Buyplyers(Players players, Teams teams ,Connection connection , Scanner scanner){
    System.out.print("Enter Players Id: ");
    int playersId = scanner.nextInt();
    System.out.print("Enter Teams Id: ");
    int teamsId = scanner.nextInt();
    System.out.print("Enter players Name: ");
    String playersName = scanner.next();
    scanner.nextLine();
    System.out.print("Enter Team Name: ");
    String TeamName = scanner.next();
    System.out.println("Enter amount to buy Player");
    double playeramount = scanner.nextDouble();
      if (players.getPlayersbyid(playersId) && teams.getTeamsbyid(teamsId)) {
        if (cheakPlayersAvailable(playersId, connection)) {
          String buyplayersquery = "INSERT INTO bcci(players_id , teams_id , Player_Name , Team_Name , player_totalprice) VALUES(?,?,?,?,?)";
          String debitquery = "UPDATE teams SET totalamount = totalamount - ? WHERE  teams_id = ?";
          String creditquery = "UPDATE  players SET baseprice =  baseprice + ? WHERE players_id = ?";
          try{
              connection.setAutoCommit(false);
              
              // Purchasing Player 
              PreparedStatement buyPlayerStmt = connection.prepareStatement(buyplayersquery);
              buyPlayerStmt.setInt(1, playersId);
              buyPlayerStmt.setInt(2, teamsId);
              buyPlayerStmt.setString(3, playersName);
              buyPlayerStmt.setString(4, TeamName);
              buyPlayerStmt.setDouble(5, playeramount);
              
              // Debit Amount From Team Table
              PreparedStatement debitpreparedStatement = connection.prepareStatement(debitquery);
              debitpreparedStatement.setDouble(1, playeramount);
              debitpreparedStatement.setInt(2, teamsId);

              // Credit Amount in Player Baseprize
              PreparedStatement creditpreparedStatement = connection.prepareStatement(creditquery);
              creditpreparedStatement.setDouble(1, playeramount);
              creditpreparedStatement.setInt(2, playersId);

              // All Task Perform Here
              if (isSufficentAmount(teamsId, connection, playeramount)) {
                debitpreparedStatement.executeUpdate();
                creditpreparedStatement.executeUpdate();
                buyPlayerStmt.executeUpdate();
                connection.commit();
                connection.setAutoCommit(true);
                System.out.println("Player Purchased Successful !!!");
              }
              else{
                connection.rollback();
                connection.setAutoCommit(true);
                System.out.println("Insufficent Balance");
              }
          }catch(SQLException e){
            try {
              connection.rollback();
              System.out.println("Transaction rolled back due to an error.");
              } catch (SQLException rollbackException) {
              rollbackException.printStackTrace();
            }
              e.printStackTrace();
          }
         }else {
            System.out.println("Player is already purchased.");
        }
      }
      else{
      System.out.println("Players and Teams doesn't Exits");
      }
  }
 // Cheak Player Availvilty
  public static boolean cheakPlayersAvailable(int playersId , Connection connection){
    String cheakPlayerquery ="SELECT COUNT(*) FROM bcci WHERE players_id = ?";
    try{
     PreparedStatement preparedStatement = connection.prepareStatement(cheakPlayerquery);
     preparedStatement.setInt(1, playersId);
     ResultSet resultSet = preparedStatement.executeQuery();
     if (resultSet.next()) {
         int count = resultSet.getInt(1);
         if (count == 0) {
             System.out.println("Player Already Purchased");
             return false;
         }else{
             return true;
         } 
     }
     }catch(SQLException e){
     e.printStackTrace();
     }
     return false;
 }
     // Cheak Team Amount
  public static boolean isSufficentAmount(int teamsId , Connection connection , double totalamount){
    try{
     String cheakbalencequeryString = "SELECT totalamount FROM teams WHERE teams_id = ?";

     PreparedStatement preparedStatement = connection.prepareStatement(cheakbalencequeryString);
     preparedStatement.setInt(1, teamsId);
     ResultSet resultSet = preparedStatement.executeQuery();
     if (resultSet.next()) {
        double currentBalance = resultSet.getDouble("totalamount");
        if (totalamount > currentBalance) {
            System.out.println("Insufficient Amount");
            return false;
        }else{
            System.out.println("Sufficient ");
            return true;
        }
     }
    }catch(SQLException e){
      e.printStackTrace();
    }
    return false;
 }
}

