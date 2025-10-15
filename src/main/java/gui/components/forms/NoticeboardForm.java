package gui.components.forms;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.regex.Pattern;

import dto.NoticeboardDTO;

/**
 * <p>A class that spawns a Form to insert information about a {@link model.Noticeboard}</p>
 */
public class NoticeboardForm extends JDialog {
    //GUI variables
    private JPanel mainPanel;


    //Data fields
    private int boardID = -1;
    private int userID = -1;

    //GUI fields
    private JFormattedTextField titleField;
    private JFormattedTextField descriptionField;

    //Buttons
    private JButton confirmButton;
    private JButton cancelButton;


    //Field error flags
    private boolean isDataStateValid = false;

    private boolean hasTitleErrored = false;


    //Utility variables
    private final Border defaultBorder = titleField.getBorder(); //Save border to apply it when a field goes from errored to a valid state

    //Constructor
    /**
     * <p>Instantiates a new NoticeboardForm.</p>
     * @param sourceNoticeboard influences the appearance and behavior of the form, set a {@link NoticeboardDTO} if an existing Noticeboard needs to be edited, set {@code null} if a new Noticeboard needs to be created
     */
    public NoticeboardForm(NoticeboardDTO sourceNoticeboard){
        //Initialization
        this.setContentPane(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Set the data's and fields initial state
        String title = null;
        if(sourceNoticeboard == null) {
            title = "New Noticeboard";
            titleField.setValue("");
            descriptionField.setValue("");
        }
        else {
            this.boardID = sourceNoticeboard.getBoardID();
            this.userID = sourceNoticeboard.getUserID();

            title = "Edit Noticeboard";
            titleField.setValue(sourceNoticeboard.getTitle());
            descriptionField.setValue(sourceNoticeboard.getDescription());
        }

        //Set border title
        mainPanel.setBorder(new TitledBorder(
                new LineBorder(Color.black, 2, true),
                title,
                TitledBorder.CENTER, TitledBorder.BELOW_TOP,
                new Font("Dialog", Font.BOLD, 16)));


        //Setting up GUI state
        this.setTitle("Insert the information about this Noticeboard");


        //Setting up formatted fields state
        titleField.setToolTipText("The allowed characters are: a-z, A-Z, 0-9, '@', '#', '&', '_', '-', '.', '/', up to 128 characters."); //Set titleField tooltip
        titleField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to titleField
            private final Pattern pattern = Pattern.compile("^[A-Za-z0-9@#&_.\\-/ ]*$");
            private static final int CHAR_LIMIT = 128;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* auto-ock invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (pattern.matcher(text).matches() && text.length() <= CHAR_LIMIT){
                    if(hasTitleErrored)
                        titleField.setBorder(defaultBorder);
                    return text;
                }
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        descriptionField.setToolTipText("Any character is allowed, up to 256 characters."); //Set descriptionField tooltip
        descriptionField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to titleField
            private static final int CHAR_LIMIT = 256;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* block invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() <= CHAR_LIMIT)
                    return text;

                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        //Setting up buttons listeners
        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //Validate all user input
                isDataStateValid = validateTitleField();

                if(isDataStateValid)
                    dispose();
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                isDataStateValid = false;
                dispose();
            }
        });
    }


    //Methods
    /**
     * <p>Shows the NoticeboardForm to the user and builds a {@link NoticeboardDTO} from user input.</p>
     * @return if the inserted input is valid, returns its corresponding {@link NoticeboardDTO} object, otherwise returns {@code null}
     */
    public NoticeboardDTO showBoardForm(){
        this.pack();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = this.getSize();
        this.setLocation(screenDim.width/2 - frameDim.width/2, screenDim.height/2 - frameDim.height/2); //Sets position at the center of the screen

        this.setVisible(true);
        mainPanel.grabFocus(); //Grab focus to avoid focusing on fields on opening

        if(!isDataStateValid)
            return null;

        return new NoticeboardDTO(this.boardID, (String)titleField.getValue(), (String)descriptionField.getValue(), this.userID);
    }

    //Field validation methods
    /**
     * <p>Validates the title field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateTitleField(){
        try {
            titleField.commitEdit();

            //Assert that title is valid
            String titleValue = (String)titleField.getValue();
            if(titleValue.isBlank()) {
                hasTitleErrored = true;
                titleField.setBorder(new LineBorder(Color.RED, 2, true));
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
            return true;
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
