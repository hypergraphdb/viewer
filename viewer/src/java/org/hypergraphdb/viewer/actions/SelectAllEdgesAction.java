//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class SelectAllEdgesAction extends HGVAction  {

  public SelectAllEdgesAction () {
        super ("Select all edges");
        setPreferredMenu( "Select.Edges" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.selectAllEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	HGViewer.getCurrentNetwork().getFlagger().flagAllEdges();
    }//action performed
}

