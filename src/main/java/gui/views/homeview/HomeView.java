package gui.views.homeview;

//Java imports
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.regex.Pattern;

//App imports
import controller.Controller;

import gui.GUI;
import gui.views.GUIView;
import gui.views.boardview.BoardView;

/**
 * <p>The HomeView, displays a form where a User can sign up or log in.</p>
 */
public class HomeView implements GUIView {
    private final JFrame homeFrame;
    private JPanel mainPanel;

    private JFormattedTextField usernameField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;

    //Implemented methods
    public void disposeView() {
        homeFrame.setVisible(false);
        homeFrame.dispose();
    }

    //Constructor
    /**
     * <p>Instantiates a new HomeView.</p>
     * @param parentGUI the parent GUI
     */
    public HomeView(GUI parentGUI) {
        //Initialization
        homeFrame = new JFrame("Sign up or Login");
        homeFrame.setContentPane(mainPanel);
        homeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);

        //Packing and readjusting frame position
        homeFrame.pack();
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = homeFrame.getSize();
        homeFrame.setLocation(screenDim.width/2 - frameDim.width/2, screenDim.height/2 - frameDim.height/2); //Sets position at the center of the screen

        //Setting up GUI state
        homeFrame.requestFocus(); //Request focus to avoid focusing on usernameField on view opening

        //Setting up formatted fields state
        usernameField.setToolTipText("The allowed characters are: a-z, A-Z, 0-9, '.', '_', '-', up to 128 characters."); //Set usernameField tooltip
        usernameField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to usernameField
            private static final Pattern pattern = Pattern.compile("[a-zA-Z0-9._\\-]*");
            private static final int CHAR_LIMIT = 128;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* auto-block invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (pattern.matcher(text).matches() && text.length() <= CHAR_LIMIT)
                    return text;
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        passField.setToolTipText("Any character is allowed, up to 128 characters."); //Set passField tooltip


        //Setting up buttons listeners
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(registerUser())
                    parentGUI.swapAndDisposeView(new BoardView());
            }
        });

        loginBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if(loginUser())
                    parentGUI.swapAndDisposeView(new BoardView());
            }
        });
    }

    //Methods
    /**
     * <p>Registers a User in the system.</p>
     * @return {@code true} if the User has been registered, {@code false} otherwise.
     */
    private boolean registerUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passField.getPassword());

        //Validate credentials
        if(!this.areUserCredentialsValid(user, pass))
            return false;

        //Register User
        try {
            return Controller.getInstance().registerUser(user, pass);
        }
        catch (IllegalArgumentException _){
            JOptionPane.showMessageDialog(homeFrame, "The username is not valid, usernames can only contain numbers, letters and the following symbols:'.','-','_'.' up to a maximum of 128 characters.", "Error - Invalid username", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        catch (IllegalStateException _) {
            JOptionPane.showMessageDialog(homeFrame, "The user " + user +" already exists in the system.", "Error - User already exists.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * <p>Authenticates an existing User in the system.</p>
     * @return {@code true} if the User has been authenticated, {@code false} otherwise.
     */
    private boolean loginUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passField.getPassword());

        //Validate credentials
        if(!this.areUserCredentialsValid(user, pass))
            return false;

        //Log in User
        try {
            int res = Controller.getInstance().authenticateUser(user, pass);
            if(res == 0)
                return true;
            else if (res == -1) //Wrong pass
                JOptionPane.showMessageDialog(homeFrame, "Wrong password.", "Error - Wrong password", JOptionPane.ERROR_MESSAGE);
            else if (res == -2)
                JOptionPane.showMessageDialog(homeFrame, "This username is not currently registered.", "Error - User not registered", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IllegalArgumentException _){
            JOptionPane.showMessageDialog(homeFrame, "The username is not valid, usernames can only contain numbers, letters and the following symbols:'.','-','_'.' up to a maximum of 128 characters.", "Error - Invalid username", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }

    /**
     * <p>Handles the validation of the user input the user credentials.</p>
     * @return {@code true} if the User's credentials are valid, {@code false} otherwise.
     */
    private boolean areUserCredentialsValid(String username, String password) {
        if(username.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank username is not allowed", "Error - Blank usernames are not allowed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(password.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank password is not allowed", "Error - Blank passwords are not allowed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(username.length() > 128){
            JOptionPane.showMessageDialog(homeFrame, "The username is not valid, usernames can only contain up to a maximum of 128 characters.", "Error - Username is too long", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(password.length() > 128){
            JOptionPane.showMessageDialog(homeFrame, "The password is not valid, passwords can only contain up to a maximum of 128 characters.", "Error - Password is too long", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
