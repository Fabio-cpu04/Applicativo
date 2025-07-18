package model;

import java.lang.String;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * <p>A User in the model, identified by its username.</p>
 * <p>Every user has a collection of {@link Noticeboard}.</p>
 * <p>The class provides methods to retrieve, add, remove and get the count of noticeboards.</p>
 */
public class User {
    private final String username;
    private final String password;
    private final ArrayList<Noticeboard> boards;

    /**
     * <p>Instantiates a new User.</p>
     * @param username the username
     * @param password the password
     *
     * @throws IllegalArgumentException if {@code username} is {@code null} or blank
     * @throws IllegalArgumentException if {@code password} is {@code null} or blank
     */
    public User(String username, String password){
        if(username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");
        if(password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be null or blank");

        this.username = username;
        this.password = password;
        boards = new ArrayList<Noticeboard>();
    }

    //Getter & Setter methods
    /**
     * <p>Gets username.</p>
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * <p>Gets password.</p>
     * @return the password
     */
    public String getPassword() { return password; }

    //Noticeboard methods
    /**
     * <p>Gets noticeboards.</p>
     * @return the noticeboards, wrapped in a {@link ArrayList} of {@link Noticeboard}
     */
    public ArrayList<Noticeboard> getNoticeboards() { return boards; }

    /**
     * <p>Gets the count of noticeboards.</p>
     * @return the count of noticeboards
     */
    public int getNoticeboardCount() { return boards.size(); }

    /**
     * <p>Gets user's noticeboard from title.</p>
     * @param title the title
     * @return the noticeboard if the user is able to view it, else {@code null}
     */
    public Noticeboard getNoticeboard(String title){
        return boards.stream().filter(board -> board.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    /**
     * <p>Adds noticeboard.</p>
     * @param noticeboard the noticeboard
     *
     * @throws IllegalArgumentException if {@code noticeboard} is {@code null}
     * @throws IllegalStateException if a noticeboard with the same title exists already
     */
    public void addNoticeboard(Noticeboard noticeboard){
        if(noticeboard == null)
            throw new IllegalArgumentException("You cannot add a null Noticeboard to a User");

        if(this.getNoticeboard(noticeboard.getTitle()) != null) //Board exists already
            throw new IllegalStateException("A Noticeboard with the same title exists already, duplicate titles are not allowed");

        boards.add(noticeboard);
    }

    /**
     * <p>Deletes noticeboard.</p>
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
}