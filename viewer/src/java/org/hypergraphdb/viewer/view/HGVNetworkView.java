package org.hypergraphdb.viewer.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.hypergraphdb.viewer.HGVEdge;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNode;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.actions.GinyUtils;
import org.hypergraphdb.viewer.data.HGVNetworkUtilities;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.DropDownButton;
import phoebe.PGraphView;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.swing.PScrollPane;
import giny.model.GraphPerspective;
import giny.view.EdgeView;
import giny.view.NodeView;

/**
 * HGVNetworkView is responsible for actually getting a graph to show up on the
 * screen.<BR>
 * <BR>
 * HGViewer does not currently define specific classes for NodeViews and
 * EdgeViews, the deafults from the GINY graph library ( namely phoebe.PNodeView
 * and phoebe.PEdgeView ) are most commonly used. Making custom nodes is easy
 * and fun. One must implement the giny.view.NodeView interface and inherit from
 * edu.umd.cs.piccolo.PNode. The Piccolo project is what all of the paiting is
 * based on, and is very fast, flexable and powerful. Becoming acquainted with
 * Piccolo is essential for build custom nodes.<BR>
 * <BR>
 * Fortunately, if you just want basic shapes and colors, it's all built into
 * the UI already, and you really need never even use this class. Just learn how
 * to use the VisualManager to acclompish your data to view mappings. The manual is
 * a good place to start.
 */

public class HGVNetworkView extends PGraphView
{
	/**
	 * This is the title of the NetworkView, it will be displayed in a Tab, or
	 * in a Window.
	 */
	protected String title;
	/**
	 * This is the label that tells how many node/edges are in a HGVNetworkView
	 * and how many are selected/hidden
	 */
	protected JLabel statusLabel;
	/**
	 * The FlagAndSelectionHandler keeps the selection state of view objects
	 * in the HGVNetworkView nsync with the flagged state of those objects in the
	 * default flagger of the associated HGVNetwork.
	 */
	protected FlagAndSelectionHandler flagAndSelectionHandler;
	protected VisualStyle style;
	protected PBasicInputEventHandler keyEventHandler;
	protected PCanvas canvas;

	public HGVNetworkView(HGVNetwork network, String title)
	{
		super((GraphPerspective) network);
		this.title = title;
		initialize();
	}

	public PCanvas getCanvas()
	{
		return canvas;
	}

	protected void initializePGraphView(boolean setup)
	{
		isInitialized = true;
		viewComponent = new JPanel();
		viewComponent.setLayout(new BorderLayout());
		canvas = new Canvas();
		canvas
				.setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		canvas.setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		canvas.setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		PScrollPane scroll = new PScrollPane(canvas);
		viewComponent.add(scroll, BorderLayout.CENTER);
		getCanvas().getCamera().setPaint(DEFAULT_BACKGROUND_COLOR);
		// System.out.println("Phoebe just built a canvas: " + canvas);
		// Set up Layers
		nodeLayer = new PLayer() {
			public void addChild(int index, PNode child)
			{
				PNode oldParent = child.getParent();
				if (oldParent != null)
				{
					oldParent.removeChild(child);
				}
				child.setParent(this);
				getChildrenReference().add(index, child);
				child.invalidatePaint();
				invalidateFullBounds();
				if (firePiccoloEvents)
					firePropertyChange(0, PROPERTY_CHILDREN, null,
							getChildrenReference());
			}
		};
		edgeLayer = new PLayer() {
			public void addChild(int index, PNode child)
			{
				PNode oldParent = child.getParent();
				if (oldParent != null)
				{
					oldParent.removeChild(child);
				}
				child.setParent(this);
				getChildrenReference().add(index, child);
				child.invalidatePaint();
				invalidateFullBounds();
				if (firePiccoloEvents)
					firePropertyChange(0, PROPERTY_CHILDREN, null,
							getChildrenReference());
			}
		};
		objectLayer = new PLayer();
		squiggleLayer = new PLayer();
		getCanvas().getLayer().addChild(edgeLayer);
		getCanvas().getLayer().addChild(nodeLayer);
		getCanvas().getLayer().addChild(objectLayer);
		// Set up the the Piccolo Event Handlers
		initializeEventHandlers();
		// initialize all of the Viewable objects based on all of
		// the Edges and Nodes currently in the GraphPerspective
		nodeViewMap = new OpenIntObjectHashMap(PrimeFinder
				.nextPrime(perspective.getNodeCount()));
		edgeViewMap = new OpenIntObjectHashMap(PrimeFinder
				.nextPrime(perspective.getEdgeCount()));
		contextMenuStore = new HashMap(5);
		NODE_DEFAULTS = new Object[] { new Double(DEFAULT_X),
				new Double(DEFAULT_Y), new Integer(NodeView.OCTAGON),
				DEFAULT_NODE_PAINT, DEFAULT_NODE_SELECTION_PAINT,
				DEFAULT_BORDER_PAINT, new Float(1), new Double(20),
				new Double(20), "Node" };
		EDGE_DEFAULTS = new Object[] { new Integer(0), new Integer(0),
				new Float(1), new Integer(EdgeView.STRAIGHT_LINES),
				DEFAULT_EDGE_STROKE_PAINT, DEFAULT_EDGE_STROKE_PAINT_SELECTION,
				new Integer(2), DEFAULT_EDGE_END_PAINT, DEFAULT_EDGE_END_PAINT,
				new Integer(3), DEFAULT_EDGE_END_PAINT, DEFAULT_EDGE_END_PAINT };
		// only create the node/edge view if requested
		if (setup) createViewableObjects();
		ensureNodeSelectionCapacity();
		ensureEdgeSelectionCapacity();
	}
	private /*static*/ JToolBar toolbar;

	protected /*static*/ JToolBar getBottomToolbar()
	{
		//if (toolbar != null) return toolbar;
		toolbar = new JToolBar();
		toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // NOI18N
		final HGVMenus m = HGVMenus.getInstance();
		toolbar.add(createDropDown(toolbar, m.getSelectMenu(), "hand"));
		toolbar.add(createDropDown(toolbar, m.getLayoutMenu(), "layout"));
		toolbar.add(createDropDown(toolbar, m.getVizMenu(), "visual"));
		toolbar.add(createDropDown(toolbar, m.getZoomMenu(), "zoom"));
		return toolbar;
	}
	
	private static DropDownButton createDropDown(JToolBar toolbar, final JMenu m, final String name){
		DropDownButton dropdown = new DropDownButton() {
			protected JPopupMenu getPopupMenu()
			{
				JPopupMenu popup = new JPopupMenu();
				for(final Component c : m.getMenuComponents())
				{
					if(c instanceof JMenuItem && ((JMenuItem) c).getAction() != null)
						popup.add(((JMenuItem) c).getAction());
					else if(c instanceof JMenu){
						JMenu menu = new JMenu(((JMenu) c).getText()); 
						for(Component cc: ((JMenu) c).getMenuComponents())
							if(cc instanceof JMenuItem && ((JMenuItem) cc).getAction() != null)
								menu.add(((JMenuItem) cc).getAction());
						popup.add(menu);
					}
				}
				return popup;
			}
		};
		dropdown.addToToolBar(toolbar);
		dropdown.putClientProperty("hideActionText", Boolean.TRUE);
	    dropdown.setAction(createDummyAction(name));
	    return dropdown;
	} 

	protected void initialize()
	{
		// setup the StatusLabel
		statusLabel = new JLabel();
		statusLabel.setAlignmentX(SwingConstants.LEADING);
		statusLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		JPanel status = new JPanel(new GridBagLayout());
		GridBagConstraints gbg = new GridBagConstraints();
		gbg.gridy = 0;	gbg.gridx = 0;
		gbg.fill = GridBagConstraints.HORIZONTAL;
		gbg.anchor = GridBagConstraints.WEST;
		gbg.weightx = 1.0;
		
		status.add(statusLabel, gbg);
		//status.add(statusLabel, BorderLayout.WEST);
		if(HGViewer.isEmbeded())
		{
			gbg = new GridBagConstraints();
			gbg.gridy = 0;
			gbg.gridx = 1;
			gbg.insets = new Insets(0, 0, 0, 5);
			gbg.anchor = GridBagConstraints.EAST;
			//status.add(statusLabel, BorderLayout.EAST);
			JToolBar bar = getBottomToolbar();
			status.add(bar, gbg);
			viewComponent.add(status, BorderLayout.SOUTH);
		}else
			viewComponent.add(statusLabel, BorderLayout.SOUTH);
		
		updateStatusLabel();
		enableNodeSelection();
		disableEdgeSelection();
		flagAndSelectionHandler = new FlagAndSelectionHandler(
				getNetwork().getFlagger(), this);
		// TODO:
		// Add NetworkView specific ToolBars
		this.addContextMethod("phoebe.PNodeView",
						"org.hypergraphdb.viewer.giny.ContextMenuHelper",
						"expandNodeAction", new Class[] {}, getClass()
								.getClassLoader());
		this.addContextMethod("phoebe.PNodeView",
				"org.hypergraphdb.viewer.giny.ContextMenuHelper",
				"collapseNodeAction", new Class[] {}, getClass()
						.getClassLoader());
	}

	protected void initializeEventHandlers()
	{
		super.initializeEventHandlers();
		keyEventHandler = new PBasicInputEventHandler() {
			PText typeAheadNode = new PText();
			// PPath background = new PPath();
			StringBuffer typeBuffer = new StringBuffer();
			int length = 0;
			boolean space_down = false;
			boolean slash_pressed = false;

			protected void selectAndZoom()
			{
				String search_string;
				if (length == 0)
				{
					search_string = "";
				} else
				{
					search_string = typeBuffer.toString() + "*";
				}
				GinyUtils.deselectAllNodes(HGViewer.getCurrentView());
				typeAheadNode.setText(typeBuffer.toString());
				HGVNetworkUtilities.selectNodesStartingWith(HGViewer
						.getCurrentNetwork(), search_string, HGViewer
						.getCurrentView());
				org.hypergraphdb.viewer.actions.ZoomSelectedAction
						.zoomSelected();
			}

			protected void resetFind()
			{
				slash_pressed = false;
				length = 0;
				typeBuffer = new StringBuffer();
				typeAheadNode.setText("");
				getCanvas().getCamera().removeChild(typeAheadNode);
			}

			public void keyPressed(PInputEvent event)
			{
				// System.out.println( "Key Code Pressed: "+event.getKeyCode()
				// );
				// System.out.println( "Key text: "+KeyEvent.getKeyText(
				// event.getKeyCode() ) );
				if (event.getKeyCode() == KeyEvent.VK_SPACE)
				{
					space_down = true;
					getCanvas().setCursor(
							Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					getCanvas().getPanEventHandler().setEventFilter(
							new PInputEventFilter(InputEvent.BUTTON1_MASK));
					if (nodeSelection)
					{
						getCanvas().removeInputEventListener(
								getSelectionHandler());
					}
					if (edgeSelection)
					{
						getCanvas().removeInputEventListener(
								getEdgeSelectionHandler());
					}
				} else if (!slash_pressed
						&& event.getKeyCode() == KeyEvent.VK_SLASH)
				{
					// System.out.println( "start taf " );
					slash_pressed = true;
					getCanvas().getCamera().addChild(typeAheadNode);
					typeAheadNode.setOffset(20, 20);
					typeAheadNode.setPaint(new java.awt.Color(0f, 0f, 0f, .6f));
					typeAheadNode.setTextPaint(java.awt.Color.white);
					typeAheadNode.setFont(typeAheadNode.getFont().deriveFont(
							30f));
				} else if (slash_pressed
						&& event.getKeyCode() != KeyEvent.VK_ESCAPE
						&& event.getKeyCode() != KeyEvent.VK_BACK_SPACE)
				{
					// System.out.println( "Normal Press" );
					typeBuffer.append(KeyEvent.getKeyText(event.getKeyCode()));
					length++;
					selectAndZoom();
				} else if (slash_pressed
						&& event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					// System.out.println( "ESCAPRE PRESSED" );
					resetFind();
				} else if (slash_pressed
						&& event.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				{
					// System.out.println( "back space: "+length+"
					// "+typeBuffer.toString() );
					if (length != 0)
					{
						typeBuffer.deleteCharAt(length - 1);
						length--;
					}
					selectAndZoom();
					return;
				}
			}

			public void keyReleased(PInputEvent event)
			{
				if (space_down)
				{
					space_down = false;
					getCanvas().setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					getCanvas().getPanEventHandler().setEventFilter(
							new PInputEventFilter(InputEvent.BUTTON2_MASK));
					if (nodeSelection)
					{
						getCanvas()
								.addInputEventListener(getSelectionHandler());
					}
					if (edgeSelection)
					{
						getCanvas().addInputEventListener(
								getEdgeSelectionHandler());
					}
				}
			}
		};
		getCanvas().addInputEventListener(keyEventHandler);
		getCanvas().getRoot().getDefaultInputManager().setKeyboardFocus(
				keyEventHandler);
	}

	public HGVNetwork getNetwork()
	{
		return (HGVNetwork) getGraphPerspective();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String new_title)
	{
		this.title = new_title;
	}

	public void redrawGraph()
	{
		getCanvas().setInteracting(true);
		VisualManager.getInstance().applyAppearances(this);
		// getCanvas().paintImmediately();
		getCanvas().setInteracting(false);
	}

	public VisualStyle getVisualStyle()
	{
		// if (style == null)
		// style = VisualManager.getInstance().getDefaultVisualStyle();
		return style;
	}

	public void setVisualStyle(VisualStyle style)
	{
		//System.out.println("setVisualStyle: " + style);
		this.style = style;
		//redrawGraph();
	}


	// ------------------------------//
	// Event Handling and Response
	/**
	 * Overwritten version of fireGraphViewChanged so that the label can be
	 * updated
	 */
	protected void fireGraphViewChanged(ChangeEvent event)
	{
		updateStatusLabel();
		// fire the event to everyone else.
		super.fireGraphViewChanged(event);
	}

	/**
	 * Resets the info label status bar text with the current number of nodes,
	 * edges, selected nodes, and selected edges.
	 */
	public void updateStatusLabel()
	{
		int nodeCount = getNodeViewCount();
		int edgeCount = getEdgeViewCount();
		int selectedNodes = getSelectedNodes().size();
		int selectedEdges = getSelectedEdges().size();
		statusLabel.setText("  Nodes: " + nodeCount + " (" + selectedNodes
				+ " selected)" + " Edges: " + edgeCount + " (" + selectedEdges
				+ " selected)");
	}

	// -------------------------------//
	// Layouts and VizMaps
	// --------------------//
	// Convience Methods
	/**
	 * Sets the Given nodes Selected
	 */
	public boolean setSelected(HGVNode[] nodes)
	{
		return setSelected(convertToViews(nodes));
	}

	/**
	 * Sets the Given nodes Selected
	 */
	public boolean setSelected(NodeView[] node_views)
	{
		for (int i = 0; i < node_views.length; ++i)
		{
			node_views[i].select();
		}
		return true;
	}

	/**
	 * Sets the Given edges Selected
	 */
	public boolean setSelected(HGVEdge[] edges)
	{
		return setSelected(convertToViews(edges));
	}

	/**
	 * Sets the Given edges Selected
	 */
	public boolean setSelected(EdgeView[] edge_views)
	{
		for (int i = 0; i < edge_views.length; ++i)
		{
			edge_views[i].select();
		}
		return true;
	}

	protected NodeView[] convertToViews(HGVNode[] nodes)
	{
		NodeView[] views = new NodeView[nodes.length];
		for (int i = 0; i < nodes.length; ++i)
		{
			views[i] = getNodeView(nodes[i]);
		}
		return views;
	}

	protected EdgeView[] convertToViews(HGVEdge[] edges)
	{
		EdgeView[] views = new EdgeView[edges.length];
		for (int i = 0; i < edges.length; ++i)
		{
			views[i] = getEdgeView(edges[i]);
		}
		return views;
	}

	protected NodeView[] convertToNodeViews(int[] nodes)
	{
		NodeView[] views = new NodeView[nodes.length];
		for (int i = 0; i < nodes.length; ++i)
		{
			views[i] = getNodeView(nodes[i]);
		}
		return views;
	}

	protected EdgeView[] convertToEdgeViews(int[] edges)
	{
		EdgeView[] views = new EdgeView[edges.length];
		for (int i = 0; i < edges.length; ++i)
		{
			views[i] = getEdgeView(edges[i]);
		}
		return views;
	}

	protected NodeView[] getInverseViews(NodeView[] given)
	{
		NodeView[] inverse = new NodeView[getNodeViewCount() - given.length];
		List node_views = getNodeViewsList();
		int count = 0;
		Iterator i = node_views.iterator();
		Arrays.sort(given);
		while (i.hasNext())
		{
			NodeView view = (NodeView) i.next();
			if (Arrays.binarySearch(given, view) < 0)
			{
				// not a given, add
				inverse[count] = view;
				count++;
			}
		}
		return inverse;
	}

	protected EdgeView[] getInverseViews(EdgeView[] given)
	{
		EdgeView[] inverse = new EdgeView[getEdgeViewCount() - given.length];
		List edge_views = getEdgeViewsList();
		int count = 0;
		Iterator i = edge_views.iterator();
		Arrays.sort(given);
		while (i.hasNext())
		{
			EdgeView view = (EdgeView) i.next();
			if (Arrays.binarySearch(given, view) < 0)
			{
				// not a given, add
				inverse[count] = view;
				count++;
			}
		}
		return inverse;
	}

	public class Canvas extends PCanvas implements FocusListener
	{
		public Canvas()
		{
			super();
		}

		public HGVNetworkView getView()
		{
			return HGVNetworkView.this;
		}

		public void repaint(long a, int x, int y, int w, int h)
		{
			if (((x + w) > 1) || ((y + h) > 2)) super.repaint(a, x, y, w, h);
		}

		public void focusGained(FocusEvent e)
		{
			HGViewer.setCurrentView(getView());
		}

		public void focusLost(FocusEvent e)
		{
			// DO NOTHING
		}

		@Override
		public void addNotify()
		{
			super.addNotify();
			addFocusListener(this);
		}

		@Override
		public void removeNotify()
		{
			super.removeNotify();
			removeFocusListener(this);
			//HGVNetwork network = this.getView().getNetwork();
			//HGViewer.getNetworkSet()
		}
	};

	private static Action createDummyAction(final String name)
	{
		return new AbstractAction(name) {
			{
				putValue(Action.SMALL_ICON, new ImageIcon(getClass()
						.getResource(
								"/org/hypergraphdb/viewer/images/" + name + ".gif")));
			}

			public void actionPerformed(ActionEvent e)
			{
			}
		};
	}
}


