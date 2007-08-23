//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------

package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.dialogs.AppConfigPanel;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.giny.*;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.util.*;

public class PreferenceAction extends HGVAction  {

    public final static String MENU_LABEL = "Preferences...";
    
     public PreferenceAction () {
        super (MENU_LABEL);
        setPreferredMenu( "Edit" );
    }

    public void actionPerformed(ActionEvent e) {
    	 AppConfigPanel p = new AppConfigPanel();
    	 DialogDescriptor dd = new DialogDescriptor(GUIUtilities
    				.getFrame(), p,
    				"HGViewer Properties");
    	 DialogDisplayer.getDefault().notify(dd);
    	 return;
    } // actionPerformed
}

