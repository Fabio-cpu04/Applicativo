import controller.Controller;
import gui.GUI;
import model.Noticeboard;
import model.ToDo;
import model.User;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        User u1 = new User("user1", "passwordsicura1");
        User u2 = new User("user2", "passwordsicura2");
        User u3 = new User("user3", "passwordsicura3");

        Noticeboard tb1 = new Noticeboard("testboard1", "");
        Noticeboard tb2 = new Noticeboard("testboard2", "");
        u1.addNoticeboard(tb1);
        u1.addNoticeboard(tb2);

        //Creating and adding
        LocalDateTime now = LocalDateTime.now();
        ToDo t1 = new ToDo("todotest1","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now.plusDays(10), "https://google.com" , "", u1, "");
        ToDo t2 = new ToDo("todotest2","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1, "#101010");
        ToDo t3 = new ToDo("todotest3","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1, "");
        ToDo t4 = new ToDo("todotest4","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1, "");
        ToDo t5 = new ToDo("todotest5","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1, "");
        ToDo t6 = new ToDo("todotest6","a very complete non-lorem-ipsum-ian description\n for a very nice ToDo: todotest1", now, "" , "", u1, "");
        tb1.addToDo(t1);
        tb1.addToDo(t2);
        tb1.addToDo(t3);
        tb1.addToDo(t4);
        tb1.addToDo(t5);
        tb1.addToDo(t6);
        tb1.getToDo("todotest2").changeState();

        u1.getNoticeboard("Tempo libero").addToDo(new ToDo("todo1", "aaa", now, "", "", u1, ""));
        u1.getNoticeboard("Tempo libero").addToDo(new ToDo("todo2", "bbb", now, "", "", u1, ""));
        u1.getNoticeboard("Universita").addToDo(new ToDo("uni", "", now, "","", u1, ""));
        u1.getNoticeboard("Lavoro").addToDo(new ToDo("lavourr", "", now, "","", u1, ""));
        u1.getNoticeboard("Universita").getToDo("uni").changeState();

        t1.addSharedUser(u2);
        t1.addSharedUser(u3);
        t2.addSharedUser(u2);
        t3.addSharedUser(u2);

        Controller.getController().addUser(u1);
        Controller.getController().addUser(u2);
        Controller.getController().addUser(u3);


        //ATTENTION
        //THE initGUI METHOD HAS TEMPORARILY AN user PARAMETER, THIS IS A REPLACEMENT FOR THE LOGIN SYSTEM

        GUI.initGUI(u1);
        //ATTENTION

    }
}
