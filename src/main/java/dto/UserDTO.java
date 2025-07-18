package dto;

//App imports
import model.User;

//Java imports
import java.util.List;

/**
 * <p>A User Data Transfer Object, identified by its username.</p>
 * <p>The class provides methods to retrieve the User's attributes and its noticeboards.</p>
 */
public class UserDTO {
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

        this.username = source.getUsername();
        this.password = source.getPassword();
        this.boards = source.getNoticeboards().stream().map(NoticeboardDTO::new).toList();
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
     * @return the noticeboards, wrapped in a {@link List} of {@link NoticeboardDTO}
     */
    public List<NoticeboardDTO> getNoticeboards() { return boards; }

    /**
     * <p>Gets user's noticeboard from title.</p>
     * @param title the title
     * @return the noticeboard if the user is able to view it, else {@code null}
     */
    public NoticeboardDTO getNoticeboard(String title){
        return boards.stream().filter(board -> board.getTitle().equals(title))
                .findFirst().orElse(null);
    }
}
