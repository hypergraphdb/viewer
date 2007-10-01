//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class SelectAllNodesAction extends HGVAction  {

    public SelectAllNodesAction () {
        super (ActionManager.SELECT_ALL_NODES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
      if(HGVKit.getCurrentView() != null)
    	GinyUtils.selectAllNodes(HGVKit.getCurrentView());
        // HGVKit.getCurrentNetwork().getFlagger().flagAllNodes();
    }//action performed
}

