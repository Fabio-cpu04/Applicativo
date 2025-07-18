package model;

import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <p>A ToDo in the model, identified by its title.</p>
 * <p>The class provides methods to manage the ToDo's state, attributes and its user sharing status.</p>
 */
public class ToDo {
    private enum ToDoState { Completed, NotCompleted }

    //Members
    private ToDoState state;

    private String title;
    private String description;
    private String activityURL;
    private String imageURL;
    private LocalDateTime expiryDate;
    private String backGroundColor;

    private final String owner;
    private final ArrayList<String> sharedUsers;

    /**
     * <p>Instantiates a new, not completed, ToDo with the specified attributes.</p>
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
    public ToDo(String title, String description, LocalDateTime expiryDate, String activityURL, String imageURL, String ownerUsername, String backGroundColor) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("ToDo title cannot be null or blank");

        //Setting up ToDo attributes
        this.state = ToDoState.NotCompleted;

        this.title = title;
        this.description = description;
        this.expiryDate = expiryDate;
        this.activityURL = activityURL;
        this.imageURL = imageURL;
        this.backGroundColor = backGroundColor;

        this.owner = ownerUsername;
        this.sharedUsers = new ArrayList<String>();
    }

    //ToDo State methods
    /**
     * <p>Checks if ToDo is expired.</p>
     * @return returns {@code true} if ToDo's expiry date is past the current date and time, otherwise {@code false}
     */
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }

    /**
     * <p>Checks if todo is completed.</p>
     * @return returns {@code true} if ToDo's {@code state == ToDoState.Completed}, otherwise {@code false}
     */
    public boolean isCompleted() { return state == ToDoState.Completed; }

    /**
     * <p>Changes ToDo state from non completed to completed and vice versa.</p>
     */
    public void changeCompletionState() { this.state = this.isCompleted() ? ToDoState.NotCompleted: ToDoState.Completed; }

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
    public ArrayList<String> getSharedUsers() { return sharedUsers; }

    /**
     * <p>Sets title.</p>
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
     * <p>Sets description.</p>
     * @param description the description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * <p>Sets expiry date.</p>
     * @param expiryDate the expiry date
     */
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    /**
     * <p>Sets activity url.</p>
     * @param activityURL the activity url
     */
    public void setActivityURL(String activityURL) { this.activityURL = activityURL; }

    /**
     * <p>Sets image url.</p>
     * @param imageURL the image url
     */
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    /**
     * <p>Sets background color.</p>
     * @param backGroundColor the background color as an hexadecimal RBG string in the "#RRGGBB" format
     */
    public void setBackGroundColor(String backGroundColor) { this.backGroundColor = backGroundColor; }

    //User sharing methods
    /**
     * <p>Checks if ToDo is shared with the user identified by {@code username}.</p>
     * @param username the username
     * @return {@code true} if shared, else {@code false}
     */
    public boolean isSharedWith(String username) {
        return sharedUsers.contains(username);
    }

    /**
     * <p>Shares the ToDo with the user identified by {@code username}.</p>
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
     * <p>Unshares the ToDo with {@code username}.</p>
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
        sb.append("Title: ").append(this.title).append(", ");
        sb.append("Description: ").append(this.description).append(", ");
        sb.append("Expiration: ").append(this.expiryDate).append(", ");
        sb.append("ActivityURL: ").append(this.activityURL).append(", ");
        sb.append("ImageURL:").append(this.imageURL).append(", ");
        sb.append("Owner: ").append(this.owner).append(", ");
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
        sb.append("Color: ").append(this.backGroundColor).append(", ");
        sb.append("State: ").append(this.state).append("}");
        return sb.toString();
    }
}
