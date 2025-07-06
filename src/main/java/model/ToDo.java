package model;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ToDo {
    private enum ToDoState { Completed, NotCompleted };
    private ToDoState state;

    private String title;
    private String description;
    private String activityURL;
    private String imageURL;
    private LocalDateTime expiryDate;
    private String backGroundColor;

    private final User owner;
    private ArrayList<User> sharedUsers;

    public ToDo(String title, String description, LocalDateTime expiryDate, String activityURL, String imageURL, User owner, String backGroundColor) {
        this.state = ToDoState.NotCompleted;

        this.title = title;
        this.description = description;
        this.expiryDate = expiryDate;
        this.activityURL = activityURL;
        this.imageURL = imageURL;
        this.backGroundColor = backGroundColor;

        this.owner = owner;
        this.sharedUsers = new ArrayList<User>();
    }

    //ToDo State methods
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }
    public boolean isCompleted() { return state == ToDoState.Completed; }
    public void changeState() { this.state = this.isCompleted() ? ToDoState.NotCompleted: ToDoState.Completed; }

    //Getter & Setter methods
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public String getActivityURL() { return activityURL; }
    public String getImageURL() { return imageURL; }
    public String getBackGroundColor() { return backGroundColor; }
    public User getOwner() { return owner; }
    public ArrayList<User> getSharedUsers() { return sharedUsers; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public void setActivityURL(String activityURL) { this.activityURL = activityURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setBackGroundColor(String backGroundColor) { this.backGroundColor = backGroundColor; }

    //User sharing methods
    public boolean isSharedWith(User user) {
        return sharedUsers.contains(user);
    }
    public int addSharedUser(User user) {
        if(!this.isSharedWith(user))
            this.sharedUsers.add(user);
        else
            return -1;

        return 0;
    }
    public int removeSharedUser(User user) {
        if(this.isSharedWith(user))
            this.sharedUsers.remove(user);
        else
            return -1;

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
        sb.append("Owner: ").append(this.owner.getUsername()).append(", ");
        sb.append("Shared Users: [");
        if(this.sharedUsers != null) {
            for (int i = 0; i < this.sharedUsers.size(); i++) {
                if (i != this.sharedUsers.size() - 1)
                    sb.append(this.sharedUsers.get(i).getUsername()).append(", ");
                else
                    sb.append(this.sharedUsers.get(i).getUsername());
            }
        }
        sb.append("], ");
        sb.append("Color: ").append(this.backGroundColor).append(", ");
        sb.append("State: ").append(this.state).append("}");
        return sb.toString();
    }
}
