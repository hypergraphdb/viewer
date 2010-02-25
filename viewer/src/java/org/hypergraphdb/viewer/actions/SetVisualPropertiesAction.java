//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//------------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.visual.ui.PainterPropsPanel;
import org.hypergraphdb.viewer.visual.ui.PaintersPanel;

import org.hypergraphdb.viewer.dialogs.*;

//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends HGVAction   {

  public SetVisualPropertiesAction () {
    super(ActionManager.VISUAL_PROPERTIES_ACTION);
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_V, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK) ;
  }
  
  public void actionPerformed (ActionEvent e) {
	 HGVNetworkView view = HGVKit.getCurrentView();
	 if(view == null) return;
	 PaintersPanel p = new PaintersPanel();
	 p.setView(view);
	 DialogDescriptor dd = new DialogDescriptor(GUIUtilities.getFrame(), p,
				ActionManager.VISUAL_PROPERTIES_ACTION);
	 DialogDisplayer.getDefault().notify(dd);
	 VisualManager.getInstance().save();
	 view.redrawGraph();
  }
}

