package gui.views.board.forms;

//Java imports
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.regex.Pattern;

//App imports
import dto.ToDoDTO;

/**
 * <p>A class that creates a Form to insert information about a ToDo</p>
 */
public class ToDoForm extends JDialog{
    //GUI variables
    private JPanel mainPanel;

    private JLabel completionStateLabel;
    private JScrollPane descriptionScrollPane;
    private JPanel colorDisplay;
    private ButtonGroup todoState;


    //Data fields
    private int todoID = -1;
    private int ownerUserID = -1;

    private LocalDateTime expiryDate;


    //GUI fields
    private JRadioButton completeRadioButton;
    private JRadioButton notCompleteRadioButton;

    private JFormattedTextField titleField;
    private JTextArea descriptionField;
    private JFormattedTextField activityURLField;
    private JFormattedTextField imageURLField;
    private JFormattedTextField expiryDateField;

    private JButton colorButton;

    //Buttons
    private JButton confirmButton;
    private JButton cancelButton;


    //Field error flags
    private boolean isDataStateValid = false;

    private boolean hasTitleErrored = false;
    private boolean hasDescriptionErrored = false;
    private boolean hasActivityURLErrored = false;
    private boolean hasImageURLErrored = false;
    private boolean hasExpiryDateErrored = false;

    
    //Utility variables
    private final Border defaultBorder = titleField.getBorder(); //Save border to apply it when a field goes from errored to a valid state

    //Constructor
    /**
     * <p>Instantiates a new ToDoForm.</p>
     * @param sourceToDo influences the appearance and behavior of the form, set a {@link ToDoDTO} if an existing ToDo needs to be edited, set {@code null} if a new ToDo needs to be created
     */
    public ToDoForm(ToDoDTO sourceToDo){
        //Initialization
        this.setContentPane(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        //Choose appropriate border title and fill in fields from an existing ToDo
        String title = null;
        if(sourceToDo == null) {
            title = "New Noticeboard";
            notCompleteRadioButton.setSelected(true);
            titleField.setValue("");
            descriptionField.setText("");
            activityURLField.setValue("");
            imageURLField.setValue("");
            expiryDateField.setValue("");
            colorDisplay.setBackground(Color.decode("#B6B6B6")); //Hex for Color.GRAY.brighter()
            }
        else {
            this.todoID = sourceToDo.getToDoID();
            this.ownerUserID = sourceToDo.getOwnerUserID();

            title = "Edit Noticeboard";
            completionStateLabel.setVisible(false);
            completeRadioButton.setVisible(false);
            notCompleteRadioButton.setVisible(false);
            if (sourceToDo.isCompleted())
                completeRadioButton.setSelected(true);
            else
                notCompleteRadioButton.setSelected(true);
            titleField.setValue(sourceToDo.getTitle());
            descriptionField.setText(sourceToDo.getDescription());
            activityURLField.setValue(sourceToDo.getActivityURL());
            imageURLField.setValue(sourceToDo.getImageURL());
            if(sourceToDo.getExpiryDate() != null)
                expiryDateField.setValue(sourceToDo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")));
            else
                expiryDateField.setValue("");
            colorDisplay.setBackground(Color.decode(sourceToDo.getBackgroundColor()));
        }

        //Set border title
        mainPanel.setBorder(new TitledBorder(
                new LineBorder(Color.black, 2, true),
                title,
                TitledBorder.CENTER, TitledBorder.BELOW_TOP,
                new Font("Dialog", Font.BOLD, 16)));


        //Setting up GUI state
        this.setTitle("Insert the information about this ToDo");


        //Setting up formatted fields state
        titleField.setToolTipText("The allowed characters are: a-z, A-Z, 0-9, '@', '#', '&', '_', '-', '.', '/', up to 128 characters."); //Set titleField tooltip
        titleField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to titleField
            private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9@#&_.\\-/ ]*$");
            private static final int CHAR_LIMIT = 128;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* auto-block invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (PATTERN.matcher(text).matches() && text.length() <= CHAR_LIMIT){
                        if(hasTitleErrored)
                            titleField.setBorder(defaultBorder);
                        return text;
                    }
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        //Setting up description field state
        descriptionField.setToolTipText("Any character is allowed, up to 256 characters."); //Set descriptionField tooltip

        //Setting up activityURl field state
        activityURLField.setToolTipText("Only URLs are allowed, up to 2048 characters."); //Set activityURLField tooltip
        activityURLField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to activityURLField
            private static final int CHAR_LIMIT = 2048;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* auto-block invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() <= CHAR_LIMIT){
                    if(hasActivityURLErrored)
                        activityURLField.setBorder(defaultBorder);
                    return text;
                }
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        //Setting up imageURL field state
        imageURLField.setToolTipText("Only URLs are allowed, up to 2048 characters."); //Set imageURLField tooltip
        imageURLField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() { //Add format to imageURLField
            private static final int CHAR_LIMIT = 2048;
            { /* Anonymous initialization block */
                setAllowsInvalid(false); /* auto-block invalid formatted insertions */
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() <= CHAR_LIMIT){
                    if(hasImageURLErrored)
                        imageURLField.setBorder(defaultBorder);
                    return text;
                }
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));

        //Setting up expiryDate field state
        expiryDateField.setToolTipText("The allowed expiry date formats are [dd/MM/yyyy hh:mm]."); //Set imageURLField tooltip
        expiryDateField.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter() {
            private static final Pattern PATTERN = Pattern.compile("^$|(?:(?:0[1-9]?|[1-2]\\d?|3[0-1]?)?(?:/(?:(0[1-9]?|1[0-2]?)?(?:/(?:(?!0000)\\d{0,4}(?: (?:(?:[0-1]\\d?|2[0-3]?)(?::(?:[0-5]\\d?)?)?)?)?)?)?)?)?)?$");
            private static final int CHAR_LIMIT = 16;
            {  //Anonymous initialization block
                setAllowsInvalid(false); // auto-block invalid formatted insertions
                setValueClass(String.class);
            }

            @Override
            public Object stringToValue(String text) throws ParseException {
                if (PATTERN.matcher(text).matches() && text.length() <= CHAR_LIMIT){
                        if(hasExpiryDateErrored)
                            expiryDateField.setBorder(defaultBorder);
                        return text;
                }
                throw new ParseException("Invalid input: " + text, 0);
            }
        }));


        //Setting up buttons listeners
        colorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Color color = JColorChooser.showDialog(mainPanel, "Choose the ToDo color (optional)", colorDisplay.getBackground());
                if(color != null)
                    colorDisplay.setBackground(color);
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //Validate all user input
                boolean titlevalid = validateTitleField();
                boolean descvalid = validateDescriptionField();
                boolean actvalid = validateActivityURLField();
                boolean imgvalid = validateImageURLField();
                boolean expvalid = validateExpiryDateField();
                isDataStateValid = titlevalid && descvalid && actvalid && imgvalid && expvalid;

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
     * <p>Shows the ToDoForm to the user and builds a {@link ToDoDTO} from user input.</p>
     * @return if the inserted input is valid, returns its corresponding {@link ToDoDTO} object, otherwise returns {@code null}
     */
    public ToDoDTO showToDoForm(){
        this.pack();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = this.getSize();
        this.setLocation(screenDim.width/2 - frameDim.width/2, screenDim.height/2 - frameDim.height/2); //Sets position at the center of the screen

        this.setVisible(true);
        mainPanel.grabFocus(); //Grab focus to avoid focusing on fields on opening

        if(!isDataStateValid)
            return null;

        return new ToDoDTO(
                this.todoID,
                completeRadioButton.isSelected(),
                (String)titleField.getValue(),
                descriptionField.getText(),
                (String)activityURLField.getValue(),
                (String)imageURLField.getValue(),
                expiryDate,
                this.ownerUserID,
                "#" + Integer.toHexString(colorDisplay.getBackground().getRGB()).substring(2) //Converts color string to hex RGB
        );
    }

    //Field validation methods
    /**
     * <p>Validates the title field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateTitleField() {
        try {
            //Assert that title is valid
            titleField.commitEdit();
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

    /**
     * <p>Validates the description field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateDescriptionField() {
        //Assert that description is valid
        String descriptionValue = descriptionField.getText();
        if (descriptionValue.length() > 256) {
            hasDescriptionErrored = true;
            descriptionScrollPane.setBorder(new LineBorder(Color.RED, 2, true));
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        else if(hasDescriptionErrored)
            descriptionScrollPane.setBorder(defaultBorder);

        return true;
    }

    /**
     * <p>Validates the activity URL field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateActivityURLField() {
        try {
            //Assert that url is valid
            activityURLField.commitEdit();
            String activityURLValue = (String)activityURLField.getValue();

            if(!activityURLValue.isBlank() && !activityURLValue.matches("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")) {
                hasActivityURLErrored = true;
                activityURLField.setBorder(new LineBorder(Color.RED, 2, true));
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

    /**
     * <p>Validates the image URL field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateImageURLField() {
        //Assert that url is valid
        try {
            imageURLField.commitEdit();
            String imageURLValue = (String)imageURLField.getValue();

            if(!imageURLValue.isBlank() && !imageURLValue.matches("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")) {
                hasImageURLErrored = true;
                imageURLField.setBorder(new LineBorder(Color.RED, 2, true));
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

    /**
     * <p>Validates the expiry date field and highlights an error.</p>
     * @return {@code true} if no input errors are detected, otherwise {@code true}
     */
    private boolean validateExpiryDateField() {
        //Assert that expiry date is valid
        try {
            expiryDateField.commitEdit();
            String expiryDateValue = (String)expiryDateField.getValue();

            if (!expiryDateValue.isBlank()) {
                try {
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");
                    expiryDate = LocalDateTime.parse(expiryDateValue, format);
                    return true;
                } catch (DateTimeParseException _) {
                    hasExpiryDateErrored = true;
                    expiryDateField.setBorder(new LineBorder(Color.RED, 2, true));
                    Toolkit.getDefaultToolkit().beep();
                    return false;
                }
            }
            return true;
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}