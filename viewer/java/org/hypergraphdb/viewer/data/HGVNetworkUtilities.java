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

// $Revision: 1.2 $
// $Date: 2006/02/08 19:03:35 $
// $Author: bizi $

package org.hypergraphdb.viewer.data;

import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

import giny.model.*;
import giny.view.*;

import org.hypergraphdb.viewer.HGVNode;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.*;
import ViolinStrings.Strings;

//-------------------------------------------------------------------------
/**
 * This class provides static methods that operate on a HGVNetwork to perform
 * various useful tasks. Many of these methods make assumptions about the
 * data types that are available in the node and edge attributes of the network.
 */
public class HGVNetworkUtilities {
//-------------------------------------------------------------------------

//-------------------------------------------------------------------------
/**
 * Selects every node in the current view whose canonical name, label, or
 * any known synonym starts with the string specified by the second argument.
 * Note that synonyms are only available if a naming server is available.
 *
 * This method does not change the selection state of any node that doesn't
 * match the given key, allowing multiple selection queries to be concatenated.
 */
public static boolean selectNodesStartingWith(HGVNetwork network, String key,
                                              HGVNetworkView networkView) {
    if (network == null || key == null || networkView == null) {return false;}
    key = key.toLowerCase();
    boolean found = false;
   

    GraphPerspective theGraph = (GraphPerspective) network;
    for (Iterator i = theGraph.nodesIterator(); i.hasNext(); ) {
        HGVNode node = (HGVNode)i.next();
        
        HyperGraph hg = HGViewer.getCurrentNetwork().getHyperGraph();
        String nodeLabel = "" + hg.get(node.getHandle());
        //TODO: ???maybe we should call the calculator 
        //or search based on a class in this action too;
        boolean matched = false;
        if (nodeLabel != null &&  Strings.isLike(  nodeLabel, key, 0, true ) ) {
            matched = true;
	        found = true;
        } 
        if (matched) {//only select matches, don't deselect ones that don't match
            networkView.getNodeView(node).setSelected(matched);
        }
    }
    return found;
}
//-------------------------------------------------------------------------
}

