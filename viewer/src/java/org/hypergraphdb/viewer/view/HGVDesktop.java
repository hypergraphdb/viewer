package org.hypergraphdb.viewer.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVComponent;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.props.PropertySetPanel;
import org.hypergraphdb.viewer.visual.VisualStyle;

import phoebe.PNodeView;


/**
 * The HGVDesktop is the central Window for working with HGVKit
 */
public class HGVDesktop extends JFrame implements PropertyChangeListener
{
    private static HGVDesktop instance;
    // Static variables
    public static String NETWORK_VIEW_FOCUSED = "NETWORK_VIEW_FOCUSED";
    public static String NETWORK_VIEW_CREATED = "NETWORK_VIEW_CREATED";
    public static String NETWORK_VIEW_DESTROYED = "NETWORK_VIEW_DESTROYED";
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
    protected HGVMenus cyMenus;

    protected JTabbedPane tabbedPane;

    // --------------------//
    // Event Support
    /**
     * provides support for property change events
     */
    protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
            this);
    private/* final */TitledBorder propertiesTableBorder_ = new TitledBorder("");
   
    protected PropertySetPanel propsPanel;
    private JPanel mainPropsPanel;
    private JComboBox styleBox;

    // ----------------------------------------//
    // Constructors
    // ----------------------------------------//
    /**
     * Create a HGVDesktop that conforms the given view type.
     * 
     * @param view_type
     *            one of the ViewTypes
     */
    private HGVDesktop()
    {
        super("HGVKit Desktop");
        initialize();
    }

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
       
        cyMenus = HGVMenus.getInstance();
       

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
                HGVComponent comp = (HGVComponent) tabbedPane.getComponentAt(
                        tabbedPane.getSelectedIndex());
                HGVComponent.setFocusedComponent(comp);
                firePropertyChange(HGVDesktop.NETWORK_VIEW_FOCUSED, null, comp.getView());
            }
        });
        JScrollPane scroll_tab = new JScrollPane(tabbedPane);
        propsPanel = new PropertySetPanel(this);
        propsPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(
                this);
        mainPropsPanel = new JPanel();
        mainPropsPanel.setLayout(new GridBagLayout());
        mainPropsPanel.setBorder(propertiesTableBorder_);
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 2;
        gridBagConstraints.weighty = 2;
        mainPropsPanel.add(propsPanel, gridBagConstraints);
        javax.swing.JSplitPane sp = new javax.swing.JSplitPane();
        sp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp.setDividerSize(5);
        sp.setTopComponent(networkPanel);
        sp.setBottomComponent(mainPropsPanel);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
                sp,// networkPanel,
                scroll_tab);
        split.setOneTouchExpandable(true);
         main_panel.add(split, BorderLayout.CENTER);
        // split.setDividerLocation(0.2);
        main_panel.add(cyMenus.getToolBar(), BorderLayout.NORTH);
        setJMenuBar(cyMenus.getMenuBar());
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

    public HGVMenus getHGVMenus()
    {
        return cyMenus;
    }

    protected void setupStyleSelector(JPanel panel)
    {
        // Add the StyleSelector to the ToolBar
        // TODO: maybe put this somewhere else to make it easier to make
        // vertical ToolBars.

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
                HGVNetworkView view = HGVKit.getCurrentView();
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
                HGVNetworkView view = HGVKit.getCurrentView();
                if (view == null) return;
                VisualStyle vs = ((VisualStyle) styleBox.getSelectedItem());
                if (!vs.equals(view.getVisualStyle()))
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
        JToolBar toolBar = cyMenus.getToolBar();
        toolBar.add(styleBox);
        toolBar.addSeparator();
    }

    protected void updateFocus(HGVNetworkView view)
    {
//        // deal with the new Network
//        VisualStyle new_style = view.getVisualStyle();
//        if (new_style == null)
//        {
//            new_style = VisualManager.getInstance().getDefaultVisualStyle();
//            if (new_style != null) view.setVisualStyle(new_style);
//        }
//        System.out.println("HGVDesktop - updateFocus network: "
//                + view.getIdentifier() + ":" + view.getVisualStyle() + ":"
//                + new_style);
//
//        styleBox.setSelectedItem(new_style);
//        cyMenus.setNodesRequiredItemsEnabled();
//        view.redrawGraph();
    }

    public void setFocus(HGVNetworkView view)
    {
        pcs.firePropertyChange(new PropertyChangeEvent(this,
                NETWORK_VIEW_FOCUSED, null, view));
        updateFocus(view);
    }

    public SwingPropertyChangeSupport getSwingPropertyChangeSupport()
    {
        return pcs;
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        System.out.println("HGVDesktop - getPropertyName(): "
                + e.getPropertyName() + ":" + e.getNewValue());
        if (e.getPropertyName() == NETWORK_VIEW_CREATED)
        {
            HGVNetworkView view = ((HGVNetworkView) e.getNewValue());
            HGVKit.getDesktop().getTabbedPane().addTab(
                    view.getIdentifier(), view.getComponent());
            // pass on the event
            pcs.firePropertyChange(e);
            networkPanel.focusNetworkNode(view);
        }
        else if (e.getPropertyName() == NETWORK_VIEW_FOCUSED)
        {
            // get focus event from NetworkViewManager
            updateFocus(((HGVNetworkView) e.getNewValue()));
            pcs.firePropertyChange(e);
            // attachPopupMenu(HGVKit.getCurrentNetworkView().getComponent());
        }
        else if (e.getPropertyName() == NETWORK_VIEW_DESTROYED)
        {
            HGVNetworkView view = ((HGVNetworkView) e.getNewValue());
            JTabbedPane tp = HGVKit.getDesktop().getTabbedPane();
            for(int i = 0; i < tp.getTabCount(); i++)
            {
                if(tp.getComponentAt(i).equals(view.getComponent()))
                {
                   tp.removeTabAt(i);
                   break;
                }
            }
            // pass on the event
            pcs.firePropertyChange(e);
        }
      
    }

    public void updatePropsPanel()
    {
        HGVNetworkView view = HGVKit.getCurrentView();
        PNodeView nv = view.getSelectedNodeView();
        if (nv != null)
        {
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
        }
    }
}
