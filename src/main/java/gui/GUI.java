package gui;

//App imports
import controller.Controller;
import gui.views.*;
import model.User;

public class GUI {
    private static GUIView currentView;

    //Methods
    public static void swapAndDisposeFrame(GUIView newMenu) {
        if (currentView != null && newMenu != null) {
            currentView.getFrame().setVisible(false);
            currentView.getFrame().dispose();
        }
        currentView = newMenu;
    }

    public static void initGUI(User user) {
        //swapAndDisposeFrame(new Home(controller));
        Controller.getController().setLoggedUser(user);
        swapAndDisposeFrame(new Home());
    }
}
