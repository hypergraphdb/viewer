//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.*;

import org.hypergraphdb.viewer.*;

import phoebe.PEdgeView;
import phoebe.PNodeView;

import fing.model.FEdge;
import fing.model.FNode;


//-------------------------------------------------------------------------
/**
 * Giny version of the original class. Note that the original version was
 * only available when editing mode was enabled, and caused the selected
 * nodes to be permanently removed from the graph (and, necessarily, the view).
 * This version hides the selected nodes from both the graph and the view,
 * as there are currently no methods to remove a node view from the graph view
 * in Giny. The semantics of this and related classes for modifying the
 * graph and view should be clarified.
 */
public class DeleteSelectedAction extends AbstractAction {
    
    
    public DeleteSelectedAction() {
        super("Delete Selected Nodes and Edges");
    }
    
    public void actionPerformed(ActionEvent e) { 
                       
    	HGVNetworkView view = HGVKit.getCurrentView();
    	//FGraphPerspective perspective = view.getGraphPerspective();
        // get the Selected node and edge indices
    	//List<PNodeView> node_indicies = view.getSelectedNodes();
    	//List<PEdgeView> edge_indicies = view.getSelectedEdges();
        //and the node/edge vew objects
        List<PNodeView> selected_nodeViews = view.getSelectedNodes();
        List<PEdgeView> selected_edgeViews = view.getSelectedEdges();

        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodeViews );
        view.hideGraphObjects( selected_edgeViews );
        //perspective.hideEdges( edge_indicies );
        //perspective.hideNodes( node_indicies );
        
        view.redrawGraph();
     } // actionPerformed
} // inner class DeleteSelectedAction
