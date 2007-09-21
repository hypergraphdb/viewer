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
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import fing.model.FEdge;
import fing.model.FNode;
//-------------------------------------------------------------------------
/**
 *  select every first neighbor (directly connected nodes) of the currently
 *  selected nodes.
 */
public class SelectFirstNeighborsAction extends HGVAction {
    
    public SelectFirstNeighborsAction () { 
        super (ActionManager.SELECTED_FIRST_NEIGHBORS_ACTION); 
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_F6,0 );
    }
    public void actionPerformed (ActionEvent e) {
      HGVNetwork net = HGVKit.getCurrentNetwork();
      if(net == null) return;
      Set<FNode> set = net.getFlagger().getFlaggedNodes();
      Set<FNode> new_set = new HashSet<FNode>();
      for (FNode o: set){
    	 int[] ids  = net.getAdjacentEdgeIndicesArray(
    			 o.getRootGraphIndex(), true, true, true);
    	 for(int i = 0; i < ids.length; i++){
    		 FEdge edge = net.getEdge(ids[i]);
    		 //System.out.println("E:" + edge + ":" + ((FEdge)edge).getTarget());
        	 new_set.add(edge.getTarget());
        	 new_set.add(edge.getSource());
    	 }
	  }
      net.getFlagger().setFlaggedNodes(new_set, true);
    } // actionPerformed
} // SelectFirstNeighborsAction

