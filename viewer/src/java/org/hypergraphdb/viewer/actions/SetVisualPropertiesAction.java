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
import org.hypergraphdb.viewer.util.*;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.visual.ui.PainterPropsPanel;
import org.hypergraphdb.viewer.visual.ui.PaintersPanel;

import org.hypergraphdb.viewer.dialogs.*;

//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends HGVAction   {

   
  public SetVisualPropertiesAction () {
    super(ActionManager.VISUAL_PROPERTIES_ACTION);
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_V, ActionEvent.ALT_MASK) ;
  }
  
  /** The constructor that takes a boolean shows no label,
   *  no matter what the value of the boolean actually is.
   *  This makes is appropriate for an icon, but inappropriate
   *  for the pulldown menu system. */
  public SetVisualPropertiesAction ( boolean showLabel) {
    super();
  }
    
  public void actionPerformed (ActionEvent e) {
	 HGVNetworkView view = HGVKit.getCurrentView();
	 if(view == null) return;
	 PaintersPanel p = new PaintersPanel();
	 p.setHyperGraph(view.getNetwork().getHyperGraph());
	 DialogDescriptor dd = new DialogDescriptor(GUIUtilities
				.getFrame(view.getComponent()), p,
				ActionManager.VISUAL_PROPERTIES_ACTION);
	 DialogDisplayer.getDefault().notify(dd);
	 view.redrawGraph();
  }
}

