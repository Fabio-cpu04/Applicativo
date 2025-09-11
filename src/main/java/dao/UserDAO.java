package dao;

//Java imports
import java.util.Map;

//App imports
import model.User;

public interface UserDAO {
    /**
     * <p>Registers a {@link User} to the system.</p>
     * @param username the User's username
     * @param password the User's password
     * @return the User's ID if successful, {@code -1} if the User already exists in the system.
     *
     * @throws IllegalArgumentException if {@code username} is not a valid username or if either the username or password are too long
     * @throws IllegalStateException if a User with the same username exists already
     */
    int registerUser(String username, String password);
     /**
     * <p>Authenticates the user via its username and password in the system.</p>
     * @param username the user's username
     * @param password the user's password
     * @return the user's id if successful, {@code -1} if the password is incorrect and {@code -2} if the user is not registered in the system.
     */
    int authUser(String username, String password);

    /**
     * <p>Gets a {@link Map} of all the Users'pairs of usernames|userID.</p>
     * @return a {@link Map} of all usernames|userIDs.
     */
    Map<String, Integer> getUsers();

    /**
     * <p>Checks if a User exists.</p>
     * @param username the User's username
     * @return if found returns the User's userID, otherwise returns {@code -1}
     */
    int userExists(String username);

    /**
     * <p>Gets a User.</p>
     * @param userID the user's ID
     * @return the user, otherwise {@code null}.
     */
    User getUserMetadata(int userID);
}