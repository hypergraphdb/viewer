package org.hypergraphdb.viewer.dialogs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGVKit;

public class SquiggleMenu extends JMenu 
{
    private JMenuItem squiggleMode;
    private JMenuItem clearSquiggle;
    private boolean enabled;

    public SquiggleMenu  () {
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
