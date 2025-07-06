package gui.components;

import controller.Controller;
import gui.views.BoardViewer;
import model.Noticeboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class BoardSelector {
    private final JComboBox<String> boardSelector;

    private final BoardComponent parentBoardComponent;

    //Getters
    public JComboBox<String> getComboBox() { return boardSelector; }

    //Constructor
    public BoardSelector(BoardComponent parent, Dimension boardSize) {
        //Setting state
        this.parentBoardComponent = parent;

        //Initializing component
        ArrayList<Noticeboard> boards = new ArrayList<Noticeboard>(Controller.getController().getLoggedUser().getNoticeboards());
        ArrayList<Noticeboard> displayed = new ArrayList<Noticeboard>(parentBoardComponent.getParentViewer().getCurrentlyDisplayedBoards());
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

        Dimension bsDim = new Dimension((int)((double)boardSize.width / 1.5), boardSize.height / 16);
        boardSelector.setPreferredSize(bsDim);
        boardSelector.setVisible(true);

        //Setting up action listener
        boardSelector.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardSelector.setToolTipText((String) boardSelector.getSelectedItem());

                //Sync GUI changes
                BoardViewer viewer = parentBoardComponent.getParentViewer();
                int currentBoardIndex = viewer.getCurrentlyDisplayedBoards().indexOf(parentBoardComponent.getBoard());
                int newBoardIndex = boardSelector.getSelectedIndex();

                swapViewerBoard(currentBoardIndex, boards.get(newBoardIndex));
            }
        });
    }

    //Methods
    private int swapViewerBoard(int viewerIndex, Noticeboard newBoard) {
        BoardViewer viewer = parentBoardComponent.getParentViewer();
        ArrayList<Noticeboard> toDisplay = viewer.getCurrentlyDisplayedBoards();

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
