import model.ToDo;
import model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        User u1 = new User("user1", "passwordsicura1");
        User u2 = new User("user2", "passwordsicura2");
        User u3 = new User("user3", "passwordsicura3");

        u1.addNoticeboard("testboard1", "");
        u1.addNoticeboard("testboard2", "");

        //Creating and adding
        LocalDateTime now = LocalDateTime.now();
        ToDo t1 = new ToDo("todotest1","", now, "", "" , "", u1, "#000000");
        ToDo t2 = new ToDo("todotest2","", now, "", "" , "", u1, "#000000");
        ToDo t3 = new ToDo("todotest3","", now, "", "" , "", u1, "#000000");
        ToDo t4 = new ToDo("todotest4","", now, "", "" , "", u1, "#000000");
        ToDo t5 = new ToDo("todotest5","", now, "", "" , "", u1, "#000000");
        ToDo t6 = new ToDo("todotest6","", now, "", "" , "", u1, "#000000");
        u1.addToDo("testboard1", t1);
        u1.addToDo("testboard1", t2);
        u1.addToDo("testboard1", t3);
        u1.addToDo("testboard2", t4);
        u1.addToDo("testboard2", t5);
        u1.addToDo("testboard2", t6);

        System.out.println(u1.getNoticeboard("testboard1").toString());
        System.out.println(u1.getNoticeboard("testboard2").toString());

        u1.addSharing("testboard1", "todotest1", u2);
        u1.addSharing("testboard1", "todotest2", u2);
        u1.addSharing("testboard1", "todotest3", u2);
        u1.addSharing("testboard2", "todotest4", u3);
        u1.addSharing("testboard2", "todotest5", u3);
        u1.addSharing("testboard2", "todotest6", u3);

        System.out.println("\nSharing testboard2 with U3");
        System.out.println(t6.toString());
        System.out.println(u3.getNoticeboard("testboard2").toString());

        System.out.println("\nUnsharing todotest6 in testboard2 from U3");
        u1.removeSharing("testboard2","todotest6",u3);
        System.out.println(t6.toString());
        System.out.println(u3.getNoticeboard("testboard2").toString());
    }
}