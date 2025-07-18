package controller;

//App imports
import model.*;
import dto.*;

//Java imports
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>The Controller, acts as an interface between the App's state and the GUI.</p>
 */
public class Controller {
    //Members
    private static Controller instance = null;
    private User loggedUser;

    //Constructor
    /**
     * <p>Private constructor for the Controller class, instantiates a Controller without a state.</p>
     */
    private Controller() {
        //Setting up state
        this.loggedUser = null;
    }


    //Singleton methods
    /**
     * <p>Gets the global instance of the Controller</p>
     * @return the global controller
     */
    public static Controller get() {
        if(instance == null)
            instance = new Controller();

        return instance;
    }

    //State methods
    /**
     * <p>Checks if a user is logged in the system.</p>
     * @return returns {@code true} if a user is currently logged, {@code false} otherwise
     */
    public boolean isUserLogged() { return loggedUser != null; }

    //TEMPORARILY ADOPTED SOLUTION BEFORE DATABASE
    /**
     * <p>Checks if a user exists in the system.</p>
     * @param username the user's username
     * @return returns {@code true} if the user exists, {@code false} otherwise
     */
    public boolean userExists(String username) { return true; }


    //User Methods
    /**
     * <p>Gets the current logged user's DTO.</p>
     * @return if a user is logged, returns the current logged user's DTO, otherwise returns {@code null}
     */
    public UserDTO getLoggedUser() {
        if(loggedUser == null)
            return null;
        else
            return new UserDTO(loggedUser);
    }

    /**
     * <p>Sets the current logged user.</p>
     * @param user the user
     */
    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    /**
     * <p>Registers a user in the system.</p>
     * @param username the user's username
     * @param password the user's passowrd
     * @return returns {@code 0} if successful, returns {@code -1} if user exists already
     */
    public int registerUser(String username, String password) {
        /*
        if(userExistsInDatabase(...))
            return -1;

        User newUser = new User(username, password);
        ArrayList<Noticeboard> boards = newUser.getNoticeboards();
        boards.add(new Noticeboard("Universita","Studio, Homework e Progetti."));
        boards.add(new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro"));
        boards.add(new Noticeboard("Tempo libero","Attivita ed Hobby da svolgere durante il tempo Libero"));

        addUserToDatabase(...);
         */

        return 0;
    }

    /**
     * <p>Logs a user in the system.</p>
     * @param username the user's username
     * @param password the user's passowrd
     * @return returns {@code 0} if successful, otherwise returns {@code -1}
     */
    public int loginUser(String username, String password) {
        /*
        if(!userExistsInDatabase(...))
            return -1;

        User newUser = new User(username, password);
        ArrayList<Noticeboard> boards = newUser.getNoticeboards();
        boards.add(new Noticeboard("Universita","Studio, Homework e Progetti."));
        boards.add(new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro"));
        boards.add(new Noticeboard("Tempo libero","Attivita ed Hobby da svolgere durante il tempo Libero"));

        addUserToDatabase(...);
         */

        return 0;
    }


    //Noticeboard methods
    /**
     * <p>Gets the logged user's noticeboard identified by {@code title}</p>
     * @param title the noticeboard's title
     * @return if found, returns the noticeboard wrapped as a {@link NoticeboardDTO}, otherwise returns {@code null}
     */
    public NoticeboardDTO getNoticeboard(String title) {
        return loggedUser.getNoticeboards().stream().filter(board -> board.getTitle().equals(title))
                .findFirst().map(NoticeboardDTO::new).orElse(null);
    }

    /**
     * <p>Gets the logged user's noticeboard count</p>
     * @return returns the noticeboards'count
     */
    public int getNoticeboardCount() {
        return loggedUser.getNoticeboardCount();
    }

    /**
     * <p>Gets the logged user's noticeboards</p>
     * @return returns the noticeboards wrapped as a {@link List} of {@link NoticeboardDTO}
     */
    public List<NoticeboardDTO> getNoticeboards() {
        return loggedUser.getNoticeboards().stream().map(NoticeboardDTO::new).toList();
    }

    /**
     * <p>Adds a noticeboard to the logged user</p>
     * @param noticeboard the noticeboard
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}
     * @throws IllegalStateException if a noticeboard with the same title exists already
     */
    public void addNoticeboard(NoticeboardDTO noticeboard) {
        loggedUser.addNoticeboard(fromDTO(noticeboard));
    }

    /**
     * <p>Deletes the logged user's noticeboard.</p>
     * @param title the target noticeboard title
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     */
    public void deleteNoticeboard(String title) {
        loggedUser.deleteNoticeboard(title);
    }

    /**
     * <p>Updates the title and description of the logged user's noticeboard</p>
     * @param noticeboardTitle the target noticeboard's title
     * @param noticeboard the new noticeboard's data
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}
     * @throws NoSuchElementException if the target noticeboard does not exist
     */
    public void updateNoticeboard(String noticeboardTitle, NoticeboardDTO noticeboard) {
        if(noticeboard == null)
            throw new IllegalArgumentException("The new noticeboard data cannot be null");

        Noticeboard target = loggedUser.getNoticeboard(noticeboardTitle);
        if(target == null)
            throw new NoSuchElementException("The target noticeboard " + noticeboardTitle + " does not exist");

        target.setTitle(noticeboard.getTitle());
        target.setDescription(noticeboard.getDescription());
    }


    //ToDo methods
    /**
     * <p>Gets ToDo from a Noticeboard, both identified by their title.</p>
     * @param boardTitle the noticeboard's title
     * @param todoTitle the todo's title
     * @return the ToDo if the noticeboard contains it, {@code null} otherwise
     *
     * @throws NoSuchElementException if the target noticeboard does not exist
     */
    public ToDo getToDo(String boardTitle, String todoTitle){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        return board.getToDo(todoTitle);
    }

    /**
     * <p>Gets ToDos from the Noticeboard identified by {@code title}.</p>
     * @param title the noticeboard's title
     * @return if the board exists, returns the ToDos, wrapped as a {@link List} of {@link ToDoDTO}
     */
    public List<ToDoDTO> getToDos(String title) {
        Noticeboard board = loggedUser.getNoticeboard(title);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + title + " does not exist");

        return board.getToDos().stream().map(ToDoDTO::new).toList();
    }

    /**
     * <p>Adds ToDo to the noticeboard identified by {@code boardTitle}.</p>
     * @param boardTitle the board's title
     * @param todo the todo
     *
     * @throws IllegalArgumentException if {@code todo} is {@code null}
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws IllegalStateException if a todo with the same title already exists in the board
     *
     */
    public void addToDo(String boardTitle, ToDoDTO todo){
        if(todo == null)
            throw new IllegalArgumentException("Cannot add a null ToDo to a Noticeboard");

        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        if(board.getToDo(todo.getTitle()) != null) //ToDo exists already
            throw new IllegalStateException("A ToDo with the same title exists already, duplicate titles are not allowed");

        board.addToDo(fromDTO(todo));
    }

    /**
     * <p>Deletes todo from the user's noticeboard, both are identified by their title.</p>
     * <p>If the logged user is the ToDo's owner, removes ToDo from the noticeboard of the users who share it.</p>
     * @param todoTitle the todo's title
     * @param boardTitle the noticeboard's title
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     *
     */
    public void deleteToDo(String boardTitle, String todoTitle){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo todo = board.getToDo(todoTitle);
        if(todo == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        //Remove sharing data
        if(todo.getOwner().equals(loggedUser.getUsername())){
            ArrayList<String> shared = todo.getSharedUsers();
            while(!shared.isEmpty())
                todo.removeSharedUser(shared.getFirst());
        }

        board.deleteToDo(todoTitle);
    }

    /**
     * <p>Updates the attributes of a noticeboard's todo.</p>
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param todo the new todo's data
     *
     * @throws IllegalArgumentException if {@code todo} is {@code null}
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDo(String boardTitle, String todoTitle, ToDoDTO todo) {
        if(todo == null)
            throw new IllegalArgumentException("The new todo data cannot be null");

        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist in board " + boardTitle);

        target.setTitle(todo.getTitle());
        target.setDescription(todo.getDescription());
        target.setExpiryDate(todo.getExpiryDate());
        target.setActivityURL(todo.getActivityURL());
        target.setImageURL(todo.getImageURL());
        target.setBackGroundColor(todo.getBackGroundColor());
    }

    /**
     * <p>Moves a todo from its position in its noticeboard to another position {@code newIndex}</p>
     * @param boardTitle the board's title
     * @param todoTitle the todo's title
     * @param newIndex the new position's index
     *
     * @throws IndexOutOfBoundsException if {@code newIndex < 0} or {@code newIndex} is bigger or equal to the noticeboard's size
     */
    public void moveToDoToIndex(String boardTitle, String todoTitle, int newIndex){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        ToDo target = board.getToDo(todoTitle);

        if(newIndex < 0 || newIndex >= board.getToDoCount())
            throw new IndexOutOfBoundsException("Invalid index.\nCannot move " + todoTitle + " to index " + newIndex);
        else {
            ArrayList<ToDo> todos = board.getToDos();
            int targetIndex = todos.indexOf(target);

            todos.remove(targetIndex);

            // Adjust newIndex if the removal shifted it
            if (newIndex > targetIndex)
                newIndex--;

            todos.add(newIndex, target);
        }
    }

    /**
     * <p>Moves a todo from its original noticeboard to another.</p>
     * @param boardTitle the original noticeboard's title
     * @param todoTitle the todo's title
     * @param newBoardTitle the new board's title
     *
     * @throws NoSuchElementException if either the original or new noticeboards do not exist, or if the todo does not exist
     */
    public void moveToDoToBoard(String boardTitle, String todoTitle, String newBoardTitle){
        Noticeboard oldBoard = loggedUser.getNoticeboard(boardTitle);
        Noticeboard newBoard = loggedUser.getNoticeboard(newBoardTitle);
        if(oldBoard == null)
            throw new NoSuchElementException("The original noticeboard does not exist");
        if(newBoard == null)
            throw new NoSuchElementException("The new noticeboard does not exist");

        ToDo todo = oldBoard.getToDo(todoTitle);
        if(todo == null)
            throw new NoSuchElementException("The target todo does not exist");

        newBoard.addToDo(todo);
        oldBoard.deleteToDo(todo.getTitle());
    }

    /**
     * Updates the completion state of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void changeCompletionState(String boardTitle, String todoTitle){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.changeCompletionState();
    }

    /**
     * Updates the title of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param title the title
     *
     * @throws IllegalArgumentException if the new title is null or blank
     * @throws IllegalStateException if a todo with the same title already exists
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoTitle(String boardTitle, String todoTitle, String title){
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("The new title cannot be null or empty");

        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        //Check if title is already used
        if(board.getToDo(title) != null)
            throw new IllegalStateException("A noticeboard with the title \"" + boardTitle + "\" already exists");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setTitle(title);
    }

    /**
     * Updates the description of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param description the description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoDescription(String boardTitle, String todoTitle, String description){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setDescription(description);
    }

    /**
     * Updates the expirtyDate of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param expiryDate the description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoExpiryDate(String boardTitle, String todoTitle, LocalDateTime expiryDate){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setExpiryDate(expiryDate);
    }

    /**
     * Updates the activity URL of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param activityURL the description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoActivityURL(String boardTitle, String todoTitle, String activityURL){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setActivityURL(activityURL);
    }

    /**
     * Updates the image URL of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param imageURL the description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoImageURL(String boardTitle, String todoTitle, String imageURL){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setImageURL(imageURL);
    }

    /**
     * Updates the background color of a noticeboard's todo.
     * @param boardTitle the target noticeboard's title
     * @param todoTitle the target todo's title
     * @param backgroundColor the description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoBackgroundColor(String boardTitle, String todoTitle, String backgroundColor){
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo target = board.getToDo(todoTitle);
        if(target == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        target.setBackGroundColor(backgroundColor);
    }

    /**
     * Shares a todo from a noticeboard, both identified with their title, with the user identified by {@code username}.
     * @param boardTitle the noticeboard's title
     * @param todoTitle the todo's title
     * @param username the user's username
     * @return returns {@code 0} if successful, returns {@code -1} if todo is already shared with {@code user}
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public int addSharedUser(String boardTitle, String todoTitle, String username) {
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo todo = board.getToDo(todoTitle);
        if(todo == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        return todo.addSharedUser(username);
    }

    /**
     * Unshares a todo from a noticeboard, both identified with their title, with the user identified by {@code username}.
     * @param boardTitle the noticeboard's title
     * @param todoTitle the todo's title
     * @param username the user's username
     * @return returns {@code 0} if successful, returns {@code -1} if todo is not shared with {@code user}, returns {@code -2} if todo doesn't exist and returns {@code -3} if board doesn't exist
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public int removeSharedUser(String boardTitle, String todoTitle, String username) {
        Noticeboard board = loggedUser.getNoticeboard(boardTitle);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard " + boardTitle + " does not exist");

        ToDo todo = board.getToDo(todoTitle);
        if(todo == null)
            throw new NoSuchElementException("The target todo " + todoTitle + " does not exist");

        return todo.removeSharedUser(username);
    }


    //Utility methods
    /**
     * Creates a new instance of User from a UserDTO object source.
     * @param userDTO the source UserDTO
     * @return the new user instance
     */
    private User fromDTO(UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getPassword());

        //Convert DTOs to Noticeboard, then add to user
        List<NoticeboardDTO> sourceBoards = userDTO.getNoticeboards();
        if(sourceBoards != null) {
            List<Noticeboard> noticeboards = sourceBoards.stream().map(this::fromDTO).toList();
            user.getNoticeboards().addAll(noticeboards);
        }

        return user;
    }

    /**
     * Creates a new instance of Noticeboard from a NoticeboardDTo object source.
     * @param noticeboardDTO the source NoticeboardDTO
     * @return the new noticeboard instance
     */
    private Noticeboard fromDTO(NoticeboardDTO noticeboardDTO) {
        Noticeboard noticeboard = new Noticeboard(noticeboardDTO.getTitle(), noticeboardDTO.getDescription());

        //Convert DTOs to ToDos, then add to board
        List<ToDoDTO> sourceTodos = noticeboardDTO.getToDos();
        if(sourceTodos != null){
            List<ToDo> todos = sourceTodos.stream().map(this::fromDTO).toList();
            noticeboard.getToDos().addAll(todos);
        }

        return noticeboard;
    }

    /**
     * Creates a new instance of ToDo from a ToDoDTO object source.
     * @param todoDTO the source ToDoDTO
     * @return the new todo instance
     */
    private ToDo fromDTO(ToDoDTO todoDTO) {
        ToDo todo = new ToDo(todoDTO.getTitle(), todoDTO.getDescription(), todoDTO.getExpiryDate(), todoDTO.getActivityURL(), todoDTO.getImageURL(), todoDTO.getOwner(), todoDTO.getBackGroundColor());

        //Copy the state
        if(todoDTO.isCompleted())
            todo.changeCompletionState();

        //Copy sharing data
        List<String> shares = todoDTO.getSharedUsers();
        if(shares != null)
            for(String share : shares)
                todo.addSharedUser(share);

        return todo;
    }
}