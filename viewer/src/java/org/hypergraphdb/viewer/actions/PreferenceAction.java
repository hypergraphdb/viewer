//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------

package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.dialogs.AppConfigPanel;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

public class PreferenceAction extends HGVAction 
{

    public PreferenceAction () {
        super (ActionManager.PREFERENCES_ACTION);
     }

    public void actionPerformed(ActionEvent e) {
    	 AppConfigPanel p = new AppConfigPanel();
    	 DialogDescriptor dd = new DialogDescriptor(GUIUtilities
    				.getFrame(), p,
    				"HGVKit Properties");
    	 DialogDisplayer.getDefault().notify(dd);
    	 return;
    } // actionPerformed
}

