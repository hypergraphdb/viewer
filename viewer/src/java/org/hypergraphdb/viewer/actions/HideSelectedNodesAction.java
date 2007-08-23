//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class HideSelectedNodesAction extends HGVAction   {
    
    public HideSelectedNodesAction () {
        super("Hide selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_H, ActionEvent.CTRL_MASK );
    }

  public HideSelectedNodesAction ( boolean label ) {
    super();
       
  }

    public void actionPerformed ( ActionEvent e ) {
      GinyUtils.hideSelectedNodes( HGViewer.getCurrentView() );
    }

}
