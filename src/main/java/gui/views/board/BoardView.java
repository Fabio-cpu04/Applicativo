package gui.views.board;

//Java imports
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

//App imports
import dto.ToDoDTO;
import gui.views.GUIView;

import controller.Controller;
import dto.NoticeboardDTO;

/**
 * The Board view, displays the logged user's Noticeboards and enables the user to add, modify and delete Noticeboards and their ToDos, and use the app's ToDos search functions.
 */
public class BoardView implements GUIView {
    private JFrame viewerFrame;
    private JPanel mainPanel;

    private final ArrayList<NoticeboardDTO> toDisplay;

    //Implemented methods
    public void disposeView() {
        viewerFrame.setVisible(false);
        viewerFrame.dispose();
    }

    //Setters and getters
    /**
     * Gets the currently displayed boards (only up to three noticeboards can be displayed at once)
     * @return the currently displayed {@link NoticeboardDTO} wrapped in a {@link ArrayList}
     */
    public ArrayList<NoticeboardDTO> getCurrentlyDisplayedBoards() { return toDisplay; }

    //Constructor

    /**
     * Instantiates a new Board view and displays the first three Noticeboards of the logged user.
     */
    public BoardView() {
        //Initialize GUI
        this.initializeViewer();

        //Initialize Board components
        List<NoticeboardDTO> userBoards = Controller.get().getNoticeboards();
        toDisplay = new ArrayList<NoticeboardDTO>();
        for(int i = 0; i < Math.min(3, userBoards.size()); i++)
            toDisplay.add(userBoards.get(i));

        //Initializing board components
        this.drawBoards();
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
        int nBoards = Controller.get().getNoticeboardCount();
        mainPanel = new JPanel(new GridLayout(1, 3)); //Hard coded to only show a max of 3 NoticeBoards
        scrollPane.setViewportView(mainPanel);
    }

    private void initializeMenu() {
        //Create, init, and attach menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu noticeboardsMenu = new JMenu("Noticeboards");
        JMenu userMenu = new JMenu("ToDos");

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

                try {
                    //Sync App state
                    Controller.get().addNoticeboard(new NoticeboardDTO(title, description));
                    //Sync GUI state
                    reloadBoardComponents();
                }
                catch(IllegalStateException exc) {
                    JOptionPane.showMessageDialog(mainPanel, "Couldn't add Noticeboard, the Noticeboard exists already.");
                }

            }
        });

        modifyNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create ListComponent object
                List<String> items = Controller.get().getNoticeboards().stream().map(NoticeboardDTO::getTitle).toList();
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
                        List<NoticeboardDTO> boards = Controller.get().getNoticeboards();
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
                            String targetTitle = list.getModel().get(index);
                            NoticeboardDTO updatedBoard = new NoticeboardDTO(title, description);
                            Controller.get().updateNoticeboard(targetTitle, updatedBoard);

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
                List<String> items = Controller.get().getNoticeboards().stream().map(NoticeboardDTO::getTitle).toList();
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
                        List<NoticeboardDTO> boards = Controller.get().getNoticeboards();
                        if(index < 0 || index >= boards.size()) {
                            JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            String title = boards.get(index).getTitle();

                            if(JOptionPane.YES_OPTION ==
                                    JOptionPane.showConfirmDialog(list.getPanel(), "Are you really sure you want to delete the Board \"" + title + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                                //Sync App change
                                Controller.get().deleteNoticeboard(title);

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
                LocalDate today = LocalDate.now();

                ArrayList<String> expiringToday = new ArrayList<String>();
                for (NoticeboardDTO board : Controller.get().getNoticeboards())
                    for(ToDoDTO todo : board.getToDos())
                        if (todo.getExpiryDate().toLocalDate().isEqual(today) && !todo.isExpired())
                            expiringToday.add(todo.getTitle() + " (from " + board.getTitle() + ")" +
                                    "   Expires at " + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) +  ")");

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
                ArrayList<String> expiringAtDate = new ArrayList<String>();
                for (NoticeboardDTO board : Controller.get().getNoticeboards()) {
                    for (ToDoDTO todo : board.getToDos()){
                        boolean beforeDate = todo.getExpiryDate().toLocalDate().isBefore(date);
                        if (beforeDate && !todo.isExpired())
                            expiringAtDate.add(todo.getTitle() + " (from " + board.getTitle() + ")" +
                                    "   (Expires at " + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) +  ")");
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

                ArrayList<String> matches = new ArrayList<String>();
                for (NoticeboardDTO board : Controller.get().getNoticeboards()) {
                    for (ToDoDTO todo : board.getToDos()) {
                        if(todo.getTitle().contains(title))
                            matches.add(todo.getTitle() + " (from " + board.getTitle() + ")");
                    }
                }

                ListComponent list = new ListComponent(matches.stream().toList());
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

    /**
     * Refreshes the view.
     */
    /* package */ void refreshBoardComponents() {
        mainPanel.removeAll();

        //Refresh DTOs'data
        toDisplay.replaceAll(board -> Controller.get().getNoticeboard(board.getTitle()));

        //Refresh components
        this.drawBoards();

        //Redraw BoardComponent
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Reloads the view and all of its components.
     */
    /* package */ void reloadBoardComponents() {
        List<NoticeboardDTO> userBoards = Controller.get().getNoticeboards();
        toDisplay.clear();
        for(int i = 0; i < Math.min(3, userBoards.size()); i++)
            toDisplay.add(userBoards.get(i));

        this.refreshBoardComponents();
    }

    /**
     * Draws the BoardComponents & dummies if needed.
     */
    private void drawBoards() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);

        for (NoticeboardDTO board : toDisplay) {
            BoardComponent boardComponent = new BoardComponent(this, board);
            mainPanel.add(boardComponent.getPanel());
        }

        this.addDummyBoards(boardSize);
    }

    /**
     * Draws dummies.
     * @param boardSize the size of the boards, needed to size the dummies appropriately
     */
    private void addDummyBoards(Dimension boardSize) {
        for(int i = 0; i < 3 - toDisplay.size(); i++){
            JPanel dummyBoard = new JPanel();
            dummyBoard.setSize(boardSize);
            dummyBoard.setBorder(BorderFactory.createMatteBorder(0,2,0,2, Color.lightGray));
            mainPanel.add(dummyBoard);
        }
    }

}