//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.model.*;
import giny.view.*;
import java.util.*;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class HideSelectedAction extends HGVAction  {

    public HideSelectedAction () {
        super( "Hide Selected" );
    }

   public HideSelectedAction ( boolean label) {
        super(  );
        
    }
    
    public void actionPerformed (ActionEvent e) {
        GinyUtils.hideSelectedNodes( HGVKit.getCurrentView() );
        GinyUtils.hideSelectedEdges( HGVKit.getCurrentView() );
    }//action performed
}

