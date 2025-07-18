package gui.views.board;

//Java imports
import javax.swing.*;
import java.awt.*;
import java.util.List;

//App imports


/**
 * The ListComponent GUI component, displays a list of {@link String} elements.
 */
class ListComponent {
    //Members
    private final JList<String> list;
    private final DefaultListModel<String> listModel;

    private final JFrame frame;
    private final JPanel mainPanel;

    //Getters and Setters

    /**
     * Gets frame.
     * @return the component's frame
     */
    public JFrame getFrame() { return frame; };

    /**
     * Gets panel.
     * @return the component's panel
     */
    public JPanel getPanel() { return mainPanel; };

    /**
     * Gets list.
     * @return the component's Jlist
     */
    public JList<String> getList() { return list; }

    /**
     * Gets model.
     * @return the list's model
     */
    public DefaultListModel<String> getModel() { return listModel; }

    //Constructor

    /**
     * Instantiates a new ListComponent that displays {@code items}.
     * @param items the items, wrapped in a {@link List} of {@link String}
     */
    public ListComponent(List<String> items) {
        //Create new JFrame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create scrollpane and set its stuff
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        frame.getContentPane().add(scrollPane);

        mainPanel = new JPanel(new GridBagLayout());
        scrollPane.setViewportView(mainPanel);

        //Create list and init its settings
        listModel = new DefaultListModel<String>();
        listModel.addAll(items);

        list = new JList<String>(listModel);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(list, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        list.setVisible(true);

        this.reloadGUIComponent();
    }

    //Methods
    /**
     * Reloads the gui component.
     */
    public void reloadGUIComponent() {
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }
}
