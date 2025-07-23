package gui.views.board;

//Java imports
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//App imports
import controller.Controller;
import dto.NoticeboardDTO;
import dto.ToDoDTO;

/**
 * Represents a {@link NoticeboardDTO} in the GUI
 */
class BoardComponent {
    private final JPanel mainPanel;
    private final JPanel todoPanel;

    private final BoardView parentBoardView;

    private NoticeboardDTO board;

    //Setters and getters

    /**
     * Gets panel.
     * @return the panel
     */
    public JPanel getPanel() { return mainPanel; }

    /**
     * Gets parent viewer.
     * @return the parent {@link BoardView} component
     */
    /* package */ BoardView getParentViewer() { return parentBoardView; }

    /**
     * Gets board.
     * @return the {@link NoticeboardDTO} the component links to
     */
    /* package */ NoticeboardDTO getBoard() { return board; }

    //Constructors

    /**
     * Instantiates a new Board component linked to {@code board} and attaches it to the {@code parent} component
     * @param parent the parent {@link BoardView}
     * @param board the linked {@link NoticeboardDTO}
     */
    /* package */ BoardComponent(BoardView parent, NoticeboardDTO board) {
        //Setting up state
        this.parentBoardView = parent;
        this.board = board;

        //Initialize GUI
        mainPanel = new JPanel(new GridBagLayout());
        this.InitializeBoard(board);

        //Initialize ToDo components
        GridBagConstraints c = new GridBagConstraints(0, 1, 2, 1, 0.5, 0.5,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        todoPanel = new JPanel(new GridLayout(board.getToDos().size(), 1));
        todoPanel.setVisible(true);
        mainPanel.add(todoPanel, c);

        for (ToDoDTO todo : board.getToDos()) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        mainPanel.setVisible(true);
    }

    //Utility Methods
    private void InitializeBoard(NoticeboardDTO board) {
        //Setting up main panel and its layout
        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets( 0,0,0,0), 0, 0);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);
        //mainPanel.setPreferredSize(boardSize);
        mainPanel.setBorder(BorderFactory.createMatteBorder(0,2,0,2, Color.lightGray));

        //Setting up the board selector element
        BoardSelector boardSelector = new BoardSelector(this, boardSize);
        mainPanel.add(boardSelector.getComboBox(), c);

        //Setting up "Add" button
        JButton boardButton = new JButton("Add");
        boardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try{
                    //Sync App state
                    ToDoDTO todo = ToDoComponent.getToDoFromUserInput(mainPanel);
                    Controller.get().addToDo(board.getTitle(), todo);
                    //Sync GUI state
                    reloadToDoComponent();
                }
                catch (IllegalStateException exc) {
                    JOptionPane.showMessageDialog(mainPanel, "Couldn't add ToDo, a ToDo with the same title exists already.");
                }
            }
        });

        Dimension btnDim = boardButton.getPreferredSize();
        boardButton.setPreferredSize(new Dimension(btnDim.width, boardSize.height / 16)); //Make height relative to board selector ComboBox
        c.gridx = 1;
        mainPanel.add(boardButton, c);
    }

    /**
     * Reloads the ToDo components after a change.
     */
    /* package */ void reloadToDoComponent() {
        //Reset DTO to mimic the updated model's state
        board = Controller.get().getNoticeboard(board.getTitle());

        todoPanel.removeAll();
        todoPanel.setLayout(new GridLayout(board.getToDos().size(), 1));

        for (ToDoDTO todo : board.getToDos()) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }
}