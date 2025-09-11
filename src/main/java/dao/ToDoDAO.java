package dao;

//Java imports
import java.time.LocalDateTime;

import java.util.List;
import java.util.List;
import java.util.NoSuchElementException;

//App imports
import dto.ToDoDTO;
import model.Noticeboard;
import model.ToDo;

/**
 * <p>Manages the fundamental Database operations for {@link ToDo} objects.</p>
 */
public interface ToDoDAO {
    /**
     * <p>Gets a List of all the ToDos of a Noticeboard.</p>
     * @param boardID the Noticeboard's ID
     * @return a {@link List} of {@link Integer} of all ToDo IDs.
     */
    List<Integer> getToDoListByBoardID(int boardID);

    /**
     * <p>Loads the ToDos of a Noticeboard.</p>
     * @param boardID the Noticeboard's ID
     * @param todos a {@link List} object to load the {@link Noticeboard} objects into
     * @return {@code 0} if successful, otherwise {@code -1}.
     *
     * @throws IllegalArgumentException if {@code todos==null}
     */
    int loadToDosByBoardID(int boardID, List<ToDo> todos);
    /**
     * <p>Loads a ToDo.</p>
     * @param todoID the ToDo's ID
     * @return the {@link ToDo}, otherwise {@code null}.
     */
    ToDo loadToDoByToDoID(int todoID);

    /**
     * <p>Adds a ToDo to the system.</p>
     * @param todo the corresponding {@link ToDoDTO} object
     * @param boardID the target Noticeboard's ID
     * @return the ToDo's ID if successful, otherwise returns {@code -1}.
     *
     * @throws NoSuchElementException if no Noticeboard with the ID {@code boardID} exists
     * @throws IllegalStateException if a ToDo with the same title exists already in the board
     * @throws IllegalArgumentException if the ToDo's title is not valid or too long or if the background color is in the wrong format
     */
    int addToDo(ToDoDTO todo, int boardID, int boardIndex);

    /**
     * <p>Updates the ToDo's state.</p>
     * @param isCompleted the new ToDo's new state, {@code true} -> {@code Completed}, {@code false} -> {@code NotCompleted}
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     */
    void updateToDoCompletionState(boolean isCompleted, int todoID);

    /**
     * <p>Updates the ToDo's title.</p>
     * @param newTitle the new title
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalStateException if the title is already used by another ToDo
     * @throws IllegalArgumentException if the title is not valid or too long
     */
    void updateToDoTitle(String newTitle, int todoID);

    /**
     * <p>Updates the ToDo's description.</p>
     * @param newDescription the new description
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalArgumentException if the description is too long
     */
    void updateToDoDescription(String newDescription, int todoID);

    /**
     * <p>Updates the ToDo's activity URL.</p>
     * @param newURL the new URL
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalArgumentException if the activity URL is too long
     */
    void updateToDoActivityURL(String newURL, int todoID);

    /**
     * <p>Updates the ToDo's image URL.</p>
     * @param newURL the new URL
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalArgumentException if the image URL is too long
     */
    void updateToDoImageURL(String newURL, int todoID);

    /**
     * <p>Updates the ToDo's expiry date.</p>
     * @param newDate the new expiry date
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalArgumentException if the expiry date is not valid
     */
    void updateToDoExpiryDate(LocalDateTime newDate, int todoID);

    /**
     * <p>Updates the ToDo's background color.</p>
     * @param newColor the new background color
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws IllegalArgumentException if the background color is not valid or too long
     */
    void updateToDoBackgroundColor(String newColor, int todoID);

    /**
     * <p>Moves a ToDo from its index in the board to another index.</p>
     * @param todoID the ToDo's ID
     * @param newIndex the new index
     */
    void moveToDoToIndex(int todoID, int newIndex);

    /**
     * <p>Moves a ToDo from its original Noticeboard to another Noticeboard.</p>
     * @param todoID the ToDo's ID
     * @param oldBoardID the old Noticeboard's ID
     * @param newBoardID the new Noticeboard's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     * @throws NoSuchElementException if no Noticeboard with the ID {@code boardID} exists
     * @throws IllegalStateException if the new Noticeboard already owns a ToDo with the same name as the ToDo
     */
    void moveToDoToBoard(int todoID, int oldBoardID, int newBoardID);

    /**
     * <p>Removes a ToDo from the system.</p>
     * @param todoID the ToDo's ID
     *
     * @throws NoSuchElementException if the target ToDo does not exist
     */
    void removeToDo(int todoID);
}