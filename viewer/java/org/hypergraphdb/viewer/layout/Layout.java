package org.hypergraphdb.viewer.layout;

/**
 * A Layout can be applied to a HGVNetworkView
 */
public interface Layout {

  public String getName();
  public void applyLayout ();

}
