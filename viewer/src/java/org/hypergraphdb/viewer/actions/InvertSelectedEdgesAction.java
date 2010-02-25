//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGVKit;

import phoebe.PEdgeView;
//-------------------------------------------------------------------------
public class InvertSelectedEdgesAction extends HGVAction {
    
    public InvertSelectedEdgesAction () {
        super(ActionManager.INVERT_EDGE_SELECTION_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
        HGVNetworkView view = HGVKit.getCurrentView();
        List<PEdgeView> flaggedEdgeIndices = view.getSelectedEdges();
	    for(PEdgeView ev: view.getEdgeViews())
	        ev.select();
	    for(PEdgeView ev : flaggedEdgeIndices)
	        ev.unselect();
	 
    }
}

