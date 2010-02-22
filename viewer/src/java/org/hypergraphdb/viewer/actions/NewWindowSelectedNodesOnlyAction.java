//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/01/03 17:07:47 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Set;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.util.HGVNetworkNaming;

public class NewWindowSelectedNodesOnlyAction extends HGVAction {

	public NewWindowSelectedNodesOnlyAction() {
		super(ActionManager.NEW_WINDOW_SELECTED_NODES_ONLY_ACTION);
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		
	    HGVNetworkView view = HGVKit.getCurrentView();
		if(view == null || HGVKit.isEmbeded()) return;
		Set<FNode> nodes = view.getFlaggedNodes();
		Set<FEdge> edges = Collections.emptySet();//current_network.getConnectingEdges(n_idx);
		HGVNetworkView new_view = 
		    HGVKit.createNetworkView( view.getHyperGraph(), nodes, edges);
		new_view.setIdentifier(HGVNetworkNaming
				.getSuggestedSubnetworkTitle(view));
//		HGVNetworkView new_view = HGVKit.getNetworkView(new_network);
//		if (new_view == null)return;
//
//		if (current_network_view != null) {
//
//			Iterator<FNode> i = new_network.nodesIterator();
//			while (i.hasNext()) {
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
