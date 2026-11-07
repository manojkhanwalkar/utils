
import simplepool.Wrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DBWrapper implements Wrapper
{


    Connection connection= null;



    public void init() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Credentials come from the environment instead of being hard-coded.
            String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/test");
            connection = DriverManager.getConnection(url, System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public synchronized void persist(long id, String request) {

        try {
            // Parameterized statement: request text is bound, never concatenated into SQL.
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Request VALUES (?, ?)")) {
                stmt.setInt(1, (int) id);
                stmt.setString(2, request);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void destroy() {

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        DBWrapper wrapper = new DBWrapper();

        wrapper.init();
        wrapper.persist(100, "Hello World");
        wrapper.destroy();


    }



}
