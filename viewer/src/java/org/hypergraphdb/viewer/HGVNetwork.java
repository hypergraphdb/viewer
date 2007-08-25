package org.hypergraphdb.viewer;

import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphPerspective;
import java.util.*;
import org.hypergraphdb.HyperGraph;

import org.hypergraphdb.viewer.data.FlagFilter;
import org.hypergraphdb.viewer.data.FlagEventListener;

/**
 *HGVNetwork is the primary class for algorithm writing.&nbsp; All
algorithms should take a HGVNetwork as input, and do their best to only
use the API of HGVNetwork.&nbsp; Plugins that want to affect the display
of a graph can look into using HGVNetworkView as well.<br>
<br>
A HGVNetwork can create Nodes or Edges.&nbsp; Any Nodes or Edges that
wish to be added to a HGVNetwork firt need to be created in <span
 style="font-style: italic;">org.hypergraphdb.viewer.</span>&nbsp; <br>
<br>
The methods that are defined by HGVNetwork mostly deal with data
integration and flagging of nodes/edges.&nbsp; All methods that deal
with graph traversal are part of the inherited API of the
GraphPerspective class.&nbsp; Links to which can be found at the bottom
of the methods list.&nbsp; <br>
<br>
In general, all methods are supported for working with Nodes/Edges as
objects, and as indices.<br>
 */
public interface HGVNetwork extends GraphPerspective {

  /**
   * Can Change
   */
  public String getTitle ();

  
  /**
   * Can Change
   */
  public void setTitle ( String new_id );

  /**
   * Can't Change
   */
  public String getIdentifier ();

  
  /**
   * Can't Change
   */
  public String setIdentifier ( String new_id );



  //----------------------------------------//
  // Network Methods
  //----------------------------------------//
  
  /**
   * Appends all of the nodes and edges in the given Network to 
   * this Network
   */
  public void appendNetwork ( HGVNetwork network );

 

  public FlagFilter getFlagger ();


  
  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data );

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames ();
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name );
    

  /**
   * Add a node to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this node
   */
  public int addNode ( int cytoscape_node );

  /**
   * Add a node to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this node
   */
  public HGVNode addNode ( Node cytoscape_node );
 
  /**
   * This will remove this node from the Network. However,
   * unless forced, it will remain in HGViewer to be possibly
   * resused by another Network in the future.
   * @param set_remove true removes this node from all of HGViewer, 
   *                   false lets it be used by other CyNetworks
   * @return true if the node is still present in HGViewer 
   *          ( i.e. in another Network )
   */
  public boolean removeNode ( int node_index, boolean set_remove );

  


  //--------------------//
  // Edges

  /**
   * Add a edge to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this edge
   */
  public int addEdge ( int edge_index );

  /**
   * Add a edge to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this edge
   */
  public HGVEdge addEdge ( Edge edge );
 
  /**
   * This will remove this edge from the Network. However,
   * unless forced, it will remain in HGViewer to be possibly
   * resused by another Network in the future.
   * @param set_remove true removes this edge from all of HGViewer, 
   *                   false lets it be used by other CyNetworks
   * @return true if the edge is still present in HGViewer 
   *          ( i.e. in another Network )
   */
  public boolean removeEdge ( int edge_index, boolean set_remove );
  
  public HyperGraph getHyperGraph();
  public void setHyperGraph(HyperGraph h);
 
}
