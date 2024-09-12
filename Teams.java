package Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Teams {

    private Connection connection;

    public Teams(Connection connection){
    this.connection = connection;
    }
    public void view_teams(){
      String query = "SELECT * FROM teams";
      try{
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet =preparedStatement.executeQuery();
        System.out.println("Teams: ");
        System.out.println("+-----------+-------------------+---------------+--------------+");
        System.out.println("| Teams id  | Team Name         | Total Players | Total Amount |");
        System.out.println("+-----------+-------------------+---------------+--------------+");
        while (resultSet.next()) {
          int id = resultSet.getInt(1);
          String name = resultSet.getString("name");
          int totalPlayer = resultSet.getInt("totalplayer");
          double totalAmount = resultSet.getDouble("totalamount");
          System.out.printf("| %-9s | %-17s | %-13s | %-12s |\n" ,id ,name ,totalPlayer ,totalAmount);
          System.out.println("+-----------+-------------------+---------------+--------------+");
        }
      }
      catch(SQLException e){
        e.printStackTrace();
      }
    }

    public boolean getTeamsbyid(int id){
      String query = "SELECT * FROM teams WHERE id = ?";
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

