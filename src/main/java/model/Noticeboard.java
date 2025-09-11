package model;

//Java imports
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A Noticeboard in the model.</p>
 * <p>Every Noticeboard has a collection of displayed {@link ToDo}.</p>
 * <p>The class provides methods to retrieve, add and remove the displayed ToDos.</p>
 */
public class Noticeboard {
    private final int boardID;
    private String title;
    private String description;
    private final int userID;

    private final ArrayList<ToDo> todos;

    /**
     * <p>Instantiates a new Noticeboard with no todos.</p>
     * @param boardID the id
     * @param title the title
     * @param description the description
     * @param userID the owner user's ID
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public Noticeboard(int boardID, String title, String description, int userID) {
        if(boardID < 0)
            throw new IllegalArgumentException("Board ID cannot be negative");
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Noticeboard title cannot be null or blank");

        this.boardID = boardID;
        this.title = title;
        this.description = description;
        this.userID = userID;
        this.todos = new ArrayList<>();
    }

    //Getter & Setter methods
    /**
     * <p>Gets the Noticeboard's ID.</p>
     * @return the id
     */
    public int getBoardID() { return boardID; }

    /**
     * <p>Gets the Noticeboard'stitle.</p>
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * <p>Gets the Noticeboard'sdescription.</p>
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * <p>Gets the Noticeboard's owner User's ID</p>
     * @return the ID
     */
    public int getUserID() { return userID; }

    /**
     * <p>Sets the Noticeboard's title.</p>
     * @param title the title
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public void setTitle(String title) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Noticeboard title cannot be null or blank");

        this.title = title;
    }

    /**
     * <p>Sets the Noticeboard's description.</p>
     * @param description the description
     */
    public void setDescription(String description) { this.description = description; }

    //ToDo methods
    /**
     * <p>Gets the Noticeboard's {@link ToDo}s.</p>
     * @return the ToDos, as a {@link List} of {@link ToDo}
     */
    public List<ToDo> getToDos() { return todos; }

    /**
     * <p>Gets the count of ToDos.</p>
     * @return the count of ToDos
     */
    public int getToDoCount() { return todos.size(); }

    /**
     * <p>Gets a ToDo from its title.</p>
     * @param title the title
     * @return the ToDo if the Noticeboard displays it, {@code null} otherwise
     */
    public ToDo getToDo(String title){
        return todos.stream().filter(todo -> todo.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    /**
     * <p>Gets a ToDo from its ID.</p>
     * @param todoID the ID
     * @return the ToDo if the Noticeboard displays it, {@code null} otherwise
     */
    public ToDo getToDo(int todoID){
        return todos.stream().filter(todo -> todo.getToDoID() == todoID)
                .findFirst().orElse(null);
    }

    /**
     * <p>Adds ToDo to the Noticeboard.</p>
     * @param todo the ToDo
     *
     * @throws IllegalArgumentException if {@code todo} is {@code null}
     * @throws NoSuchElementException if the Noticeboard does not exist
     * @throws IllegalStateException if a ToDo with the same title already exists in the Noticeboard
     */
    public void addToDo(ToDo todo){
        if(todo == null)
            throw new IllegalArgumentException("Cannot add a null ToDo to a Noticeboard");

        if(this.getToDo(todo.getToDoID()) != null) //ToDo exists already
            throw new IllegalStateException("A ToDo with the same title exists already, duplicate titles are not allowed");

        todos.add(todo);
    }

    /**
     * <p>Deletes a ToDo from the Noticeboard.</p>
     * @param title the title
     *
     * @throws NoSuchElementException if the ToDo does not exist
     */
    public void deleteToDo(String title){
        ToDo todo = this.getToDo(title);
        if(todo == null)
            throw new NoSuchElementException("Cannot remove ToDo \"" + title + "\", it does not exist");

        todos.remove(todo);
    }

    /**
     * <p>Deletes a ToDo from the Noticeboard.</p>
     * @param todoID the ID
     *
     * @throws NoSuchElementException if the ToDo does not exist
     */
    public void deleteToDo(int todoID){
        ToDo todo = this.getToDo(todoID);
        if(todo == null)
            throw new NoSuchElementException("Cannot remove ToDo-" + todoID + ", it does not exist");

        todos.remove(todo);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Board{");
        sb.append("ID=").append(boardID).append(", ");
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
