package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.HGVNetworkView;



public final class HGVNetworkEdgesAddedEvent
  extends HGVNetworkChangeEventAdapter
{

   private final FEdge[] m_restoredEdgeInx;

  // Note that no copy of the array restoredEdgeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  public HGVNetworkEdgesAddedEvent(HGVNetworkView persp,
		  FEdge[] restoredEdgeInx)
  {
    super(persp);
    m_restoredEdgeInx = restoredEdgeInx;
  }

  public final int getType()
  {
    return EDGES_ADDED_TYPE;
  }

  public final FEdge[] getAddedEdges()
  {
    final FEdge[] returnThis = new FEdge[m_restoredEdgeInx.length];
    System.arraycopy(m_restoredEdgeInx, 0, returnThis, 0,
                     m_restoredEdgeInx.length);
    return returnThis;
  }

}
