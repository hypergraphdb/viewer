//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class SelectAllAction extends HGVAction  {

    public SelectAllAction () {
        super (ActionManager.SELECT_ALL_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
        //GinyUtils.selectAllNodes( org.hypergraphdb.viewer.getCurrentNetworkView() );
        //GinyUtils.selectAllEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	if(HGVKit.getCurrentNetwork() == null) return;
    	HGVKit.getCurrentNetwork().getFlagger().flagAllNodes();
        HGVKit.getCurrentNetwork().getFlagger().flagAllEdges();
    }//action performed
}

