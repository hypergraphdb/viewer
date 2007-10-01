//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.HGVKit;
//-------------------------------------------------------------------------
public class ShowAllAction extends HGVAction {
       
    public ShowAllAction () {
        super();
    }
    
    public void actionPerformed(ActionEvent e) {
      GinyUtils.unHideAll( HGVKit.getCurrentView() );
        //networkView.redrawGraph(false, true);
    }
}

