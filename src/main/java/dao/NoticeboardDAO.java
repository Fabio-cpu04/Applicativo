package dao;

import java.util.List;

import model.User;

public interface NoticeboardDAO {
    /**
     * Gets a List of all the Noticeboards of a user.
     * @return a {@link List<String>} of all usernames.
     */
    List<String> getAllUsernames();
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
