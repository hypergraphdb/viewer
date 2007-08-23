package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.*;
import java.awt.event.*;

public class DestroyNetworkAction extends HGVAction {

  public DestroyNetworkAction () {
    super( "Destroy Network" );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
  }
                               
  public DestroyNetworkAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyCurrentNetwork();
  }

  public static void destroyCurrentNetwork () {
  	HGViewer.destroyNetwork( HGViewer.getCurrentNetwork() );
  }
}
