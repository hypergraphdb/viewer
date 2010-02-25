//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/02/08 19:03:35 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class DeselectAllAction extends HGVAction {
       
    public DeselectAllAction () {
        super(ActionManager.DESELECT_ALL_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK|ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed(ActionEvent e) {
     if(HGVKit.getCurrentView() == null) return;
      HGVKit.getCurrentView().unselectAllEdges();
      HGVKit.getCurrentView().unselectAllNodes();
    }
}

