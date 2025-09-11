package dto;

//App imports
import model.User;

//Java imports
import java.util.List;

/**
 * <p>A User Data Transfer Object.</p>
 * <p>The class provides methods to retrieve the attributes and Noticeboards of a {@link User}.</p>
 */
public class UserDTO {
    private final int userID;
    private final String username;
    private final String password;

    private final List<NoticeboardDTO> boards;

    /**
     * <p>Instantiates a new User DTO copied from a User.</p>
     * @param source the User to copy the attributes and Noticeboard list from.
     *
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public UserDTO(User source){
        if(source == null)
            throw new IllegalArgumentException("Source User cannot be null");

        this.userID = source.getUserID();
        this.username = source.getUsername();
        this.password = source.getPassword();
        this.boards = source.getNoticeboards().stream().map(NoticeboardDTO::new).toList();
    }

    //Getter & Setter methods
    /**
     * <p>Gets the User's ID.</p>
     * @return the userID
     */
    public int getUserID() { return userID; }
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
     * <p>Gets the User's Noticeboards.</p>
     * @return the Noticeboards, as a {@link List} of {@link NoticeboardDTO}
     */
    public List<NoticeboardDTO> getNoticeboards() { return boards; }
}
