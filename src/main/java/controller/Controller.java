package controller;

//Java imports
import java.sql.Connection;
import java.time.LocalDateTime;

import java.util.*;

//App imports
import database.DatabaseConnection;

import dao.*;
import daopostgresimplementation.*;

import model.*;
import dto.*;

/**
 * <p>The Controller, acts as an interface between the App's state and the GUI</p>
 */
public class Controller {
    //Members
    private static Controller instance = null;
    private User loggedUser;

    //Utility variables & constants
    private static final String VALID_USERNAME_REGEX = "^[A-Za-z0-9._\\-]+$";

    //Constructor
    /**
     * <p>Private constructor for the Controller class, instantiates a Controller without a state</p>
     */
    private Controller() {
        //Set state
        this.loggedUser = null;
    }


    //Singleton methods
    /**
     * <p>Gets the global instance of the Controller</p>
     * @return the global controller
     */
    public static Controller getInstance() {
        if(instance == null)
            instance = new Controller();

        return instance;
    }

    //State methods
    /**
     * <p>Checks if a User is logged in the system</p>
     * @return returns {@code true} if a user is currently logged, {@code false} otherwise
     */
    public boolean isUserLogged() { return loggedUser != null; }

    /**
     * <p>Gets the current logged User's DTO</p>
     * @return if a user is logged, returns the current logged user's DTO, otherwise returns {@code null}
     */
    public UserDTO getLoggedUser() {
        if(loggedUser == null)
            return null;

        return new UserDTO(loggedUser);
    }

    /**
     * <p>Sets the current logged User</p>
     * @param user the user
     */
    private void setLoggedUser(User user) {
        this.loggedUser = user;
    }


    //User Methods
    /**
     * <p>Gets a {@link Map} of all the Users' pairs of userID|usernames.</p>
     * @return a {@link Map} of all userID|usernames.
     *
     */
    public Map<Integer, String> getUsers(){
        Map<String, Integer> userList = new PostgresUserDAO(DatabaseConnection.getInstance().getConnection()).getUsers();

        Map<Integer, String> reversedUserList = new TreeMap<>();

        if(userList == null)
            return reversedUserList;

        //Add each pair in the user list........ but reversed 😬
        userList.forEach(
            (key, value) -> reversedUserList.put(value, key)
        );

        return reversedUserList;
    }

    /**
     * <p>Checks if a User exists in the system</p>
     * @param username the user's username
     * @return if found returns the User's userID, otherwise returns {@code -1}
     *
     * @throws IllegalArgumentException if the username is not valid or too long
     */
    public int userExists(String username) {
        if(username.length() > 128)
            throw new IllegalArgumentException("The username cannot be longer than 128 characters.");
        if(!username.matches(VALID_USERNAME_REGEX))
            throw new IllegalArgumentException("The username is not valid.");

        UserDAO userDAO = new PostgresUserDAO(DatabaseConnection.getInstance().getConnection());
        return userDAO.userExists(username);
    }

    /**
     * <p>Registers a User in the system and sets it as current User</p>
     * @param username the user's username
     * @param password the user's password
     * @return {@code true } if successful, {@code false} if the user already exists in the system.
     *
     * @throws IllegalArgumentException if the username is not valid or either the username or password are too long
     */
    public boolean registerUser(String username, String password){
        //Validate credentials
        this.validateUserCredentials(username, password);

        //Register user
        Connection con = DatabaseConnection.getInstance().getConnection();
        UserDAO userDAO = new PostgresUserDAO(con);

        int authResult = userDAO.registerUser(username, password);
        if(authResult == -1)
            return false;
        else
            this.initUserSession(authResult, userDAO);

        return true;
    }

    /**
     * <p>Authenticates a User in the system and sets it as current User</p>
     * @param username the user's username
     * @param password the user's password
     * @return {@code 0} if successful, {@code -1} if the password is incorrect and {@code -2} if the user is not registered in the system.
     *
     * @throws IllegalArgumentException if the username is not valid or either the username or password are too long
     */
    public int authenticateUser(String username, String password) {
        //Validate credentials
        this.validateUserCredentials(username, password);

        //Auth user
        Connection con = DatabaseConnection.getInstance().getConnection();
        UserDAO userDAO = new PostgresUserDAO(con);

        int authResult = userDAO.authUser(username, password);
        if(authResult < 0)
            return authResult;
        else
            this.initUserSession(authResult, userDAO);

        return 0;
    }

    /**
     * <p>Initializes a new session for a User.</p>
     * @param userID the User's ID
     * @param userDAO the UserDAO to load the User data from
     */
    private void initUserSession(int userID, UserDAO userDAO) {
        //Initialize and set current user
        User usr = userDAO.getUserMetadata(userID);
        this.loadUserNoticeboards(usr.getUserID(), usr.getNoticeboards());
        this.setLoggedUser(usr);
    }

    /**
     * <p>Invalidates the current User's cached data </p>
     *
     * @throws IllegalStateException if no user is currently logged in the system.
     */
    public void reloadUserData(){
        if(!isUserLogged())
            throw new IllegalStateException("The user is not logged.");

        //Invalidate cached data and reload
        this.loadUserNoticeboards(this.loggedUser.getUserID(), this.loggedUser.getNoticeboards());
    }

    //Noticeboard methods
    /**
     * <p>Gets the logged User's Noticeboard identified by {@code boardID}</p>
     * @param boardID the Noticeboard's ID
     * @return if found, returns the noticeboard wrapped as a {@link NoticeboardDTO}, otherwise returns {@code null}
     */
    public NoticeboardDTO getNoticeboard(int boardID) {
        return loggedUser.getNoticeboards().stream().filter(board -> board.getBoardID() == boardID)
                .findFirst().map(NoticeboardDTO::new).orElse(null);
    }

    /**
     * <p>Gets the logged User's Noticeboards</p>
     * @return returns the noticeboards wrapped as a {@link List} of {@link NoticeboardDTO}
     */
    public List<NoticeboardDTO> getNoticeboards() {
        return loggedUser.getNoticeboards().stream().map(NoticeboardDTO::new).toList();
    }

    /**
     * <p>Adds a Noticeboard to the logged User</p>
     * @param noticeboard the noticeboard
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}, or if the title is not valid, or the title or description are too long
     * @throws IllegalStateException if a noticeboard with the same title exists already for the user
     */
    public void addNoticeboard(NoticeboardDTO noticeboard) {
        //Validity check on Noticeboard metadata
        validateNoticeboardMetadata(noticeboard);

        //Sync DB state
        NoticeboardDAO boardDAO = new PostgresNoticeboardDAO(DatabaseConnection.getInstance().getConnection());
        int boardID = boardDAO.addNoticeboard(noticeboard.getTitle(), noticeboard.getDescription(), loggedUser.getUserID());

        //Sync App state
        if (boardID != -1) {
            NoticeboardDTO newBoard = new NoticeboardDTO(boardID, noticeboard.getTitle(), noticeboard.getDescription(), this.getLoggedUser().getUserID());
            loggedUser.addNoticeboard(fromDTO(newBoard));
        }
    }

    /**
     * <p>Deletes the logged User's Noticeboard</p>
     * @param boardID the target Noticeboard's ID
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     */
    public void deleteNoticeboardByID(int boardID) {
        //Sync DB state
        NoticeboardDAO boardDAO = new PostgresNoticeboardDAO(DatabaseConnection.getInstance().getConnection());
        boardDAO.removeNoticeboard(boardID);

        //Sync App state
        loggedUser.deleteNoticeboard(boardID);
    }

    /**
     * <p>Updates the title and description of the logged User's Noticeboard</p>
     * @param noticeboard the new Noticeboard's data
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}
     * @throws NoSuchElementException if the target noticeboard does not exist
     * @throws IllegalArgumentException if the board's ID is negative, if the title is not valid or if either the title or description are too long
     */
    public void updateNoticeboard(NoticeboardDTO noticeboard) {
        //Validity check on Noticeboard metadata and boardID
        this.validateNoticeboardMetadata(noticeboard);
        if(noticeboard.getBoardID() < 0)
            throw new IllegalArgumentException("The board ID cannot be negative");

        Noticeboard target = loggedUser.getNoticeboard(noticeboard.getBoardID());
        if(target == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + noticeboard.getUserID() + ") does not exist");

        //Sync DB state
        NoticeboardDAO boardDAO = new PostgresNoticeboardDAO(DatabaseConnection.getInstance().getConnection());
        boardDAO.updateNoticeboard(noticeboard);

        //Sync App state
        target.setTitle(noticeboard.getTitle());
        target.setDescription(noticeboard.getDescription());
    }


    //ToDo methods
    /**
     * <p>Gets ToDo from a Noticeboard</p>
     * @param boardID the Noticeboard's ID
     * @param todoID the ToDo's ID
     * @return the ToDo if the noticeboard contains it, {@code null} otherwise
     *
     * @throws NoSuchElementException if the target noticeboard does not exist
     */
    public ToDo getToDo(int boardID, int todoID){
        Noticeboard board = loggedUser.getNoticeboard(boardID);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");

        return board.getToDo(todoID);
    }

    /**
     * <p>Adds ToDo to a Noticeboard</p>
     * @param boardID the board's ID
     * @param todo the todo
     *
     * @throws IllegalArgumentException if {@code todo} is {@code null}
     * @throws IllegalArgumentException if the title is invalid or too long, or if the background color's format is not valid
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws IllegalStateException if a todo with the same title already exists in the board
     */
    public void addToDo(int boardID, ToDoDTO todo){
        if(todo == null)
            throw new IllegalArgumentException("Cannot add a null ToDo to a Noticeboard");

        //Validity check on ToDo metadata
        if(todo.getTitle().length() > 128)
            throw new IllegalArgumentException("The title cannot be longer than 128 characters.");
        if(!todo.getTitle().matches("^[A-Za-z0-9@#&_.\\- ]+$"))
            throw new IllegalArgumentException("The title is not valid.");
        if(!todo.getBackgroundColor().matches("^#[0-9A-Fa-f]{6}$"))
            throw new IllegalArgumentException("ToDo background color is not valid, must be in the following format \"#RRGGBB\"");

        Noticeboard board = loggedUser.getNoticeboard(boardID);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");

        if(board.getToDo(todo.getToDoID()) != null) //ToDo exists already
            throw new IllegalStateException("A ToDo with the same title exists already, duplicate titles are not allowed");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        int todoID = todoDAO.addToDo(todo, boardID, board.getToDoCount());

        //Sync App state
        if (todoID != -1) {
            ToDoDTO newToDo = new ToDoDTO(todoID, todo.isCompleted(), todo.getTitle(), todo.getDescription(), todo.getActivityURL(), todo.getImageURL(), todo.getExpiryDate(), todo.getOwnerUserID(), todo.getBackgroundColor());
            board.addToDo(fromDTO(newToDo));
        }
    }

    /**
     * <p>Deletes ToDo from the User's Noticeboard</p>
     * <p>If the logged User is the ToDo's owner, removes ToDo from the Noticeboard of the Users who share it</p>
     * @param todoID the ToDo's ID
     * @param boardID the Noticeboard's ID
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     *
     */
    public void deleteToDo(int boardID, int todoID){
        Noticeboard board = loggedUser.getNoticeboard(boardID);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");

        ToDo todo = board.getToDo(todoID);
        if(todo == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.removeToDo(todo.getToDoID()); //Removes ToDo and sharing data from DB

        //Sync App State
        board.deleteToDo(todoID);
    }

    /**
     * <p>Updates the completion state of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateCompletionState(int boardID, int todoID){
        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoCompletionState(!target.isCompleted(), todoID);

        //Sync App state
        target.changeCompletionState();
    }

    /**
     * <p>Updates the title of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newTitle the new title
     *
     * @throws IllegalArgumentException if the new title is null or blank
     * @throws IllegalStateException if a todo with the same title already exists
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoTitle(int boardID, int todoID, String newTitle){
        //Validity check on ToDo metadata
        if(newTitle == null || newTitle.isBlank())
            throw new IllegalArgumentException("The new title cannot be null or empty");
        if(newTitle.length() > 128)
            throw new IllegalArgumentException("The title cannot be longer than 128 characters.");
        if(!newTitle.matches("^[A-Za-z0-9@#&_.\\- ]+$"))
            throw new IllegalArgumentException("The title is not valid.");

        Noticeboard board = loggedUser.getNoticeboard(boardID);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");

        //Check if title is already used
        if(board.getToDo(newTitle) != null)
            throw new IllegalStateException("A todo with the title \"" + newTitle + "\" already exists");

        ToDo target = board.getToDo(todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoTitle(newTitle, todoID);

        //Sync App state
        target.setTitle(newTitle);
    }

    /**
     * <p>Updates the description of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newDescription the new description
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     * @throws IllegalArgumentException if the new description is too long
     */
    public void updateToDoDescription(int boardID, int todoID, String newDescription){
        if(newDescription.length() > 256)
            throw new IllegalArgumentException("The new description cannot be longer than 256 characters.");

        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoDescription(newDescription, todoID);

        //Sync App state
        target.setDescription(newDescription);
    }

    /**
     * <p>Updates the expiry date of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newExpiryDate the new expiry date
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     */
    public void updateToDoExpiryDate(int boardID, int todoID, LocalDateTime newExpiryDate){
        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoExpiryDate(newExpiryDate, todoID);

        //Sync App state
        target.setExpiryDate(newExpiryDate);
    }

    /**
     * <p>Updates the activity URL of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newActivityURL the new activity url
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     * @throws IllegalArgumentException if the new activity URL is too long
     */
    public void updateToDoActivityURL(int boardID, int todoID, String newActivityURL){
        if(newActivityURL.length() > 256)
            throw new IllegalArgumentException("The new activity URL cannot be longer than 256 characters.");

        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoActivityURL(newActivityURL, todoID);

        //Sync App state
        target.setActivityURL(newActivityURL);
    }

    /**
     * <p>Updates the image URL of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newImageURL the new image url
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     * @throws IllegalArgumentException if the new image URL is too long
     */
    public void updateToDoImageURL(int boardID, int todoID, String newImageURL){
        if(newImageURL.length() > 256)
            throw new IllegalArgumentException("The new image URL cannot be longer than 256 characters.");

        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoImageURL(newImageURL, todoID);

        //Sync App state
        target.setImageURL(newImageURL);
    }

    /**
     * <p>Updates the background color of a Noticeboard's ToDo.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param newBackgroundColor the new background color
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     * @throws IllegalArgumentException if the new background color is not valid or too long
     */
    public void updateToDoBackgroundColor(int boardID, int todoID, String newBackgroundColor){
        if(!newBackgroundColor.matches("^#[0-9A-Fa-f]{6}$"))
            throw new IllegalArgumentException("ToDo background color is not valid, must be in the following format \"#RRGGBB\"");
        if(newBackgroundColor.length() > 7)
            throw new IllegalArgumentException("The new background color cannot be longer than 7 characters.");

        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.updateToDoBackgroundColor(newBackgroundColor, todoID);

        //Sync App state
        target.setBackgroundColor(newBackgroundColor);
    }

    /**
     * <p>Shares a ToDo from a Noticeboard of a User.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param username the User's username
     *
     * @throws NoSuchElementException if the Noticeboard does not exist
     * @throws NoSuchElementException if the ToDo does not exist
     * @throws NoSuchElementException if the User does not exist
     * @throws IllegalStateException if the ToDo is already shared with the User
     * @throws IllegalArgumentException if the User is trying to share a ToDo with itself
     */
    public void addSharedUser(int boardID, int todoID, String username){
        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Assert that user exists & get its ID
        int userID = this.userExists(username);

        if(userID == -1) {
            //Sync DB state
            SharingDAO shareDAO = new PostgresSharingDAO(DatabaseConnection.getInstance().getConnection());
            shareDAO.shareToDo(userID, todoID);

            //Sync App state
            target.addSharedUser(username);
        }
    }

    /**
     * <p>Unshares a ToDo from a Noticeboard of a User.</p>
     * @param boardID the target Noticeboard's ID
     * @param todoID the target ToDo's ID
     * @param username the user's username
     *
     * @throws NoSuchElementException if the Noticeboard does not exist
     * @throws NoSuchElementException if the ToDo does not exist
     * @throws NoSuchElementException if the User does not exist
     * @throws IllegalStateException if the ToDo is not shared with the User
     */
    public void removeSharedUser(int boardID, int todoID, String username){
        //Check for local Noticeboard & ToDo existence
        ToDo target = this.getToDo(boardID, todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

        //Assert that user exists & get its ID
        int userID = this.userExists(username);

        if(userID != -1) {
            //Sync DB state
            SharingDAO shareDAO = new PostgresSharingDAO(DatabaseConnection.getInstance().getConnection());
            shareDAO.unshareToDo(userID, todoID);

            //Sync App state
            target.removeSharedUser(username);
        }
    }

    /**
     * <p>Moves a ToDo from its position in its Noticeboard to another position</p>
     * @param boardID the Noticeboard's ID
     * @param todoID the ToDo's ID
     * @param newIndex the new position's index
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws NoSuchElementException if the todo does not exist
     * @throws IndexOutOfBoundsException if {@code newIndex < 0} or {@code newIndex} is bigger or equal to the Noticeboard's size
     */
    public void moveToDoToIndex(int boardID, int todoID, int newIndex){
        Noticeboard board = loggedUser.getNoticeboard(boardID);
        if(board == null)
            throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");

        ToDo target = board.getToDo(todoID);
        if(target == null)
            throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist in board (ID: " + boardID + ")");

        if(newIndex < 0 || newIndex >= board.getToDoCount())
            throw new IndexOutOfBoundsException("Invalid index.\nCannot move todo to index " + newIndex);
        else {
            //Sync DB state
            ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
            todoDAO.moveToDoToIndex(todoID, newIndex);

            //Sync App state
            List<ToDo> todos = board.getToDos();
            int targetIndex = todos.indexOf(target);

            todos.remove(targetIndex);

            // Adjust newIndex if the removal shifted it
            if (newIndex > targetIndex)
                newIndex--;

            todos.add(newIndex, target);
        }
    }

    /**
     * <p>Moves a ToDo from its original Noticeboard to another</p>
     * @param todoID the ToDo's ID
     * @param newBoardID the new Noticeboard's ID
     *
     * @throws NoSuchElementException if either the original or new Noticeboard do not exist, or if the todo does not exist
     * @throws IllegalArgumentException if the new Noticeboard is not owned by the User
     * @throws IllegalStateException if the new Noticeboard already owns a ToDo with the same name as the ToDo
     */
    public void moveToDoToBoard(int boardID, int todoID, int newBoardID){
        Noticeboard oldBoard = loggedUser.getNoticeboard(boardID);
        Noticeboard newBoard = loggedUser.getNoticeboard(newBoardID);
        if(oldBoard == null)
            throw new NoSuchElementException("The original noticeboard does not exist");
        if(newBoard == null)
            throw new NoSuchElementException("The new noticeboard does not exist");

        if(newBoard.getUserID() != this.getLoggedUser().getUserID())
            throw new IllegalArgumentException("The new noticeboard is not owned by User \"" + this.getLoggedUser().getUsername() + "\"");

        ToDo todo = oldBoard.getToDo(todoID);
        if(todo == null)
            throw new NoSuchElementException("The target todo does not exist");

        //Sync DB state
        ToDoDAO todoDAO = new PostgresToDoDAO(DatabaseConnection.getInstance().getConnection());
        todoDAO.moveToDoToBoard(todoID, boardID, newBoardID);

        //Sync App state
        newBoard.addToDo(todo);
        oldBoard.deleteToDo(todo.getToDoID());
    }

    //Utility methods
    /**
     * <p>Loads the Noticeboards and ToDos of a User.</p>
     * @param userID the user's ID
     * @param boards a {@link List} object to load the {@link Noticeboard} objects into (it'll be cleared if not empty)
     */
    private void loadUserNoticeboards(int userID, List<Noticeboard> boards) {
        //Clear any data
        boards.clear();

        //Load User's Noticeboards&ToDos
        Connection con = DatabaseConnection.getInstance().getConnection();

        PostgresNoticeboardDAO boardDAO = new PostgresNoticeboardDAO(con);
        PostgresSharingDAO sharingDAO = new PostgresSharingDAO(con);

        boardDAO.loadNoticeboardsByUserID(userID, boards);
        sharingDAO.loadSharedNoticeboardsByUserID(userID, boards);
    }
    /**
     * <p>Creates a new instance of Noticeboard from a NoticeboardDTO object source.</p>
     * @param noticeboardDTO the source NoticeboardDTO
     * @return the new noticeboard instance
     */
    private Noticeboard fromDTO(NoticeboardDTO noticeboardDTO) {
        Noticeboard noticeboard = new Noticeboard(noticeboardDTO.getBoardID(), noticeboardDTO.getTitle(), noticeboardDTO.getDescription(), noticeboardDTO.getUserID());

        //Convert DTOs to ToDos, then add to board
        List<ToDoDTO> sourceTodos = noticeboardDTO.getToDos();
        if(!sourceTodos.isEmpty()) {
            List<ToDo> todos = sourceTodos.stream().map(this::fromDTO).toList();
            noticeboard.getToDos().addAll(todos);
        }

        return noticeboard;
    }
    /**
     * <p>Creates a new instance of ToDo from a ToDoDTO object source.</p>
     * @param todoDTO the source ToDoDTO
     * @return the new todo instance
     */
    private ToDo fromDTO(ToDoDTO todoDTO) {
        ToDo todo = new ToDo(todoDTO.getToDoID(), todoDTO.isCompleted(), todoDTO.getTitle(), todoDTO.getDescription(), todoDTO.getActivityURL(), todoDTO.getImageURL(), todoDTO.getExpiryDate(), todoDTO.getOwnerUserID(), todoDTO.getBackgroundColor());

        //Copy sharing data
        List<String> shares = todoDTO.getSharedUsers();
        if(!shares.isEmpty())
            for(String share : shares)
                todo.addSharedUser(share);

        return todo;
    }

    /**
     * <p>Asserts that the User credentials are valid.</p>
     * @param username the User's username
     * @param password the User's password
     *
     * @throws IllegalArgumentException if the username is not valid or either the username or password are too long
     */
    private void validateUserCredentials(String username, String password) {
        if(username.length() > 128)
            throw new IllegalArgumentException("The username cannot be longer than 128 characters.");
        if(!username.matches(VALID_USERNAME_REGEX))
            throw new IllegalArgumentException("The username is not valid.");
        if(password.length() > 128)
            throw new IllegalArgumentException("The password cannot be longer than 128 characters.");
    }
    /**
     * <p>Asserts that the metadata of a Noticeboard is valid.</p>
     * @param noticeboard the Noticeboard's DTO
     *
     * @throws IllegalArgumentException if the title is not valid, or the title or description are too long
     */
    private void validateNoticeboardMetadata(NoticeboardDTO noticeboard) {
        if(noticeboard.getTitle().length() > 128)
            throw new IllegalArgumentException("The title cannot be longer than 128 characters.");
        if(!noticeboard.getTitle().matches("[A-Za-z0-9@#&_.\\- ]+$"))
            throw new IllegalArgumentException("The title is not valid.");
        if(noticeboard.getDescription().length() > 256)
            throw new IllegalArgumentException("The description cannot be longer than 256 characters.");
    }
}