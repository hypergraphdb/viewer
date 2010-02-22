//-------------------------------------------------------------------------
// $Revision: 1.3 $
// $Date: 2006/02/27 19:59:18 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVNetworkNaming;
import org.hypergraphdb.viewer.util.HGVAction;

import org.hypergraphdb.viewer.FEdge;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends HGVAction
{
	public NewWindowSelectedNodesEdgesAction()
	{
		super(ActionManager.NEW_WINDOW_SELECTED_NODES_EDGES_ACTION);
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
				| ActionEvent.SHIFT_MASK);
	}

	public void actionPerformed(ActionEvent e)
	{
	    // save the vizmapper catalog
		if (HGVKit.isEmbeded()) return;
		HGVNetworkView view =  HGVKit.getCurrentView();
		Collection<FNode> nodes = view.getFlaggedNodes();
		Collection<FEdge> edges = view.getFlaggedEdges();
		HGVNetworkView new_view = HGVKit.createNetworkView(view.getHyperGraph(), nodes, edges);
		new_view.setIdentifier(HGVNetworkNaming
				.getSuggestedSubnetworkTitle(view));
		//String title = " selection";
//		HGVNetworkView new_view = HGVKit.getNetworkView(new_network);
//		if (new_view == null) return;
//		if (current_network_view != null)
//		{
//			Iterator<FNode> i = new_network.nodesIterator();
//			while (i.hasNext())
//			{
//				FNode node = i.next();
//				new_view.getNodeView(node).setOffset(
//						current_network_view.getNodeDoubleProperty(node,
//								HGVNetworkView.NODE_X_POSITION),
//						current_network_view.getNodeDoubleProperty(node,
//								HGVNetworkView.NODE_Y_POSITION));
//			}
//		}
	}
}
