package Auction;

import java.sql.Connection;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class Players {
    
    private Connection connection;
    private Scanner scanner;

    public Players(Connection connection , Scanner scanner){
      this.connection = connection;
      this.scanner = scanner;
    }

    public void add_players(){
      System.out.println("Enter Player Name: ");
      scanner.nextLine();
      String playername = scanner.nextLine();
      System.out.println("Enter Player Age: ");
      int age = scanner.nextInt();
      System.out.println("Enter Player Country: ");
      scanner.nextLine();
      String playercountry = scanner.nextLine();
      System.out.println("Enter Player Base prize: ");
      double baseprize = scanner.nextDouble();
      System.out.println("Enter Player Catagory: ");
      scanner.nextLine();
      String catagory = scanner.nextLine();
      
      try{
        String query = "INSERT INTO players(Full_Name ,age ,Country ,baseprice ,Categery) Values(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement .setString(1, playername);
        preparedStatement.setInt(2, age);
        preparedStatement.setString(3, playercountry);
        preparedStatement.setDouble(4, baseprize);
        preparedStatement.setString(5, catagory);
        int rowAfected = preparedStatement.executeUpdate();
        if (rowAfected>0) {
          System.out.println("Player bought");
        }
        else{
          System.out.println("Player not bought");
        }

      }catch(SQLException e){
         e.printStackTrace();
      }
    }

    public void view_player(){
      String query = "SELECT * FROM  players";
      try{
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();
      System.out.println("Players: ");
      System.out.println("+-----------+-------------+------------+----------------+------------------+-----------------+");
      System.out.println("| Player id | Player Name | Player Age | Player Country | Player Baseprice | Player Category |");
      System.out.println("+-----------+-------------+------------+----------------+------------------+-----------------+");
      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name =resultSet.getString("Full_Name");
        int age = resultSet.getInt("age");
        String country = resultSet.getString("Country");
        double baseprice = resultSet.getDouble("baseprice");
        String category = resultSet.getString("Categery");
       System.out.printf("| %-9s | %-11s | %-10s | %-14s | %-16s | %-15s |",id, name,age,country,baseprice,category);
       System.out.println();
       System.out.println("+-----------+-------------+------------+----------------+------------------+-----------------+");
      }
      }
      catch(SQLException e){
      e.printStackTrace();
      }
    }
    public boolean getPlayersbyid(int id){
      String query = "SELECT * FROM players WHERE id = ?";
      try{
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return true;
        }
        else{
          return false;
        }

      }
      catch(SQLException e){
       e.printStackTrace();
      }
      return false;
    }
}
