//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.util.*;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class DestroySelectedAction extends HGVAction  {

    public DestroySelectedAction () {
        super( ActionManager.DESTROY_SELECTED_NODES_EDGES_ACTION);
    }
    
    public DestroySelectedAction ( boolean label) {
        super(  );
    }
    
    public void actionPerformed (ActionEvent e) {
	HGVNetwork gp =HGVKit.getCurrentNetwork();
	Set<FNode> flaggedNodes = gp.getFlagger().getFlaggedNodes();
	Set<FEdge> flaggedEdges = gp.getFlagger().getFlaggedEdges();
	FNode [] hiddenNodeIndices = flaggedNodes.toArray(new FNode [flaggedNodes.size()]);
	FEdge [] hiddenEdgeIndices = flaggedEdges.toArray(new FEdge [flaggedEdges.size()]);
	
	// unflag then hide nodes from graph perspective
	gp.getFlagger().unflagAllNodes();
	gp.getFlagger().unflagAllEdges();
	gp.removeEdges(hiddenEdgeIndices);
	gp.removeNodes(hiddenNodeIndices);
	    
    }//action performed

}

