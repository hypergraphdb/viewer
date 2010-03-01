//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PNodeView;
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
      GraphView net = HGVKit.getCurrentView();
      if(net == null) return;
      Set<FNode> set = new HashSet<FNode>();
      for(PNodeView v :  net.getSelectedNodes())
          set.add(v.getNode());
      Set<FNode> new_set = new HashSet<FNode>();
      for (FNode o: set){
    	 FEdge[] ids  = net.getAdjacentEdges(o, true, true);
    	 for(int i = 0; i < ids.length; i++){
    		 FEdge edge = ids[i];
    		 //System.out.println("E:" + edge + ":" + ((FEdge)edge).getTarget());
        	 new_set.add(edge.getTarget());
        	 new_set.add(edge.getSource());
    	 }
	  }
      net.selectNodes(new_set, true);
    } // actionPerformed
} // SelectFirstNeighborsAction

