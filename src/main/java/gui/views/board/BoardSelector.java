package gui.views.board;

import controller.Controller;
import dto.NoticeboardDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * A utility GUI component, spawns a combo box attached to a {@link BoardComponent} in order to change the displayed noticeboard.
 */
/* package */ class BoardSelector {
    private final JComboBox<String> boardSelector;

    private final BoardComponent parentBoardComponent;

    //Getters

    /**
     * Gets the component's associated {@link JComboBox<String>} component.
     * @return the component's combo box
     */
    /* package */ JComboBox<String> getComboBox() { return boardSelector; }

    //Constructor
    /**
     * Initializes a new BoardSelector bound to a {@link BoardComponent}
     * @param parent the parent {@link BoardComponent}
     * @param parentBoardSize the size of the parent component
     */
    /* package */ BoardSelector(BoardComponent parent, Dimension parentBoardSize) {
        //Setting state
        this.parentBoardComponent = parent;

        //Initializing component
        ArrayList<NoticeboardDTO> boards = new ArrayList<NoticeboardDTO>(Controller.get().getNoticeboards());
        ArrayList<NoticeboardDTO> displayed = new ArrayList<NoticeboardDTO>(parentBoardComponent.getParentViewer().getCurrentlyDisplayedBoards());
        displayed.remove(parentBoardComponent.getBoard());
        boards.removeAll(displayed);

        ArrayList<String> titleDesc = new ArrayList<>();
        boards.forEach(board -> titleDesc.add(
                board.getTitle() + (board.getDescription().isEmpty() ? "/ ..." : " / " + board.getDescription())
        ));

        String originalTitle = parentBoardComponent.getBoard().getTitle();
        String originalDescription = parentBoardComponent.getBoard().getDescription();

        boardSelector = new JComboBox<String>(titleDesc.toArray(new String[0]));
        boardSelector.setSelectedIndex(boards.indexOf(parentBoardComponent.getBoard()));
        boardSelector.setToolTipText(originalTitle + (originalDescription.isEmpty() ? " / ..." : " / " + originalDescription));

        Dimension bsDim = new Dimension((int)((double)parentBoardSize.width / 1.5), parentBoardSize.height / 16);
        boardSelector.setPreferredSize(bsDim);
        boardSelector.setVisible(true);

        //Setting up action listener
        boardSelector.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardSelector.setToolTipText((String) boardSelector.getSelectedItem());

                //Sync GUI changes
                BoardView viewer = parentBoardComponent.getParentViewer();
                int currentBoardIndex = viewer.getCurrentlyDisplayedBoards().indexOf(parentBoardComponent.getBoard());
                int newBoardIndex = boardSelector.getSelectedIndex();

                swapViewerBoard(currentBoardIndex, boards.get(newBoardIndex));
            }
        });
    }

    //Methods
    private int swapViewerBoard(int viewerIndex, NoticeboardDTO newBoard) {
        BoardView viewer = parentBoardComponent.getParentViewer();
        ArrayList<NoticeboardDTO> toDisplay = viewer.getCurrentlyDisplayedBoards();

        if(viewerIndex < 0 || viewerIndex > 3)
            return -1;
        if(newBoard == null)
            return -2;
        if(!toDisplay.contains(newBoard) && toDisplay.get(viewerIndex) != newBoard) {
            toDisplay.set(viewerIndex, newBoard);
            viewer.refreshBoardComponents();
        }

        return 0;
    }

}
