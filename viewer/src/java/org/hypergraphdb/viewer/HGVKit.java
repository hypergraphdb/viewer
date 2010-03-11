//  $Revision: 1.9 $
//  $Date: 2006/02/27 19:59:18 $
//  $Author: bizi $
//---------------------------------------------------------------------------
package org.hypergraphdb.viewer;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.SwingPropertyChangeSupport;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.layout.GEM;
import org.hypergraphdb.viewer.layout.HierarchicalLayout;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.layout.Radial;
import org.hypergraphdb.viewer.layout.SpringEmbeddedLayout;
import org.hypergraphdb.viewer.layout.SpringLayout;

import phoebe.PEdgeView;
import phoebe.PNodeView;

/**
 * This class, HGVKit is <i>the</i> primary class in the API.
 * 
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
	protected static List<GraphView> graphViewList;
	static boolean embeded = true;
	protected static Layout prefered_layout;
	protected static ArrayList<Layout> layouts = new ArrayList<Layout>();
	static
	{
		Layout l = new GEM(); 
		layouts.add(new Radial());
		layouts.add(new HierarchicalLayout());
		layouts.add(new SpringLayout());
		layouts.add(new SpringEmbeddedLayout());
		//layouts.add(new Sugiyama());
		layouts.add(l);
		setPreferedLayout(l);
	}

	public static boolean isEmbeded()
	{
		return embeded;
	}

	/**
	 * Shuts down HGVKit.
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

	public static HGViewer getCurrentViewer()
    {
        HGViewer comp = HGViewer.getFocusedComponent();
        return comp;
    }

	 /**
	 * Return the GraphView that currently has the focus. 
	 */
	public static GraphView getCurrentView()
	{
	    HGViewer comp = getCurrentViewer();
		return comp != null ? comp.getView() : null;
	}

	/**
	 * @return the reference to the One HGVDesktop
	 */
	public static HGVDesktop getDesktop()
	{
		return HGVDesktop.getInstance();
	}

	/**
	 */
	public static List<GraphView> getViewersList() 
	{
		if (graphViewList == null)
			graphViewList = new ArrayList<GraphView>();
		return graphViewList;
	}

	
	/**
	 * destroys the network view, including any layout information
	 */
	public static void destroyNetworkView(GraphView view)
	{
		getViewersList().remove(view);
		firePropertyChange(HGVDesktop.NETWORK_VIEW_DESTROYED, null, view);
		// theoretically this should not be set to null till after the events
		// firing is done
		view = null;
		// TODO: do we want here?
		System.gc();
	}

	public static HGViewer createHGViewer(GraphView view)
	{
	    Collection<FNode> nodes = new ArrayList<FNode>();
	    for(PNodeView nv : view.getNodeViews())
	        nodes.add(nv.getNode());
	    Collection<FEdge> edges  = new ArrayList<FEdge>();
        for(PEdgeView ev : view.getEdgeViews())
            edges.add(ev.getEdge()); 
        return createHGViewer(view.getHyperGraph(), nodes, edges);
	}
		
	/**
	 * Creates a new Network, that inherits from the given ParentNetwork
	 */
	public static HGViewer createHGViewer(HyperGraph db, 
	        Collection<FNode> nodes, Collection<FEdge> edges)
	{
	    HGViewer comp = new HGViewer(db, nodes, edges);
	    getViewersList().add(comp.getView());
	    firePropertyChange(HGVDesktop.NETWORK_VIEW_CREATED, null, comp.getView());
		return comp;
	}

	
	public static void firePropertyChange(String property_type,
			Object old_value, Object new_value)
	{
		PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type,
				old_value, new_value);
		//System.out.println("HGVKit FIRING : " + property_type);
	    getSwingPropertyChangeSupport().firePropertyChange(e);
	}

	public static void setSquiggleState(boolean isEnabled)
	{
	    squiggleEnabled = isEnabled;
		// enable Squiggle on all network views
		for (GraphView view : getViewersList())
		{
			if (view == null) continue;
			if (isEnabled)
				view.getSquiggleHandler().beginSquiggling();
			else
				view.getSquiggleHandler().stopSquiggling();
		}
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
		for (GraphView view : getViewersList())
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
	public static void setSelectionMode(int selectionMode, GraphView view)
	{
		// first, disable node and edge selection on the view
		view.setNodeSelection(false);
		view.setEdgeSelection(false);
		// then, based on selection mode, enable node and/or edge selection
		switch (selectionMode)
		{
		case SELECT_NODES_ONLY:
			view.setNodeSelection(true);
			break;
		case SELECT_EDGES_ONLY:
			view.setEdgeSelection(true);
			break;
		case SELECT_NODES_AND_EDGES:
			view.setNodeSelection(true);
			view.setEdgeSelection(true);
			break;
		}
	}

	public static ArrayList<Layout> getLayouts()
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
	
}
