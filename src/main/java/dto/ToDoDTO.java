package dto;

//App imports
import model.ToDo;

//Java imports
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A ToDo Data Transfer Object, identified by its title.</p>
 * <p>The class provides methods to retrieve the ToDo's state, attributes and its user sharing status.</p>
 */
public class ToDoDTO {
    private enum ToDoState { Completed, NotCompleted }

    //Members
    private final ToDoState state;

    private final String title;
    private final String description;
    private final String activityURL;
    private final String imageURL;
    private final LocalDateTime expiryDate;
    private final String backGroundColor;

    private final String owner;
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

        this.state = source.isCompleted() ? ToDoState.Completed : ToDoState.NotCompleted;

        this.title = source.getTitle();
        this.description = source.getDescription();
        this.expiryDate = source.getExpiryDate();
        this.activityURL = source.getActivityURL();
        this.imageURL = source.getImageURL();
        this.backGroundColor = source.getBackGroundColor();

        this.owner = source.getOwner();
        this.sharedUsers = List.copyOf(source.getSharedUsers());
    }

    /**
     * <p>Instantiates a new, not completed, ToDo DTO with the specified attributes.</p>
     * @param title           the title
     * @param description     the description
     * @param expiryDate      the expiry date
     * @param activityURL     the activity url
     * @param imageURL        the image url
     * @param ownerUsername   the owner's username
     * @param backGroundColor the background color
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public ToDoDTO(String title, String description, LocalDateTime expiryDate, String activityURL, String imageURL, String ownerUsername, String backGroundColor) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("ToDo title cannot be null or blank");

        this.state = ToDoState.NotCompleted;

        this.title = title;
        this.description = description;
        this.expiryDate = expiryDate;
        this.activityURL = activityURL;
        this.imageURL = imageURL;
        this.backGroundColor = backGroundColor;

        this.owner = ownerUsername;
        this.sharedUsers = null;
    }

    //ToDoDTO State methods
    /**
     * <p>Checks if ToDo is expired</p>
     * @return returns {@code true} if ToDo's expiry date is past the current date and time, otherwise {@code false}
     */
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }

    /**
     * <p>Checks if ToDo is completed.</p>
     * @return returns {@code true} if ToDo's {@code state == ToDoState.Completed}, otherwise {@code false}
     */
    public boolean isCompleted() { return state == ToDoState.Completed; }

    //Getter & Setter methods
    /**
     * <p>Gets title.</p>
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * <p>Gets description.</p>
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * <p>Gets expiry date.</p>
     * @return the expiry date
     */
    public LocalDateTime getExpiryDate() { return expiryDate; }

    /**
     * <p>Gets activity url.</p>
     * @return the activity url
     */
    public String getActivityURL() { return activityURL; }

    /**
     * <p>Gets image url.</p>
     * @return the image url
     */
    public String getImageURL() { return imageURL; }

    /**
     * <p>Gets background color.</p>
     * @return the background color as an hexadecimal RBG string in the "#RRGGBB" format
     */
    public String getBackGroundColor() { return backGroundColor; }

    /**
     * <p>Gets owner.</p>
     * @return the owner
     */
    public String getOwner() { return owner; }

    /**
     * <p>Gets shared users.</p>
     * @return the shared users usernames, wrapped in a {@link ArrayList} of {@link String}
     */
    public List<String> getSharedUsers() { return sharedUsers; }

    //User sharing methods
    /**
     * <p>Checks if ToDo is shared with the user identified by {@code username}.</p>
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

        return this.isCompleted() == todo.isCompleted() &&
                this.title.equals(todo.getTitle()) &&
                this.description.equals(todo.getDescription()) &&
                this.expiryDate.equals(todo.getExpiryDate()) &&
                this.activityURL.equals(todo.getActivityURL()) &&
                this.imageURL.equals(todo.getImageURL()) &&
                this.backGroundColor.equals(todo.getBackGroundColor()) &&
                this.owner.equals(todo.getOwner()) &&
                this.sharedUsers.equals(todo.getSharedUsers());

    }
}