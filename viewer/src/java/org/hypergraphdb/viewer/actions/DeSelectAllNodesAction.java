//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
//-------------------------------------------------------------------------
public class DeSelectAllNodesAction extends HGVAction  {

    public DeSelectAllNodesAction () {
        super (ActionManager.DESELECT_ALL_NODES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
    	if(HGVKit.getCurrentView() != null)
          HGVKit.getCurrentView().unflagAllNodes();
    }
}

