//-------------------------------------------------------------------------
// $Revision: 1.3 $
// $Date: 2006/02/27 19:59:18 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.util.HGVNetworkNaming;
import org.hypergraphdb.viewer.util.HGVAction;

import org.hypergraphdb.viewer.FEdge;

import phoebe.PEdgeView;
import phoebe.PNodeView;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
		GraphView view =  HGVKit.getCurrentView();
		Collection<FNode> nodes = new ArrayList<FNode>();
		for(PNodeView v :  view.getSelectedNodes())
		    nodes.add(v.getNode());
		Collection<FEdge> edges = new ArrayList<FEdge>();
		for(PEdgeView v : view.getSelectedEdges())
		    edges.add(v.getEdge());
       	
		HGViewer new_view = HGVKit.createHGVComponent(view.getHyperGraph(), nodes, edges);
		new_view.getView().setIdentifier(HGVNetworkNaming.getSuggestedSubnetworkTitle(view));
	}
}
