package dao;

import java.util.List;
import java.util.NoSuchElementException;

import model.Noticeboard;

/**
 * <p>Manages the fundamental Database operations for the sharing of {@link model.ToDo} between system users.</p>
 */
public interface SharingDAO {
    /**
     * <p>Loads a User's shared ToDos in each appropriate Noticeboard.</p>
     * @param userID the User's ID
     * @param boards a {@link List} object to load the {@link Noticeboard} objects into
     * @return {@code 0} if successful, otherwise {@code -1}.
     *
     * @throws IllegalArgumentException if {@code boards==null}
     */
    int loadSharedNoticeboardsByUserID(int userID, List<Noticeboard> boards);
    /**
     * <p>Gets the metadata of the Noticeboards shared with a User.</p>
     * @param userID the User's ID
     * @param boards a {@link List} object to load the {@link Noticeboard} objects into
     * @return {@code 0} if successful, otherwise {@code -1}.
     *
     * @throws IllegalArgumentException if {@code boards==null}
     */
    int getSharedNoticeboardsMetadataByUserID(int userID, List<Noticeboard> boards);

    /**
     * <p>Shares a ToDo with a User.</p>
     * @param userID the User's ID
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if either the User or the ToDo do not exist
     * @throws IllegalStateException if the ToDo is already shared with the User
     * @throws IllegalArgumentException if the User is trying to share with itself a ToDo it owns
     */
    void shareToDo(int userID, int todoID);
    /**
     * <p>Unshares a ToDo with a User.</p>
     * @param userID the User's ID
     * @param todoID the ToDo's ID
     *
     * @throws IllegalStateException if the User does not exist or if the ToDo is not shared with the User
     */
    void unshareToDo(int userID, int todoID);
}