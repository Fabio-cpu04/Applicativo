package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static DatabaseConnection instance;

    public Connection connection = null;

    private final String nome = "postgres";
    private final String password = "password";
    private final String url = "jdbc:postgresql://localhost:5432/";
    private final String driver = "org.postgresql.Driver";

    /**
     * Private Constructor for DatabaseConnection, sets up a PostgreSQL driver and tries to get a connection.
     */
    private DatabaseConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, nome, password);
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (SQLException sqlex){
            System.out.println("Could not connect to the PostgreSQL database.\n" + sqlex.getMessage());
        }

    }

    public static DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        else {
            try {
                if (instance.connection.isClosed())
                    instance = new DatabaseConnection();
            } catch (SQLException sqlex) {
                System.out.println(sqlex.getMessage());
            }
        }

        return instance;
    }
}