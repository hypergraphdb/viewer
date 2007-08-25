package org.hypergraphdb.viewer.giny;

import java.util.*;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;

import fing.model.*;
import cern.colt.map.*;

import org.hypergraphdb.viewer.*;
import org.hypergraphdb.viewer.giny.*;

import org.hypergraphdb.viewer.data.FlagFilter;
import org.hypergraphdb.viewer.data.FlagEventListener;

import cytoscape.util.intr.*;
import org.hypergraphdb.HyperGraph;

/**
 * ColtCyNetwork extends the GraphPerspective implementation found 
 * in the coltginy pacakge of the GINY distribution.
 *
 * ColtCyNetwork provides an implementation of the HGVNetwork interface,
 * as well as the GraphPerspective inteface, and also provides the 
 * functionality formally provided by GraphObjAttributes.
 *
 * The Network will notify listeners when nodes/edges are added/removed 
 * and when relavant data changes.
 */
public class FingHGVNetwork 
  extends FingExtensibleGraphPerspective
  implements HGVNetwork,
             GraphPerspective {
    
  private static int uid_counter = 0;

  private String identifier;
  protected String title;
   
 
  /**
   * The ClientData map
   */
  protected Map clientData;
  
  /**
   * The default object for flagging graph objects
   */
  protected FlagFilter flagger;

  private HyperGraph hg;
 
  //----------------------------------------//
  // Constructors
  //----------------------------------------//

   /**
   * rootGraphNodeInx need not contain all endpoint nodes corresponding to
   * edges in rootGraphEdgeInx - this is calculated automatically by this
   * constructor.  If any index does not correspond to an existing node or
   * edge, an IllegalArgumentException is thrown.  The indices lists need not
   * be non-repeating - the logic in this constructor handles duplicate
   * filtering.
   **/
  public FingHGVNetwork ( FingExtensibleRootGraph root,
                         IntIterator rootGraphNodeInx,
                         IntIterator rootGraphEdgeInx)
  {
    super(root, rootGraphNodeInx, rootGraphEdgeInx);
    initialize();
  }

  protected void initialize () {

    // TODO: get a better naming system in place
    Integer i = new Integer( uid_counter );
    identifier = i.toString();
    uid_counter++;

    clientData = new HashMap();
    flagger = new FlagFilter(this);
  }

  /**
   * Can Change
   */
  public String getTitle () {
    if ( title == null ) 
      return identifier;
    return title;
  }

  
  /**
   * Can Change
   */
  public void setTitle ( String new_id ) {
    title = new_id;
  }

  public String getIdentifier () {
    return identifier;
  }

  public String setIdentifier ( String new_id ) {
    identifier = new_id;
    return identifier;
  }


  //------------------------------//
  // Client Data
  //------------------------------//

  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data ) {
    clientData.put( data_name, data );
  }

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames () {
    return clientData.keySet();
  }
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name ) {
    return clientData.get( data_name );
  }
  

  //------------------------------//
  // Depercation
  //------------------------------//
 
  
  /**
   * Appends all of the nodes and edges in teh given Network to 
   * this Network
   */
  public void appendNetwork ( HGVNetwork network ) {
    int[] nodes = network.getNodeIndicesArray();
    int[] edges = network.getEdgeIndicesArray();
    restoreNodes( nodes );
    restoreEdges( edges );
  }
    
  
  /**
    * Returns the default object for flagging graph objects.
    */
  public FlagFilter getFlagger() {return flagger;}

  /**
   * This method will create a new node.
   * @return the HGViewer index of the created node 
   */
  public int createNode () {
    return restoreNode(  HGViewer.getRootGraph().createNode() );
  }

  /**
   * Add a node to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this node
   */
  public int addNode ( int cytoscape_node ) {
    return restoreNode( cytoscape_node );
  }

  /**
   * Add a node to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this node
   */
  public HGVNode addNode ( Node cytoscape_node ) {
    return ( HGVNode )restoreNode( cytoscape_node);
  }
 
  /**
   * Adds a node to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Index of this node
   */
  public int addNode ( String attribute, Object value ) {
    return 0;
  }

  /**
   * This will remove this node from the Network. However,
   * unless forced, it will remain in HGViewer to be possibly
   * resused by another Network in the future.
   * @param force force this node to be removed from all Networks
   * @return true if the node is still present in HGViewer 
   *          ( i.e. in another Network )
   */
  public boolean removeNode ( int node_index, boolean force ) {
    hideNode( node_index );
    return true;
  }

  //--------------------//
  // Edges

  /**
   * This method will create a new edge.
   * @param source the source node
   * @param target the target node
   * @param directed weather the edge should be directed
   * @return the HGViewer index of the created edge 
   */
  public int createEdge ( int source, int target, boolean directed ) {
    return restoreEdge( HGViewer.getRootGraph().createEdge( source, target, directed ) );
  }

  /**
   * Add a edge to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this edge
   */
  public int addEdge ( int cytoscape_edge ) {
    return restoreEdge( cytoscape_edge );
  }

  /**
   * Add a edge to this Network that already exists in 
   * HGViewer
   * @return the Network Index of this edge
   */
  public HGVEdge addEdge ( Edge cytoscape_edge ) {
    return ( HGVEdge )restoreEdge( cytoscape_edge );
  }
 
  /**
   * Adds a edge to this Network, by looking it up via the 
   * given attribute and value
   * @return the Network Index of this edge
   */
  public int addEdge ( String attribute, Object value ) {
    return 0;
  }

  /**
   * This will remove this edge from the Network. However,
   * unless forced, it will remain in HGViewer to be possibly
   * resused by another Network in the future.
   * @param force force this edge to be removed from all Networks
   * @return true if the edge is still present in HGViewer 
   *          ( i.e. in another Network )
   */
  public boolean removeEdge ( int edge_index, boolean force ) {
    super.hideEdge( edge_index );
    return true;
  }
  
  public HyperGraph getHyperGraph()
  {
      return hg;
  }  

  public void setHyperGraph(HyperGraph h)
  {
      hg = h;
  }

}

