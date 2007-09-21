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
import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PEdgeView;
import phoebe.PNodeView;
import phoebe.event.GraphPerspectiveChangeEvent;
import fing.model.FEdge;
import fing.model.FGraphPerspective;
import fing.model.FNode;

/**
 * A basic <code>GraphViewHandler</code> that simply reflects <code>GraphPerspective</code>
 * changes on a given <code>GraphView</code>
 */

public class BasicGraphViewHandler implements GraphViewHandler {

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
  public void handleGraphPerspectiveEvent (GraphPerspectiveChangeEvent event, HGVNetworkView graph_view){
    
    int numTypes = 0; // An event may have more than one type
    
    // FNode Events:
    if(event.isNodesHiddenType()){
      //System.out.println("isNodesHiddenType == " + event.isNodesHiddenType());
      removeGraphViewNodes(graph_view, event.getHiddenNodeIndices());
      numTypes++;
    }

    if(event.isNodesRestoredType()){
      //System.out.println("isNodesRestoredType == " + event.isNodesRestoredType());
      restoreGraphViewNodes(graph_view, event.getRestoredNodeIndices(), true);
      numTypes++;
    }
    
    // FEdge events:
    if(event.isEdgesHiddenType()){
      //System.out.println("isEdgesHiddenType == " + event.isEdgesHiddenType());
      removeGraphViewEdges(graph_view, event.getHiddenEdgeIndices());
      numTypes++;
    }
    
    if(event.isEdgesRestoredType()){
      //System.out.println("isEdgesRestoredType == " + event.isEdgesRestoredType());
      restoreGraphViewEdges(graph_view, event.getRestoredEdgeIndices());
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
  static public int []  removeGraphViewEdges (HGVNetworkView graph_view,
                                              int [] edge_indices){
    //System.out.println("In BasicGraphViewHandler.removeGraphViewEdges()");
    ArrayList<Integer> removedEdges = new ArrayList<Integer>(edge_indices.length);
    for(int i = 0; i < edge_indices.length; i++){
      PEdgeView edgeView = graph_view.removeEdgeView(edge_indices[i]);
      if(edgeView != null){
        removedEdges.add(edge_indices[i]);
      }
    }//for i
    removedEdges.trimToSize();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewEdges()," + "num removed edges = " + removedEdges.size());
    int[] res = new int[removedEdges.size()];
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
        edgeView = graph_view.addEdgeView(edges[i].getRootGraphIndex());
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
   * It restores the views of the edges with the given indices in the given 
   * <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges' views 
   * will be restored
   * @param edge_indices the indices of the edges that will be restored
   * @return an array of indices of edges that were restored
   */
  // TODO: What if a connected node is not in the graph view or graph perspective?
  static public int [] restoreGraphViewEdges (HGVNetworkView graph_view,
                                              int [] edge_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    ArrayList<Integer> restoredEdgeIndices = new ArrayList<Integer>(edge_indices.length);
    for(int i = 0; i < edge_indices.length; i++){
      // TEST: See if the NodeViews of the connected Nodes are in graph_view
      // TODO: What to do in this case? I would say throw exception.
      //GraphPerspective graphPerspective = graph_view.getGraphPerspective();
      //int sourceRootIndex = 
      // graphPerspective.getRootGraphNodeIndex(graphPerspective.getEdgeSourceIndex(edge_indices[i]));
      //int targetRootIndex =
      //graphPerspective.getRootGraphNodeIndex(graphPerspective.getEdgeTargetIndex(edge_indices[i]));
      //PNodeView sourceNodeView = graph_view.getNodeView(sourceRootIndex);
      //PNodeView targetNodeView = graph_view.getNodeView(targetRootIndex);
      //if(sourceNodeView == null){
      //System.err.println("ERROR: Source PNodeView for edge "+edge_indices[i]+" is null");
      //}
      //if(targetNodeView == null){
      //System.err.println("ERROR: Target PNodeView for edge "+edge_indices[i]+" is null");
      //}

      // The given index can be either RootGraph index or GraphPerspective index
      PEdgeView edgeView = graph_view.getEdgeView(edge_indices[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edge_indices[i]);
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdgeIndices.add(edge_indices[i]);
      }
    }//for i
    
    int[] res = new int[restoredEdgeIndices.size()];
    for(int i = 0; i < restoredEdgeIndices.size(); i++)
    	res[i] = restoredEdgeIndices.get(i);
    return res;
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
   * It removes the nodes in the array from the given <code>giny.view.GraphView</code> object,
   * it also removes the connected edges to these nodes (an edge without a connecting node makes
   * no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
   * @param nodes the nodes whose views will be removed from <code>graph_view</code>
   * @return an array of nodes that were removed
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
  // edges and we don't need to hide them in this method
  // TESTED
  static public FNode[] removeGraphViewNodes (HGVNetworkView graph_view,
                                           FNode [] nodes){
    //System.out.println("In BasicGraphViewHandler.removeGraphViewNodes()");
    Set removedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      PNodeView nodeView = graph_view.removeNodeView(nodes[i]);
      if(nodeView != null){ 
        removedNodes.add(nodes[i]);
      }
    }//for i
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewNodes(), " +"num removed nodes = " + removedNodes.size());
    return (FNode[])removedNodes.toArray(new FNode[removedNodes.size()]);
  }//removeGraphViewNodes

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
  static public int[] removeGraphViewNodes (HGVNetworkView graph_view,
                                          int [] node_indices){
    //System.out.println("In BasicGraphViewHandler.removeGraphViewNodes()");
    ArrayList<Integer> removedNodesIndices = new ArrayList<Integer>(node_indices.length);
    for(int i = 0; i < node_indices.length; i++){
      PNodeView nodeView = graph_view.removeNodeView(node_indices[i]);
      if(nodeView != null){
        removedNodesIndices.add(node_indices[i]);
      }
    }//for i
    removedNodesIndices.trimToSize();
    int[] res = new int[removedNodesIndices.size()];
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
  static public int[] restoreGraphViewNodes (HGVNetworkView graph_view,
                                              int [] node_indices,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    ArrayList<Integer> restoredNodeIndices = new ArrayList<Integer>(node_indices.length);
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
    int[] res = new int[restoredNodeIndices.size()];
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
    int rootIndex = node.getRootGraphIndex();
    HGVNetworkView graphView = (HGVNetworkView) node_view.getGraphView();
    FGraphPerspective gp = graphView.getGraphPerspective();
    
   // int [] childrenNodeIndices = gp.getNodeMetaChildIndicesArray(rootIndex);
   // if(childrenNodeIndices == null || childrenNodeIndices.length == 0){return;}
    
   // FGraphPerspective childGP = node.getGraphPerspective();
   // if(childGP == null || childGP.getNodeCount() == 0){
   //   throw new IllegalStateException("FNode " + node.getIdentifier() + " has a non-empty array " +
  //                                " of children-node indices, but, it has no child GraphPerspective");
  //  }
    //Iterator it = childGP.nodesIterator(); 
    
    int[] children = gp.getAdjacentEdgeIndicesArray(rootIndex, true, true, true);
    double x = 0.0;
    double y = 0.0;
    double viewableChildren = 0;
    for(int i = 0; i < children.length; i++){
      FNode childNode = gp.getEdge(children[i]).getTarget();
      if(gp.getNode(childNode.getRootGraphIndex()) != null) {
        PNodeView childNV = graphView.getNodeView(childNode.getRootGraphIndex());
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

  /**
   * Updates the given graph_view to contain node and edge visual representations
   * of only nodes and edges that are in its <code>GraphPerspective</code>
   * 
   * @see GraphViewController#resumeListening()
   * @see GraphViewController#resumeListening(GraphView)
   */
  public void updateGraphView (HGVNetworkView graph_view){
    
	  FGraphPerspective graphPerspective = graph_view.getGraphPerspective();
    
    ArrayList<Integer> gpNodeIndices = new ArrayList<Integer>();
    for(Iterator it = graphPerspective.nodesIterator(); it.hasNext();)
    	gpNodeIndices.add(((FNode)it.next()).getRootGraphIndex());
    ArrayList<Integer> gpEdgeIndices = new ArrayList<Integer>();
    for(Iterator it = graphPerspective.edgesIterator(); it.hasNext();)
    	gpEdgeIndices.add(((FEdge)it.next()).getRootGraphIndex());
        
    ArrayList<Integer> gvNodeIndices = new ArrayList<Integer>(graph_view.getNodeViewCount());
    ArrayList<Integer> gvEdgeIndices = new ArrayList<Integer>(graph_view.getEdgeViewCount());
    
    // Obtain a list of nodes' root indices that are represented in graph_view
    Iterator it = graph_view.getNodeViewsIterator();
    while(it.hasNext()){
      PNodeView nodeView = (PNodeView)it.next();
      FNode gvNode = nodeView.getNode();
      if(gvNode == null){
        System.err.println("FNode for nodeView is null (nodeView  = " + nodeView + ")");
        continue;
      }
      int nodeIndex = gvNode.getRootGraphIndex();
      gvNodeIndices.add(nodeIndex);
    }// while there are more graph view nodes
    
    // Obtain a list of edges that are represented in graph_view,
    // and remove EdgeViews that are no longer in graph_perspective
    it = graph_view.getEdgeViewsIterator();
    while(it.hasNext()){
      PEdgeView edgeView = (PEdgeView)it.next();
      FEdge gvEdge = edgeView.getEdge();
      if(gvEdge == null){
        System.err.println("FEdge for edgeView is null (edgeView  = " + edgeView + ")");
        continue;
      }
      int edgeIndex = gvEdge.getRootGraphIndex();
      gvEdgeIndices.add(edgeIndex);
    }// while there are more graph view edges
    
    // Make sure that graph_view represents all nodes that are
    // currently in graphPerspective
    for(int i = 0; i < gpNodeIndices.size(); i++){
      int nodeIndex = gpNodeIndices.get(i);
      PNodeView nodeView = graph_view.getNodeView(nodeIndex);
      if(nodeView == null){
        graph_view.addNodeView(nodeIndex);
      }else{
        graph_view.showGraphObject(nodeView);
      }
    }// for each graphPerspective node
    
    // Make sure that graph_view represents all edges that are
    // currently in graphPerspective
    for(int i = 0; i < gpEdgeIndices.size(); i++){
      int edgeIndex = gpEdgeIndices.get(i);
      PEdgeView edgeView = graph_view.getEdgeView(edgeIndex);
      if(edgeView == null){
        graph_view.addEdgeView(edgeIndex);
      }else{
        graph_view.showGraphObject(edgeView);
      }
    }// for each GraphPerspective edge

    // Remove from graph_view all edge representations that are not in graphPerspective
    gvEdgeIndices.removeAll(gpEdgeIndices);
    gvEdgeIndices.trimToSize();
    for( int i = 0;  i < gvEdgeIndices.size(); i++){
      graph_view.removeEdgeView(gvEdgeIndices.get(i));
    }// for each edge that is in graph_view but that is not in graphPerspective
    
    // Remove from graph_view all node representations that are not in graphPerspective
    gvNodeIndices.removeAll(gpNodeIndices);
    gvNodeIndices.trimToSize();
    for( int i = 0;  i < gvNodeIndices.size(); i++){
      graph_view.removeNodeView(gvNodeIndices.get(i));
    }// for each node that is in graph_view but that is not in graphPerspective
  }//updateGraphview

}//classs BasicGraphViewHandler
