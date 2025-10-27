package daopostgresimplementation;

//Java imports
import java.util.Map;
import java.util.TreeMap;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//App imports
import dao.UserDAO;
import model.User;

/**
 * <p>PostgreSQL specific implementation of the {@link UserDAO} interface.</p>
 */
public class PostgresUserDAO implements UserDAO {
    private final Connection connection;

    /**
     * <p>Instantiates a new PostgresUserDAO object.</p>
     * @param con the connection to the PostgreSQL database
     */
    public PostgresUserDAO(Connection con) {
        connection = con;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int registerUser(String username, String password){
        try(PreparedStatement s = connection.prepareStatement("INSERT INTO Users (username, password) VALUES (?,?)")) {
            s.setString(1, username);
            s.setString(2, password);
            s.executeUpdate();

            //Get the new user's ID
            PreparedStatement userIDStatement = connection.prepareStatement("SELECT userID FROM Users WHERE username=?");
            userIDStatement.setString(1, username);

            ResultSet resSet = userIDStatement.executeQuery();
            resSet.next();

            return resSet.getInt(1); //Returns userID
        } catch (SQLException e) {
            String code = e.getSQLState();
            switch (code){
                case "22001" -> { //username or password exceed 128 chars
                    if(username.length() > 128)
                        throw new IllegalArgumentException("The username cannot exceed 128 characters.");
                    else if(password.length() > 128)
                        throw new IllegalArgumentException("The password cannot exceed 128 characters.");
                }
                case "23514" -> throw new IllegalArgumentException("Invalid username"); //username does not match DB regex pattern
                case "23505" -> throw new IllegalStateException("The user " + username + " exists already");
                default -> { return -1; }
            }
        }
        return -1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int authUser(String username, String password) {
        try(PreparedStatement s = connection.prepareStatement("SELECT userID, username, password FROM Users WHERE username=?")) {
            s.setString(1, username);

            ResultSet res = s.executeQuery();
            res.next();

            if(res.getString(3).equals(password)) //Gets first row's password column and checks for equality
                return res.getInt(1); //Returns userID
            else
                return -1;
        } catch (SQLException _) {
            return -2;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getUsers(){
        Map<String, Integer> map = new TreeMap<>();

        try(PreparedStatement s = connection.prepareStatement("SELECT userID, username FROM Users")) {
            ResultSet res = s.executeQuery();

            while(res.next())
                map.put(res.getString(2), res.getInt(1));

            return map;
        } catch (SQLException _) {
            return map;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int userExists(String username){
        try(PreparedStatement s = connection.prepareStatement("SELECT userID FROM Users WHERE username=?")) {
            s.setString(1, username);

            ResultSet resSet = s.executeQuery();
            resSet.next();

            return resSet.getInt(1);
        } catch (SQLException _) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMetadata(int userID) {
        try(PreparedStatement s = connection.prepareStatement("SELECT username, password FROM Users WHERE userID=?")) {
            s.setInt(1, userID);

            ResultSet res = s.executeQuery();
            res.next();

            return new User(userID, res.getString(1), res.getString(2));
        } catch (SQLException _) {
            return null;
        }
    }
}
