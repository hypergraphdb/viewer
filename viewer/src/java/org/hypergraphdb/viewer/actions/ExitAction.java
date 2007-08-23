// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $


package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;


public class ExitAction extends HGVAction {
       
  public ExitAction () {
    super("Exit");
    setPreferredMenu( "File" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_Q, ActionEvent.CTRL_MASK );
  }
    
  public void actionPerformed (ActionEvent e) {
  	HGViewer.exit();
  }
}

