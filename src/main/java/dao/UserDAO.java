package dao;

import java.util.List;

import model.User;

public interface UserDAO {
    /**
     * Checks if the user {@code username} can be authenticated with {@code password} in the system.
     * @param username the user's username
     * @param password the user's password
     * @return the user's id if successful, {@code -1} if the password is incorrect and {@code -2} if the user is not registered in the system.
     */
    int authUser(String username, String password);

    /**
     * Gets a List of all the usernames.
     * @return if successful, a {@link List <String>} of all usernames, otherwise null.
     */
    List<String> getUserList();
    /**
     * Gets and parses a model User identified by {@code username}.
     * @param username the user's username
     * @return the user, otherwise {@code null}.
     */
    User getUserByUsername(String username);

    /**
     * Adds a user to the system, identified by its username {@code username} and its password {@code password}
     * @param username the user's username
     * @param password the user's password
     * @return {@code 0} if successful, else returns {@code -1} if the user already exists in the system.
     */
    int addUser(String username, String password);
    /**
     * Removes a user from the system, identified by its username {@code username}
     * @param username the user's username
     * @return {@code 0} if successful, else returns {@code -1} if the user does not exist in the system.
     */
    int removeUser(String username);
}
