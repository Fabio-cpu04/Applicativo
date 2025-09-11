package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The DatabaseConnection, acts as an interface between the Database's state and the Data Access Object classes.</p>
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;

    private static Connection connection = null;

    private static final String POSTGRES_USERNAME = "postgres";
    private static final String POSTGRES_PASSWORD = "password";
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/";

    /**
     * <p>Private Constructor for DatabaseConnection, sets up a PostgreSQL driver and tries to get a connection.</p>
     */
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD);
        }
        catch (SQLException sqlex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Could not connect to the PostgreSQL database.", sqlex);
        }
    }

    /**
     * <p>Gets the singleton instance.</p>
     * @return the DatabaseConnection singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        else {
            try {
                if (DatabaseConnection.connection.isClosed())
                    instance = new DatabaseConnection();
            } catch (SQLException sqlex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Could not connect to the PostgreSQL database.", sqlex);
            }
        }
        return instance;
    }

    /**
     * Gets the {@link Connection} associated with the driver.
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }
}