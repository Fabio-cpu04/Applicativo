package gui.views.boardview;

//Java imports
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.function.ToDoubleFunction;

import java.net.URI;

//App imports
import controller.Controller;

import controller.InexistentModelEntityException;
import controller.InvalidControllerOperationException;
import dto.*;

import gui.components.ListComponent;
import gui.components.forms.ToDoForm;

/**
 * <p>Represents a {@link model.ToDo} in the GUI.</p>
 */
class ToDoComponent {
    private final JPanel mainPanel;
    private final BoardComponent parentBoardComponent;

    private final ToDoDTO todo;

    //Setters and getters
    /**
     * <p>Gets panel.</p>
     * @return ToDoComponent 's main JPanel
     */
    JPanel getPanel() { return mainPanel; }

    //Constructors
    /**
     * <p>Instantiates a new ToDoComponent attached to a {@link BoardComponent} and linked to a {@link model.ToDo}.</p>
     * @param parent the parent {@link BoardComponent}
     * @param todo   the linked {@link ToDoDTO}
     */
    /* package */ ToDoComponent(BoardComponent parent, ToDoDTO todo) {
        //Setting up state
        this.parentBoardComponent = parent;
        this.todo = todo;

        //Setting up main panel and its layout
        mainPanel = new JPanel(new GridBagLayout());

        //Setting up "+" button
        JButton moreButton = new JButton("(+)");
        moreButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JPopupMenu popup = getTodoPopupMenu();
                popup.show(moreButton, e.getX(), e.getY());
            }
        });

        //Draw the ToDoComponent's elements
        mainPanel.add(moreButton, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
        addToDoAttributeElements(mainPanel);

        mainPanel.setVisible(true);
    }

    //Methods

    /**
     * <p>Draws the parent ToDo's attributes in the ToDoComponent.</p>
     * @param mainPanel the {@link JPanel} to draw the attributes into
     */
    private void addToDoAttributeElements(JPanel mainPanel) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);

        //Calculating color properties
        Color backgroundColor = Color.decode(todo.getBackgroundColor());
        Color textColor = (calculateContrast(Color.WHITE, backgroundColor) > calculateContrast(Color.BLACK, backgroundColor)) ? Color.WHITE : Color.BLACK;

        GridBagConstraints attributeGridBadConstraints = new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);

        //Setting up ToDoState (necessary)
        String state = todo.isCompleted() ? "[Completed] " : "[Not Completed]";
        JLabel s = new JLabel(state);
        if(todo.isCompleted())
            s.setForeground((calculateContrast(Color.GREEN.brighter(), backgroundColor) > calculateContrast(Color.GREEN.darker(), backgroundColor)) ? Color.GREEN.brighter() : Color.GREEN.darker());
        else
            s.setForeground(textColor);

        mainPanel.add(s, attributeGridBadConstraints);

        //Setting up ToDo title (necessary) and ToDo description (optional)
        this.drawTitleAndDescription(textColor, backgroundColor, boardSize, attributeGridBadConstraints);

        //Setting up ToDo image (optional)
        this.drawImage(textColor, backgroundColor, boardSize, attributeGridBadConstraints);

        //Setting up ToDo activityURL (optional)
        this.drawActivityURL(backgroundColor, attributeGridBadConstraints);

        //Setting up ToDo expiry date (optional)
        this.drawExpiryDate(textColor, backgroundColor, attributeGridBadConstraints);

        //Set up ToDo background color and border
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createMatteBorder(1,1,2,1, backgroundColor.darker()));
    }

    //ToDo Attributes Renderer Helpers
    private void drawTitleAndDescription(Color textColor, Color backgroundColor, Dimension todoSize, GridBagConstraints constraints) {
        constraints.gridy += 1;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;

        String title = todo.getTitle();
        String desc = todo.getDescription();

        //Calculate title color
        Color titleColor = textColor;
        if ((todo.isExpired() && !todo.isCompleted())) {
            if (calculateContrast(Color.RED.brighter(), backgroundColor) > calculateContrast(Color.RED.darker(), backgroundColor))
                titleColor = Color.RED.brighter();
            else
                titleColor = Color.RED.darker();
        }

        //If description exists, let title be its border's title
        if (desc != null && !desc.isEmpty()) {
            Border titleBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
                            (calculateContrast(backgroundColor.brighter(), backgroundColor) > calculateContrast(backgroundColor.darker(), backgroundColor)) ? backgroundColor.brighter() : backgroundColor.darker(), 2, true),
                    title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, titleColor);

            JTextPane p = styleText(desc, false, false, textColor, false, false);
            p.setBackground(backgroundColor);
            p.setBorder(titleBorder);
            p.setToolTipText(title);
            p.setSize(new Dimension(todoSize.width, Short.MAX_VALUE));
            p.setPreferredSize(new Dimension(todoSize.width, p.getPreferredSize().height)); //Size workaround, first forces size to board width & max height, then resets height to fit
            mainPanel.add(p, constraints);
        }
        //If description does not exist, only draw the title
        else {
            JTextPane t = styleText(title, true, false, titleColor, false, false);
            t.setBackground(backgroundColor);
            t.setSize(new Dimension(todoSize.width, Short.MAX_VALUE));
            t.setPreferredSize(new Dimension(todoSize.width, t.getPreferredSize().height)); //Size workaround, first forces size to board width & max height, then resets height to fit
            mainPanel.add(t, constraints);
        }
    }

    private void drawImage(Color textColor, Color backgroundColor, Dimension todoSize, GridBagConstraints constraints) {
        String imgPath = todo.getImageURL();
        if(imgPath != null && !imgPath.isEmpty()){
            //Load image and handle errors
            try {
                URI uri = new URI(imgPath);
                Image imgData = ImageIO.read(uri.toURL());
                ImageIcon img = new ImageIcon(imgData);

                if (img.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    constraints.gridy += 1;
                    //Rescale image
                    float widthHeightRatio = (float) img.getIconWidth() / img.getIconHeight();
                    img.setImage(img.getImage().getScaledInstance(todoSize.width, (int) (todoSize.width / widthHeightRatio), Image.SCALE_SMOOTH));

                    JLabel imgLabel = new JLabel(img);
                    mainPanel.add(imgLabel, constraints);
                }
            }
            catch (Exception e) { //Either URISyntaxException or IOException
                String errorMsg = null;
                if(!e.getClass().equals(URISyntaxException.class))
                    errorMsg = "[Image could not be loaded, invalid URL syntax]";
                else
                    errorMsg = "[Image could not be loaded from the URL]";

                constraints.gridy += 1;
                JTextPane p = styleText(errorMsg, true, false, textColor, false, false);
                p.setBackground(backgroundColor);
                mainPanel.add(p, constraints);
            }
        }
    }

    private void drawActivityURL (Color backgroundColor, GridBagConstraints constraints) {
        String activityURL = todo.getActivityURL();
        if(!activityURL.isEmpty()) {
            constraints.gridy += 1;

            Color urlColor = calculateContrast(Color.BLUE.brighter(), backgroundColor) > calculateContrast(Color.BLUE, backgroundColor) ? Color.BLUE.brighter() : Color.BLUE;
            String formattedURL = activityURL;

            //If URL is "too long" trim it for "beauty purposes"
            if(activityURL.length() > 48)
                formattedURL = formattedURL.substring(0, 32).concat("...");

            JTextPane p = styleText(formattedURL, false, false, urlColor, false, false);
            p.setBackground(backgroundColor);
            p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mainPanel.add(p, constraints);

            //Listener to open link with browser on click
            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        Desktop.getDesktop().browse(new URI(activityURL));
                    } catch (Exception _) {
                        invalidateLink();
                    }
                }

                private void invalidateLink() {
                    //Remove listener, reset cursor and add "Bad Link" label
                    p.removeMouseListener(this);
                    p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                    Document d = p.getStyledDocument();
                    Style style = p.getStyle("customStyle");
                    StyleConstants.setBold(style, true);

                    Color badlinkColor = (calculateContrast(Color.RED.brighter(), backgroundColor) > calculateContrast(Color.RED.darker(), backgroundColor)) ? Color.RED.brighter() : Color.RED.darker();
                    StyleConstants.setForeground(style, badlinkColor);
                    try {
                        d.insertString(d.getLength(), " (Bad Link)", style);
                    } catch (Exception exc) {
                        exc.printStackTrace(); //This is supposed to be very-hardly-reachable, how did you even trigger this exception?
                    }
                }
            });
        }
    }

    private void drawExpiryDate(Color textColor, Color backgroundColor, GridBagConstraints constraints) {
        LocalDateTime expiryDate = todo.getExpiryDate();
        if(todo.getExpiryDate() != null && todo.getExpiryDate() != LocalDateTime.MAX) {
            constraints.gridy += 1;
            JTextPane p = styleText(expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")), false, true, textColor, false, false);
            p.setBackground(backgroundColor);

            if (todo.isExpired()) { //If expired, add "(expired)" at end of date.
                Document doc = p.getStyledDocument();
                Style style = p.getStyle("customStyle");

                StyleConstants.setBold(style, true); //Set bold

                //If expired and not completed set color red
                if(!todo.isCompleted()) {
                    if (calculateContrast(Color.RED.brighter(), backgroundColor) > calculateContrast(Color.RED.darker(), backgroundColor))
                        StyleConstants.setForeground(style, Color.RED.brighter());
                    else
                        StyleConstants.setForeground(style, Color.RED.darker());
                }

                try { //Add '(expired)' to the date
                    doc.insertString(doc.getLength(), " (expired)", style);
                } catch (Exception e) {
                    e.printStackTrace(); //This is not really supposed to be reachable
                }
            }
            mainPanel.add(p, constraints);
        }
    }

    /**
     * <p>Initializes a customized popup menu for {@link ToDoComponent}.</p>
     * @return the generated {@link JPopupMenu}
     */
    private JPopupMenu getTodoPopupMenu() {
        JPopupMenu popup = new JPopupMenu("");

        //ToDo state section
        String completeMenuItemName = "Set " + (todo.isCompleted() ? "not " : "") +  "complete";
        JMenuItem completeItem = new JMenuItem(completeMenuItemName);
        //ToDo edit section
        JMenuItem editItem = new JMenuItem("Edit ToDo");
        //Todo sharing state section
        JMenuItem sharedUsersItem = new JMenuItem("Show the Users who share this ToDo");
        JMenuItem addSharedUserItem = new JMenuItem("Share ToDo with a User");
        JMenuItem removeSharedUserItem = new JMenuItem("Unshare ToDo with a User");
        //Todo positioning section
        JMenuItem changePositionItem = new JMenuItem("Change the ToDo's position in the Noticeboard");
        JMenuItem changeBoardItem = new JMenuItem("Move ToDo to a new Noticeboard");
        //ToDo delete section
        JMenuItem deleteItem = new JMenuItem("Delete ToDo");

        //Adding item listeners
        completeItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeAction();
            }
        });

        editItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editAction();
            }
        });

        sharedUsersItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> shares = todo.getSharedUsers();
                new ListComponent(shares); //Spawn ListComponent
            }
        });

        addSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSharedUserAction();
            }
        });

        removeSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSharedUserAction();
            }
        });

        changePositionItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePositionAction();
            }
        });

        changeBoardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeBoardAction();
            }
        });

        deleteItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.YES_OPTION ==
                        JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to delete the ToDo?", "", JOptionPane.YES_NO_OPTION)) {
                    NoticeboardDTO board = parentBoardComponent.getBoard();
                    Controller.getInstance().deleteToDo(board.getBoardID(), todo.getToDoID());

                    parentBoardComponent.reloadToDoComponent(); //Sync GUI state
                }
            }
        });

        //If ToDo owner is the current user, display all items
        if(todo.getOwnerUserID() == Controller.getInstance().getLoggedUser().getUserID()) {
            //ToDo state section
            popup.add(completeItem);
            popup.addSeparator();
            //ToDo edit section
            popup.add(editItem);
            popup.addSeparator();
            //Todo sharing state section
            popup.add(sharedUsersItem);
            popup.add(addSharedUserItem);
            popup.add(removeSharedUserItem);
            //Todo positioning section
            popup.addSeparator();
            popup.add(changePositionItem);
            popup.add(changeBoardItem);
            //ToDo delete section
            popup.addSeparator();
            popup.add(deleteItem);
        }
        else
            popup.add(sharedUsersItem); //Otherwise only add the option to show Users who share the ToDo

        return popup;
    }

    //Menu helpers
    private void completeAction() {
        String message = "Are you really sure you want to set the todo as ";
        message += todo.isCompleted() ? "not complete?" : "complete?";

        if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainPanel, message, "", JOptionPane.YES_NO_OPTION)) {
            int boardID = parentBoardComponent.getBoard().getBoardID();
            int todoID = todo.getToDoID();
            Controller.getInstance().updateCompletionState(boardID, todoID);

            parentBoardComponent.reloadToDoComponent();
        }
    }

    private void editAction() {
        ToDoForm form = new ToDoForm(todo);
        ToDoDTO newToDo = form.showToDoForm();
        if (newToDo == null)
            return;

        //Get board&todo IDs
        int boardID = parentBoardComponent.getBoard().getBoardID();
        int todoID = todo.getToDoID();

        //If necessary replace title
        if (!newToDo.getTitle().equals(todo.getTitle())) { //If newtitle != oldtitle
            try {
                Controller.getInstance().updateToDoTitle(boardID, todoID, newToDo.getTitle());
            }
            catch (InvalidControllerOperationException _) {
                JOptionPane.showMessageDialog(mainPanel, "A ToDo with the same title already exists in the board", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        //If necessary replace description
        if(!newToDo.getDescription().equals(todo.getDescription()))
            Controller.getInstance().updateToDoDescription(boardID, todoID, newToDo.getDescription());

        //If necessary (null case is to avoid dealing with NullPointerException) replace expiry date
        if((newToDo.getExpiryDate() == null && todo.getExpiryDate() != null) || !newToDo.getExpiryDate().equals(todo.getExpiryDate()))
                Controller.getInstance().updateToDoExpiryDate(boardID, todoID, newToDo.getExpiryDate());

        //If necessary replace activity url
        if(!newToDo.getActivityURL().equals(todo.getActivityURL()))
            Controller.getInstance().updateToDoActivityURL(boardID, todoID, newToDo.getActivityURL());

        //If necessary replace activity url
        if(!newToDo.getImageURL().equals(todo.getImageURL()))
            Controller.getInstance().updateToDoImageURL(boardID, todoID, newToDo.getImageURL());

        //If necessary replace background color
        if(!newToDo.getBackgroundColor().equals(todo.getBackgroundColor()))
            Controller.getInstance().updateToDoBackgroundColor(boardID, todoID, newToDo.getBackgroundColor());

        parentBoardComponent.reloadToDoComponent();
    }

    private void addSharedUserAction() {
        String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
        if(username != null){
            int boardID = parentBoardComponent.getBoard().getBoardID();
            int todoID = todo.getToDoID();

            try {
                Controller.getInstance().addSharedUser(boardID, todoID, username);
            }
            catch (InvalidControllerOperationException invalidop) {
                if(invalidop.getErrorType() == InvalidControllerOperationException.InvalidOperationType.TODO_IS_ALREADY_SHARED)
                    JOptionPane.showMessageDialog(mainPanel, "ToDo is already shared with user \"" + username + "\".", "Error", JOptionPane.ERROR_MESSAGE);

                if(invalidop.getErrorType() == InvalidControllerOperationException.InvalidOperationType.CANNOT_SHARE_TODO_WITH_YOURSELF)
                    JOptionPane.showMessageDialog(mainPanel, "You cannot share a ToDo with yourself.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (InexistentModelEntityException inexistententity) {
                if(inexistententity.getEntityType() == InexistentModelEntityException.EntityType.INEXISTENT_USER)
                    JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                else
                    throw inexistententity;
            }
        }

        parentBoardComponent.reloadToDoComponent();
    }

    private void removeSharedUserAction() {
        String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
        if(username != null) {
            int boardID = parentBoardComponent.getBoard().getBoardID();
            int todoID = todo.getToDoID();

            try {
                Controller.getInstance().removeSharedUser(boardID, todoID, username);
            }
            catch (InvalidControllerOperationException _) {
                JOptionPane.showMessageDialog(mainPanel, "ToDo is not shared with user \"" + username + "\".", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (InexistentModelEntityException entityexc) {
                if(entityexc.getEntityType() == InexistentModelEntityException.EntityType.INEXISTENT_USER)
                    JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                else
                    throw entityexc;
            }
        }

        parentBoardComponent.reloadToDoComponent();
    }

    private void changePositionAction() {
        //Create ListComponent object and remove the todo from the list
        NoticeboardDTO board = parentBoardComponent.getBoard();
        List<ToDoDTO> todos = board.getToDos();
        List<String> items = todos.stream().map(ToDoDTO::getTitle).toList();
        ListComponent list = new ListComponent(items, "Move ToDo");

        String todoTitle = todo.getTitle();
        list.getModel().removeElement(todoTitle);

        //Create "Move" button and init its settings
        JButton button = new JButton("Move to the top");
        if(todos.size() == 1)
            button.setEnabled(false);

        list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        button.setVisible(true);
        list.reloadListComponent();

        //Add list and button listeners
        list.getList().addListSelectionListener(e -> {
            button.setEnabled(true);
            button.setText("Move after ToDo");
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if(!button.isEnabled())
                    return;

                //Sync App change
                String selectedToDoTitle = list.getList().getSelectedValue();
                int predecessorIndex = (selectedToDoTitle == null) ? 0 : todos.indexOf(board.getToDo(selectedToDoTitle)) + 1;

                Controller.getInstance().moveToDoToIndex(board.getBoardID(), todo.getToDoID(), predecessorIndex);

                //Sync GUI state
                list.dispose();
                parentBoardComponent.getParentViewer().refreshBoardComponents();
            }
        });
    }

    private void changeBoardAction() {
        //Create BoardList object with all the boards owned by the logged user
        int userID = Controller.getInstance().getLoggedUser().getUserID();
        List<NoticeboardDTO> ownedBoards = Controller.getInstance().getNoticeboards().stream().filter(board -> board.getUserID() == userID).toList();
        List<String> items = ownedBoards.stream().map(NoticeboardDTO::getTitle).toList();

        ListComponent list = new ListComponent(items, "Move ToDo to a new board");
        list.getModel().removeElement(parentBoardComponent.getBoard().getTitle()); //Remove the original board from the list

        //Create "Move" button and init its settings
        JButton button = new JButton("Move");
        button.setEnabled(false);
        list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        button.setVisible(true);
        list.reloadListComponent();

        //Add list and button listeners
        list.getList().addListSelectionListener(e -> button.setEnabled(true));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if(!button.isEnabled())
                    return;

                List<NoticeboardDTO> boards = Controller.getInstance().getNoticeboards();

                String newBoardTitle = list.getList().getSelectedValue();

                if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(list.getPanel(),
                        "Are you really sure you want to move the ToDo to \"" + newBoardTitle + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                    try {
                        //Sync App change
                        int newBoardID = boards.stream().filter(board -> board.getTitle().equals(newBoardTitle)).findFirst().get().getBoardID(); //Calculate the correct index
                        Controller.getInstance().moveToDoToBoard(parentBoardComponent.getBoard().getBoardID(), todo.getToDoID(), newBoardID);

                        //Sync GUI state
                        list.dispose();
                        parentBoardComponent.getParentViewer().refreshBoardComponents();
                    }
                    catch (InvalidControllerOperationException exc){
                        if(exc.getErrorType() == InvalidControllerOperationException.InvalidOperationType.TODO_TITLE_ALREADY_EXISTS)
                            JOptionPane.showMessageDialog(mainPanel, "The destination Noticeboard already owns a ToDo titled \"" + todo.getTitle() + "\".", "Error", JOptionPane.ERROR_MESSAGE);
                        else
                            throw exc;
                    }
                }
            }
        });
    }

    //Utility Methods
    /**
     * <p>Calculates the contrast between a foreground color and a background color.</p>
     * @param foregroundColor the foreground color
     * @param backgroundColor the background color
     * @return the contrast ratio
     */
    private static double calculateContrast(Color foregroundColor, Color backgroundColor) {
        //Magic lambda function to calculate luminance for contrast ratio (https://www.w3.org/TR/WCAG21/#dfn-contrast-ratio)
        ToDoubleFunction<Color> getRelativeLuminance = c -> {
            // Normalize RGB to [0,1]
            double r = c.getRed() / 255.0;
            double g = c.getGreen() / 255.0;
            double b = c.getBlue() / 255.0;

            r = (r <= 0.03928) ? (r / 12.92) : Math.pow((r + 0.055) / 1.055, 2.4);
            g = (g <= 0.03928) ? (g / 12.92) : Math.pow((g + 0.055) / 1.055, 2.4);
            b = (b <= 0.03928) ? (b / 12.92) : Math.pow((b + 0.055) / 1.055, 2.4);

            return 0.2126 * r + 0.7152 * g + 0.0722 * b;
        };

        double l1 = getRelativeLuminance.applyAsDouble(foregroundColor);
        double l2 = getRelativeLuminance.applyAsDouble(backgroundColor);

        // Contrast ratio
        return (Math.max(l1,l2) + 0.05) / (Math.min(l1,l2) + 0.05);
    }

    /**
     * <p>Instantiates a {@link JTextPane} with the specified text and settings.</p>
     * @param text the text to display
     * @param bold whether the text should be bold or not
     * @param italic whether the text should be italic or not
     * @param color the text's color
     * @param editable whether the text should be editable or not
     * @param selectable whether the text should be selectable or not
     * @return a JTextPane initialized with the specified text and settings
     */
    private JTextPane styleText(String text, boolean bold, boolean italic, Color color, boolean editable, boolean selectable) {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(editable);
        textPane.setFocusable(selectable);

        StyledDocument doc = textPane.getStyledDocument();
        Style style = doc.addStyle("customStyle", null);

        if(bold)
            StyleConstants.setBold(style, true);
        if(italic)
            StyleConstants.setItalic(style, true);
        if(color != null)
            StyleConstants.setForeground(style, color);

        textPane.setStyledDocument(doc);

        try {
            doc.insertString(0, text, style);
        }
        catch (Exception e){
            e.printStackTrace(); //This is supposed to be very-hardly-reachable, how did you even trigger this exception?
        }

        return textPane;
    }
}