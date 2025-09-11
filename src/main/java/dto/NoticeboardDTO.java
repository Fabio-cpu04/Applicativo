package dto;

//App imports
import model.Noticeboard;

//Java imports
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A Noticeboard Data Transfer Object.</p>
 * <p>The class provides methods to retrieve the attributes and ToDos of a {@link Noticeboard}.</p>
 */
public class NoticeboardDTO {
    private final int boardID;
    private final String title;
    private final String description;
    private final int userID;

    private final List<ToDoDTO> todos;

    /**
     * <p>Instantiates a new NoticeboardDTO copied from a {@link Noticeboard} object.</p>
     * @param source the Noticeboard to copy the attributes and ToDo list from
     *
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public NoticeboardDTO(Noticeboard source) {
        if(source == null)
            throw new IllegalArgumentException("Source Noticeboard cannot be null");

        this.boardID = source.getBoardID();
        this.title = source.getTitle();
        this.description = source.getDescription();
        this.userID = source.getUserID();
        this.todos = source.getToDos().stream().map(ToDoDTO::new).toList();
    }

    /**
     * <p>Instantiates a new NoticeboardDTO with no todos.</p>
     * @param boardID     the Noticeboard's ID or {@code -1} for an invalid ID
     * @param title       the title
     * @param description the description
     * @param userID      the User's ID or {@code -1} for an invalid ID
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public NoticeboardDTO(int boardID, String title, String description, int userID) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be null or blank");

        this.boardID = boardID;
        this.title = title;
        this.description = description;
        this.userID = userID;
        this.todos = new ArrayList<>();
    }

    /**
     * <p>Instantiates a new NoticeboardDTO with no ToDos.</p>
     * @param title       the title
     * @param description the description
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public NoticeboardDTO(String title, String description) {
        //Delegates to
        //  NoticeboardDTO(int boardID, String title, String description)
        this(-1, title, description, -1);
    }

    //Getter & Setter methods
    /**
     * <p>Gets the Noticeboard's ID.</p>
     * @return the id
     */
    public int getBoardID() { return boardID; }
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
     * <p>Gets the owning User's ID.</p>
     * @return the id
     */
    public int getUserID() { return userID; }

    //ToDo methods
    /**
     * <p>Gets the ToDos.</p>
     * @return if any ToDo exist returns them as a {@link List} of {@link ToDoDTO}, otherwise returns {@code null}
     */
    public List<ToDoDTO> getToDos() { return todos; }

    /**
     * <p>Gets the count of the ToDos of the Noticeboard</p>
     * @return the count of the ToDos
     */
    public int getToDoCount() {
        if(todos == null)
            return 0;

        return todos.size();
    }

    /**
     * <p>Gets ToDo from its title.</p>
     * @param title the title
     * @return the ToDoDTO if the Noticeboard displays it, {@code null} otherwise
     */
    public ToDoDTO getToDo(String title){
        if(todos == null)
            return null;

        return todos.stream().filter(todo -> todo.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    //Utility methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NoticeboardDTO noticeboard = (NoticeboardDTO)obj;
        return this.boardID == noticeboard.boardID &&
                this.title.equals(noticeboard.getTitle()) &&
                this.description.equals(noticeboard.getDescription()) &&
                this.getToDos().equals(noticeboard.getToDos());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("BoardDTO{");
        sb.append("Title: ").append(this.title).append(", ");
        sb.append("Description: ").append(this.description).append(", ");
        sb.append("ToDos: [");
        if(todos != null) {
            for (int i = 0; i < todos.size(); i++) {
                if (i != todos.size() - 1)
                    sb.append(todos.get(i).getTitle()).append(", ");
                else
                    sb.append(todos.get(i).getTitle());
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
