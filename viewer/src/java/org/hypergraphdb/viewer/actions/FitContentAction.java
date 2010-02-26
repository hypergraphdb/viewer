//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class FitContentAction extends HGVAction {
      
    public FitContentAction () {
        super("Zoom To Fit");
        setAcceleratorCombo(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
    }
    
    public void actionPerformed(ActionEvent e) {
    	HGVNetworkView view =HGVKit.getCurrentView();
      if(view != null)
        view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getFullBounds(), true, 50l );
   }
}

