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
/**
 * @author Iliana Avila-Campillo <iavila@systemsbiology.org>
 * @version %I%, %G%
 * @since 2.0
 */

package org.hypergraphdb.viewer.view;

import java.util.*;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.event.HGVNetworkChangeEvent;

import phoebe.PEdgeView;
import phoebe.PNodeView;

/**
 * A basic <code>GraphViewHandler</code> that simply reflects <code>GraphPerspective</code>
 * changes on a given <code>GraphView</code>
 */

public class BasicGraphViewHandler implements GraphViewHandler
{

  /**
   * ConstructorLink
   */
  public BasicGraphViewHandler (){}//BasicGraphViewHandler
  
  /**
   * Handles the event as desired by updating the given <code>giny.view.GraphView</code>.
   *
   * @param event the event to handle
   * @param graph_view the <code>giny.view.GraphView</code> that views the 
   * <code>giny.model.GraphPerspective</code> that generated the event and that should
   * be updated as necessary
   */
  public void handleGraphPerspectiveEvent (HGVNetworkChangeEvent event, HGVNetworkView graph_view){
    
    int numTypes = 0; // An event may have more than one type
    
    // FNode Events:
    if(event.isNodesRemovedType()){
      //System.out.println("isNodesHiddenType == " + event.isNodesHiddenType());
      removeGraphViewNodes(graph_view, event.getRemovedNodes());
      numTypes++;
    }

    if(event.isNodesAddedType()){
      //System.out.println("isNodesRestoredType == " + event.isNodesRestoredType());
      restoreGraphViewNodes(graph_view, event.getAddedNodes(), true);
      numTypes++;
    }
    
    // FEdge events:
    if(event.isEdgesRemovedType()){
      //System.out.println("isEdgesHiddenType == " + event.isEdgesHiddenType());
      removeGraphViewEdges(graph_view, event.getRemovedEdges());
      numTypes++;
    }
    
    if(event.isEdgesAddedType()){
      //System.out.println("isEdgesRestoredType == " + event.isEdgesRestoredType());
      restoreGraphViewEdges(graph_view, event.getAddedEdges());
      numTypes++;
    }
    
    if(numTypes == 0){
      //System.err.println("In BasicGraphViewHandler.handleGraphPerspectiveEvent, "
      //+ "unrecognized event type");
      return;
    }
    
    graph_view.updateView();
              
    if ( graph_view instanceof org.hypergraphdb.viewer.HGVNetworkView ) {
      ( ( org.hypergraphdb.viewer.HGVNetworkView )graph_view ).redrawGraph();
    }

    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.handleGraphPerspectiveEvent()." +
    //" numTypes caught = " + numTypes);
  }//handleGraphPerspectiveEvent

  
  /**
   * It removes the views of the edges in the array from the given <code>giny.view.GraphView</code> 
   * object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which edges will be removed
   * @param edge_indices the indices of the edges that will be removed
   * @return an array of edge indices that were removed
   */
  static public FEdge []  removeGraphViewEdges (HGVNetworkView graph_view,
		  FEdge [] edge_indices){
    //System.out.println("In BasicGraphViewHandler.removeGraphViewEdges()");
    ArrayList<FEdge> removedEdges = new ArrayList<FEdge>(edge_indices.length);
    for(int i = 0; i < edge_indices.length; i++){
      PEdgeView edgeView = graph_view.removeEdgeView(edge_indices[i]);
      if(edgeView != null){
        removedEdges.add(edge_indices[i]);
      }
    }//for i
    removedEdges.trimToSize();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewEdges()," + "num removed edges = " + removedEdges.size());
    FEdge[] res = new FEdge[removedEdges.size()];
    for(int i = 0; i < removedEdges.size(); i++)
    	res[i] = removedEdges.get(i);
    return res;
  }//removeGraphViewEdges
  
  /**
   * It restores the views of the edges in the array in the given <code>giny.view.GraphView</code> 
   * object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be restored
   * @param edges the edges that will be restored
   * @return an array of edges that were restored
   */
  // TESTED
  static public FEdge [] restoreGraphViewEdges (HGVNetworkView graph_view,
                                               FEdge [] edges){
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    Set restoredEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      PEdgeView edgeView = graph_view.getEdgeView(edges[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edges[i]);
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdges.add(edgeView.getEdge());
      }
    }//for i
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewEdges(), "+"num restored edges = " + restoredEdges.size() );
    return (FEdge[])restoredEdges.toArray(new FEdge[restoredEdges.size()]);
  }//restoreGraphViewEdges

 
  /**
   * It selects the edges in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be selected
   * @param edges the edges in <code>graph_view</code> that will be selected
   * @return the edges that were selected
   */
  static public FEdge [] selectGraphViewEdges (HGVNetworkView graph_view,
                                              FEdge [] edges){
    //System.out.println("In BasicGraphViewHandler.selectGraphViewEdges()");
    Set selectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      PEdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(true);
        selectedEdges.add(edges[i]);
      }
    }//for i
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewEdges()," +"num selected edges = " + selectedEdges.size());
    return (FEdge[])selectedEdges.toArray(new FEdge[selectedEdges.size()]);
  }//selectGraphViewEdges

  /**
   * It unselects the edges in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be unselected
   * @param edges the edges that will be unselected in <code>graph_view</code>
   * @return an array of edges that were unselected
   */
  static public FEdge[] unselectGraphViewEdges (HGVNetworkView graph_view,
                                               FEdge [] edges){
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewEdges()");
    Set unselectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      PEdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(false);
        unselectedEdges.add(edges[i]);
      }
    }//for i
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewEdges()," +"num unselected edges = " + unselectedEdges.size());
    return (FEdge[])unselectedEdges.toArray(new FEdge[unselectedEdges.size()]);
  }//unselectGraphViewEdges

  
   /**
   * It removes the views of the nodes with the given indices that are contained in the given 
   * <code>giny.view.GraphView</code> object, it also removes the connected edges to 
   * these nodes (an edge without a connecting node makes no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
   * @param node_indices the indices of the nodes that will be removed
   * @return an array of indices of nodes that were removed
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
  // edges and we don't need to remove them in this method
  static public FNode[] removeGraphViewNodes (HGVNetworkView graph_view,
                                          FNode [] node_indices){
    //System.out.println("In BasicGraphViewHandler.removeGraphViewNodes()");
    ArrayList<FNode> removedNodesIndices = new ArrayList<FNode>(node_indices.length);
    for(int i = 0; i < node_indices.length; i++){
      PNodeView nodeView = graph_view.removeNodeView(node_indices[i]);
      if(nodeView != null){
        removedNodesIndices.add(node_indices[i]);
      }
    }//for i
    removedNodesIndices.trimToSize();
    FNode[] res = new FNode[removedNodesIndices.size()];
    for(int i = 0; i < removedNodesIndices.size(); i++)
    	res[i] = removedNodesIndices.get(i);
    return res;
  }//removeGraphViewNodes
  
  
  /**
   * It restores the views of the nodes with the given indices in the given 
   * <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which node views will be restored
   * @param node_indices the indices of the nodes whose views will be restored
   * @param restore_connected_edges whether or not the connected edges to the restored nodes
   * should also be restored or not (for now this argument is ignored)
   * @return an array of indices of the nodes whose views were restored
   */
  //TODO: Depending on restore_connected_edges, restore connected edges or not.
  static public FNode[] restoreGraphViewNodes (HGVNetworkView graph_view,
		  FNode [] node_indices,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    ArrayList<FNode> restoredNodeIndices = new ArrayList<FNode>(node_indices.length);
    for(int i = 0; i < node_indices.length; i++){
      
      PNodeView nodeView = graph_view.getNodeView(node_indices[i]);
      boolean restored = false;
      if(nodeView == null){
        // This means that the nodes that were restored had never been viewed by
        // the graph_view, so we need to create a new PNodeView.
        nodeView = graph_view.addNodeView(node_indices[i]);
        if(nodeView != null){
          restored = true;
        }
      }else{
        // This means that the nodes that were restored had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show them
        restored = graph_view.showGraphObject(nodeView);
      }
      if(restored){
        restoredNodeIndices.add(node_indices[i]);
        positionToBarycenter(nodeView);
        //TODO: Remove
        //System.err.println("PNodeView for node index " + node_indices[i] + " was added to graph_view");
      }else{
        //TODO: Remove
        //System.err.println("ERROR: PNodeView for node index " + node_indices[i] +" was NOT added to graph_view");
      }
    }//for i
    restoredNodeIndices.trimToSize();
    FNode[] res = new FNode[restoredNodeIndices.size()];
    for(int i = 0; i < restoredNodeIndices.size(); i++)
    	res[i] = restoredNodeIndices.get(i);
    return res; 
  }//restoreGraphViewNodes
  
  
  /**
   * It selects the nodes in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be selected
   * @param nodes the nodes in <code>graph_view</code> that will be selected
   * @return the nodes that were selected
   */
  static public FNode [] selectGraphViewNodes (HGVNetworkView graph_view,
                                              FNode [] nodes){
    //TODO: Remove
    ////System.out.println("In BasicGraphViewHandler.selectGraphViewNodes()"); 
    Set selectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      PNodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(true);
        selectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewNodes(),"+"num selected nodes = " + selectedNodes.size()); 
    return (FNode[])selectedNodes.toArray(new FNode[selectedNodes.size()]);
  }//selectGraphViewNodes

  /**
   * It unselects the nodes in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be unselected
   * @param nodes the nodes that will be unselected in <code>graph_view</code>
   * @return an array of nodes that were unselected
   */
  static public FNode[] unselectGraphViewNodes (HGVNetworkView graph_view,
                                               FNode [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewNodes()");
    Set unselectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      PNodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(false);
        unselectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewNodes()," +"num unselected nodes = " + unselectedNodes.size());
    return (FNode[])unselectedNodes.toArray(new FNode[unselectedNodes.size()]);
  }//unselectGraphViewNodes

  /**
   * If the node that node_view represents is a meta-node, then it 
   * positions it at the barycenter of its viewable children nodes.
   *
   * @param node_view the <code>giny.view.PNodeView</code> that will be positioned
   * to the barycenter of its children
   */
  static public void positionToBarycenter (PNodeView node_view){
    FNode node = node_view.getNode();
    HGVNetworkView graphView = (HGVNetworkView) node_view.getGraphView();
    HGVNetwork gp = graphView.getNetwork();
    
   // int [] childrenNodeIndices = gp.getNodeMetaChildIndicesArray(rootIndex);
   // if(childrenNodeIndices == null || childrenNodeIndices.length == 0){return;}
    
   // FGraphPerspective childGP = node.getGraphPerspective();
   // if(childGP == null || childGP.getNodeCount() == 0){
   //   throw new IllegalStateException("FNode " + node.getIdentifier() + " has a non-empty array " +
  //                                " of children-node indices, but, it has no child GraphPerspective");
  //  }
    //Iterator it = childGP.nodesIterator(); 
    
    FEdge[] children = gp.getAdjacentEdges(node, true, true);
    double x = 0.0;
    double y = 0.0;
    double viewableChildren = 0;
    for(int i = 0; i < children.length; i++){
      FNode childNode = children[i].getTarget();
      if(childNode!= null) {
        PNodeView childNV = graphView.getNodeView(childNode);
        if(childNV != null){
          x += childNV.getXPosition();
          y += childNV.getYPosition();
          viewableChildren++;
        }
      }
    }//while it
    if(viewableChildren != 0){
      x /= viewableChildren;
      y /= viewableChildren;
      node_view.setXPosition(x);
      node_view.setYPosition(y);
    }
  }//positionToBarycenter

}//classs BasicGraphViewHandler
