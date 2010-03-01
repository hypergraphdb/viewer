package org.hypergraphdb.viewer.layout;

import org.hypergraphdb.viewer.GraphView;

/**
 * A Layout can be applied to a HGVNetworkView
 */
public interface Layout {

  public String getName();
  public void applyLayout (GraphView view);

}
