package controller;

//Java imports
import java.util.ArrayList;

//App imports
import model.*;

public class Controller {
    public enum State
    {
        NONE,
        HOME,
        VIEWER
    }

    //Members
    private static Controller ctrl;
    private State state = State.NONE;
    private User currentUser;

    private ArrayList<User> users;

    //Getters and Setters
    public void setState(State state) { if(ctrl != null && state != State.NONE) this.state = state; }
    public State getState() { return state; }

    public void setLoggedUser(User user) { this.currentUser = user; }
    public User getLoggedUser() { return currentUser; }

    //Singleton instance getter
    public static Controller getController() {
        if(ctrl == null)
            ctrl = new Controller();
        return ctrl;
    }

    //Private Constructor
    private Controller() {
        users = new ArrayList<User>();
    }

    //Methods
    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String username) {
        for (User user : users) {
            if(user.getUsername().equals(username))
                return user;
        }
        return null;
    }

}
