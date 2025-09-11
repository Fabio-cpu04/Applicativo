package dto;

//App imports
import model.ToDo;

//Java imports
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A ToDo Data Transfer Object.</p>
 * <p>The class provides methods to retrieve the state, attributes and User sharing status of a {@link ToDo}.</p>
 */
public class ToDoDTO {
    private enum ToDoState { COMPLETED, NOTCOMPLETED }

    //Members
    private final int todoID;
    private final ToDoState state;
    private final String title;
    private final String description;
    private final String activityURL;
    private final String imageURL;
    private final LocalDateTime expiryDate;
    private final String backgroundColor;

    private final int ownerUserID;
    private final List<String> sharedUsers;

    /**
     * <p>Instantiates a new ToDoTDO object copied from a ToDo.</p>
     * @param source the ToDo to copy the attributes from
     *
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public ToDoDTO(ToDo source) {
        if(source == null)
            throw new IllegalArgumentException("Source ToDo cannot be null");

        this.todoID = source.getToDoID();
        this.state = source.isCompleted() ? ToDoState.COMPLETED : ToDoState.NOTCOMPLETED;
        this.title = source.getTitle();
        this.description = source.getDescription();
        this.activityURL = source.getActivityURL();
        this.imageURL = source.getImageURL();
        this.expiryDate = source.getExpiryDate();
        this.backgroundColor = source.getBackgroundColor();

        this.ownerUserID = source.getOwnerUserID();
        this.sharedUsers = List.copyOf(source.getSharedUsers());
    }

    /**
     * <p>Instantiates a new ToDoDTO with the specified attributes.</p>
     * @param todoID          the ToDo's ID
     * @param completed       the completion state as a boolean ({@code true} for Complete, {@code false} for Not Complete)
     * @param title           the title
     * @param description     the description
     * @param activityURL     the activity url
     * @param imageURL        the image url
     * @param expiryDate      the expiry date
     * @param ownerUserID     the owner's userID
     * @param backgroundColor the background color
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public ToDoDTO(int todoID, boolean completed, String title, String description, String activityURL, String imageURL, LocalDateTime expiryDate, int ownerUserID, String backgroundColor) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("ToDo title cannot be null or blank");

        this.todoID = todoID;

        this.state = completed ? ToDoState.COMPLETED : ToDoState.NOTCOMPLETED;

        this.title = title;
        this.description = description;
        this.activityURL = activityURL;
        this.imageURL = imageURL;
        this.expiryDate = expiryDate;
        this.backgroundColor = backgroundColor;

        this.ownerUserID = ownerUserID;
        this.sharedUsers = new ArrayList<>();
    }

    /**
     * <p>Instantiates a new, not completed, ToDoDTO with the specified attributes.</p>
     *
     * @param title           the title
     * @param description     the description
     * @param activityURL     the activity url
     * @param imageURL        the image url
     * @param expiryDate      the expiry date
     * @param ownerUserID     the owner's userID
     * @param backgroundColor the background color
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public ToDoDTO(String title, String description, String activityURL, String imageURL, LocalDateTime expiryDate, int ownerUserID, String backgroundColor) {
        //Delegates to:
        // ToDoDTO(int todoID, boolean completed, String title, String description, String activityURL, String imageURL, LocalDateTime expiryDate, int ownerUserID, String backgroundColor)
        this(-1, false, title, description, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor);
    }

    //ToDoDTO State methods
    /**
     * <p>Checks if the ToDo is expired.</p>
     * @return returns {@code true} if ToDo's expiry date is past the current date and time, otherwise {@code false}
     */
    public boolean isExpired() {
        if (expiryDate == null)
            return false;

        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * <p>Checks if the ToDo is completed.</p>
     * @return returns {@code true} if ToDo is completed, otherwise {@code false}
     */
    public boolean isCompleted() { return state == ToDoState.COMPLETED; }

    //Getter & Setter methods
    /**
     * <p>Gets the ToDo's ID.</p>
     * @return the id
     */
    public int getToDoID() { return todoID; }

    /**
     * <p>Gets the ToDo'stitle.</p>
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
     * <p>Gets the ToDo's shared users.</p>
     * @return the shared Users'usernames, wrapped in a {@link ArrayList} of {@link String}
     */
    public List<String> getSharedUsers() { return sharedUsers; }

    //User sharing methods
    /**
     * <p>Checks if the ToDo is shared with a User.</p>
     * @param username the username
     * @return {@code true} if shared, else {@code false}
     */
    public boolean isSharedWith(String username) {
            return sharedUsers.contains(username);
    }

    //Utility methods
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ToDoDTO todo = (ToDoDTO)obj;

        return this.todoID == todo.getToDoID() &&
                this.isCompleted() == todo.isCompleted() &&
                this.title.equals(todo.getTitle()) &&
                this.description.equals(todo.getDescription()) &&
                ((this.expiryDate == null) ? todo.getExpiryDate() == null : this.expiryDate.equals(todo.getExpiryDate())) &&
                //expiryDate is nullable, therefore we need to check beforehand whether it can be compared with .equals()
                this.activityURL.equals(todo.getActivityURL()) &&
                this.imageURL.equals(todo.getImageURL()) &&
                this.backgroundColor.equals(todo.getBackgroundColor()) &&
                this.ownerUserID == todo.getOwnerUserID() &&
                this.sharedUsers.equals(todo.getSharedUsers());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}