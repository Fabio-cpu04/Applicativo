package model;
import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private final String username;
    private final String password;
    final HashMap<String, Noticeboard> boards;

    public User(String login, String password){
        this.username = login;
        this.password = password;
        this.boards = new HashMap<String, Noticeboard>();

        this.boards.put("Universita",new Noticeboard("Universita","Studio, Homework e Progetti."));
        this.boards.put("Lavoro", new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro"));
        this.boards.put("Tempo Libero", new Noticeboard("Tempo Libero","Attivita ed Hobby da svolgere durante il tempo Libero"));
    }

    //Getter & Setter methods
    public String getUsername() { return username; }
    private String getPassword() { return password; }

    //User methods
    public int verifyLogin(String username, String password){
        // Placeholder login verification.
        return 0;
    }

    //Noticeboard methods
    public int getNoticeboardNumber() { return boards.size(); }
    public Noticeboard[] getNoticeboards() { return boards.values().toArray(new Noticeboard[boards.size()]); }
    public Noticeboard getNoticeboard(String title){ return this.boards.get(title); }

    public int addNoticeboard(String title, String description){
        if(this.boards.get(title) != null)
            return -1;

        this.boards.put(title, new Noticeboard(title,description));
        return 0;
    }
    public int modifyNoticeboard(String title, String description){
        Noticeboard b = this.boards.get(title);
        if(b == null)
            return -1;

        b.setTitle(title);
        b.setDescription(description);
        return 0;
    }
    public int deleteNoticeboard(String title){
        Noticeboard b = this.boards.get(title);
        if(b == null)
            return -1;

        this.boards.remove(title);
        return 0;
    }

    //ToDo utility methods
    public ToDo[] getToDos(){
        ArrayList<ToDo> todos = new ArrayList<ToDo>();
        for(Noticeboard b : this.boards.values()){
            for(ToDo todo : b.getToDos())
                todos.add(todo);
        }

        ToDo[] array = new ToDo[todos.size()];
        return todos.toArray(array);
    }
    public int addToDo (String boardTitle, ToDo todo){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        return b.addToDo(todo);
    }
    public int addToDo(String boardTitle, String todoTitle, String description, LocalDateTime expiration, String link, String activityURL, String imageURL, String color){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        return b.addToDo(todoTitle, description, expiration, link, activityURL, imageURL, this, color);
    }
    public int modifyToDo(String boardTitle, String todoTitle, String newTitle, String description, LocalDateTime expiration, String link, String activityURL, String imageURL){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        return b.modifyToDo(todoTitle, newTitle, description, expiration, link, activityURL, imageURL, imageURL);
    }
    public int deleteToDo(String boardTitle, String todoTitle){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        return b.deleteToDo(todoTitle);
    }

    //ToDo sharing methods
    public int addSharing(String boardTitle, String todoTitle, User user){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        ToDo todo = b.getToDo(todoTitle);
        if(this != todo.getOwner())
            return -1;

        return b.shareToDo(todo, user);
    }
    public int removeSharing(String boardTitle, String todoTitle, User user){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        ToDo todo = b.getToDo(todoTitle);
        if(this != todo.getOwner())
            return -1;

        return b.unshareToDo(todo, user);
    }

    //ToDo positioning methods
    public int changeToDoOrder(String boardTitle, String todoTitle, int newIndex){
        Noticeboard b = this.boards.get(boardTitle);
        if(b == null)
            return -1;

        return b.changeToDoOrder(todoTitle, newIndex);
    }
    public int changeToDoNoticeboard(String originBoard, String todoTitle, String destBoard){
        Noticeboard origin = this.getNoticeboard(originBoard);
        Noticeboard dest = this.getNoticeboard(destBoard);
        if(origin == null || dest == null)
            return -1;

        ToDo todo = origin.getToDo(todoTitle);
        if(todo == null)
            return -1;

        dest.addToDo(todo);
        return origin.deleteToDo(todoTitle);
    }

    //ToDo searching methods
    public ToDo[] getToDosExpiringToday(){
        ToDo[] allTodos = this.getToDos();
        LocalDate today = LocalDate.now();

        ArrayList<ToDo> expiringToday = new ArrayList<ToDo>();
        for (ToDo todo : allTodos)
            if (todo.getExpiryDate().toLocalDate().isEqual(today) && !todo.isExpired())
                expiringToday.add(todo);

        ToDo[] result = new ToDo[expiringToday.size()];
        return expiringToday.toArray(result);
    }
    public ToDo[] getToDosExpiringAt(LocalDate date){
        ToDo[] allTodos = this.getToDos();

        ArrayList<ToDo> expiringAtDate = new ArrayList<ToDo>();
        for (ToDo todo : allTodos) {
            int comparisonResult = todo.getExpiryDate().toLocalDate().compareTo(date);
            if (comparisonResult > 0 || (comparisonResult == 0 && !todo.isExpired()))
                expiringAtDate.add(todo);
        }

        ToDo[] result = new ToDo[expiringAtDate.size()];
        return expiringAtDate.toArray(result);
    }
    public ToDo[] searchToDos(String title){
        ToDo[] allTodos = this.getToDos();

        ArrayList<ToDo> matches = new ArrayList<ToDo>();
        for (ToDo todo : allTodos) {
            if(todo.getTitle().contains(title))
                matches.add(todo);
        }

        ToDo[] result = new ToDo[matches.size()];
        return matches.toArray(result);
    }
}