//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class DeSelectAllEdgesAction extends HGVAction  {

    public DeSelectAllEdgesAction () {
        super ("Deselect all edges");
        setPreferredMenu( "Select.Edges" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.ALT_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
      //GinyUtils.deselectAllEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	HGViewer.getCurrentNetwork().getFlagger().unflagAllEdges();
    }//action performed
}

