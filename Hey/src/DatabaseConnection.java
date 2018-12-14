import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //as it is needed
    public DatabaseConnection() {
    }

    public static Connection createConnection() {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver"); //postgresql driver stringer
            String url = "jdbc:postgresql://localhost:5432/BDDatabase";
            connection = DriverManager.getConnection(url, "postgres", "password"); //password relativa ao postgres


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("class not found");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("connection failed");
        }
        System.out.println("Connected successfuly");
        return connection;
    }
}

