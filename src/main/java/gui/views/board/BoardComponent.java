package gui.views.board;

//Java imports
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

//App imports
import controller.Controller;
import dto.NoticeboardDTO;
import dto.ToDoDTO;
import gui.views.board.forms.ToDoForm;

/**
 * <p>Represents a {@link model.Noticeboard} in the GUI.</p>
 */
class BoardComponent {
    private final JPanel mainPanel;
    private final JPanel todoPanel;

    private final BoardView parentBoardView;

    private NoticeboardDTO board;

    //Setters and getters
    /**
     * <p>Gets panel.</p>
     * @return the panel
     */
    public JPanel getPanel() { return mainPanel; }

    /**
     * <p>Gets parent viewer.</p>
     * @return the parent {@link BoardView} component
     */
    /* package */ BoardView getParentViewer() { return parentBoardView; }

    /**
     * <p>Gets board.</p>
     * @return the {@link NoticeboardDTO} the component is sourced from
     */
    /* package */ NoticeboardDTO getBoard() { return board; }

    //Constructors
    /**
     * <p>Instantiates a new BoardComponent attached to a {@link BoardView} and linked to a {@link model.Noticeboard}.</p>
     * @param parent the parent {@link BoardView}
     * @param board the linked {@link NoticeboardDTO}
     * @param shouldDrawShared set {@code true} if the component should draw the ToDos that are shared with the User, set {@code false} otherwise
     */
    /* package */ BoardComponent(BoardView parent, NoticeboardDTO board, boolean shouldDrawShared) {
        //Setting up state
        this.parentBoardView = parent;
        this.board = board;

        //Initialize GUI
        mainPanel = new JPanel(new GridBagLayout());

        this.initializeBoard(board, shouldDrawShared);

        //Initialize ToDo components
        GridBagConstraints todoPanelConstraints = new GridBagConstraints(0, 1, 2, 1, 0.5, 0.5,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.PAGE_AXIS));
        todoPanel.setVisible(true);
        mainPanel.add(todoPanel, todoPanelConstraints);

        for (ToDoDTO todo : this.getVisibleToDos(shouldDrawShared)) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        mainPanel.setVisible(true);
    }

    //Utility Methods
    private void initializeBoard(NoticeboardDTO board, boolean shouldDrawShared) {
        //Setting up main panel and its layout
        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.9, 0.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets( 0,0,0,0), 0, 0);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.width / 6, screenSize.height / 2);

        //Setting up Board border (if the user does not own the Noticeboard, it'll appear dashed)
        Border border = BorderFactory.createMatteBorder(0,2,0,2, Color.lightGray);
        if(board.getUserID() != Controller.getInstance().getLoggedUser().getUserID())
            border = BorderFactory.createDashedBorder(Color.gray, 2, 8, 3, false);
        mainPanel.setBorder(border);

        //Setting up the board selector element
        BoardSelector boardSelector = new BoardSelector(this, boardSize, shouldDrawShared);
        mainPanel.add(boardSelector.getComboBox(), c);

        //Setting up "Add" button
        JButton newToDoButton = new JButton("New ToDo");
        if(board.getUserID() != Controller.getInstance().getLoggedUser().getUserID())
            newToDoButton.setEnabled(false);
        else
        {
            newToDoButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    ToDoForm form = new ToDoForm(null);
                    ToDoDTO todo = form.showToDoForm();

                    if (todo != null) {
                        //Sync App state
                        ToDoDTO todoToAdd = new ToDoDTO(-1, todo.isCompleted(), todo.getTitle(), todo.getDescription(), todo.getActivityURL(), todo.getImageURL(), todo.getExpiryDate(), Controller.getInstance().getLoggedUser().getUserID(), todo.getBackgroundColor());
                        Controller.getInstance().addToDo(board.getBoardID(), todoToAdd);

                        //Sync GUI state
                        reloadToDoComponent();
                    }
                } catch (IllegalStateException _) {
                    JOptionPane.showMessageDialog(mainPanel, "Couldn't add ToDo, a ToDo with the same title exists already.");
                }
                }
            });
        }

        Dimension btnDim = newToDoButton.getPreferredSize();
        newToDoButton.setPreferredSize(new Dimension(btnDim.width, boardSize.height / 16)); //Make height relative to board selector ComboBox
        c.gridx = 1;
        c.weightx = 0.1;
        mainPanel.add(newToDoButton, c);
    }

    /**
     * <p>Invalidates the cached {@link model.Noticeboard} and reloads the {@link ToDoComponent}s.</p>
     */
    /* package */ void reloadToDoComponent() {
        //Reset DTO to mimic the updated model's state
        board = Controller.getInstance().getNoticeboard(board.getBoardID());

        //Remove all ToDoComponents from the BoardComponent
        todoPanel.removeAll();

        for (ToDoDTO todo : board.getToDos()) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    /**
     * <p>Gets the {@link model.Noticeboard}s that are currently visible to the logged User.</p>
     * @param shouldDrawSharedToDos {@code true} if the component should draw the ToDoComponents of ToDos that are shared with the User, {@code false} otherwise
     * @return a {@link List} of {@link ToDoDTO} containing the visible ToDos
     */
    private List<ToDoDTO> getVisibleToDos(boolean shouldDrawSharedToDos) {
        List<ToDoDTO> boardToDos = this.board.getToDos();

        ArrayList<ToDoDTO> visibleToDos = new ArrayList<>();
        for (ToDoDTO todo : boardToDos)
            if(shouldDrawSharedToDos || todo.getOwnerUserID() == Controller.getInstance().getLoggedUser().getUserID())
                visibleToDos.add(todo);

        return visibleToDos;
    }
}