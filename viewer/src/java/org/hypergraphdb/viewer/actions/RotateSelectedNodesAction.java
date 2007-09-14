//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.dialogs.PhoebeNodeControl;
//-------------------------------------------------------------------------
/**
 * Rotates the given selection by the specified amount.
 *
 * added by dramage 2002-08-20
 */
public class RotateSelectedNodesAction extends AbstractAction {
    
    public RotateSelectedNodesAction () {
        super("Rotate Selected Nodes");
    }

    public void actionPerformed (ActionEvent e) {
        PhoebeNodeControl pnc = new PhoebeNodeControl(HGVKit.getCurrentView());
    }
}

