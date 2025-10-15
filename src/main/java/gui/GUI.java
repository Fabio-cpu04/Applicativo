package gui;

//App imports
import controller.Controller;

import gui.views.GUIView;
import gui.views.homeview.HomeView;
import gui.views.boardview.BoardView;

/**
 * <p>The GUI, controls which View is displayed.</p>
 *
 * @see GUIView GUIView
 * @see HomeView HomeView
 * @see HomeView BoardView
 */
public class GUI {
    private GUIView currentView;

    //Constructor
    /**
     * <p>Initializes a new GUI, with an initial window prompting for user login or signup.</p>
     */
    public GUI(){
        //Setting up state
        this.currentView = null;

        //Initializing HomeView
        swapAndDisposeView(new HomeView(this));
        //Controller.getInstance().authenticateUser("admin", "admin");
        //swapAndDisposeView(new BoardView());
    }

    //Methods
    /**
     * <p>Disposes the current View and replaces it with another.</p>
     * @param view the new view
     */
    public void swapAndDisposeView(GUIView view) {
        if (currentView != null && view != null)
            currentView.disposeView();

        currentView = view;
    }

    //Main method
    /**
     * The Main function of the App, spawns a {@link GUI}.
     * @param args unused main arguments
     */
    public static void main(String[] args) {
        new GUI();
    }
}
