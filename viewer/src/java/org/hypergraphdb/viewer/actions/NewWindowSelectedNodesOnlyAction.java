//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/01/03 17:07:47 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVNetworkNaming;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NewWindowSelectedNodesOnlyAction extends HGVAction {

	public NewWindowSelectedNodesOnlyAction() {
		super(ActionManager.NEW_WINDOW_SELECTED_NODES_ONLY_ACTION);
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		
		HGVNetwork current_network = HGViewer.getCurrentNetwork();
		if(current_network == null || HGViewer.isEmbeded()) return;
		HGVNetworkView current_network_view = null;
		if (HGViewer.viewExists(current_network)) {
			current_network_view = HGViewer.getNetworkView(current_network);
		} // end of if ()

		Set nodes = current_network.getFlagger().getFlaggedNodes();
		List nlist = new ArrayList(nodes.size());
		for(Object o: nodes)
			nlist.add(o);
		HGVNetwork new_network = HGViewer.createNetwork(nodes, current_network
				.getConnectingEdges(nlist), null, current_network);
		new_network.setTitle(HGVNetworkNaming
				.getSuggestedSubnetworkTitle(current_network));
		HGVNetworkView new_view = HGViewer.getNetworkView(new_network);
		if (new_view == null)
			return;

		if (current_network_view != null) {

			Iterator i = new_network.nodesIterator();
			while (i.hasNext()) {
				Node node = (Node) i.next();
				new_view.getNodeView(node).setOffset(
						current_network_view.getNodeDoubleProperty(node
								.getRootGraphIndex(),
								HGVNetworkView.NODE_X_POSITION),
						current_network_view.getNodeDoubleProperty(node
								.getRootGraphIndex(),
								HGVNetworkView.NODE_Y_POSITION));
			}
		}

	}

}
