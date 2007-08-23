// ShrinkExpandGraph plugin

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//--------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//--------------------------------------------------------------------------
package org.hypergraphdb.viewer.dialogs;
//--------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.*;

import giny.view.*;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;

/**
 * This class shifts the nodes to shrink or expand the graph:
 * it averages the coordinates of all the nodes to find the center
 * it translates the graph to a center at (0,0)
 * it multiplies each node coordinate by a factor m
 * it translates the graph back to the original center
 */
public class ShrinkExpandGraph extends HGVAction {
    
  protected double m;
  
  ShrinkExpandGraph ( String change, double m ) {
    super (change);
    setPreferredMenu( "Layout" );
    this.m = m;
  }
   
  public void actionPerformed(ActionEvent e) {
  
    // sum of coordinates
    double sx = 0;
    double sy = 0;
    
    // coordinates of center of graph
    double cx = 0;
    double cy = 0;
      
    // coordinates with graph centered at (0,0)
    double nx;
    double ny;
  

    HGVNetworkView parent = HGViewer.getCurrentView();
    //loop through each node to add up all x and all y coordinates
    for (Iterator i = parent.getNodeViewsIterator(); i.hasNext(); ){
	    NodeView nodeView = (NodeView)i.next();
	    // get coordinates of node
	    double ax = nodeView.getXPosition();
	    double ay = nodeView.getYPosition();
	    // sum up coordinates of all the nodes
	    sx += ax;
	    sy += ay;
    }
    
    // set new coordinates of each node at center (0,0), shrink, then return to
    // original center at (cx, cy)
    for ( Iterator i = parent.getNodeViewsIterator(); i.hasNext(); ){
      NodeView nodeView = (NodeView)i.next();
	   nodeView.setXPosition(m*((nodeView.getXPosition())-cx) + cx);
	   nodeView.setYPosition(m*((nodeView.getYPosition())-cy) + cy);
    }
	
    // remove bends
    for ( Iterator i = parent.getEdgeViewsIterator(); i.hasNext(); ){
      EdgeView edgeView = ( EdgeView )i.next();
      edgeView.getBend().removeAllHandles();
    }

  }//Action Performed
  
}//ShrinkExpandGraph class


    
    

	
	
       

    
    
    
    
    


	

	

	


