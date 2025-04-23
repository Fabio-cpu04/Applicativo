package model;
import java.lang.String;
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
    public int getToDoNumber() { return todos.size(); }
    public int getToDoIndex(String title) { return todos.indexOf(getToDo(title)); }

    public ToDo[] getToDos() { return this.todos.toArray(new ToDo[this.todos.size()]); }
    public ToDo getToDo(String title){
        for(ToDo todo : this.todos)
            if(todo.getTitle().equals(title))
                return todo;

        return null;
    }
    public ToDo getToDo(int index){
        int boardSize = this.getToDoNumber();
        if(boardSize == 0 || index < 0 || index >= boardSize)
            return null;
        else
            return todos.get(index);
    }

    public int addToDo(ToDo todo){
        if(todo == null || this.getToDo(todo.getTitle()) != null)
            return -1;

        this.todos.add(todo);
        return 0;
    }
    public int addToDo(String title, String description, LocalDateTime expiration, String link, String activityURL,  String imageURL, User owner, String color){
        if(this.getToDo(title) != null)
            return -1;

        ToDo todo = new ToDo(title, description, expiration, link, activityURL, imageURL, owner, color);

        return this.addToDo(todo);
    }
    public int modifyToDo(String title, String newTitle, String description, LocalDateTime expiration, String link, String activityURL, String imageURL, String color){
        ToDo todo = this.getToDo(title);
        if(todo == null)
            return -1;

        todo.setTitle(newTitle);
        todo.setDescription(description);
        todo.setExpiryDate(expiration);
        todo.setLink(link);
        todo.setActivityURL(activityURL);
        todo.setImageURL(imageURL);
        todo.setBackGroundColor(color);
        return 0;
    }
    public int deleteToDo(String title){
        ToDo todo = this.getToDo(title);
        if(todo == null)
            return -1;

        for (User u : todo.getSharedUsers())
            unshareToDo(todo, u);

        this.todos.remove(todo);
        return 0;
    }
    public int changeToDoOrder(String todoTitle, int newIndex){
        if(getToDo(todoTitle) == null || newIndex < 0 || newIndex > this.getToDoNumber())
            return -1;

        int evictedIndex = this.getToDoIndex(todoTitle);
        Collections.swap(this.todos, newIndex, evictedIndex);
        return 0;
    }

    //ToDo sharing methods
    int shareToDo(ToDo todo, User user){
        if(user == todo.getOwner())
            return -1;

        todo.addSharedUser(user);
        user.boards.putIfAbsent(this.title, new Noticeboard(this.title, this.description));

        if(user.getNoticeboard(this.title).getToDo(todo.getTitle()) == null)
            user.getNoticeboard(this.title).addToDo(todo);
        return 0;
    }
    int unshareToDo(ToDo todo, User user){
        if(user.getNoticeboard(this.title).getToDo(todo.getTitle()) == null)
            return -1;

        todo.removeSharedUser(user);
        user.getNoticeboard(this.title).deleteToDo(todo.getTitle());

        if(user.getNoticeboard(this.title).getToDoNumber() == 0)
            user.deleteNoticeboard(this.title);
        return 0;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Board{");
        sb.append("Title: ").append(this.title).append(", ");
        sb.append("Description: ").append(this.description).append(", ");
        sb.append("ToDos: [");
        if(this.todos != null) {
            for (int i = 0; i < this.todos.size(); i++) {
                if (i != this.todos.size() - 1)
                    sb.append(this.todos.get(i).getTitle()).append(", ");
                else
                    sb.append(this.todos.get(i).getTitle());
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
