//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------

package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import org.hypergraphdb.viewer.giny.*;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.util.HGVAction;

public class PrintAction extends HGVAction  {
    
    public final static String MENU_LABEL = "Print...";
    
    public PrintAction () {
        super (MENU_LABEL);
        setPreferredMenu( "File" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed(ActionEvent e) {

    	HGVNetworkView phoebeView = HGViewer.getCurrentView();
    	if(phoebeView != null)
	      phoebeView.getCanvas().getLayer().print();
     	
    } // actionPerformed
}

