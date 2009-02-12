package org.hypergraphdb.viewer.view;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.view.HGVMenus;
import org.hypergraphdb.viewer.visual.*;
import org.hypergraphdb.viewer.props.*;
import org.hypergraphdb.*;
import phoebe.PGraphView;
import phoebe.PNodeView;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.beans.*;
import javax.help.HelpBroker;
import javax.help.HelpSet;

/**
 * The HGVDesktop is the central Window for working with HGVKit
 */
public class HGVDesktop extends JFrame implements PropertyChangeListener
{
	protected long lastPluginRegistryUpdate;
	// --------------------//
	// Static variables
	public static String NETWORK_VIEW_FOCUSED = "NETWORK_VIEW_FOCUSED";
	public static String NETWORK_VIEW_FOCUS = "NETWORK_VIEW_FOCUS";
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
	/**
	 * The NetworkViewManager can support three types of interfaces.
	 * Tabbed/InternalFrame/ExternalFrame
	 */
	protected NetworkViewManager networkViewManager;
	/**
	 * The HelpBroker provides access to JavaHelp
	 */
	protected HGVHelpBroker cyHelpBroker;
	// --------------------//
	// Event Support
	/**
	 * provides support for property change events
	 */
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);
	private/* final */TitledBorder propertiesTableBorder_ = new TitledBorder("");
	// --------------------//
	// VizMap Variables
	protected PropertySetPanel propsPanel;
	private JPanel mainPropsPanel;
	private JComboBox styleBox;

	// ----------------------------------------//
	// Constructors
	// ----------------------------------------//
	/**
	 * Create a HGVDesktop that conforms the given view type.
	 * 
	 * @param view_type one of the ViewTypes
	 */
	public HGVDesktop()
	{
		super("HGVKit Desktop");
		initialize();
	}

	protected void initialize()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getClassLoader().getResource(
						"org/hypergraphdb/viewer/images/c16.png")));
		// initialize Help system with HGVKit help set - define
		// context-sensitive
		// help as we create components
		cyHelpBroker = new HGVHelpBroker();
		JPanel main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());
		// enable context-sensitive help generally
		// TODO: temporarily switched off
		// getHelpBroker().enableHelpKey(getRootPane(),"intro", null);
		// enable context-sensitive help for main panel
		// TODO: temporarily switched off
		// getHelpBroker().enableHelp(main_panel,"intro", null);
		// ------------------------------//
		// Set up the Panels, Menus, and Event Firing
		networkPanel = new NetworkPanel();
		// enable context-sensitive help for networkPanel
		// //TODO: temporarily switched off
		// getHelpBroker().enableHelp(networkPanel,"network-view-manager",
		// null);
		cyMenus = HGVMenus.getInstance();
		// enable context-sensitive help for menus/menubar
		// TODO: temporarily switched off
		// getHelpBroker().enableHelp(cyMenus.getMenuBar(),"menus", null);
		networkViewManager = NetworkViewManager.getInstance();
		// Listener Setup
		// ----------------------------------------
		// |----------|
		// | HGVMenus |
		// |----------|
		// |
		// |
		// |-----| |---------| |------| |-------|
		// | N P |------| Desktop |----| NVM |--| Views |
		// |-----| |---------| |------| |-------|
		// |
		// |
		// |-----------|
		// | HGVKit |
		// |-----------|
		// The HGVDesktop listens to NETWORK_VIEW_CREATED events,
		// and passes them on, The NetworkPanel listens for them
		// The Desktop also keeps HGVKit up2date, but NOT via events
		HGVKit.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);
		// The Networkviewmanager listens to the HGVDesktop to know when to
		// put new NetworkViews in the userspace and to get passed focus events
		// from
		// the NetworkPanel. The HGVDesktop also listens to the NVM
		this.getSwingPropertyChangeSupport().addPropertyChangeListener(
				networkViewManager);
		networkViewManager.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);
		// The NetworkPanel listens to the HGVDesktop for NETWORK_CREATED_EVENTS
		// a
		// as well as for passing focused events from the Networkviewmanager.
		// The
		// HGVDesktop also listens to the NetworkPanel
		this.getSwingPropertyChangeSupport().addPropertyChangeListener(
				networkPanel);
		networkPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		// initialize Help Menu
		// TODO: temporarily switched off
		// cyMenus.initializeHelp(cyHelpBroker.getHelpBroker());
		// create the HGVDesktop
		JScrollPane scroll_tab = new JScrollPane(networkViewManager
				.getTabbedPane());
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
		// propsPanel.setVisible(false);
		// propsPanel.setModelObject(new String("test"));
		javax.swing.JSplitPane sp = new javax.swing.JSplitPane();
		sp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		sp.setDividerSize(5);
		sp.setTopComponent(networkPanel);
		sp.setBottomComponent(mainPropsPanel);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
				sp,// networkPanel,
				scroll_tab);
		split.setOneTouchExpandable(true);
		// JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
		// false,
		// networkPanel,
		// networkViewManager.getTabbedPane() );
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

			public void windowClosed()
			{
			}
		});
		// show the Desktop
		setContentPane(main_panel);
		pack();
		setSize(800, 700);
		setVisible(true);
	}

	public NetworkPanel getNetworkPanel()
	{
		return networkPanel;
	}

	public HelpBroker getHelpBroker()
	{
		return cyHelpBroker.getHelpBroker();
	}

	public HelpSet getHelpSet()
	{
		return cyHelpBroker.getHelpSet();
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
				//System.out.println("StyleChange: " + view.getVisualStyle());
				if(view != null)
				  styleBox.setSelectedItem(view.getVisualStyle());
			}
		});
		String comboBoxHelp = "Change the current visual style";
		styleBox.setToolTipText(comboBoxHelp);
		styleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				HGVNetworkView view = HGVKit.getCurrentView();
				//System.out.println("StyleSel: " + view.getTitle());
				if (view == null) return;
				VisualStyle vs = ((VisualStyle) styleBox.getSelectedItem());
				//System.out.println("StyleSel: " + vs + " old:"
				//		+ view.getVisualStyle());
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
		if (HGVKit.setCurrentView(view))
		{
			// deal with the new Network
			VisualStyle new_style = view.getVisualStyle();
			// propsPanel.setVisible(new_view.getNetwork().getHyperGraph() !=
			// null);
			if (new_style == null)
			{
				new_style = VisualManager.getInstance().getDefaultVisualStyle();
				if (new_style != null)
					view.setVisualStyle(new_style);
			}
		 System.out.println("HGVDesktop - updateFocus network: " +
			 view.getIdentifier() + ":" + view.getVisualStyle() + ":" + new_style);
			
			styleBox.setSelectedItem(new_style);
			cyMenus.setNodesRequiredItemsEnabled();
			view.redrawGraph();
		}
	}

	public void setFocus(HGVNetworkView view)
	{
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				NETWORK_VIEW_FOCUSED, null, view));
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				NETWORK_VIEW_FOCUS, null, view));
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
			// add the new view to the GraphViewController
			HGVKit.getGraphViewController().addGraphView(
					(HGVNetworkView) e.getNewValue());
			// pass on the event
			pcs.firePropertyChange(e);
			networkPanel.focusNetworkNode(((HGVNetworkView) e.getNewValue()));
			networkPanel.fireFocus(((HGVNetworkView) e.getNewValue()));
		} else if (e.getPropertyName() == NETWORK_VIEW_FOCUSED)
		{
			// get focus event from NetworkViewManager
			updateFocus(((HGVNetworkView) e.getNewValue()));
			pcs.firePropertyChange(e);
			// attachPopupMenu(HGVKit.getCurrentNetworkView().getComponent());
		} else if (e.getPropertyName() == NETWORK_VIEW_FOCUS)
		{
			// get Focus from NetworkPanel
			updateFocus(((HGVNetworkView) e.getNewValue()));
			pcs.firePropertyChange(e);
		} else if (e.getPropertyName() == HGVKit.NETWORK_CREATED)
		{
			// fire the event so that the NetworkPanel can catch it
			pcs.firePropertyChange(e);
		} else if (e.getPropertyName() == HGVKit.NETWORK_DESTROYED)
		{
			// fire the event so that the NetworkPanel can catch it
			pcs.firePropertyChange(e);
		} else if (e.getPropertyName() == NETWORK_VIEW_DESTROYED)
		{
			// remove the view from the GraphViewController
			HGVKit.getGraphViewController().removeGraphView(
					(HGVNetworkView) e.getNewValue());
			// pass on the event
			pcs.firePropertyChange(e);
		} else if (e.getPropertyName() == BeanProperty.PROP_VALUE)
		{
			PGraphView view = HGVKit.getCurrentView();
			List selected_nodeViews = view.getSelectedNodes();
			FNode node = ((PNodeView) selected_nodeViews.get(0))
					.getNode();
			HyperGraph hg = HGVKit.getCurrentNetwork().getHyperGraph();
			// System.out.println("HGVDesktop - replace: "
			// + propsPanel.getModelObject());
			hg.replace(node.getHandle(), propsPanel.getModelObject());
			// pass on the event
			pcs.firePropertyChange(e);
		}
	}

	public void updatePropsPanel()
	{
		HGVNetworkView view = HGVKit.getCurrentView();
		List selected_nodeViews = view.getSelectedNodes();
		if (!selected_nodeViews.isEmpty())
		{
			FNode node = ((PNodeView) selected_nodeViews.get(0)).getNode();
			// System.out.println("updatePropsPanel()" +
			// ((HGVNode)node).getHandle());
			Object obj = view.getNetwork().getHyperGraph().get(node.getHandle());
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
