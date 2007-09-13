//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.model.*;
import giny.view.*;
import java.util.*;

import org.hypergraphdb.viewer.ActionManager;
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
	Set flaggedNodes = gp.getFlagger().getFlaggedNodes();
	Set flaggedEdges = gp.getFlagger().getFlaggedEdges();
	int [] hiddenNodeIndices = new int [flaggedNodes.size()];
	int [] hiddenEdgeIndices = new int [flaggedEdges.size()];
	
	int j=0;
	for (Iterator i = flaggedNodes.iterator(); i.hasNext(); ) {
	    hiddenNodeIndices[j++] = gp.getIndex((Node) i.next());
	}
	j=0;
	for (Iterator i = flaggedEdges.iterator(); i.hasNext(); ) {
	    hiddenEdgeIndices[j++] = gp.getIndex((Edge) i.next());
	}

	// unflag then hide nodes from graph perspective
	gp.getFlagger().unflagAllNodes();
	gp.getFlagger().unflagAllEdges();
	gp.hideEdges(hiddenEdgeIndices);
	gp.hideNodes(hiddenNodeIndices);
	    
    }//action performed

}

