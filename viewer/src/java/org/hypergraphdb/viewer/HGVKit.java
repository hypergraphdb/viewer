//  $Revision: 1.9 $
//  $Date: 2006/02/27 19:59:18 $
//  $Author: bizi $
//---------------------------------------------------------------------------
package org.hypergraphdb.viewer;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.layout.GEM;
import org.hypergraphdb.viewer.layout.HierarchicalLayout;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.layout.Radial;
import org.hypergraphdb.viewer.layout.SpringLayout;
import org.hypergraphdb.viewer.view.HGVDesktop;

import phoebe.PEdgeView;
import phoebe.PGraphView;
import phoebe.PNodeView;

/**
 * This class, HGVKit is <i>the</i> primary class in the API.
 * 
 * All Nodes and Edges must be created using the methods getHGVNode and
 * getHGVEdge, available only in this class. Once a node or edge is created
 * using these methods it can then be added to a HGVNetworkView, where it can be
 * used algorithmically.<BR>
 * <BR>
 */
public abstract class HGVKit
{
	public static String EXIT = "EXIT";
	// constants for tracking selection mode globally
	public static final int SELECT_NODES_ONLY = 1;
	public static final int SELECT_EDGES_ONLY = 2;
	public static final int SELECT_NODES_AND_EDGES = 3;
	// global to represent which selection mode is active
	private static int currentSelectionMode = SELECT_NODES_ONLY;
	// global flag to indicate if Squiggle is turned on
	private static boolean squiggleEnabled = false;
	protected static Object pcsO = new Object();
	protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			pcsO);
	protected static List<HGVNetworkView> networkViewList;
	protected static Layout prefered_layout;
	protected static Set<Layout> layouts = new HashSet<Layout>();
	static
	{
		Layout l = new GEM(); 
		layouts.add(new Radial());
		layouts.add(new HierarchicalLayout());
		layouts.add(new SpringLayout());
		//layouts.add(new SpringEmbeddedLayout());
		//layouts.add(new Sugiyama());
		layouts.add(l);
		setPreferedLayout(l);
	}

	static boolean embeded = false;

	public static boolean isEmbeded()
	{
		return embeded;
	}

	/**
	 * Shuts down HGVKit, after giving plugins time to react.
	 */
	public static void exit()
	{
		try
		{
			firePropertyChange(EXIT, null, "now");
		}
		catch (Exception e)
		{
			System.out.println("Errors on close, closed anyways.");
		}
		System.exit(0);
	}

	// --------------------//
	// Root Graph Methods
	// --------------------//
	/**
	 * Bound events are:
	 * <ol>
	 * <li>NETWORK_CREATED
	 * <li>NETWORK_DESTROYED
	 * </ol>
	 */
	public static SwingPropertyChangeSupport getSwingPropertyChangeSupport()
	{
		return pcs;
	}


	/**
	 * @param handle a handle for the node
	 * @param create will create a node if one does not exist
	 * @return will always return a node, if <code>create</code> is true
	 */
	public static FNode getHGVNode(HGHandle handle, boolean create)
	{
		FNode node = new FNode(handle);
		return node;
	}

	/**
	 * Gets the first Edge found.
	 * 
	 * @param node_1 one end of the edge
	 * @param node_2 the other end of the edge
	 * @param type the type of the edge
	 * @param create will create an edge if one does not exist
	 * @return returns an existing HGVEdge if present, or creates one if
	 * <code>create</code> is true, otherwise returns null.
	 */
	public static FEdge getHGVEdge(FNode node_1, FNode node_2, boolean create)
	{
		FEdge edge = new FEdge(node_1, node_2);
		return edge;
	}

	/**
	 * @param source_alias an source node handle
	 * @param target_alias an target node handle
	 * @return will always return an edge or null
	 */
	public static FEdge getHGVEdge(HGHandle sourceH, HGHandle targetH)
	{
		// System.out.println("getHGVEdge - source: " + sourceH + " edge: "
		//+ " target:" + targetH);
		FNode source = getHGVNode(sourceH, true);
		FNode target = getHGVNode(targetH, true);
		
		return getHGVEdge(source, target, true);
	}

     /**
	 * Return the HGVNetworkView that currently has the focus. 
	 */
	public static HGVNetworkView getCurrentView()
	{
	    HGVComponent comp = HGVComponent.getFocusedComponent();
		return comp != null ? comp.getView() : null;
	}

	/**
	 * @return the reference to the One CytoscapeDesktop
	 */
	public static HGVDesktop getDesktop()
	{
		return HGVDesktop.getInstance();
	}

	/**
	 * This Map has keys that are Strings ( network_ids ) and values that are
	 * networks.
	 */
	public static List<HGVNetworkView> getNetworkViewsList()
	{
		if (networkViewList == null)
			networkViewList = new ArrayList<HGVNetworkView>();
		return networkViewList;
	}

	
	/**
	 * destroys the network view, including any layout information
	 */
	public static void destroyNetworkView(HGVNetworkView view)
	{
		// System.out.println( "destroying: "+view.getIdentifier()+" :
		// "+getNetworkViewMap().get( view.getIdentifier() ) );
	    getNetworkViewsList().remove(view);
		// System.out.println( "gone from hash: "+view.getIdentifier()+" :
		// "+getNetworkViewMap().get( view.getIdentifier() ) );
		firePropertyChange(HGVDesktop.NETWORK_VIEW_DESTROYED, null, view);
		// theoretically this should not be set to null till after the events
		// firing is done
		view = null;
		// TODO: do we want here?
		System.gc();
	}

	public static HGVComponent createHGVComponent(HGVNetworkView view)
	{
	    Collection<FNode> nodes = new ArrayList<FNode>();
	    for(PNodeView nv : view.getNodeViews())
	        nodes.add(nv.getNode());
	    Collection<FEdge> edges  = new ArrayList<FEdge>();
        for(PEdgeView ev : view.getEdgeViews())
            edges.add(ev.getEdge()); 
        return createHGVComponent(view.getHyperGraph(), nodes, edges);
	}
		
	/**
	 * Creates a new Network, that inherits from the given ParentNetwork
	 */
	public static HGVComponent createHGVComponent(HyperGraph db, 
	        Collection<FNode> nodes, Collection<FEdge> edges)
	{
	    HGVComponent comp = new HGVComponent(db, nodes, edges);
	    getNetworkViewsList().add(comp.getView());
	    firePropertyChange(HGVDesktop.NETWORK_VIEW_CREATED, null, comp.getView());
		return comp;
	}

	
	public static void firePropertyChange(String property_type,
			Object old_value, Object new_value)
	{
		PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type,
				old_value, new_value);
		System.out.println("HGVKit FIRING : " + property_type);
		// for(PropertyChangeListener l:
		// getSwingPropertyChangeSupport().getPropertyChangeListeners())
		// System.out.println("" + l);
		getSwingPropertyChangeSupport().firePropertyChange(e);
	}

	private static void setSquiggleState(boolean isEnabled)
	{
		// enable Squiggle on all network views
		for (HGVNetworkView view : getNetworkViewsList())
		{
			if (view == null) continue;
			if (isEnabled)
				view.getSquiggleHandler().beginSquiggling();
			else
				view.getSquiggleHandler().stopSquiggling();
		}
	}

	/**
	 * Utility method to enable Squiggle function.
	 */
	public static void enableSquiggle()
	{
		// set the global flag to indicate that Squiggle is enabled
		squiggleEnabled = true;
		setSquiggleState(true);
	}

	/**
	 * Utility method to disable Squiggle function.
	 */
	public static void disableSquiggle()
	{
		// set the global flag to indicate that Squiggle is disabled
		squiggleEnabled = false;
		setSquiggleState(false);
	}

	/**
	 * Returns the value of the global flag to indicate whether the Squiggle
	 * function is enabled.
	 */
	public static boolean isSquiggleEnabled()
	{
		return squiggleEnabled;
	}

	/**
	 * Gets the selection mode value.
	 */
	public static int getSelectionMode()
	{
		return currentSelectionMode;
	}

	/**
	 * Sets the specified selection mode on all views.
	 * 
	 * @param selectionMode SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
	 * SELECT_NODES_AND_EDGES.
	 */
	public static void setSelectionMode(int selectionMode)
	{
		// set the selection mode on all the views
		for (HGVNetworkView view : getNetworkViewsList())
			setSelectionMode(selectionMode, view);
		
		// update the global indicating the selection mode
		currentSelectionMode = selectionMode;
	}

	/**
	 * Utility method to set the selection mode on the specified GraphView.
	 * 
	 * @param selectionMode SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
	 * SELECT_NODES_AND_EDGES.
	 * @param view the GraphView to set the selection mode on.
	 */
	public static void setSelectionMode(int selectionMode, PGraphView view)
	{
		// first, disable node and edge selection on the view
		view.disableNodeSelection();
		view.disableEdgeSelection();
		// then, based on selection mode, enable node and/or edge selection
		switch (selectionMode)
		{
		case SELECT_NODES_ONLY:
			view.enableNodeSelection();
			break;
		case SELECT_EDGES_ONLY:
			view.enableEdgeSelection();
			break;
		case SELECT_NODES_AND_EDGES:
			view.enableNodeSelection();
			view.enableEdgeSelection();
			break;
		}
	}

	public static Set<Layout> getLayouts()
	{
		return layouts;
	}

	public static void addLayout(Layout layout)
	{
		layouts.add(layout);
	}

	public static Layout getPreferedLayout()
	{
		return prefered_layout;
	}

	public static void setPreferedLayout(Layout pref_layout)
	{
		prefered_layout = pref_layout;
	}

	public static HGVNetworkView getStandaloneView(HyperGraph graph, HGWNReader reader)
	{
		embeded = true;
		try
		{
		    HGVComponent comp = 
			    createHGVComponent(graph, reader.getNodes(), reader.getEdges());
			comp.getView().setIdentifier(graph.getStore().getDatabaseLocation());
			return comp.getView();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static HGVNetworkView getStandaloneView(HyperGraph hg, 
												   HGHandle h,
												   int depth, 
												   HGAtomPredicate cond)
	{
		final HGWNReader reader = new HGWNReader(hg);
		reader.read(h, depth, cond); 
		return getStandaloneView(hg, reader);
	}
	
}
