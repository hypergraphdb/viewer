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
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.freehep.util.export.ExportDialog;

//-------------------------------------------------------------------------
public class ExportAction extends HGVAction
{
	public ExportAction()
	{
		super(ActionManager.EXPORT_ACTION);
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK
				| ActionEvent.SHIFT_MASK);
	}

	public void actionPerformed(ActionEvent e)
	{
		HGVNetworkView view = HGVKit.getCurrentView();
		if (view == null) return;
		view.getCanvas().getCamera().addClientProperty(
				PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");
		ExportDialog export = new ExportDialog();
		export.showExportDialog(view.getCanvas(), "Export view as ...", view
				.getCanvas(), "export");
		view.getCanvas().getCamera().addClientProperty(
				PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);
	} // actionPerformed
}
