package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;

public class SquiggleAction extends JMenu {
  
  private JMenuItem squiggleMode;
  private JMenuItem clearSquiggle;
  private boolean enabled;

  public SquiggleAction  () {
    super("Squiggle");

    squiggleMode = new JMenuItem( new AbstractAction( "Enable" ) {
	  public void actionPerformed ( ActionEvent e ) {
	    // Do this in the GUI Event Dispatch thread...
	    SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
	    	//HGVNetworkView view = HGVKit.getCurrentView();
		    if (enabled) {
              HGVKit.enableSquiggle();
              squiggleMode.setText("Disable");
            } else {
              HGVKit.disableSquiggle();
              squiggleMode.setText("Enable");
            }
            clearSquiggle.setEnabled(enabled);
            enabled = !enabled;
	  } } ); } } ) ;
    add(squiggleMode);
    squiggleMode.setAccelerator( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F12, 0 ) );

    clearSquiggle =  new JMenuItem( new AbstractAction( "Clear" ) {
      public void actionPerformed ( ActionEvent e ) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater( new Runnable() {
          public void run() {
              GraphView view = HGVKit.getCurrentView();
              view.getSquiggleHandler().clearSquiggles();
      } } ); } } );
    clearSquiggle.setEnabled(false);
    add(clearSquiggle);
  }
}
