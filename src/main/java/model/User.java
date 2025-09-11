package model;

//Java imports
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A User in the model.</p>
 * <p>Every User has a collection of {@link Noticeboard}.</p>
 * <p>The class provides methods to retrieve, add, remove and get the count of the Noticeboards.</p>
 */
public class User {
    private final int userID;
    private final String username;
    private final String password;

    private final ArrayList<Noticeboard> boards;

    /**
     * <p>Instantiates a new User.</p>
     * @param userID the user ID
     * @param username the username
     * @param password the password
     *
     * @throws IllegalArgumentException if {@code userID} is negative
     * @throws IllegalArgumentException if {@code username} is {@code null} or blank
     * @throws IllegalArgumentException if {@code password} is {@code null} or blank
     */
    public User(int userID, String username, String password){
        if(userID < 0)
            throw new IllegalArgumentException("User ID cannot be negative");
        if(username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");
        if(password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be null or blank");

        this.userID = userID;
        this.username = username;
        this.password = password;
        boards = new ArrayList<>();
    }

    //Getter & Setter methods
    /**
     * <p>Gets the User's ID.</p>
     * @return the ID
     */
    public int getUserID(){ return userID; }
    /**
     * <p>Gets the User's username.</p>
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * <p>Gets the User's password.</p>
     * @return the password
     */
    public String getPassword() { return password; }

    //Noticeboard methods
    /**
     * <p>Gets the User's {@link Noticeboard}s.</p>
     * @return the Noticeboards, as a {@link List} of {@link Noticeboard}
     */
    public List<Noticeboard> getNoticeboards() { return boards; }

    /**
     * <p>Gets the count of the User's Noticeboards.</p>
     * @return the count of Noticeboards
     */
    public int getNoticeboardCount() { return boards.size(); }

    /**
     * <p>Gets a Noticeboard from the User.</p>
     * @param title the title
     * @return the noticeboard if the user is able to view it, else {@code null}
     */
    public Noticeboard getNoticeboard(String title){
        return boards.stream().filter(board -> board.getTitle().equals(title))
                .findFirst().orElse(null);
    }
    /**
     * <p>Gets a Noticeboard from the User.</p>
     * @param boardID the ID
     * @return the noticeboard if the user is able to view it, else {@code null}
     */
    public Noticeboard getNoticeboard(int boardID){
        return boards.stream().filter(board -> board.getBoardID() == boardID)
                .findFirst().orElse(null);
    }

    /**
     * <p>Adds a Noticeboard to the User.</p>
     * @param noticeboard the noticeboard
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}
     * @throws IllegalStateException if a noticeboard with the same ID exists already
     */
    public void addNoticeboard(Noticeboard noticeboard){
        if(noticeboard == null)
            throw new IllegalArgumentException("You cannot add a null Noticeboard to a User");

        if(this.getNoticeboard(noticeboard.getBoardID()) != null) //Board exists already
            throw new IllegalStateException("A Noticeboard with the same title exists already, duplicate titles are not allowed");

        boards.add(noticeboard);
    }

    /**
     * <p>Deletes a Noticeboard from the User.</p>
     * @param title the title
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     */
    public void deleteNoticeboard(String title){
        Noticeboard b = this.getNoticeboard(title);
        if(b == null)
            throw new NoSuchElementException("Cannot remove Noticeboard  \"" + title + "\", it does not exist");

        boards.remove(b);
    }
    /**
     * <p>Deletes a Noticeboard from the User.</p>
     * @param boardID the ID
     *
     * @throws NoSuchElementException if the noticeboard does not exist
     */
    public void deleteNoticeboard(int boardID){
        Noticeboard b = this.getNoticeboard(boardID);
        if(b == null)
            throw new NoSuchElementException("Cannot remove Noticeboard-" + boardID + ", it does not exist");

        boards.remove(b);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        sb.append("ID: ").append(userID).append(", ");
        sb.append("Username: ").append(this.username).append(", ");
        sb.append("Password: ").append(this.password).append(", ");
        sb.append("Boards: [");
        if(boards != null) {
            for (int i = 0; i < boards.size(); i++) {
                if (i != boards.size() - 1)
                    sb.append(boards.get(i).getTitle()).append(", ");
                else
                    sb.append(boards.get(i).getTitle());
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}