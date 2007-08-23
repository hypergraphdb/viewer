package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.*;
import java.awt.event.*;

public class DestroyNetworkViewAction extends HGVAction {

  public DestroyNetworkViewAction () {
    super( "Destroy View" );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK) ;
}
                               
  public DestroyNetworkViewAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyViewFromCurrentNetwork();
  }

  public static void destroyViewFromCurrentNetwork () {
  	HGViewer.destroyNetworkView( HGViewer.getCurrentNetwork() );
  }
}
