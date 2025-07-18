import controller.Controller;
import gui.GUI;
import model.Noticeboard;
import model.ToDo;
import model.User;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Controller ctrl = Controller.get();

        User u1 = new User("user1", "passwordsicura1");
        u1.addNoticeboard(new Noticeboard("Universita","Studio, Homework e Progetti."));
        u1.addNoticeboard(new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro"));
        u1.addNoticeboard(new Noticeboard("Tempo libero","Attivita ed Hobby da svolgere durante il tempo Libero"));
        ctrl.setLoggedUser(u1);

        Noticeboard tb1 = new Noticeboard("testboard1", "");
        Noticeboard tb2 = new Noticeboard("testboard2", "");
        u1.addNoticeboard(tb1);
        u1.addNoticeboard(tb2);

        //Creating and adding
        LocalDateTime now = LocalDateTime.now();
        ToDo t1 = new ToDo("todotest1","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now.plusDays(10), "https://google.com" , "", u1.getUsername(), "");
        ToDo t2 = new ToDo("todotest2","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1.getUsername(), "#101010");
        ToDo t3 = new ToDo("todotest3","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1.getUsername(), "");
        ToDo t4 = new ToDo("todotest4","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1.getUsername(), "");
        ToDo t5 = new ToDo("todotest5","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1.getUsername(), "");
        ToDo t6 = new ToDo("todotest6","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1.getUsername(), "");
        tb1.addToDo(t1);
        tb1.addToDo(t2);
        tb1.addToDo(t3);
        tb1.addToDo(t4);
        tb1.addToDo(t5);
        tb1.addToDo(t6);
        tb1.getToDo("todotest2").changeCompletionState();

        u1.getNoticeboard("Tempo libero").addToDo(new ToDo("todo1", "aaa", now, "", "", u1.getUsername(), ""));
        u1.getNoticeboard("Tempo libero").addToDo(new ToDo("todo2", "bbb", now, "", "", u1.getUsername(), ""));
        u1.getNoticeboard("Universita").addToDo(new ToDo("uni", "", now, "","", u1.getUsername(), ""));
        u1.getNoticeboard("Lavoro").addToDo(new ToDo("lavourr", "", now, "","", u1.getUsername(), ""));
        u1.getNoticeboard("Universita").getToDo("uni").changeCompletionState();

        //TEMPORARILY INCLUDES A Controller PARAMETER, THIS IS A REPLACEMENT FOR A DATABASE CONNECTION
        GUI gui = new GUI();
    }
}
