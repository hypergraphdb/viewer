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
public class UnHideSelectedEdgesAction extends HGVAction  {

    public UnHideSelectedEdgesAction () {
        super (ActionManager.SHOW_ALL_EDGES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_H, ActionEvent.ALT_MASK| ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
      //GinyUtils.unHideSelectedEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
      GinyUtils.unHideAll( HGVKit.getCurrentView() );
    }//action performed
}

