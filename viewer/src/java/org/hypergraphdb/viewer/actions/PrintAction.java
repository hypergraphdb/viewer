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

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.util.*;

public class PrintAction extends HGVAction  {
    
    public PrintAction () {
        super (ActionManager.PRINT_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed(ActionEvent e) {

    	GraphView phoebeView = HGVKit.getCurrentView();
    	if(phoebeView != null)
	      phoebeView.getCanvas().getLayer().print();
     	
    } // actionPerformed
}

