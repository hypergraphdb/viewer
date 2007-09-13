//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGVKit;
//-------------------------------------------------------------------------
public class InvertSelectedEdgesAction extends HGVAction {
    
    public InvertSelectedEdgesAction () {
        super(ActionManager.INVERT_EDGE_SELECTION_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
	HGVNetwork cyNetwork = HGVKit.getCurrentNetwork();
	Set flaggedEdgeIndices = cyNetwork.getFlagger().getFlaggedEdges();
	cyNetwork.getFlagger().flagAllEdges();
	cyNetwork.getFlagger().setFlaggedEdges(flaggedEdgeIndices,false);
    }
}

