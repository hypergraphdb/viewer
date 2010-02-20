//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/01/03 17:07:47 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVNetworkNaming;
import org.hypergraphdb.viewer.util.HGVAction;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NewWindowSelectedNodesOnlyAction extends HGVAction {

	public NewWindowSelectedNodesOnlyAction() {
		super(ActionManager.NEW_WINDOW_SELECTED_NODES_ONLY_ACTION);
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		
		HGVNetwork current_network = HGVKit.getCurrentNetwork();
		if(current_network == null || HGVKit.isEmbeded()) return;
		HGVNetworkView current_network_view = null;
		if (HGVKit.viewExists(current_network))
			current_network_view = HGVKit.getNetworkView(current_network);
		Set<FNode> nodes = current_network.getFlagger().getFlaggedNodes();
		Set<FEdge> edges = Collections.emptySet();//current_network.getConnectingEdges(n_idx);
		HGVNetwork new_network = HGVKit.createNetwork(nodes, edges, 
		        current_network.getHyperGraph(), 
		        current_network);
		new_network.setTitle(HGVNetworkNaming
				.getSuggestedSubnetworkTitle(current_network));
		HGVNetworkView new_view = HGVKit.getNetworkView(new_network);
		if (new_view == null)return;

		if (current_network_view != null) {

			Iterator<FNode> i = new_network.nodesIterator();
			while (i.hasNext()) {
				FNode node = i.next();
				new_view.getNodeView(node).setOffset(
						current_network_view.getNodeDoubleProperty(node,
								HGVNetworkView.NODE_X_POSITION),
						current_network_view.getNodeDoubleProperty(node,
								HGVNetworkView.NODE_Y_POSITION));
			}
		}

	}

}
