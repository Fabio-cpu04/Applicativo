package gui.views.board;

//Java imports
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.regex.Pattern;

import java.net.URI;

//App imports
import controller.Controller;
import dto.*;

/**
 * Represents a {@link ToDoDTO} in the GUI.
 */
class ToDoComponent {
    private final JPanel mainPanel;
    private final BoardComponent parentBoardComponent;

    private final ToDoDTO todo;

    //Setters and getters

    /**
     * Gets panel.
     * @return ToDoComponent 's main JPanel
     */
    JPanel getPanel() { return mainPanel; }

    //Constructors
    /**
     * Instantiates a new ToDoComponent linked to {@code todo} to be attached to the {@code parent} component.
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
    private void addToDoAttributeElements(JPanel mainPanel) {
        //Setting up ToDo Components
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);
        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);

        //Calculating properties
        Color backgroundColor = todo.getBackGroundColor().isEmpty() ? Color.GRAY.brighter() : Color.decode(todo.getBackGroundColor());

        //Setting up ToDoState (necessary)
        String state = todo.isCompleted() ? "[Completed] " : "[Not Completed]";
        JLabel s = new JLabel(state);
        if(todo.isCompleted())
            s.setForeground(Color.GREEN.darker());

        mainPanel.add(s, c);
        c.gridy += 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        //Setting up ToDo title (necessary)
        String title = todo.getTitle();
        JTextPane t = styleText(title, true, false, (todo.isExpired() ? Color.RED.darker() : null), false, false);
        t.setBackground(backgroundColor);
        mainPanel.add(t, c);

        //Setting up ToDo description (optional)
        String desc = todo.getDescription();
        if(desc != null && !desc.isEmpty()) {
            c.gridy += 1;
            JTextPane p = styleText(desc, false, false, null, false, false);
            p.setBackground(backgroundColor.brighter());
            mainPanel.add(p, c);
        }

        //Setting up ToDo image (optional)
        String imgPath = todo.getImageURL();
        if(imgPath != null && !imgPath.isEmpty()){
            c.gridy += 1;
            ImageIcon img = new ImageIcon(imgPath, "Image could not be loaded");
            JLabel imgLabel = new JLabel(img);
            mainPanel.add(imgLabel, c);
        }

        //Setting up ToDo activityURL (optional)
        String activityURL = todo.getActivityURL();
        if(activityURL != null && !activityURL.isEmpty()) {
            c.gridy += 1;
            JTextPane p = styleText(activityURL, false, false, Color.BLUE, false, false);
            p.setBackground(backgroundColor);
            p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mainPanel.add(p, c);

            //Listener to open link with browser on click
            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        Desktop.getDesktop().browse(new URI(activityURL));
                    } catch (Exception ex) {
                        //Remove listener, reset cursor and add "Bad Link" label
                        p.removeMouseListener(this);
                        p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                        Document d = p.getStyledDocument();
                        Style style = p.getStyle("customStyle");
                        StyleConstants.setBold(style, true);
                        StyleConstants.setForeground(style, Color.RED.darker());
                        try {
                            d.insertString(d.getLength(), " (Bad Link)", style);
                        } catch (Exception exc) {
                            exc.printStackTrace(); //This is supposed to be very-hardly-reachable, how did you even trigger this exception?
                        }
                    }
                }
            });
        }

        //Setting up ToDo expiry date (optional)
        LocalDateTime expiryDate = todo.getExpiryDate();
        if(todo.getExpiryDate() != null && todo.getExpiryDate() != LocalDateTime.MAX) {
            c.gridy += 1;
            JTextPane p = styleText(expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")), false, true, null, false, false);
            p.setBackground(backgroundColor);

            if (todo.isExpired()) { //If expired, add "(exp)" at end of date.
                Document doc = p.getStyledDocument();
                Style style = p.getStyle("customStyle");
                StyleConstants.setBold(style, true);
                try {
                    doc.insertString(doc.getLength(), " (expired)", style);
                } catch (Exception e) {
                    e.printStackTrace(); //This is supposed to be very-hardly-reachable, how did you even trigger this exception?
                }
            }
            mainPanel.add(p, c);
        }

        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createMatteBorder(1,1,2,1, backgroundColor.darker()));
    }

    //Utility Methods

    /**
     * Gets a valid to do from user input.
     * @param mainPanel the panel that spawns the dialogues
     * @return a ToDo constructed according to the user's input.
     */
    /* package */ static ToDoDTO getToDoFromUserInput(JPanel mainPanel) {
        boolean validInput = false;
        String title = null, description = null, expiryDateString = null, link = null, activityURL = null, imageURL = null, backGroundColor = null; //null because of some stupid wrong compiler error
        LocalDateTime expiryDate = null;

        //Get title (necessary)
        while(!validInput) {
            title = JOptionPane.showInputDialog(mainPanel, "Insert todo title", "", JOptionPane.PLAIN_MESSAGE);
            if(title == null)
                return null;
            else
                validInput = !title.isEmpty();
        }
        validInput = false;

        //Get description (optional)
        description = JOptionPane.showInputDialog(mainPanel, "Insert todo description (optional)", "", JOptionPane.PLAIN_MESSAGE);
        if(description == null)
            return null;

        //Get expiryDate (optional)
        while(!validInput) {
            expiryDateString = JOptionPane.showInputDialog(mainPanel, "Choose expiration date (use the following format \"dd/mm/yyyy hh:mm\", date is optional, leave blank to use no date)", "", JOptionPane.PLAIN_MESSAGE);
            if(expiryDateString == null)
                return null;
            else {
                validInput = true;

                if(expiryDateString.isEmpty())
                    expiryDate = LocalDateTime.MAX;
                else
                {
                    try {
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");
                        expiryDate = LocalDateTime.parse(expiryDateString, format);
                    }
                    catch (DateTimeParseException e) {
                        validInput = false;
                    }
                }
            }
        }
        validInput = false;

        //Get activity url (optional)
        while(!validInput) {
            activityURL = JOptionPane.showInputDialog(mainPanel, "Insert todo activity's URL (optional)", "", JOptionPane.PLAIN_MESSAGE);
            if(activityURL != null)
                validInput = activityURL.isEmpty() || Pattern.matches("^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", activityURL);
            else
                return null;
        }

        //Get image url (optional)
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showDialog(mainPanel, "Choose ToDo's image");
        if(returnVal == JFileChooser.APPROVE_OPTION)
            imageURL = fc.getSelectedFile().getAbsolutePath().replace('\\', '/');
        else if(returnVal == JFileChooser.CANCEL_OPTION)
            imageURL = "";
        else
            return null;

        //Get color (optional)
        Color color = JColorChooser.showDialog(mainPanel, "Choose todo's color (optional)", null);
        if(color == null)
            backGroundColor = "";
        else
            backGroundColor = "#" + Integer.toHexString(color.getRGB()).substring(2); //Converts to hex RGB (color.getRGB() returns in AARRGGBB format)

        return new ToDoDTO(title, description, expiryDate, activityURL, imageURL, Controller.get().getLoggedUser().getUsername(), backGroundColor);
    }

    private JTextPane styleText(String text, boolean bold, boolean italic, Color color, boolean editable, boolean focusable) {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(editable);
        textPane.setFocusable(focusable);

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

    private JPopupMenu getTodoPopupMenu() {
        JPopupMenu popup = new JPopupMenu("");
        //ToDo delete section
        JMenuItem deleteItem = new JMenuItem("Delete ToDo");
        //ToDo state section
        JMenuItem completeItem = todo.isCompleted() ? new JMenuItem("Set not complete") : new JMenuItem("Set complete");
        //ToDo data change section
        JMenuItem titleItem = new JMenuItem("Change title");
        JMenuItem descriptionItem = new JMenuItem("Change description");
        JMenuItem expiryDateItem = new JMenuItem("Change expiry date");
        JMenuItem activityItem = new JMenuItem("Change activity URL");
        JMenuItem imageItem = new JMenuItem(todo.getImageURL().isEmpty() ? "Change image" : "Remove image");
        JMenuItem backgroundColorItem = new JMenuItem("Change background color");
        //Todo sharing state section
        JMenuItem sharedUsersItem = new JMenuItem("Show users who share this ToDo");
        JMenuItem addSharedUserItem = new JMenuItem("Share with user");
        JMenuItem removeSharedUserItem = new JMenuItem("Unshare with user");
        //Todo positioning section
        JMenuItem changePositionItem = new JMenuItem("Change position");
        JMenuItem changeBoardItem = new JMenuItem("Move ToDo to board");

        //Adding item listeners
        deleteItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.YES_OPTION ==
                JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to delete the ToDo?", "", JOptionPane.YES_NO_OPTION)) {
                    NoticeboardDTO board = parentBoardComponent.getBoard();
                    Controller.get().deleteToDo(board.getTitle(), todo.getTitle());

                    parentBoardComponent.reloadToDoComponent(); //Sync GUI state
                }
            }
        });

        completeItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.YES_OPTION ==
                JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to " + (todo.isCompleted() ? "set as non complete" : "set as complete") + "?", "", JOptionPane.YES_NO_OPTION)) {
                    String boardTitle = parentBoardComponent.getBoard().getTitle();
                    String todoTitle = todo.getTitle();
                    Controller.get().changeCompletionState(boardTitle, todoTitle);

                    parentBoardComponent.reloadToDoComponent();
                }
            }
        });

        titleItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean validInput = false;
                do {
                    String title = JOptionPane.showInputDialog(mainPanel, "Insert todo title", "", JOptionPane.PLAIN_MESSAGE);
                    if(title == null)
                        validInput = true;
                    else if (!title.isEmpty()) {
                        validInput = true;

                        String boardTitle = parentBoardComponent.getBoard().getTitle();
                        String todoTitle = todo.getTitle();
                        try {
                            Controller.get().updateToDoTitle(boardTitle, todoTitle, title);
                        }
                        catch (IllegalStateException exc){
                            JOptionPane.showMessageDialog(mainPanel, "A ToDo with the same title already exists in the board", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } while(!validInput);
                parentBoardComponent.reloadToDoComponent();
            }
        });

        descriptionItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = JOptionPane.showInputDialog(mainPanel, "Insert todo description (optional)", "", JOptionPane.PLAIN_MESSAGE);
                if(description != null) {
                    String boardTitle = parentBoardComponent.getBoard().getTitle();
                    String todoTitle = todo.getTitle();
                    Controller.get().updateToDoDescription(boardTitle, todoTitle, description);

                    parentBoardComponent.reloadToDoComponent();
                }

            }
        });

        expiryDateItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean validInput = false;
                while(!validInput) {
                    String expiryDateString = JOptionPane.showInputDialog(mainPanel, "Choose expiration date (use the following format \"dd/mm/yyyy hh:mm\", date is optional, leave blank to use no date)", "", JOptionPane.PLAIN_MESSAGE);

                    validInput = true;
                    if(expiryDateString != null) {
                        String boardTitle = parentBoardComponent.getBoard().getTitle();
                        String todoTitle = todo.getTitle();

                        if(expiryDateString.isEmpty())
                            Controller.get().updateToDoExpiryDate(boardTitle, todoTitle, LocalDateTime.MAX);
                        else {
                            try {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");
                                LocalDateTime expiryDate = LocalDateTime.parse(expiryDateString, format);
                                Controller.get().updateToDoExpiryDate(boardTitle, todoTitle, expiryDate);
                            }
                            catch (DateTimeParseException exc) {
                                validInput = false;
                            }
                        }
                    }
                }
                parentBoardComponent.reloadToDoComponent();
            }
        });

        activityItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean validInput = false;
                do {
                    validInput = true;
                    String activityURL = JOptionPane.showInputDialog(mainPanel, "Insert todo activity's URL (optional)", "", JOptionPane.PLAIN_MESSAGE);
                    if(activityURL != null) {
                        validInput = activityURL.isEmpty() || Pattern.matches("^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", activityURL);
                        if(validInput) {
                            String boardTitle = parentBoardComponent.getBoard().getTitle();
                            String todoTitle = todo.getTitle();
                            Controller.get().updateToDoActivityURL(boardTitle, todoTitle, activityURL);
                        }
                    }
                } while(!validInput);

                parentBoardComponent.reloadToDoComponent();
            }
        });

        imageItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String boardTitle = parentBoardComponent.getBoard().getTitle();
                String todoTitle = todo.getTitle();

                if (todo.getImageURL().isEmpty()) {
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showDialog(mainPanel, "Choose ToDo's image");

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                        Controller.get().updateToDoImageURL(boardTitle, todoTitle, fc.getSelectedFile().getAbsolutePath().replace('\\', '/'));

                    parentBoardComponent.reloadToDoComponent();
                }
                else {
                    if (JOptionPane.YES_OPTION ==
                    JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to remove the image?", "", JOptionPane.YES_NO_OPTION)) {
                        Controller.get().updateToDoImageURL(boardTitle, todoTitle, "");
                        parentBoardComponent.reloadToDoComponent();
                    }
                }
            }
        });

        backgroundColorItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(mainPanel, "Choose todo's color (optional)", null);

                String boardTitle = parentBoardComponent.getBoard().getTitle();
                String todoTitle = todo.getTitle();
                if(color != null)
                    Controller.get().updateToDoBackgroundColor(boardTitle, todoTitle, "#" + Integer.toHexString(color.getRGB()).substring(2));
                    //Converts to hex RGB (color.getRGB() returns in AARRGGBB format)

                parentBoardComponent.reloadToDoComponent();
            }
        });

        sharedUsersItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> shares = todo.getSharedUsers();
                ListComponent list = new ListComponent(shares);
            }
        });

        addSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
                if(username != null){
                    if(Controller.get().userExists(username)){
                        String boardTitle = parentBoardComponent.getBoard().getTitle();
                        String todoTitle = todo.getTitle();
                        int res = Controller.get().addSharedUser(boardTitle, todoTitle, username);
                        if(res == -1)
                            JOptionPane.showMessageDialog(mainPanel, "ToDo is already shared with user " + username + " .", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                parentBoardComponent.reloadToDoComponent();
            }
        });

        removeSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
                if(username != null){
                    if(Controller.get().userExists(username)) {
                        String boardTitle = parentBoardComponent.getBoard().getTitle();
                        String todoTitle = todo.getTitle();
                        int res = Controller.get().removeSharedUser(boardTitle, todoTitle, username);
                        if(res == -1)
                            JOptionPane.showMessageDialog(mainPanel, "ToDo is not shared with user " + username + " .", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                parentBoardComponent.reloadToDoComponent();
            }
        });

        changePositionItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create ListComponent object and remove the todo from the list
                NoticeboardDTO board = parentBoardComponent.getBoard();
                List<ToDoDTO> todos = board.getToDos();
                List<String> items = todos.stream().map(ToDoDTO::getTitle).toList();
                ListComponent list = new ListComponent(items);

                String todoTitle = todo.getTitle();
                list.getModel().removeElement(todoTitle);

                //Create "Move" button and init its settings
                JButton button = new JButton("Move to the top");
                if(todos.size() == 1)
                    button.setEnabled(false);
                list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                button.setVisible(true);
                list.reloadGUIComponent();

                //Add list and button listeners
                list.getList().addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        button.setEnabled(true);
                        button.setText("Move after ToDo");
                    }
                });

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);

                        //Sync App change
                        String selectedToDoTitle = list.getList().getSelectedValue();
                        int predecessorIndex = (selectedToDoTitle == null) ? 0 : todos.indexOf(board.getToDo(selectedToDoTitle)) + 1;
                        Controller.get().moveToDoToIndex(board.getTitle(), todoTitle, predecessorIndex);

                        //Sync GUI state
                        list.dispose();
                        parentBoardComponent.getParentViewer().refreshBoardComponents();
                    }
                });
            }
        });

        changeBoardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create BoardList object and remove the todo-owning board from the list
                List<String> items = Controller.get().getNoticeboards().stream().map(NoticeboardDTO::getTitle).toList();
                ListComponent list = new ListComponent(items);
                String boardTitle = parentBoardComponent.getBoard().getTitle();
                list.getModel().removeElement(boardTitle);

                //Create "Move" button and init its settings
                JButton button = new JButton("Move");
                button.setEnabled(false);
                list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                button.setVisible(true);
                list.reloadGUIComponent();

                //Add list and button listeners
                list.getList().addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        button.setEnabled(true);
                    }
                });

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        List<NoticeboardDTO> boards = Controller.get().getNoticeboards();

                        String newBoardTitle = list.getList().getSelectedValue();
                        int index = boards.indexOf(Controller.get().getNoticeboard(newBoardTitle)); //Calculate the correct index

                        if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(list.getPanel(),
                        "Are you really sure you want to move the ToDo to \"" + newBoardTitle + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                            //Sync App change
                            Controller.get().moveToDoToBoard(parentBoardComponent.getBoard().getTitle(), todo.getTitle(), newBoardTitle);

                            //Sync GUI state
                            list.dispose();
                            parentBoardComponent.getParentViewer().refreshBoardComponents();
                        }
                    }
                });
            }
        });

        //ToDo delete section
        popup.add(deleteItem);
        popup.addSeparator();
        //ToDo state section
        popup.add(completeItem);
        popup.addSeparator();
        //ToDo data change section
        popup.add(titleItem);
        popup.add(descriptionItem);
        popup.add(expiryDateItem);
        popup.add(activityItem);
        popup.add(imageItem);
        popup.add(backgroundColorItem);
        popup.addSeparator();
        //Todo sharing state section
        popup.add(sharedUsersItem);
        popup.add(addSharedUserItem);
        popup.add(removeSharedUserItem);
        //Todo positioning section
        popup.addSeparator();
        popup.add(changePositionItem);
        popup.add(changeBoardItem);

        return popup;
    }
}