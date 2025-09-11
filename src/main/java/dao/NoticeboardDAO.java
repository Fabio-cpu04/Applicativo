package dao;

//Java imports
import java.util.List;
import java.util.List;

import java.util.NoSuchElementException;

//App imports
import dto.NoticeboardDTO;
import model.Noticeboard;

/**
 * <p>Manages the fundamental Database operations for {@link Noticeboard} objects.</p>
 */
public interface NoticeboardDAO {
    /**
     * <p>Gets a list of all the Noticeboard IDs owned by a User.</p>
     * @param userID the User's ID
     * @return a {@link List} of {@link Integer} of all Noticeboard IDs.
     */
    List<Integer> getNoticeboardIDsByUserID(int userID);

    /**
     * <p>Loads a User's Noticeboards and their ToDos.</p>
     * @param userID the User's ID
     * @param boards a {@link List} object to load the {@link Noticeboard} objects into
     * @return {@code 0} if successful, otherwise {@code -1}.
     *
     * @throws IllegalArgumentException if {@code boards==null}
     */
    int loadNoticeboardsByUserID(int userID, List<Noticeboard> boards);
    /**
     * <p>Loads a User's Noticeboard with its ToDos.</p>
     * @param boardID the Noticeboard's ID
     * @return the {@link Noticeboard} if successful, {@code null} otherwise
     */
    Noticeboard loadNoticeboardByBoardID(int boardID);

    /**
     * <p>Gets the metadata of the Noticeboards owned by a User.</p>
     * @param userID the User's ID
     * @param boards a {@link List} object to load the {@link Noticeboard} objects into
     * @return {@code 0} if successful, otherwise {@code -1}.
     *
     * @throws IllegalArgumentException if {@code boards==null}
     */
    int getNoticeboardsMetadataByUserID(int userID, List<Noticeboard> boards);
    /**
     * <p>Gets the metadata of a Noticeboard.</p>
     * @param boardID the Noticeboard's ID
     * @return the {@link Noticeboard}, otherwise {@code null}.
     */
    Noticeboard getNoticeboardMetadataByBoardID(int boardID);

    /**
     * <p>Adds a Noticeboard to the system.</p>
     * @param title the Noticeboard's title
     * @param description the Noticeboard's description
     * @param userID the owning User's ID
     * @return the Noticeboard's ID if successful, otherwise returns {@code -1}.
     *
     * @throws IllegalArgumentException if the Noticeboard's title is not valid or if either the title or description are too long
     * @throws IllegalArgumentException if no User with the ID {@code userID} exists
     * @throws IllegalStateException if a Noticeboard with the same title exists already
     */
    int addNoticeboard(String title, String description, int userID);

    /**
     * <p>Updates the metadata of a Noticeboard.</p>
     * @param board the new Noticeboard's metadata
     *
     * @throws NoSuchElementException if the target Noticeboard does not exist
     * @throws IllegalArgumentException if the title is not valid or if either the title or description are too long
     * @throws IllegalStateException if the title is already used by another Noticeboard
     */
    void updateNoticeboard(NoticeboardDTO board);

    /**
     * <p>Changes the title of a noticeboard.</p>
     * @param newTitle the new title
     * @param boardID the Noticeboard's ID
     *
     * @throws NoSuchElementException if the target Noticeboard does not exist
     * @throws IllegalStateException if the title is already used by another Noticeboard
     * @throws IllegalArgumentException if the title is not valid or if it's too long
     */
    void updateNoticeboardTitle(String newTitle, int boardID);

    /**
     * <p>Changes the description of a Noticeboard.</p>
     * @param newDescription the new description
     * @param boardID the Noticeboard's ID
     *
     * @throws NoSuchElementException if the target Noticeboard does not exist
     * @throws IllegalArgumentException if the description is too long
     */
    void updateNoticeboardDescription(String newDescription, int boardID);

    /**
     * <p>Removes a Noticeboard from the system.</p>
     * @param boardID the Noticeboard's ID
     *
     * @throws NoSuchElementException if the target Noticeboard does not exist
     */
    void removeNoticeboard(int boardID);
}
