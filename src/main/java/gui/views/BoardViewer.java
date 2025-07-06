package gui.views;

//Java imports
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//App imports
import model.*;
import controller.Controller;
import gui.components.*;

public class BoardViewer implements GUIView {
    private JFrame viewerFrame;
    private JPanel mainPanel;

    private final ArrayList<Noticeboard> toDisplay;

    //Setters and getters
    public JFrame getFrame() { return viewerFrame; }
    public ArrayList<Noticeboard> getCurrentlyDisplayedBoards() { return toDisplay; }

    //Constructor
    public BoardViewer() {
        //Set state
        Controller.getController().setState(Controller.State.VIEWER);
        User user = Controller.getController().getLoggedUser();

        //Initialize GUI
        this.initializeViewer();

        //Initialize Board components
        ArrayList<Noticeboard> userBoards = user.getNoticeboards();
        toDisplay = new ArrayList<Noticeboard>();
        for(int i = 0; i < Math.min(3, userBoards.size()); i++)
            toDisplay.add(userBoards.get(i));

        for(Noticeboard board : toDisplay) {
            BoardComponent b = new BoardComponent(this, board);
            JPanel panel = b.getPanel();
            mainPanel.add(panel);
        }

        mainPanel.setVisible(true);

        //Attach menu
        this.initializeMenu();

        //Readjust frame
        viewerFrame.pack();
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = viewerFrame.getSize();
        viewerFrame.setLocation(screenDim.width / 2 - frameDim.width / 2, screenDim.height / 2 - frameDim.height / 2); //Sets position at the center of the screen
        viewerFrame.setVisible(true);
    }

    //Methods
    private void initializeViewer() {
        //Create viewer frame and init settings
        viewerFrame = new JFrame();
        viewerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        viewerFrame.setPreferredSize(new Dimension(screenDim.width / 2, screenDim.height / 2));

        //Create scrollpane and init settings
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        viewerFrame.getContentPane().add(scrollPane);

        //Attach main content panel
        User user = Controller.getController().getLoggedUser();
        int nBoards = user.getNoticeboards().size();
        mainPanel = new JPanel(new GridLayout(1, Math.min(nBoards, 3))); //Hard coded to only show <=3 NoticeBoards
        scrollPane.setViewportView(mainPanel);
    }

    private void initializeMenu() {
        //Create, init, and attach menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu noticeboardsMenu = new JMenu("Noticeboards");
        JMenu userMenu = new JMenu("User");

        //Noticeboard menu options
        JMenuItem newNoticeboardItem = new JMenuItem("New Noticeboard");
        JMenuItem modifyNoticeboardItem = new JMenuItem("Modify Noticeboard");
        JMenuItem deleteNoticeboardItem = new JMenuItem("Delete Noticeboard");
        //User menu options
        JMenuItem expiringTodayItem = new JMenuItem("Show ToDos that expire today");
        JMenuItem expiringBeforeItem = new JMenuItem("Show ToDos that expire before a certain date");
        JMenuItem searchByTitleItem = new JMenuItem("Search ToDos by title");

        newNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = null;
                String description = null;

                boolean validInput = false;
                while(!validInput) {
                    title = JOptionPane.showInputDialog(mainPanel, "Insert the noticeboard's title", "", JOptionPane.PLAIN_MESSAGE);
                    if(title == null)
                        return;
                    else if (!title.isEmpty())
                        validInput = true;
                }

                description = JOptionPane.showInputDialog(mainPanel, "Insert the noticeboard's description (optional)", "", JOptionPane.PLAIN_MESSAGE);
                if(description == null)
                    return;

                //Sync App state
                User user = Controller.getController().getLoggedUser();
                user.addNoticeboard(new Noticeboard(title, description));

                //Sync GUI state
                reloadBoardComponents();
            }
        });

        modifyNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create ListComponent object
                User user = Controller.getController().getLoggedUser();
                List<String> items = user.getNoticeboards().stream().map(Noticeboard::getTitle).toList();
                ListComponent list = new ListComponent(items);

                //Create "Modify" button and init its settings
                JButton button = new JButton("Modify");
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

                        int index = list.getList().getSelectedIndex();
                        ArrayList<Noticeboard> boards = Controller.getController().getLoggedUser().getNoticeboards();
                        if(index < 0 || index >= boards.size()) {
                            JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            String title = null;
                            String description = null;

                            boolean validInput = false;
                            while(!validInput) {
                                title = JOptionPane.showInputDialog(mainPanel, "Insert the noticeboard's new title", "", JOptionPane.PLAIN_MESSAGE);
                                if(title == null)
                                    return;
                                else if (!title.isEmpty())
                                    validInput = true;
                            }

                            description = JOptionPane.showInputDialog(mainPanel, "Insert the noticeboard's new description (optional)", "", JOptionPane.PLAIN_MESSAGE);
                            if(description == null)
                                return;

                            //Sync App state
                            User user = Controller.getController().getLoggedUser();
                            Noticeboard board = user.getNoticeboard(list.getModel().get(index));
                            board.setTitle(title);
                            board.setDescription(description);

                            //Sync GUI state
                            DefaultListModel<String> model = list.getModel();
                            model.add(index, title);
                            model.remove(index + 1);
                            reloadBoardComponents();
                        }
                    }
                });
            }
        });

        deleteNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create ListComponent object
                User user = Controller.getController().getLoggedUser();
                List<String> items = user.getNoticeboards().stream().map(Noticeboard::getTitle).toList();
                ListComponent list = new ListComponent(items);

                //Create "Delete" button and init its settings
                JButton button = new JButton("Delete");
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

                        int index = list.getList().getSelectedIndex();
                        ArrayList<Noticeboard> boards = Controller.getController().getLoggedUser().getNoticeboards();
                        if(index < 0 || index >= boards.size()) {
                            JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            String title = boards.get(index).getTitle();

                            if(JOptionPane.YES_OPTION ==
                                    JOptionPane.showConfirmDialog(list.getPanel(), "Are you really sure you want to delete the Board \"" + title + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                                //Sync App change
                                User user = Controller.getController().getLoggedUser();
                                user.deleteNoticeboard(title);

                                //Sync GUI change
                                list.getModel().remove(index);
                                reloadBoardComponents();
                            }
                        }

                    }
                });

            }
        });

        expiringTodayItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = Controller.getController().getLoggedUser();
                LocalDate today = LocalDate.now();

                ArrayList<String> expiringToday = new ArrayList<String>();
                for (Noticeboard board : user.getNoticeboards())
                    for(ToDo todo : board.getToDos())
                        if (todo.getExpiryDate().toLocalDate().isEqual(today) && !todo.isExpired())
                            expiringToday.add(board.getTitle() + "'s \"" + todo.getTitle() + "\" (Expires at" + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) +  ")");

                ListComponent list = new ListComponent(expiringToday.stream().toList());
            }
        });

        expiringBeforeItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get date
                LocalDate date = null;
                String dateString = null;

                boolean validInput = false;
                while(!validInput) {
                    dateString = JOptionPane.showInputDialog(mainPanel, "Insert a date (use the following format \"dd/mm/yyyy\", leave blank to see all unexpired ToDos)", "", JOptionPane.PLAIN_MESSAGE);
                    if(dateString == null) {
                        return;
                    }
                    else {
                        validInput = true;

                        if(dateString.isEmpty())
                            date = LocalDate.MAX;
                        else
                        {
                            try {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                date = LocalDate.parse(dateString, format);
                            }
                            catch (DateTimeParseException exc) {
                                validInput = false;
                            }
                        }
                    }
                }

                //Collect all unexpired ToDos
                User user = Controller.getController().getLoggedUser();

                ArrayList<String> expiringAtDate = new ArrayList<String>();
                for (Noticeboard board : user.getNoticeboards()) {
                    for (ToDo todo : board.getToDos()){
                        boolean beforeDate = todo.getExpiryDate().toLocalDate().isBefore(date);
                        if (beforeDate && !todo.isExpired())
                            expiringAtDate.add(board.getTitle() + "'s \"" + todo.getTitle() + "\" (Expires at" + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) +  ")");
                    }
                }
                ListComponent list = new ListComponent(expiringAtDate.stream().toList());
            }
        });

        searchByTitleItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = null;
                boolean validInput = false;

                while(!validInput) {
                    title = JOptionPane.showInputDialog(mainPanel, "Insert a title or part of it.", "", JOptionPane.PLAIN_MESSAGE);
                    if (title == null) {
                        return;
                    }
                    else
                        validInput = true;
                }

                User user = Controller.getController().getLoggedUser();
                ArrayList<ToDo> allTodos = user.getToDos();

                ArrayList<ToDo> matches = new ArrayList<ToDo>();
                for (ToDo todo : allTodos) {
                    if(todo.getTitle().contains(title))
                        matches.add(todo);
                }

                List<String> items = matches.stream().map(ToDo::getTitle).toList();
                ListComponent list = new ListComponent(items);
            }
        });

        noticeboardsMenu.add(newNoticeboardItem);
        noticeboardsMenu.add(modifyNoticeboardItem);
        noticeboardsMenu.add(deleteNoticeboardItem);
        userMenu.add(expiringTodayItem);
        userMenu.add(expiringBeforeItem);
        userMenu.add(searchByTitleItem);

        menuBar.add(noticeboardsMenu);
        menuBar.add(userMenu);

        viewerFrame.setJMenuBar(menuBar);
    }

    public void refreshBoardComponents() {
        User user = Controller.getController().getLoggedUser();
        int nBoards = user.getNoticeboards().size();

        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(1, nBoards));

        for(Noticeboard board : toDisplay) {
            BoardComponent b = new BoardComponent(this, board);
            mainPanel.add(b.getPanel());
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void reloadBoardComponents() {
        User user = Controller.getController().getLoggedUser();

        ArrayList<Noticeboard> userBoards = user.getNoticeboards();
        toDisplay.clear();
        for(int i = 0; i < Math.min(3, userBoards.size()); i++)
            toDisplay.add(userBoards.get(i));

        this.refreshBoardComponents();
    }
}