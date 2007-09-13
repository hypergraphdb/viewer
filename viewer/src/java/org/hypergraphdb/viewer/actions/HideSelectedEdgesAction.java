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
public class HideSelectedEdgesAction extends HGVAction {
    
    
    public HideSelectedEdgesAction() {
        super(ActionManager.HIDE_EDGE_SELECTION_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_H, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
       if( HGVKit.getCurrentView() != null)
          GinyUtils.hideSelectedEdges( HGVKit.getCurrentView() );
    }
}

