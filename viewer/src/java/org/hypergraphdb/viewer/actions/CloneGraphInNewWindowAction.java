//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/01/03 17:07:47 $
// $Author: bizi $
//-------------------------------------------------------------------------

package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;

import java.awt.event.ActionEvent;

public class CloneGraphInNewWindowAction extends HGVAction {

  public CloneGraphInNewWindowAction () {
    super(ActionManager.NEW_WINDOW_CLONE_WHOLE_GRAPH_ACTION);
  }

  public void actionPerformed(ActionEvent e) {
//TODO:???
//    HGVNetwork current_network = HGVKit.getCurrentNetwork();
//    if(current_network == null || HGVKit.isEmbeded()) return;
//	
//    HGVNetwork new_network = HGVKit.createNetwork( current_network.getNodeIndicesArray(),
//                                                     current_network.getEdgeIndicesArray(),
//                                                     current_network.getHyperGraph());
//    new_network.setTitle(current_network.getTitle() + " copy");
//    String title = " selection";
//    HGVKit.createNetworkView( new_network, title );
  }

}

