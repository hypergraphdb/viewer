//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class DeSelectAllEdgesAction extends HGVAction  {

    public DeSelectAllEdgesAction () {
        super (ActionManager.DESELECT_ALL_EDGES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.ALT_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
      //GinyUtils.deselectAllEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	if(HGVKit.getCurrentView() != null)
    	  HGVKit.getCurrentView().unselectAllEdges();
    }//action performed
}

