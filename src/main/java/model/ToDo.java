package model;

//Java imports
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A ToDo in the model.</p>
 * <p>The class provides methods to manage the ToDo's state, attributes and its user sharing status.</p>
 */
public class ToDo {
    private enum ToDoState { COMPLETED, NOTCOMPLETED }

    //Members
    private final int todoID;
    private ToDoState state;
    private String title;
    private String description;
    private String activityURL;
    private String imageURL;
    private LocalDateTime expiryDate;
    private String backgroundColor;

    private final int ownerUserID;
    private final ArrayList<String> sharedUsers;

    /**
     * <p>Instantiates a new, not completed, ToDo with the specified attributes.</p>
     * @param todoID          the id
     * @param completed       the completion state as a boolean ({@code true} for Complete, {@code false} for Not Complete)
     * @param title           the title
     * @param description     the description
     * @param activityURL     the activity url
     * @param imageURL        the image url
     * @param expiryDate      the expiry date
     * @param ownerUserID   the owner's username
     * @param backgroundColor the background color
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public ToDo(int todoID, boolean completed, String title, String description, String activityURL, String imageURL, LocalDateTime expiryDate, int ownerUserID, String backgroundColor) {
        if(todoID < 0)
            throw new IllegalArgumentException("ToDo ID cannot be negative");
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("ToDo title cannot be null or blank");

        //Setting up ToDo attributes
        this.todoID = todoID;

        this.state = completed ? ToDoState.COMPLETED : ToDoState.NOTCOMPLETED;

        this.title = title;
        this.description = description == null ? "" : description;
        this.activityURL = activityURL == null ? "" : activityURL;
        this.imageURL = imageURL == null ? "" : imageURL;
        this.expiryDate = expiryDate;
        this.backgroundColor = backgroundColor == null ? "" : backgroundColor;

        this.ownerUserID = ownerUserID;
        this.sharedUsers = new ArrayList<>();
    }

    //ToDo State methods
    /**
     * <p>Checks if ToDo is expired.</p>
     * @return returns {@code true} if ToDo's expiry date is past the current date and time, otherwise {@code false}
     */
    public boolean isExpired() {
        if(expiryDate == null)
            return false;

        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * <p>Checks if ToDo is completed.</p>
     * @return returns {@code true} if ToDo is completed, otherwise {@code false}
     */
    public boolean isCompleted() { return state == ToDoState.COMPLETED; }

    /**
     * <p>Changes ToDo state from not completed to completed and vice versa.</p>
     */
    public void changeCompletionState() { this.state = this.isCompleted() ? ToDoState.NOTCOMPLETED: ToDoState.COMPLETED; }

    //Getter & Setter methods
    /**
     * <p>Gets the ToDo's ID.</p>
     * @return the ID
     */
    public int getToDoID() { return todoID; }

    /**
     * <p>Gets the ToDo's title.</p>
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * <p>Gets the ToDo's description.</p>
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * <p>Gets the ToDo's expiry date.</p>
     * @return the expiry date
     */
    public LocalDateTime getExpiryDate() { return expiryDate; }

    /**
     * <p>Gets the ToDo's activity url.</p>
     * @return the activity url
     */
    public String getActivityURL() { return activityURL; }

    /**
     * <p>Gets the ToDo's image url.</p>
     * @return the image url
     */
    public String getImageURL() { return imageURL; }

    /**
     * <p>Gets the ToDo's background color.</p>
     * @return the background color as an hexadecimal RBG string in the "#RRGGBB" format
     */
    public String getBackgroundColor() { return backgroundColor; }

    /**
     * <p>Gets the ToDo's owner User's ID.</p>
     * @return the ID
     */
    public int getOwnerUserID() { return ownerUserID; }

    /**
     * <p>Gets the ToDo's shared Users.</p>
     * @return the shared Users' usernames, as a {@link List} of {@link String}
     */
    public List<String> getSharedUsers() { return sharedUsers; }

    /**
     * <p>Sets the ToDo's title.</p>
     * @param title the title
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public void setTitle(String title) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("ToDo title cannot be null or blank");

        this.title = title;
    }

    /**
     * <p>Sets the ToDo's description.</p>
     * @param description the description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * <p>Sets the ToDo's expiry date.</p>
     * @param expiryDate the expiry date
     */
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    /**
     * <p>Sets the ToDo's activity url.</p>
     * @param activityURL the activity url
     */
    public void setActivityURL(String activityURL) { this.activityURL = activityURL; }

    /**
     * <p>Sets the ToDo's image url.</p>
     * @param imageURL the image url
     */
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    /**
     * <p>Sets the ToDo's background color.</p>
     * @param backgroundColor the background color as an hexadecimal RBG string in the "#RRGGBB" format
     */
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    //User sharing methods
    /**
     * <p>Checks if the ToDo is shared with an User.</p>
     * @param username the username
     * @return {@code true} if shared, else {@code false}
     */
    public boolean isSharedWith(String username) {
        return sharedUsers.contains(username);
    }

    /**
     * <p>Shares the ToDo with an User.</p>
     * @param username the username
     * @return returns {@code 0} if successful, returns {@code -1} if ToDo is already shared with {@code user}
     *
     * @throws IllegalArgumentException if {@code username} is {@code null} or blank
     */
    public int addSharedUser(String username) {
        if(username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");

        if(this.isSharedWith(username))
            return -1;

        this.sharedUsers.add(username);
        return 0;
    }

    /**
     * <p>Unshares the ToDo with an User.</p>
     * @param username the username
     * @return returns {@code 0} if successful, returns {@code -1} if ToDo isn't shared with the user
     *
     * @throws IllegalArgumentException if {@code username} is {@code null} or blank
     */
    public int removeSharedUser(String username) {
        if(username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");

        if(!this.isSharedWith(username))
            return -1;

        this.sharedUsers.remove(username);
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("ID: ").append(this.todoID).append(", ");
        sb.append("State: ").append(this.state).append(", ");
        sb.append("Title: ").append(this.title).append(", ");
        sb.append("Description: ").append(this.description).append(", ");
        sb.append("Expiration: ").append(this.expiryDate).append(", ");
        sb.append("ActivityURL: ").append(this.activityURL).append(", ");
        sb.append("ImageURL:").append(this.imageURL).append(", ");
        sb.append("Owner: ").append(this.ownerUserID).append(", ");
        sb.append("Shared Users: [");
        if(this.sharedUsers != null) {
            for (int i = 0; i < this.sharedUsers.size(); i++) {
                if (i != this.sharedUsers.size() - 1)
                    sb.append(this.sharedUsers.get(i)).append(", ");
                else
                    sb.append(this.sharedUsers.get(i));
            }
        }
        sb.append("], ");
        sb.append("Color: ").append(this.backgroundColor).append("}");
        return sb.toString();
    }
}
