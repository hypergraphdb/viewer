package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.*;
import org.hypergraphdb.viewer.util.*;

/**
 *
 */
public class RecordExplorerAction extends HGVAction
{
    
    /** Creates a new instance of RecordExplorerAction */
    public RecordExplorerAction()
    {
        super("Record Explorer");
        setAcceleratorCombo( KeyEvent.VK_F5, 0 );
    }
    
    
    public void actionPerformed( ActionEvent ev )
    {
        RecordExplorerPanel panel = new RecordExplorerPanel();
        DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(
        		HGViewer.getCurrentView().getComponent()), panel, "RecordExplorer");
        d.setModal(true);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        DialogDisplayer.getDefault().notify(d);
       
    }//action performed
    
}
