package gui.views.home;

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
import gui.views.board.BoardView;

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
     * <p>Handles the validation of the user input in the HomeView and the registration of a User in the system.</p>
     * @return {@code true} if the User has been registered, {@code false} otherwise.
     */
    private boolean registerUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passField.getPassword());

        //Further error checking
        if(user.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank username is not allowed", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(pass.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank password is not allowed", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(pass.length() > 128){
            JOptionPane.showMessageDialog(homeFrame, "The password is not valid, passwords can only contain up to a maximum of 128 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            return Controller.getInstance().registerUser(user, pass);
        }
        catch (IllegalArgumentException _){
            JOptionPane.showMessageDialog(homeFrame, "The username is not valid, usernames can only contain numbers, letters and the following symbols:'.','-','_'.' up to a maximum of 128 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        catch (IllegalStateException _) {
            JOptionPane.showMessageDialog(homeFrame, "The user " + user +" already exists in the system.", "", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * <p>Handles the validation of the user input in the HomeView and the logging of a User in the system.</p>
     * @return {@code true} if the User has been authenticated, {@code false} otherwise.
     */
    private boolean loginUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passField.getPassword());

        //Further error checking
        if(user.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank username is not allowed", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(pass.isBlank()) {
            JOptionPane.showMessageDialog(homeFrame, "A blank password is not allowed", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(pass.length() > 128){
            JOptionPane.showMessageDialog(homeFrame, "The password is not valid, passwords can only contain up to a maximum of 128 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try{
            int res = Controller.getInstance().authenticateUser(user, pass);
            if(res == 0)
                return true;
            else if (res == -1) //Wrong pass
                JOptionPane.showMessageDialog(homeFrame, "Wrong password.", "Error", JOptionPane.ERROR_MESSAGE);
            else if (res == -2)
                JOptionPane.showMessageDialog(homeFrame, "This username is not currently registered.", "", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IllegalArgumentException _){
            JOptionPane.showMessageDialog(homeFrame, "The username is not valid, usernames can only contain numbers, letters and the following symbols:'.','-','_'.' up to a maximum of 128 characters.", "", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }
}
