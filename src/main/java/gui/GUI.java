package gui;

//App imports
import controller.Controller;

import gui.views.*;
import gui.views.board.BoardView;

/**
 * A global GUI interface, controls which GUI view is displayed.
 */
public class GUI {
    private GUIView currentView;

    //Constructor
    /**
     * Initializes a new GUI, with an initial window prompting for user login or signup.
     */
    public GUI(){
        //Setting up state
        this.currentView = null;

        //Initializing HomeView
        swapAndDisposeView(new HomeView());

        if(Controller.get().isUserLogged())
            swapAndDisposeView(new BoardView());
    }

    //Methods
    /**
     * Disposes the current GUI view and replaces it with another one.
     * @param view the new view
     */
    public void swapAndDisposeView(GUIView view) {
        if (currentView != null && view != null) {
            currentView.disposeView();
        }
        currentView = view;
    }
}
