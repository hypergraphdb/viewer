//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import phoebe.util.*;

import org.hypergraphdb.viewer.giny.*;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGViewer;
import org.freehep.util.export.ExportDialog;

//-------------------------------------------------------------------------
public class ExportAction extends HGVAction  {

    public final static String MENU_LABEL = "Export As...";
        
    public ExportAction () {
        super (MENU_LABEL);
        setPreferredMenu( "File" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed(ActionEvent e) {
	
      HGViewer.getCurrentView().getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");

      ExportDialog export = new ExportDialog();
      export.showExportDialog( HGViewer.getCurrentView().getCanvas(), "Export view as ...", HGViewer.getCurrentView().getCanvas(), "export" );
      
      HGViewer.getCurrentView().getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);
	
    } // actionPerformed
}

