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
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PEdgeView;
import phoebe.PNodeView;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends HGVAction {
    
    public InvertSelectedNodesAction () {
        super(ActionManager.INVERT_NODE_SELECTION_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed (ActionEvent e) {
        HGVNetworkView view = HGVKit.getCurrentView();
        Set<FNode> flaggedEdgeIndices = view.getFlaggedNodes();
        for(PNodeView ev: view.getNodeViews())
            ev.select();
        for(FNode edge: flaggedEdgeIndices)
        {
            PNodeView ev = view.getNodeView(edge);
            if(ev != null)
                ev.unselect();
        }
    }
}

