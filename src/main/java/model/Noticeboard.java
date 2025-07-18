package model;

import java.lang.String;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * <p>A Noticeboard in the model, identified by its title.</p>
 * <p>Every user has a collection of displayed {@link ToDo}.</p>
 * <p>The class provides methods to retrieve, add and remove the displayed ToDos.</p>
 */
public class Noticeboard {
    private String title;
    private String description;
    private final ArrayList<ToDo> todos;

    /**
     * <p>Instantiates a new Noticeboard with no todos.</p>
     * @param title the title
     * @param description the description
     *
     * @throws IllegalArgumentException if {@code title} is {@code null} or blank
     */
    public Noticeboard(String title, String description) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Noticeboard title cannot be null or blank");

        this.title = title;
        this.description = description;
        this.todos = new ArrayList<ToDo>();
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

    /**
     * <p>Sets title.</p>
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
     * <p>Sets description.</p>
     * @param description the description
     */
    public void setDescription(String description) { this.description = description; }

    //ToDo methods
    /**
     * <p>Gets the todos.</p>
     * @return the todos, wrapped in a {@link ArrayList} of {@link ToDo}
     */
    public ArrayList<ToDo> getToDos() { return todos; }

    /**
     * <p>Gets the count of todos.</p>
     * @return the count of todos
     */
    public int getToDoCount() { return todos.size(); }

    /**
     * <p>Gets ToDo from its title.</p>
     * @param title the title
     * @return the todo if the noticeboard displays it, {@code null} otherwise
     */
    public ToDo getToDo(String title){
        return todos.stream().filter(todo -> todo.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    /**
     * <p>Adds ToDo to the noticeboard.</p>
     * @param todo the todo
     *
     * @throws IllegalArgumentException if {@code todo} is {@code null}
     * @throws NoSuchElementException if the noticeboard does not exist
     * @throws IllegalStateException if a ToDo with the same title already exists in the board
     */
    public void addToDo(ToDo todo){
        if(todo == null)
            throw new IllegalArgumentException("Cannot add a null ToDo to a Noticeboard");

        if(this.getToDo(todo.getTitle()) != null) //ToDo exists already
            throw new IllegalStateException("A ToDo with the same title exists already, duplicate titles are not allowed");

        todos.add(todo);
    }

    /**
     * <p>Deletes ToDo from the noticeboard.</p>
     * @param title the title
     *
     * @throws NoSuchElementException if the todo does not exist
     */
    public void deleteToDo(String title){
        ToDo todo = this.getToDo(title);
        if(todo == null)
            throw new NoSuchElementException("Cannot remove ToDo \"" + title + "\", it does not exist");

        todos.remove(todo);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Board{");
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
