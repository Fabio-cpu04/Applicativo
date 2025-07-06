package model;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;

public class Noticeboard {
    private String title;
    private String description;
    private ArrayList<ToDo> todos;

    public Noticeboard(String title, String description) {
        this.title = title;
        this.description = description;
        this.todos = new ArrayList<ToDo>();
    }

    //Getter & Setter methods
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    //ToDo methods
    public int getToDoCount() { return todos.size(); }
    public int getToDoIndex(String title) { return todos.indexOf(getToDo(title)); }

    public ArrayList<ToDo> getToDos() { return this.todos; }
    public ToDo getToDo(String title){
        for(ToDo todo : this.todos)
            if(todo != null && todo.getTitle().equals(title))
                return todo;

        return null;
    }
    public ToDo getToDo(int index){;
        if(index < 0 || index >= this.getToDoCount())
            return null;
        else
            return todos.get(index);
    }

    public int addToDo(ToDo todo){
        if(todo == null)
            return -1;
        if(this.getToDo(todo.getTitle()) != null) //ToDo exists already
            return -2;

        todos.add(todo);
        return 0;
    }
    public int deleteToDo(String title, User user){
        ToDo todo = this.getToDo(title);
        if(todo == null)
            return -1;

        if(user == todo.getOwner()){
            ArrayList<User> shared = todo.getSharedUsers();
            while(!shared.isEmpty())
                todo.removeSharedUser(user);
        }

        todos.remove(todo);
        return 0;
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
