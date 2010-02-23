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

/**
 */
public class DeleteSelectedAction extends AbstractAction {
    
    
    public DeleteSelectedAction() {
        super("Delete Selected Nodes and Edges");
    }
    
    public void actionPerformed(ActionEvent e) { 
                       
    	HGVNetworkView view = HGVKit.getCurrentView();
    	 // get the Selected node and edge indices
    	List<PNodeView> selected_nodeViews = view.getSelectedNodes();
        List<PEdgeView> selected_edgeViews = view.getSelectedEdges();

        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodeViews );
        view.hideGraphObjects( selected_edgeViews );
        
        view.redrawGraph();
     } // actionPerformed
} // inner class DeleteSelectedAction
