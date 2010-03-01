//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.List;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PNodeView;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends HGVAction {
    
    public InvertSelectedNodesAction () {
        super(ActionManager.INVERT_NODE_SELECTION_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed (ActionEvent e) {
        GraphView view = HGVKit.getCurrentView();
        List<PNodeView> selNodes = view.getSelectedNodes();
        for(PNodeView ev: view.getNodeViews())
            ev.select();
        for(PNodeView ev: selNodes)
                     ev.unselect();
       
    }
}

