//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class SelectAllNodesAction extends HGVAction  {

    public SelectAllNodesAction () {
        super (ActionManager.SELECT_ALL_NODES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.selectAllNodes( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	if(HGViewer.getCurrentNetwork() != null)
    	   HGViewer.getCurrentNetwork().getFlagger().flagAllNodes();
    }//action performed
}

