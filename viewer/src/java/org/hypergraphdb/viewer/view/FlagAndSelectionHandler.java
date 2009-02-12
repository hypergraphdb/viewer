//---------------------------------------------------------------------------
//  $Revision: 1.1 $ 
//  $Date: 2005/12/25 01:22:42 $
//  $Author: bobo $
//---------------------------------------------------------------------------
package org.hypergraphdb.viewer.view;

//---------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.data.FlagFilter;
import org.hypergraphdb.viewer.data.FlagEventListener;
import org.hypergraphdb.viewer.data.FlagEvent;
import org.hypergraphdb.viewer.event.HGVNetworkChangeEvent;
import org.hypergraphdb.viewer.event.HGVNetworkChangeListener;

import phoebe.PEdgeView;
import phoebe.PGraphView;
import phoebe.PNodeView;

//---------------------------------------------------------------------------
/**
 * This class synchronizes the flagged status of nodes and edges as held by a
 * FlagFilter object of a network with the selection status of the corresponding
 * node and edge views in a GraphView. An object will be selected in the view if
 * the matching object is flagged in the FlagFilter.
 */
public class FlagAndSelectionHandler implements FlagEventListener
//,HGVNetworkChangeListener
{
	FlagFilter flagFilter;
	PGraphView view;

	/**
	 * Standard constructor takes the flag filter and the view that should be
	 * synchronized. On construction, this object will synchronize the filter
	 * and view by turning on flags or selections that are currently on in one
	 * of the two objects.
	 */
	public FlagAndSelectionHandler(FlagFilter flagFilter, PGraphView view)
	{
		this.flagFilter = flagFilter;
		this.view = view;
		syncFilterAndView();
		flagFilter.addFlagEventListener(this);
	}

	/**
	 * Synchronizes the filter and view of this object by selecting every object
	 * that is currently flagged and vice versa.
	 */
	private void syncFilterAndView()
	{
		Set<FNode> flaggedNodes = flagFilter.getFlaggedNodes();
		Set<FEdge> flaggedEdges = flagFilter.getFlaggedEdges();
		List<PNodeView> selectedNodes = view.getSelectedNodes();
		List<PEdgeView> selectedEdges = view.getSelectedEdges();
		// select all nodes that are flagged but not currently selected
		for (FNode n : flaggedNodes)
		{
			PNodeView nv = view.getNodeView(n);
			if (nv == null || nv.isSelected()) continue;
			nv.setSelected(true);
		}
		// select all edges that are flagged but not currently selected
		for (FEdge e : flaggedEdges)
		{
			PEdgeView ev = view.getEdgeView(e);
			if (ev == null || ev.isSelected()) continue;
			ev.setSelected(true);
		}
		// flag all nodes that are selected but not currently flagged
		for (PNodeView nv : selectedNodes)
		{
			FNode node = nv.getNode();
			flagFilter.setFlagged(node, true); // does nothing if already
												// flagged
		}
		// flag all edges that are selected but not currently flagged
		for (PEdgeView ev : selectedEdges)
		{
			FEdge edge = ev.getEdge();
			flagFilter.setFlagged(edge, true); // does nothing if already
		}
	}

	/**
	 * Responds to selection events from the view by setting the matching
	 * flagged state in the FlagFilter object.
	 */
//	public void networkChanged(HGVNetworkChangeEvent event)
//	{
//		// GINY bug: the event we get frequently has the correct indices
//		// but incorrect FNode and FEdge objects. For now we get around this
//		// by converting indices to graph objects ourselves
//		PGraphView source = (PGraphView) event.getSource();
//		if (event.isNodesSelectedType())
//		{
//			// System.out.println( "Nodes slected type:" );
//			// System.out.println( "FlagAndSelectionHandler: "+event);
//			// System.out.println( event.getSelectedNodeIndices()+" <- nodes
//			// selected" );
//			// int[] nodes = event.getSelectedNodeIndices();
//			// for ( int i = 0; i < nodes.length; ++i ) {
//			// System.out.println( "Selected mnode: "+nodes[i]);
//			// }
//			// FNode[] selNodes = event.getSelectedNodes();
//			// List selList = Arrays.asList(selNodes);
//			FNode[] selIndices = event.getSelectedNodes();
//			List selList = new ArrayList();
//			for (int index = 0; index < selIndices.length; index++)
//			{
//				FNode node = selIndices[index];
//				// System.out.println( "Adding node: "+node);
//				selList.add(node);
//			}
//			// System.out.println( "Contents of selList: " );
//			// for( Iterator i = selList.iterator(); i.hasNext(); ) {
//			// System.out.println( "NOde: "+i.next() );
//			// }
//			flagFilter.setFlaggedNodes(selList, true);
//		} else if (event.isNodesUnselectedType())
//		{
//			// FNode[] unselNodes = event.getUnselectedNodes();
//			// List unselList = Arrays.asList(unselNodes);
//			// System.out.println( "nodes UNse;ected" );
//			FNode[] unselIndices = event.getUnselectedNodes();
//			List unselList = new ArrayList();
//			for (int index = 0; index < unselIndices.length; index++)
//			{
//				FNode node = unselIndices[index];
//				unselList.add(node);
//				// System.out.println( "Unselected node:"+node+"
//				// "+node.getRootGraphIndex() );
//			}
//			flagFilter.setFlaggedNodes(unselList, false);
//		} else if (event.isEdgesSelectedType())
//		{
//			// FEdge[] selEdges = event.getSelectedEdges();
//			// List selList = Arrays.asList(selEdges);
//			FEdge[] selIndices = event.getSelectedEdges();
//			List selList = new ArrayList();
//			for (int index = 0; index < selIndices.length; index++)
//			{
//				FEdge edge = (selIndices[index]);
//				selList.add(edge);
//			}
//			flagFilter.setFlaggedEdges(selList, true);
//		} else if (event.isEdgesUnselectedType())
//		{
//			// FEdge[] unselEdges = event.getUnselectedEdges();
//			// List unselList = Arrays.asList(unselEdges);
//			FEdge[] unselIndices = event.getUnselectedEdges();
//			List unselList = new ArrayList();
//			for (int index = 0; index < unselIndices.length; index++)
//			{
//				FEdge edge = (unselIndices[index]);
//				unselList.add(edge);
//			}
//			flagFilter.setFlaggedEdges(unselList, false);
//		}
//	}
//
	/**
	 * Responds to events indicating a change in the flagged state of one or
	 * more nodes or edges. Sets the corresponding selection state for views of
	 * those objects in the graph view.
	 */
	public void onFlagEvent(FlagEvent event)
	{
		if (event.getTargetType() == FlagEvent.SINGLE_NODE)
		{// single node
			setNodeSelected((FNode) event.getTarget(), event.getEventType());
		} else if (event.getTargetType() == FlagEvent.SINGLE_EDGE)
		{// single edge
			setEdgeSelected((FEdge) event.getTarget(), event.getEventType());
		} else if (event.getTargetType() == FlagEvent.NODE_SET)
		{// multiple nodes
			Set nodeSet = (Set) event.getTarget();
			for (Iterator iter = nodeSet.iterator(); iter.hasNext();)
			{
				FNode node = (FNode) iter.next();
				setNodeSelected(node, event.getEventType());
			}
		} else if (event.getTargetType() == FlagEvent.EDGE_SET)
		{// multiple edges
			Set edgeSet = (Set) event.getTarget();
			for (Iterator iter = edgeSet.iterator(); iter.hasNext();)
			{
				FEdge edge = (FEdge) iter.next();
				setEdgeSelected(edge, event.getEventType());
			}
		} else
		{// unexpected target type
			return;
		}
	}

	/**
	 * Helper method to set selection for a node view.
	 */
	private void setNodeSelected(FNode node, boolean selectOn)
	{
		PNodeView nodeView = view.getNodeView(node);
		if (nodeView == null)
		{
			return;
		} // sanity check
		// Giny fires a selection event even if there's no change in state
		// we trap this by only requesting a selection if there's a change
		if (nodeView.isSelected() != selectOn)
		{
			nodeView.setSelected(selectOn);
		}
	}

	/**
	 * Helper method to set selection for an edge view.
	 */
	private void setEdgeSelected(FEdge edge, boolean selectOn)
	{
		PEdgeView edgeView = view.getEdgeView(edge);
		if (edgeView == null)
		{
			return;
		} // sanity check
		// Giny fires a selection event even if there's no change in state
		// we trap this by only requesting a selection if there's a change
		if (edgeView.isSelected() != selectOn)
		{
			edgeView.setSelected(selectOn);
		}
	}
}
