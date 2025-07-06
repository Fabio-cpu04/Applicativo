package gui.components;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import java.net.URI;

//App imports
import model.*;
import controller.Controller;

public class ToDoComponent {
    private final JPanel mainPanel;
    private final BoardComponent parentBoardComponent;

    private final ToDo todo;

    //Setters and getters
    public JPanel getPanel() { return mainPanel; }

    //Constructors
    public ToDoComponent(BoardComponent father, ToDo todo) {
        //Setting up state
        this.parentBoardComponent = father;
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
        mainPanel.add(moreButton, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));


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
        mainPanel.setVisible(true);
    }


    //Utility Methods
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

    static ToDo getToDoFromUserInput(JPanel mainPanel) {
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
        }

        //Get image url (optional)
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showDialog(mainPanel, "Choose ToDo's image");
        if(returnVal == JFileChooser.APPROVE_OPTION)
            imageURL = fc.getSelectedFile().getAbsolutePath().replace('\\', '/');
        else
            imageURL = "";

        //Get color (optional)
        Color color = JColorChooser.showDialog(mainPanel, "Choose todo's color (optional)", null);
        if(color == null)
            backGroundColor = "";
        else
            backGroundColor = "#" + Integer.toHexString(color.getRGB()).substring(2); //Converts to hex RGB (color.getRGB() returns in AARRGGBB format)

        return new ToDo(title, description, expiryDate, activityURL, imageURL, Controller.getController().getLoggedUser(), backGroundColor);
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
                    User user = Controller.getController().getLoggedUser();
                    parentBoardComponent.deleteToDo(todo);
                }
            }
        });

        completeItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.YES_OPTION ==
                JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to " + (todo.isCompleted() ? "set as non complete" : "set as complete") + "?", "", JOptionPane.YES_NO_OPTION)) {
                    todo.changeState();
                    parentBoardComponent.reloadToDoComponents();
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
                        todo.setTitle(title);
                    }
                } while(!validInput);
                parentBoardComponent.reloadToDoComponents();
            }
        });

        descriptionItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = JOptionPane.showInputDialog(mainPanel, "Insert todo description (optional)", "", JOptionPane.PLAIN_MESSAGE);
                if(description != null)
                    todo.setDescription(description);

                parentBoardComponent.reloadToDoComponents();
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

                        if(expiryDateString.isEmpty())
                            todo.setExpiryDate(LocalDateTime.MAX);
                        else {
                            try {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");
                                LocalDateTime expiryDate = LocalDateTime.parse(expiryDateString, format);
                                todo.setExpiryDate(expiryDate);
                            }
                            catch (DateTimeParseException exc) {
                                validInput = false;
                            }
                        }
                    }
                }
                parentBoardComponent.reloadToDoComponents();
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
                        if(validInput)
                            todo.setActivityURL(activityURL);
                    }
                } while(!validInput);

                parentBoardComponent.reloadToDoComponents();
            }
        });

        imageItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (todo.getImageURL().isEmpty()) {
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showDialog(mainPanel, "Choose ToDo's image");

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                        todo.setImageURL(fc.getSelectedFile().getAbsolutePath().replace('\\', '/'));

                    parentBoardComponent.reloadToDoComponents();
                }
                else {
                    if (JOptionPane.YES_OPTION ==
                    JOptionPane.showConfirmDialog(mainPanel, "Are you really sure you want to remove the image?", "", JOptionPane.YES_NO_OPTION)) {
                        todo.setImageURL("");
                        parentBoardComponent.reloadToDoComponents();
                    }
                }
            }
        });

        backgroundColorItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(mainPanel, "Choose todo's color (optional)", null);
                if(color != null)
                    todo.setBackGroundColor("#" + Integer.toHexString(color.getRGB()).substring(2)); //Converts to hex RGB (color.getRGB() returns in AARRGGBB format)

                parentBoardComponent.reloadToDoComponents();
            }
        });

        sharedUsersItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<User> shares = todo.getSharedUsers();
                List<String> items = shares.stream().map(User::getUsername).toList();
                ListComponent list = new ListComponent(items);
            }
        });

        addSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
                if(username != null){
                    User user = Controller.getController().getUser(username);
                    if(user != null)
                        todo.addSharedUser(user);
                    else
                        JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                parentBoardComponent.reloadToDoComponents();
            }
        });

        removeSharedUserItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(mainPanel, "Insert the username of the user you want to share your ToDo with.", "", JOptionPane.PLAIN_MESSAGE);
                if(username != null){
                    User user = Controller.getController().getUser(username);
                    if(user != null)
                        todo.removeSharedUser(user);
                    else
                        JOptionPane.showMessageDialog(mainPanel, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                parentBoardComponent.reloadToDoComponents();
            }
        });

        changePositionItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create ListComponent object and remove the todo from the list
                Noticeboard board = parentBoardComponent.getBoard();
                ArrayList<ToDo> todos = board.getToDos();
                List<String> items = todos.stream().map(ToDo::getTitle).toList();
                ListComponent list = new ListComponent(items);
                String todoTitle = todo.getTitle();
                list.getModel().removeElement(todoTitle);

                //Create "Move" button and init its settings
                JButton button = new JButton("Move to the top");
                if(todos.isEmpty())
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

                        int selectedIndex = list.getList().getSelectedIndex();
                        if(selectedIndex >= todos.size()) {
                            JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            //Sync App change
                            todos.remove(todo);
                            if(selectedIndex == -1){
                                todos.addFirst(todo);
                            }
                            else {
                                String selectedToDoTitle = list.getList().getSelectedValue();
                                int predecessorIndex = todos.indexOf(board.getToDo(selectedToDoTitle));
                                todos.add(predecessorIndex + 1, todo);
                            }

                            //Sync GUI state
                            parentBoardComponent.getParentViewer().reloadBoardComponents();
                        }
                    }
                });
            }
        });

        changeBoardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create BoardList object and remove the todo-owning board from the list
                User user = Controller.getController().getLoggedUser();
                List<String> items = user.getNoticeboards().stream().map(Noticeboard::getTitle).toList();
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
                        User user = Controller.getController().getLoggedUser();
                        ArrayList<Noticeboard> boards = user.getNoticeboards();

                        String newBoardTitle = list.getList().getSelectedValue();
                        int index = boards.indexOf(user.getNoticeboard(newBoardTitle)); //Calculate the correct index

                        if(index < 0 || index >= boards.size()) {
                            JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            if(JOptionPane.YES_OPTION ==
                                    JOptionPane.showConfirmDialog(list.getPanel(), "Are you really sure you want to move the ToDo to \"" + newBoardTitle + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                                //Sync App change
                                Noticeboard newBoard = boards.get(index);
                                parentBoardComponent.getBoard().deleteToDo(todo.getTitle(), user);
                                newBoard.addToDo(todo);

                                //Sync GUI state
                                list.getFrame().dispose();
                                parentBoardComponent.getParentViewer().reloadBoardComponents();
                            }
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