package model;
import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private final String username;
    private final String password;
    final ArrayList<Noticeboard> boards;

    public User(String login, String password){
        this.username = login;
        this.password = password;
        boards = new ArrayList<Noticeboard>();

        boards.add(new Noticeboard("Universita","Studio, Homework e Progetti."));
        boards.add(new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro"));
        boards.add(new Noticeboard("Tempo libero","Attivita ed Hobby da svolgere durante il tempo Libero"));
    }

    //Getter & Setter methods
    public String getUsername() { return username; }
    private String getPassword() { return password; }

    //Noticeboard methods
    public int getNoticeboardCount() { return boards.size(); }
    public ArrayList<Noticeboard> getNoticeboards() { return boards; }
    public Noticeboard getNoticeboard(String title){
        return boards.stream().filter(board -> board.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    public int addNoticeboard(Noticeboard noticeboard){
        if(this.getNoticeboard(noticeboard.getTitle()) != null)
            return -1;

        boards.add(noticeboard);
        return 0;
    }
    public int deleteNoticeboard(String title){
        Noticeboard b = this.getNoticeboard(title);
        if(b == null)
            return -1;

        boards.remove(b);
        return 0;
    }

    //ToDo utility methods
    public ArrayList<ToDo> getToDos(){
        ArrayList<ToDo> todos = new ArrayList<ToDo>();
        for(Noticeboard b : boards){
            todos.addAll(b.getToDos());
        }

        return todos;
    }
}