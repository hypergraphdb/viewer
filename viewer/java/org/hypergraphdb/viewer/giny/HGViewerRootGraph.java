package org.hypergraphdb.viewer.giny;

import giny.model.*;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.viewer.*;
import java.util.Collection;

public interface HGViewerRootGraph extends RootGraph {
  
   /**
   * Uses Code copied from ColtRootGraph to create a new HGVNetwork.
   */
  public HGVNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) ;

  public HGVNetwork createNetwork ( Collection nodes, Collection edges ) ;

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public HGVNetwork createNetwork ( int[] node_indices, int[] edge_indices ) ;

  public HGVNode getNode (HGHandle identifier ) ;

  public void setNodeIdentifier ( HGHandle identifier, int index ) ;

  
}

