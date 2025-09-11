package gui.views.board;

//Java imports
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

//App imports
import controller.Controller;
import dto.NoticeboardDTO;



/**
 * <p>A utility GUI component, spawns a combo box attached to a {@link BoardComponent} in order to change the displayed {@link model.Noticeboard}.</p>
 */
/* package */ class BoardSelector {
    private final JComboBox<String> comboBox;

    private final BoardComponent parentBoardComponent;

    //Getters
    /**
     * <p>Gets the component's associated {@link JComboBox<String>} component.</p>
     * @return the component's combo box
     */
    /* package */ JComboBox<String> getComboBox() { return comboBox; }

    //Constructor
    /**
     * <p>Initializes a new BoardSelector bound to a {@link BoardComponent}.</p>
     * @param parent the parent {@link BoardComponent}
     * @param parentBoardSize the size of the parent BoardComponent
     * @param shouldDrawShared if the BoardComponent should draw the ToDos that are shared with the User
     */
    /* package */ BoardSelector(BoardComponent parent, Dimension parentBoardSize, boolean shouldDrawShared) {
        //Setting state
        this.parentBoardComponent = parent;

        //Initializing component
        ArrayList<NoticeboardDTO> boards = new ArrayList<>(Controller.getInstance().getNoticeboards());
        ArrayList<NoticeboardDTO> displayed = new ArrayList<>(parentBoardComponent.getParentViewer().getCurrentlyDisplayedBoards());
        displayed.remove(parentBoardComponent.getBoard()); //Remove the parent board from the boards that are already displayed (needs to be the only one visible)

        //Remove boards that are already displayed
        boards.removeAll(displayed);

        //Cache the logged user's ID
        int userID = Controller.getInstance().getLoggedUser().getUserID();

        //If shouldDrawShared = false -> remove non-owned boards from list
        if(!shouldDrawShared) {
            List<NoticeboardDTO> shared = boards.stream().filter(board -> board.getUserID() != userID).toList();
            boards.removeAll(shared);
        }

        //Calculate the titles for all the boards
        ArrayList<String> boardSelectorItemList = new ArrayList<>();
        Map<Integer, String> users = Controller.getInstance().getUsers();
        boards.forEach(board -> boardSelectorItemList.add(
                board.getTitle() +
                (board.getDescription().isEmpty() ? "/ ..." : " / " + board.getDescription()) +
                ((board.getUserID() == userID) ? "" : " [" + users.get(board.getUserID()) + "]")
        ));

        String originalTitle = parentBoardComponent.getBoard().getTitle();
        String originalDescription = parentBoardComponent.getBoard().getDescription();

        //Create the JComboBox and set it up
        comboBox = new JComboBox<>(boardSelectorItemList.toArray(new String[0]));
        comboBox.setSelectedIndex(boards.indexOf(parentBoardComponent.getBoard()));
        comboBox.setToolTipText(originalTitle + (originalDescription.isEmpty() ? " / ..." : " / " + originalDescription));

        Dimension bsDim = new Dimension((int)(parentBoardSize.width / 1.5), parentBoardSize.height / 16);
        comboBox.setPreferredSize(bsDim);
        comboBox.setVisible(true);

        //Setting up action listener
        comboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox.setToolTipText((String) comboBox.getSelectedItem());

                //Sync GUI changes
                BoardView viewer = parentBoardComponent.getParentViewer();
                int currentBoardIndex = viewer.getCurrentlyDisplayedBoards().indexOf(parentBoardComponent.getBoard());
                int newBoardIndex = comboBox.getSelectedIndex();

                swapViewerBoard(currentBoardIndex, boards.get(newBoardIndex));
            }
        });
    }

    //Methods
    /**
     * <p>Swaps the parent of the component with the selected Noticeboard.</p>
     * @param viewerIndex the current index of the parent BoardComponent
     * @param newBoard the new Noticeboard to replace the parent's Noticeboard with
     *
     * @throws IndexOutOfBoundsException if the parent Noticeboard's index is invalid
     * @throws IllegalArgumentException if the new Noticeboard is invalid
     */
    private void swapViewerBoard(int viewerIndex, NoticeboardDTO newBoard) {
        BoardView viewer = parentBoardComponent.getParentViewer();
        List<NoticeboardDTO> toDisplay = viewer.getCurrentlyDisplayedBoards();

        if(viewerIndex < 0 || viewerIndex > 3)
            throw new IndexOutOfBoundsException("Index " + viewerIndex + " is out of bounds, index cannot be lower than 0 or higher than 3.");
        if(newBoard == null)
            throw new IllegalArgumentException("New board cannot be null");

        if(!toDisplay.contains(newBoard) && toDisplay.get(viewerIndex) != newBoard) {
            toDisplay.set(viewerIndex, newBoard);
            viewer.refreshBoardComponents();
        }
    }

}
