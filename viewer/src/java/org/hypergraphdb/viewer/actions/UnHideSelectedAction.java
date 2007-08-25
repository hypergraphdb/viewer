//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.*;
//-------------------------------------------------------------------------
public class UnHideSelectedAction extends AbstractAction  {

    public UnHideSelectedAction() {
        super ("Un Hide selection");
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.unHideSelectedNodes(networkView.getView());
      //GinyUtils.unHideSelectedEdges(networkView.getView());
      //networkView.redrawGraph(false, true);	
      GinyUtils.unHideAll( HGViewer.getCurrentView() );
    }//action performed
}

