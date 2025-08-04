package daoPostgresImpl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;

public class PostgresUserDAO implements UserDAO {
    private final Connection connection;

    public PostgresUserDAO(Connection con) {
        connection = con;
    }

    /**
     * Checks if the user {@code username} can be authenticated with {@code password} in the system.
     * @param username the user's username
     * @param password the user's password
     * @return the user's id if successful, {@code -1} if the password is incorrect and {@code -2} if the user is not registered in the system.
     */
    @Override
    public int authUser(String username, String password) {
        try(PreparedStatement s = connection.prepareStatement("SELECT userID, username, password FROM Users WHERE username=?")) {
            s.setString(1, username);

            ResultSet res = s.executeQuery();
            res.next();

            if(res.getString(3).equals(password))
                return res.getInt(1); //Returns userID
            else
                return -1;
        } catch (SQLException e) {
            return -2;
        }
    }

    /**
     * Gets a List of all the usernames.
     * @return if successful, a {@link List <String>} of all usernames, otherwise null.
     */
    @Override
    public List<String> getUserList() {
        try(PreparedStatement s = connection.prepareStatement("SELECT username FROM Users")) {
            ResultSet res = s.executeQuery();
            ArrayList<String> list = new ArrayList<>();

            while(res.next())
                list.add(res.getString(1));

            return list.stream().toList();
        } catch (SQLException e) {
            return null;
        }
    }
    /**
     * Gets and parses a model User identified by {@code username}.
     *
     * @param username the user's username
     * @return the user, otherwise {@code null}.
     */
    @Override
    public User getUserByUsername(String username) {
        try(PreparedStatement s = connection.prepareStatement("SELECT userID, password FROM Users WHERE username=?")) {
            s.setString(1, username);

            ResultSet res = s.executeQuery();
            res.next();

            //return new User(res.getInt(1), username, res.getString(2));
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Adds a user to the system, identified by its username {@code username} and its password {@code password}
     *
     * @param username the user's username
     * @param password the user's password
     * @return {@code 0} if successful, else returns {@code -1} if the user already exists in the system.
     */
    @Override
    public int addUser(String username, String password) {
        return 0;
    }
    /**
     * Removes a user from the system, identified by its username {@code username}
     *
     * @param username the user's username
     * @return {@code 0} if successful, else returns {@code -1} if the user does not exist in the system.
     */
    @Override
    public int removeUser(String username) {
        return 0;
    }
}
