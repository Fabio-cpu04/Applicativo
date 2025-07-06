package gui.components;

//Java imports
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//App imports
import controller.Controller;
import gui.views.BoardViewer;
import model.User;
import model.Noticeboard;
import model.ToDo;

public class BoardComponent {
    private final JPanel mainPanel;
    private final JPanel todoPanel;

    private final BoardViewer parentBoardViewer;

    private final Noticeboard board;

    //Setters and getters
    public JPanel getPanel() { return mainPanel; }
    /* package */ BoardViewer getParentViewer() { return parentBoardViewer; }
    /* package */ Noticeboard getBoard() { return board; }

    //Constructors
    public BoardComponent(BoardViewer father, Noticeboard board) {
        //Setting up state
        this.parentBoardViewer = father;
        this.board = board;

        //Initialize GUI
        mainPanel = new JPanel(new GridBagLayout());
        this.InitializeBoard(board);

        //Initialize ToDo components
        GridBagConstraints c = new GridBagConstraints(0, 1, 2, 1, 0.5, 0.5,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        todoPanel = new JPanel(new GridLayout(board.getToDoCount(), 1));
        todoPanel.setVisible(true);
        mainPanel.add(todoPanel, c);

        for (ToDo todo : board.getToDos()) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        mainPanel.setVisible(true);
    }


    //Utility Methods
    private void InitializeBoard(Noticeboard board) {
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
                addToDo(ToDoComponent.getToDoFromUserInput(mainPanel));
            }
        });

        Dimension btnDim = boardButton.getPreferredSize();
        boardButton.setPreferredSize(new Dimension(btnDim.width, boardSize.height / 16)); //Make height relative to board selector ComboBox
        c.gridx = 1;
        mainPanel.add(boardButton, c);
    }

    /* package */ void reloadToDoComponents() {
        todoPanel.removeAll();
        todoPanel.setLayout(new GridLayout(board.getToDoCount(), 1));

        for (ToDo todo : board.getToDos()) {
            ToDoComponent t = new ToDoComponent(this, todo);
            todoPanel.add(t.getPanel());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    /* package */ int addToDo(ToDo todo) {
        //Sync app state
        int res = board.addToDo(todo);

        //Sync GUI state
        if(res == 0)
            this.reloadToDoComponents();

        return res;
    }

    /* package */ int deleteToDo(ToDo todo){
        //Sync app state
        User user = Controller.getController().getLoggedUser();
        if(user == null)
            return -1;
        int res = board.deleteToDo(todo.getTitle(), user);

        //Sync GUI state
        if(res == 0)
            this.reloadToDoComponents(); //Sync GUI state

        return res;
    }
}