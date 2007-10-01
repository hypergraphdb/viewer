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
public class UnHideSelectedNodesAction extends HGVAction  {

    public UnHideSelectedNodesAction () {
        super (ActionManager.SHOW_ALL_NODES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_U, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.unHideSelectedNodes( org.hypergraphdb.viewer.getCurrentNetworkView() );
      //GinyUtils.unHideSelectedEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
      if(HGVKit.getCurrentView() != null)
    	 GinyUtils.unHideAll( HGVKit.getCurrentView() );
    }//action performed
}

