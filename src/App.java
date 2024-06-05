
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class App{
    public static void main(String[] args) {
        String jdbcString = "jdbc:sqlite:/C:\\Users\\Kyle Taguiwalo\\Desktop\\SQLite\\SQLiteTools\\usersdb.db";
        try{
            Connection connection = DriverManager.getConnection(jdbcString);
            String sql = "SELECT * FROM users";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            while(result.next()){
                String name = result.getString("name");
                String email = result.getString("email");

                System.out.println(name + " | " + email);

            }
        } catch(SQLException e){
            System.out.println("Error connecting to sqlite database");
            e.printStackTrace();
        }


    }
}