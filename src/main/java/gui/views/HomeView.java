package gui.views;

//Java imports
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;

/**
 * The Home view, displays a form where a user can sign up or log in.
 */
public class HomeView implements GUIView {
    final private JFrame homeFrame;
    private JPanel mainPanel;

    private JTextField usernameField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;

    //Constructor
    /**
     * Instantiates a new Home view.
     */
    public HomeView() {
        //Initialization
        homeFrame = new JFrame();
        homeFrame.setContentPane(mainPanel);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);
        homeFrame.pack();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = homeFrame.getSize();
        homeFrame.setLocation(screenDim.width/2 - frameDim.width/2, screenDim.height/2 - frameDim.height/2); //Sets position at the center of the screen

        //Setting up buttons listeners
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                registerUser();
            }
        });

        loginBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                loginUser();
            }
        });
    }

    //Implemented methods
    public void disposeView() {
        homeFrame.setVisible(false);
        homeFrame.dispose();
    }

    /**
     * <p>Handles the validation of the user input in the Home View and the registering of a user in the system. </p>
     */
    private void registerUser() {
        String user = usernameField.getText();
        String pass = Arrays.toString(passField.getPassword());

        //User u = register(user, pass)

        if(true) { // true <- (boolean)registerFun(user, pass);
            if(registerBtn.getBackground() == Color.RED)
                registerBtn.setBackground(Color.getColor("2B2D30"));

            //Sign user up

            //Log user in the system
            //controller.setLoggedUser(u)
        }
        else {
            registerBtn.setBackground(Color.red);

            //Error dialog
            JOptionPane.showMessageDialog(homeFrame, "User is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * <p>Handles the validation of the user input in the Home View and the logging of a user in the system.</p>
     */
    private void loginUser() {
        String user = usernameField.getText();
        String pass = Arrays.toString(passField.getPassword());

        //User u = login(user, pass)

        if(true) { // true <- (boolean)loginFun(user, pass);
            if(loginBtn.getBackground() == Color.RED)
                loginBtn.setBackground(Color.getColor("2B2D30"));

            //Log user in the system
            //controller.setLoggedUser(u)
        }
        else {
            registerBtn.setBackground(Color.red);
            JOptionPane.showMessageDialog(homeFrame, "Wrong username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
