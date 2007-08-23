// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $

//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.data.HGVNetworkUtilities;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

import ViolinStrings.Strings;


public class AlphabeticalSelectionAction 
  extends 
    HGVAction  
  implements 
    ActionListener {
   
  JDialog dialog;
  JButton search, cancel;
  JTextField searchField;


  public AlphabeticalSelectionAction () {
    super("By Name...");
    setPreferredMenu( "Select.Nodes" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_F, ActionEvent.CTRL_MASK );
  }


  public void actionPerformed (ActionEvent e) {

    if ( e.getSource() == cancel ) {
      dialog.setVisible( false );
      return;
    }

    if ( e.getSource() == searchField || e.getSource() == search ) {
      String search_string = searchField.getText();
      HGVNetworkUtilities.selectNodesStartingWith( HGViewer.getCurrentNetwork(),
                                                  search_string,
                                                  HGViewer.getCurrentView() );
      return;
    }

    if ( dialog == null )
      createDialog();
    dialog.setVisible( true );
    
  }

  private JDialog createDialog () {

    dialog = new JDialog( GUIUtilities.getFrame(),
                          "Select Nodes By Name",
                          false );
    
    JPanel main_panel = new JPanel();
    main_panel.setLayout( new BorderLayout() );

    JLabel label = new JLabel(  "<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
    main_panel.add( label, BorderLayout.NORTH );


    searchField = new JTextField( 30 );
    searchField.addActionListener( this );
    main_panel.add( searchField, BorderLayout.CENTER );

    JPanel button_panel = new JPanel();
    search = new JButton( "Search" );
    cancel = new JButton( "Cancel" );
    search.addActionListener( this );
    cancel.addActionListener( this );
    button_panel.add( search );
    button_panel.add( cancel );
    main_panel.add( button_panel, BorderLayout.SOUTH );

    dialog.setContentPane( main_panel );
    dialog.pack();
    return dialog;
  }


}

