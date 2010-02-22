//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.util.*;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class DestroySelectedAction extends HGVAction  {

    public DestroySelectedAction () {
        super( ActionManager.DESTROY_SELECTED_NODES_EDGES_ACTION);
    }
    
    public DestroySelectedAction ( boolean label) {
        super(  );
    }
    
    public void actionPerformed (ActionEvent e) {
	   HGVNetworkView view = HGVKit.getCurrentView();
	   view.getSelectionHandler().deleteSelection();
	}//action performed

}

