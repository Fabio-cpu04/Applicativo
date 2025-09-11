package gui.views.board;

//Java imports
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

//App imports
import gui.views.GUIView;

import controller.Controller;

import gui.views.board.forms.BoardForm;
import gui.components.ListComponent;

import dto.NoticeboardDTO;
import dto.ToDoDTO;

/**
 * <p>The BoardView, displays the logged User's Noticeboards and enables the user to add, modify and delete Noticeboards and their ToDos and to use the advanced ToDo search functions.</p>
 */
public class BoardView implements GUIView {
    private JFrame viewerFrame;
    private JPanel mainPanel;

    private final ArrayList<NoticeboardDTO> toDisplay;

    private boolean shouldDrawShared;

    //Implemented methods
    public void disposeView() {
        viewerFrame.setVisible(false);
        viewerFrame.dispose();
    }

    //Setters and getters
    /**
     * <p>Gets the currently displayed {@link model.Noticeboard}s (up to 3 Noticeboards can be displayed at once).</p>
     * @return the displayed Noticeboard as a mutable {@link List} of {@link NoticeboardDTO}
     */
    public List<NoticeboardDTO> getCurrentlyDisplayedBoards() { return toDisplay; }

    //Constructor
    /**
     * <p>Instantiates a new BoardView.</p>
     *
     * @throws IllegalStateException if no User is logged in the system at the time of the View's instantiation
     */
    public BoardView() {
        if(!Controller.getInstance().isUserLogged())
            throw new IllegalStateException("A User needs to be logged in.");

        //Setting up state
        this.shouldDrawShared = true;

        //Initialize GUI
        this.initializeViewer();

        //Initialize Board components
        List<NoticeboardDTO> visibleBoards = this.getVisibleNoticeboards();
        toDisplay = new ArrayList<>();
        for(int i = 0; i < Math.min(3, visibleBoards.size()); i++)
                toDisplay.add(visibleBoards.get(i));

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
        viewerFrame.setExtendedState(viewerFrame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        viewerFrame.requestFocus();
    }

    //Methods
    private void initializeViewer() {
        //Create viewer frame and init settings
        viewerFrame = new JFrame("ToDo App - " + Controller.getInstance().getLoggedUser().getUsername());
        viewerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        viewerFrame.setPreferredSize(new Dimension(screenDim.width, screenDim.height));

        //Create scrollpane and init settings
        JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        viewerFrame.getContentPane().add(scrollPane);

        //Attach main content panel
        mainPanel = new JPanel(new GridLayout(1, 3)); //Hard coded to only show a max of 3 NoticeBoards
        scrollPane.setViewportView(mainPanel);
    }

    private void initializeMenu() {
        //Create, init, and attach menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu noticeboardsMenu = new JMenu("Noticeboards");
        JMenu userMenu = new JMenu("Search ToDos");
        JMenu viewMenu = new JMenu("View");

        //Noticeboard menu options
        JMenuItem newNoticeboardItem = new JMenuItem("New Noticeboard");
        JMenuItem modifyNoticeboardItem = new JMenuItem("Modify Noticeboard");
        JMenuItem deleteNoticeboardItem = new JMenuItem("Delete Noticeboard");
        //User menu options
        JMenuItem expiringTodayItem = new JMenuItem("Show ToDos that expire today");
        JMenuItem expiringBeforeItem = new JMenuItem("Show ToDos that expire before a certain date");
        JMenuItem searchByTitleItem = new JMenuItem("Search ToDos by title");
        //View menu options
        JMenuItem reloadItem = new JMenuItem("Reload View");
        JCheckBoxMenuItem showSharedNoticeboardsItem = new JCheckBoxMenuItem("Show shared noticeboards", true);

        newNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newNoticeboardAction();
            }
        });

        modifyNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyNoticeboardAction();
            }
        });

        deleteNoticeboardItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteNoticeboardAction();
            }
        });

        expiringTodayItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expiringTodayAction();
            }
        });

        expiringBeforeItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expiringBeforeAction();
            }
        });

        searchByTitleItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchByTitleAction();
            }
        });

        reloadItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.getInstance().reloadUserData(); //Invalidate cache and reload data
                reloadBoardComponents();
            }
        });

        showSharedNoticeboardsItem.addItemListener(e -> {
            shouldDrawShared = showSharedNoticeboardsItem.getState();
            reloadBoardComponents();
        });

        //Adding menu items to menus
        noticeboardsMenu.add(newNoticeboardItem);
        noticeboardsMenu.add(modifyNoticeboardItem);
        noticeboardsMenu.add(deleteNoticeboardItem);

        userMenu.add(expiringTodayItem);
        userMenu.add(expiringBeforeItem);
        userMenu.add(searchByTitleItem);

        viewMenu.add(reloadItem);
        reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)); /* Setup reloadItem accelerator */
        viewMenu.add(showSharedNoticeboardsItem);

        //Adding menus to menu bar
        menuBar.add(noticeboardsMenu);
        menuBar.add(userMenu);
        menuBar.add(viewMenu);

        //Adding menu bar to view frame
        viewerFrame.setJMenuBar(menuBar);
    }

    //Menu helpers
    private void newNoticeboardAction() {
        //Get user input data
        BoardForm form = new BoardForm(null);
        NoticeboardDTO formBoard = form.showBoardForm();
        if(formBoard == null)
            return;

        //Mashup user input data + Controller data
        NoticeboardDTO newBoard = new NoticeboardDTO(-1, formBoard.getTitle(), formBoard.getDescription(), Controller.getInstance().getLoggedUser().getUserID());

        try {
            Controller.getInstance().addNoticeboard(newBoard);
            reloadBoardComponents();
        }
        catch(IllegalStateException _) {
            JOptionPane.showMessageDialog(mainPanel, "Couldn't add Noticeboard, a Noticeboard with the same title exists already.");
        }
    }

    private void modifyNoticeboardAction() {
        //Create ListComponent object
        List<NoticeboardDTO> boards = getOwnedNoticeboards();
        List<String> items = boards.stream().map(NoticeboardDTO::getTitle).toList();
        ListComponent list = new ListComponent(items);

        //Create "Modify" button and init its settings
        JButton button = new JButton("Modify");
        button.setEnabled(false);
        list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        button.setVisible(true);
        list.reloadGUIComponent();

        //Add list and button listeners
        list.getList().addListSelectionListener(_ -> button.setEnabled(true));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!button.isEnabled())
                    return;

                super.mouseClicked(e);

                //Get user selection data
                int index = list.getList().getSelectedIndex();
                NoticeboardDTO oldBoard = boards.get(index);

                //Get board from BoardForm
                BoardForm form = new BoardForm(oldBoard);
                NoticeboardDTO formBoard = form.showBoardForm();
                if(formBoard == null)
                    return;

                //Sync App state
                Controller.getInstance().updateNoticeboard(formBoard);

                //Sync GUI state
                DefaultListModel<String> model = list.getModel();
                model.add(index, formBoard.getTitle());
                model.remove(index + 1);
                reloadBoardComponents();
            }
        });
    }

    private void deleteNoticeboardAction() {
        //Create ListComponent object
        List<NoticeboardDTO> boards = new ArrayList<>(getOwnedNoticeboards());
        List<String> items = boards.stream().map(NoticeboardDTO::getTitle).toList();
        ListComponent list = new ListComponent(items);

        //Create "Delete" button and init its settings
        JButton button = new JButton("Delete");
        button.setEnabled(false);
        list.getPanel().add(button, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        button.setVisible(true);
        list.reloadGUIComponent();

        //Add list and button listeners
        list.getList().addListSelectionListener(_ -> button.setEnabled(true));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if(!button.isEnabled())
                    return;

                int index = list.getList().getSelectedIndex();
                if(index < 0 || index >= boards.size()) {
                    JOptionPane.showMessageDialog(list.getPanel(), "Please select a valid noticeboard.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    NoticeboardDTO board = boards.get(index);

                    if(JOptionPane.YES_OPTION ==
                            JOptionPane.showConfirmDialog(list.getPanel(), "Are you really sure you want to delete the Board \"" + board.getTitle() + "\"?", "", JOptionPane.YES_NO_OPTION)) {
                        //Sync App change
                        Controller.getInstance().deleteNoticeboardByID(board.getBoardID());

                        //Sync GUI change
                        list.getModel().remove(index);
                        boards.remove(index);
                        reloadBoardComponents();
                    }
                }

            }
        });
    }

    private void expiringTodayAction() {
        LocalDate today = LocalDate.now();

        ArrayList<String> expiringToday = new ArrayList<>();
        for (NoticeboardDTO board : getVisibleNoticeboards())
            for(ToDoDTO todo : board.getToDos())
                if (todo.getExpiryDate() != null && todo.getExpiryDate().toLocalDate().isEqual(today) && !todo.isExpired())
                    expiringToday.add(todo.getTitle() + " (from " + board.getTitle() + ")" +
                            "   [Expires at " + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) +  "]");

        new ListComponent(expiringToday.stream().toList()); //Spawn ListComponent
    }

    private void expiringBeforeAction() {
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
                    catch (DateTimeParseException _) {
                        validInput = false;
                    }
                }
            }
        }

        //Collect all unexpired ToDos
        ArrayList<String> expiringAtDate = new ArrayList<>();
        for (NoticeboardDTO board : getVisibleNoticeboards()) {
            for (ToDoDTO todo : board.getToDos()){
                if(todo.getExpiryDate() != null) {
                    boolean beforeDate = todo.getExpiryDate().toLocalDate().isBefore(date);
                    if (beforeDate && !todo.isExpired())
                        expiringAtDate.add(todo.getTitle() + " (from " + board.getTitle() + ")" +
                                "   [Expires at " + todo.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm")) + "]");
                }
            }
        }
        new ListComponent(expiringAtDate.stream().toList()); //Spawn ListComponent
    }

    private void searchByTitleAction() {
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

        ArrayList<String> matches = new ArrayList<>();
        for (NoticeboardDTO board : getVisibleNoticeboards()) {
            for (ToDoDTO todo : board.getToDos()) {
                if(todo.getTitle().toLowerCase().contains(title.toLowerCase()))
                    matches.add(todo.getTitle() + " (from " + board.getTitle() + ")");
            }
        }

        new ListComponent(matches.stream().toList()); //Spawn ListComponent
    }

    /**
     * <p>Refreshes the current {@link BoardComponent}s.</p>
     */
    /* package */ void refreshBoardComponents() {
        mainPanel.removeAll();

        //Refresh DTOs'data
        toDisplay.replaceAll(board -> Controller.getInstance().getNoticeboard(board.getBoardID()));

        //Refresh components
        this.drawBoards();

        //Redraw BoardComponent
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * <p>Invalidates the cached {@link model.Noticeboard}s and reloads the associated {@link BoardComponent}s.</p>
     */
    /* package */ public void reloadBoardComponents() {
        toDisplay.clear();

        List<NoticeboardDTO> visibleBoards = this.getVisibleNoticeboards();
        for(int i = 0; i < Math.min(3, visibleBoards.size()); i++)
            toDisplay.add(visibleBoards.get(i));

        this.refreshBoardComponents();
    }

    /**
     * <p>Draws the BoardComponents and, if needed, dummy BoardComponents.</p>
     */
    private void drawBoards() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);

        for (NoticeboardDTO board : toDisplay) {
            BoardComponent boardComponent = new BoardComponent(this, board, shouldDrawShared);
            mainPanel.add(boardComponent.getPanel());
        }

        this.addDummyBoards(boardSize);
    }

    /**
     * <p>If needed, draws BoardComponent dummies.</p>
     * @param boardSize the size of the BoardComponent, needed to size the dummies appropriately
     */
    private void addDummyBoards(Dimension boardSize) {
        for(int i = 0; i < 3 - toDisplay.size(); i++){
            JPanel dummyBoard = new JPanel();
            dummyBoard.setSize(boardSize);
            dummyBoard.setBorder(BorderFactory.createMatteBorder(0,2,0,2, Color.lightGray));
            mainPanel.add(dummyBoard);
        }
    }

    /**
     * <p>Gets the Noticeboards that are currently visible by the logged User.</p>
     * @return a {@link List} of {@link NoticeboardDTO} containing the owned boards
     */
    private List<NoticeboardDTO> getVisibleNoticeboards() {
        Controller ctr = Controller.getInstance();
        List<NoticeboardDTO> userBoards = ctr.getNoticeboards();

        ArrayList<NoticeboardDTO> visibleBoards = new ArrayList<>();
        for (NoticeboardDTO board : userBoards)
            if(shouldDrawShared || board.getUserID() == ctr.getLoggedUser().getUserID())
                visibleBoards.add(board);

        return visibleBoards;
    }

    /**
     * <p>Gets the Noticeboards that are owned by the logged User.</p>
     * @return a {@link List} of {@link NoticeboardDTO} containing the visible boards
     */
    private List<NoticeboardDTO> getOwnedNoticeboards() {
        Controller ctr = Controller.getInstance();
        List<NoticeboardDTO> userBoards = ctr.getNoticeboards();

        ArrayList<NoticeboardDTO> ownedBoards = new ArrayList<>();
        for (NoticeboardDTO board : userBoards)
            if(board.getUserID() == ctr.getLoggedUser().getUserID())
                ownedBoards.add(board);

        return ownedBoards;
    }
}