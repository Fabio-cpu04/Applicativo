package dto;

//App imports
import model.Noticeboard;
import model.ToDo;

//Java imports
import java.util.List;

/**
 * <p>A Noticeboard Data Transfer Object, identified by its title.</p>
 * <p>The class provides methods to retrieve the Noticeboard's attributes and its todo list.</p>
 */
public class NoticeboardDTO {
    private final String title;
    private final String description;

    private final List<ToDoDTO> todos;

    /**
     * <p>Instantiates a new NoticeboardDTO copied from a Noticeboard.</p>
     * @param source the Noticeboard to copy the attributes and todo list from.
     *
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public NoticeboardDTO(Noticeboard source) {
        if(source == null)
            throw new IllegalArgumentException("Source Noticeboard cannot be null");

        this.title = source.getTitle();
        this.description = source.getDescription();
        this.todos = source.getToDos().stream().map(ToDoDTO::new).toList();
    }

    /**
     * <p>Instantiates a new Noticeboard DTO with no todos.</p>
     * @param title       the title
     * @param description the description
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public NoticeboardDTO(String title, String description) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be null or blank");

        this.title = title;
        this.description = description;
        this.todos = null;
    }

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

    //ToDo methods
    /**
     * <p>Gets to dos.</p>
     * @return the ToDos, wrapped in a {@link List} of {@link ToDo}
     */
    public List<ToDoDTO> getToDos() { return todos; }

    /**
     * <p>Gets ToDo from its title.</p>
     * @param title the title
     * @return the ToDoDTO if the noticeboard displays it, {@code null} otherwise
     */
    public ToDoDTO getToDo(String title){
        return todos.stream().filter(todo -> todo.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    //Utility methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NoticeboardDTO noticeboard = (NoticeboardDTO)obj;
        return this.title.equals(noticeboard.getTitle()) &&
                this.description.equals(noticeboard.getDescription()) &&
                this.getToDos().equals(noticeboard.getToDos());
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
