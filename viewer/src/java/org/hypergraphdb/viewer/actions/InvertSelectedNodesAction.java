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
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends HGVAction {
    
    public InvertSelectedNodesAction () {
        super("Invert selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed (ActionEvent e) {
	HGVNetwork cyNetwork = HGViewer.getCurrentNetwork();
	Set set = cyNetwork.getFlagger().getFlaggedNodes();
	cyNetwork.getFlagger().flagAllNodes();
	cyNetwork.getFlagger().setFlaggedNodes(set,false);
    }
}

