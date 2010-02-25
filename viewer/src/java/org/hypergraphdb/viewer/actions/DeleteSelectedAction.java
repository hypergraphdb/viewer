//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

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
    	for(PNodeView v: view.getSelectedNodes())
    	    view.removeNodeView(v.getNode());    
    	for (PEdgeView eview : view.getSelectedEdges())
             view.removeEdgeView(eview);
        
        view.redrawGraph();
     } 
} 
