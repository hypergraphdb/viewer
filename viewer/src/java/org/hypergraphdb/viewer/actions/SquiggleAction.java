package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import phoebe.PGraphView;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.HGViewer;

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
		    PGraphView view = (PGraphView)HGViewer.getCurrentView();
		    if (enabled) {
              HGViewer.enableSquiggle();
              squiggleMode.setText("Disable");
            } else {
              HGViewer.disableSquiggle();
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
            PGraphView view = (PGraphView)HGViewer.getCurrentView();
              view.getSquiggleHandler().clearSquiggles();
      } } ); } } );
    clearSquiggle.setEnabled(false);
    add(clearSquiggle);
  }
}
