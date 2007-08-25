//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;

public class ZoomAction extends HGVAction {
  
    double factor;
    
    public ZoomAction( double factor) {
        super ((factor <= 1) ? "Zoom Out": "Zoom In");
        this.factor = factor;
    }
    
  public void zoom () {
  	HGViewer.getCurrentView().setZoom( factor );
  }

    public void actionPerformed (ActionEvent e) {
    	HGViewer.getCurrentView().setZoom( factor );
    }
}

