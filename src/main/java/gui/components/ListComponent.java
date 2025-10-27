package gui.components;

//Java imports
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <p>The ListComponent GUI component, displays a list of {@link String} elements.</p>
 */
public class ListComponent {
    //Members
    private final JList<String> list;
    private final DefaultListModel<String> listModel;

    private final JFrame frame;
    private final JPanel mainPanel;

    //Getters and Setters
    /**
     * <p>Gets panel.</p>
     * @return the component's panel
     */
    public JPanel getPanel() { return mainPanel; };

    /**
     * <p>Gets list.</p>
     * @return the component's Jlist
     */
    public JList<String> getList() { return list; }

    /**
     * <p>Gets model.</p>
     * @return the list's model
     */
    public DefaultListModel<String> getModel() { return listModel; }

    //Constructor
    /**
     * <p>Instantiates a new ListComponent that is titled and displays the specified items.</p>
     * @param items the items, wrapped in a {@link List} of {@link String}
     */
    public ListComponent(List<String> items){
        this(items, "");
    }

    /**
     * <p>Instantiates a new ListComponent that is titled and displays the specified items.</p>
     * @param title the title of the list's frame
     * @param items the items, wrapped in a {@link List} of {@link String}
     */
    public ListComponent(List<String> items, String title) {
        //Create new JFrame
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension minimumSize = Toolkit.getDefaultToolkit().getScreenSize();
        minimumSize.setSize(minimumSize.getWidth() / 5, minimumSize.getHeight() / 8);
        frame.setMinimumSize(minimumSize);

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

        this.reloadListComponent();
    }

    //Methods
    /**
     * <p>Reloads the ListComponent.</p>
     */
    public void reloadListComponent() {
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);
        frame.setVisible(true);
    }

    /**
     * <p>Disposes of the ListComponent</p>
     */
    public void dispose() {
        frame.dispose();
    }
}
