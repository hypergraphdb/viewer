package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.*;
import java.awt.event.*;

public class DestroyNetworkAction extends HGVAction {

  public DestroyNetworkAction () {
    super( ActionManager.DESTROY_NETWORK_ACTION );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
  }
                               
  public DestroyNetworkAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyCurrentNetwork();
  }

  public static void destroyCurrentNetwork () {
  	HGVKit.destroyNetwork( HGVKit.getCurrentNetwork() );
  }
}
