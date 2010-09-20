package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.viewer.visual.VisualStyle;

import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.phoebe.PNodeView;




/**
 * Represents the main viewer Frame and contains the main[] method used to start
 * the standalone version of the viewer. 
 */
public class HGVDesktop extends JFrame implements PropertyChangeListener, GraphView.SelectionListener
{
    private static HGVDesktop instance;
    // Static variables
    public static String GRAPH_VIEW_FOCUSED = "GRAPH_VIEW_FOCUSED";
    public static String GRAPH_VIEW_CREATED = "GRAPH_VIEW_CREATED";
    public static String GRAPH_VIEW_DESTROYED = "GRAPH_VIEW_DESTROYED";
    // Member varaibles
    protected VisualStyle defaultVisualStyle;
    /**
     * The network panel that sends out events when a network is selected from
     * the Tree that it contains.
     */
    protected NetworkPanel networkPanel;
    /**
     * The HGVMenus object provides access to the all of the menus and toolbars
     * that will be needed.
     */
    protected HGVMenus hgvMenus;

    protected JTabbedPane tabbedPane;

    // --------------------//
    // Event Support
    /**
     * provides support for property change events
     */
    protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
            this);
    private/* final */TitledBorder propertiesTableBorder_ = new TitledBorder("");
   
    protected ObjectInspector propsPanel;
    private JPanel mainPropsPanel;
    private JComboBox styleBox;

  
    /**
     * Private constructor
     */
    private HGVDesktop()
    {
        super("HGVKit Desktop");
        initialize();
    }
    
    public static void main(String args []) throws Exception {
        HGVKit.embeded = false;
        getInstance();
    }

    /*
     * Returns the HGVDesktop singleton 
     */
    public static HGVDesktop getInstance()
    {
        if (instance == null) instance = new HGVDesktop();
        return instance;
    }

    protected void initialize()
    {
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                this.getClass().getClassLoader().getResource(
                        "org/hypergraphdb/viewer/images/c16.png")));

        JPanel main_panel = new JPanel();
        main_panel.setLayout(new BorderLayout());

        // ------------------------------//
        // Set up the Panels, Menus, and Event Firing
        networkPanel = new NetworkPanel();
       
        hgvMenus = HGVMenus.getInstance();
       

        // Listener Setup
        // The HGVDesktop listens to NETWORK_VIEW_CREATED events,
        // and passes them on, The NetworkPanel listens for them
        // The Desktop also keeps HGVKit up2date, but NOT via events
        HGVKit.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

        // The NetworkPanel listens to the HGVDesktop for NETWORK_CREATED_EVENTS
        // as well as for passing focused events from the Networkviewmanager.
        // The HGVDesktop also listens to the NetworkPanel
         getSwingPropertyChangeSupport().addPropertyChangeListener(
                networkPanel);
        networkPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(
                this);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                if (tabbedPane.getSelectedIndex() == -1) return;
                HGViewer comp = (HGViewer) tabbedPane.getComponentAt(
                        tabbedPane.getSelectedIndex());
                HGViewer.setFocusedComponent(comp);
                HGVKit.firePropertyChange(HGVDesktop.GRAPH_VIEW_FOCUSED, null, comp.getView());
            }
        });
        tabbedPane.addMouseListener(new TabbedPaneMouseListener());
        JScrollPane scroll_tab = new JScrollPane(tabbedPane);
        propsPanel = new ObjectInspector(null);
      
        mainPropsPanel = new JPanel();
        mainPropsPanel.setLayout(new GridBagLayout());
        mainPropsPanel.setBorder(propertiesTableBorder_);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 2;
        gridBagConstraints.weighty = 2;
        mainPropsPanel.add(new JScrollPane(propsPanel), gridBagConstraints);
        JSplitPane sp = new JSplitPane();
        sp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sp.setDividerSize(5);
        sp.setTopComponent(networkPanel);
        sp.setBottomComponent(mainPropsPanel);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
                sp,// networkPanel,
                scroll_tab);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(200);
         main_panel.add(split, BorderLayout.CENTER);
        // split.setDividerLocation(0.2);
        main_panel.add(hgvMenus.getToolBar(), BorderLayout.NORTH);
        setJMenuBar(hgvMenus.getMenuBar());
        setupStyleSelector(main_panel);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we)
            {
                HGVKit.exit();
            }
        });
        // show the Desktop
        setContentPane(main_panel);
        pack();
        setSize(800, 700);
        setVisible(true);
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    public NetworkPanel getNetworkPanel()
    {
        return networkPanel;
    }

 
    protected void setupStyleSelector(JPanel panel)
    {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (VisualStyle vs : VisualManager.getInstance().getVisualStyles())
            model.addElement(vs);
        styleBox = new JComboBox(model);
        VisualManager.getInstance().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                DefaultComboBoxModel model = new DefaultComboBoxModel();
                for (VisualStyle vs : VisualManager.getInstance()
                        .getVisualStyles())
                    model.addElement(vs);
                styleBox.setModel(model);
                GraphView view = HGVKit.getCurrentView();
                // System.out.println("StyleChange: " + view.getVisualStyle());
                if (view != null)
                    styleBox.setSelectedItem(view.getVisualStyle());
            }
        });
        String comboBoxHelp = "Change the current visual style";
        styleBox.setToolTipText(comboBoxHelp);
        styleBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                GraphView view = HGVKit.getCurrentView();
                if (view == null) return;
                VisualStyle vs = ((VisualStyle) styleBox.getSelectedItem());
                if (vs != null && !vs.equals(view.getVisualStyle()))
                {
                    view.setVisualStyle(vs);
                    view.redrawGraph();
                }
            }
        });
        Dimension newSize = new Dimension(150, (int) styleBox
                .getPreferredSize().getHeight());
        styleBox.setMaximumSize(newSize);
        styleBox.setPreferredSize(newSize);
        JToolBar toolBar = hgvMenus.getToolBar();
        toolBar.add(styleBox);
        toolBar.addSeparator();
    }

    protected void updateFocus(GraphView view)
    {
        // deal with the new Network
        VisualStyle new_style = view.getVisualStyle(true);
        styleBox.setSelectedItem(new_style);
        hgvMenus.setNodesRequiredItemsEnabled();
        updatePropsPanel();
    }

    public SwingPropertyChangeSupport getSwingPropertyChangeSupport()
    {
        return pcs;
    }

    public void propertyChange(PropertyChangeEvent e)
    {
       // System.out.println("HGVDesktop - getPropertyName(): "
      //          + e.getPropertyName() + ":" + e.getNewValue());
        if (e.getPropertyName() == GRAPH_VIEW_CREATED)
        {
            GraphView view = ((GraphView) e.getNewValue());
            getTabbedPane().addTab(
                    view.getIdentifier(), view.getViewer());
            getTabbedPane().setSelectedIndex(
                    getTabbedPane().getTabCount() -1);
            // pass on the event
            pcs.firePropertyChange(e);
            networkPanel.focusGraphViewNode(view);
            view.addSelectionListener(this);
        }
        else if (e.getPropertyName() == GRAPH_VIEW_FOCUSED)
        {
            // get focus event from NetworkViewManager
            updateFocus(((GraphView) e.getNewValue()));
            pcs.firePropertyChange(e);
        }
        else if (e.getPropertyName() == GRAPH_VIEW_DESTROYED)
        {
            GraphView view = ((GraphView) e.getNewValue());
            view.removeSelectionListener(this);
            JTabbedPane tp = HGVKit.getDesktop().getTabbedPane();
            for(int i = 0; i < tp.getTabCount(); i++)
            {
                if(tp.getComponentAt(i).equals(view.getViewer()))
                {
                   tp.removeTabAt(i);
                   break;
                }
            }
            // pass on the event
            pcs.firePropertyChange(e);
            
        }
    }
    
    public void selectionChanged()
    {
        updatePropsPanel();
    }

    void updatePropsPanel()
    {
        GraphView view = HGVKit.getCurrentView();
        PNodeView nv = view.getSelectedNodeView();
        if (nv != null)
        {
            propsPanel.setVisible(true);
            FNode node = nv.getNode();
            Object obj = view.getHyperGraph().get(node.getHandle());
            propsPanel.setModelObject(obj);
            if (obj != null)
            {
                String name = obj.getClass().getName();
                if (name.indexOf(".") > -1)
                    name = name.substring(name.lastIndexOf(".") + 1);
                // propertiesTableBorder_.setTitle("Class: " + name);
                propertiesTableBorder_ = new TitledBorder("Class: " + name);
                mainPropsPanel.setBorder(propertiesTableBorder_);
                // mainPropsPanel.revalidate();
            }
        }else
            propsPanel.setVisible(false);
    }
    
    static JPopupMenu tabPopupMenu;
    private static final String TAB_INDEX = "tab_index";
    private JPopupMenu getTabPopupMenu()
    {
        if (tabPopupMenu != null) return tabPopupMenu;
        tabPopupMenu = new JPopupMenu();
        Action act = new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e)
            {
               int i = ((Integer) tabPopupMenu.getClientProperty(TAB_INDEX));
               GraphView view = ((HGViewer) tabbedPane.getComponentAt(i)).getView();
               HGVKit.destroyNetworkView(view);
            }
        };

        tabPopupMenu.add(new JMenuItem(act));
        act = new AbstractAction("Rename") {
            public void actionPerformed(ActionEvent e)
            {
                int i = ((Integer) tabPopupMenu.getClientProperty(TAB_INDEX));
                GraphView view = ((HGViewer) tabbedPane.getComponentAt(i)).getView();
                NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                        HGVDesktop.this, "Name: ", "Rename");
                nd.setInputText(tabbedPane.getTitleAt(i));
                if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION)
                {
                    String t = nd.getInputText();
                    view.identifier = t;
                    tabbedPane.setTitleAt(i, t);
                    networkPanel.invalidate();
                }
            }
        };
        tabPopupMenu.add(new JMenuItem(act));
        return tabPopupMenu;
    }
    
    private class TabbedPaneMouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (SwingUtilities.isRightMouseButton(e))
            {
                Point pt = e.getPoint();
                for (int i = 0; i < tabbedPane.getTabCount(); i++)
                {
                    final Rectangle r = tabbedPane.getBoundsAt(i);
                    if (r == null || !r.contains(pt)) continue;
                    getTabPopupMenu().putClientProperty(TAB_INDEX, i);
                    getTabPopupMenu().show(tabbedPane, pt.x, pt.y);
                    break;
                }
            }
        }
    }
}
