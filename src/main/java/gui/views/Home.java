package gui.views;

//Java imports
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;

//App imports
import controller.Controller;
import gui.GUI;

public class Home implements GUIView {
    private JFrame homeFrame;
    private JPanel mainPanel;

    private JTextField usernameField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;

    public JFrame getFrame() { return homeFrame; }

    //Class Methods
    public Home() {
        //Setting state
        Controller.getController().setState(Controller.State.HOME);

        //Initialization
        homeFrame = new JFrame();
        homeFrame.setContentPane(mainPanel);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);
        homeFrame.pack();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = homeFrame.getSize();
        //homeFrame.setSize(new Dimension(screenDim.width/5, screenDim.height/5));
        homeFrame.setLocation(screenDim.width/2 - frameDim.width/2, screenDim.height/2 - frameDim.height/2); //Sets position at the center of the screen

        //Setting up buttons listeners
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                String user = usernameField.getText();
                String pass = Arrays.toString(passField.getPassword());

                //User u = User.verifyLogin(user, pass)
                //Controller.setLoggedUser(u)

                if(true){ // true <- (boolean)Controller.registerUser(user, pass);
                    if(registerBtn.getBackground() == Color.RED)
                        registerBtn.setBackground(Color.getColor("2B2D30"));
                    GUI.swapAndDisposeFrame(new BoardViewer());
                }
                else {
                    registerBtn.setBackground(Color.red);
                    //JDialog "User is already registered"
                }

            }
        });

        loginBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);

                String user = usernameField.getText();
                String pass = Arrays.toString(passField.getPassword());

                //User u = User.verifyLogin(user, pass)
                //Controller.setLoggedUser(u)

                if(true){ //true <- Controller.login(user,pass) validates
                    if(loginBtn.getBackground() == Color.RED)
                        loginBtn.setBackground(Color.getColor("2B2D30"));
                    GUI.swapAndDisposeFrame(new BoardViewer());
                }
                else {
                    loginBtn.setBackground(Color.red);
                }
            }
        });

    }
}
