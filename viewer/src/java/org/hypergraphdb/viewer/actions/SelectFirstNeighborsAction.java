//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.HGVNetwork;
import giny.model.Edge;
import giny.model.Node;
//-------------------------------------------------------------------------
/**
 *  select every first neighbor (directly connected nodes) of the currently
 *  selected nodes.
 */
public class SelectFirstNeighborsAction extends HGVAction {
    
    public SelectFirstNeighborsAction () { 
        super ("First neighbors of selected nodes"); 
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_F6,0 );
    }
    public void actionPerformed (ActionEvent e) {
      HGVNetwork net = HGViewer.getCurrentNetwork();
      Set set = net.getFlagger().getFlaggedNodes();
      Set new_set = new HashSet();
      for (Object o: set){
    	 // System.out.println("N:" + o);
    	 List l = net.getAdjacentEdgesList(((Node)o),true, true, true);
    	 for(Object edge: l){
    		 //System.out.println("E:" + edge + ":" + ((Edge)edge).getTarget());
        	 new_set.add(((Edge)edge).getTarget());
        	 new_set.add(((Edge)edge).getSource());
    	 }
	  }
      net.getFlagger().setFlaggedNodes(new_set, true);
    } // actionPerformed
} // SelectFirstNeighborsAction

