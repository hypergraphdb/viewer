package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.GraphView;



public final class HGVNetworkEdgesRemovedEvent
  extends HGVNetworkChangeEventAdapter
{

  private final FEdge[] m_hiddenEdgeInx;

  // Note that no copy of the array hiddenEdges is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  
  public HGVNetworkEdgesRemovedEvent(GraphView source,
		  FEdge[] hiddenEdges)
  {
    super(source);
    m_hiddenEdgeInx = hiddenEdges;
  }

  public final int getType()
  {
    return this.EDGES_REMOVED_TYPE;
  }

  public final FEdge[] getRemovedEdges()
  {
    final FEdge[] returnThis = new FEdge[m_hiddenEdgeInx.length];
    System.arraycopy(m_hiddenEdgeInx, 0, returnThis, 0,
                     m_hiddenEdgeInx.length);
    return returnThis;
  }

}
