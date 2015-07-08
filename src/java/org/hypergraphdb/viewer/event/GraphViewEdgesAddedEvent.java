package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.GraphView;



public final class GraphViewEdgesAddedEvent
  extends GraphViewChangeEventAdapter
{

   private final FEdge[] restoredEdges;

  // Note that no copy of the array restoredEdgeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  public GraphViewEdgesAddedEvent(GraphView persp,
		  FEdge[] restoredEdgeInx)
  {
    super(persp);
    restoredEdges = restoredEdgeInx;
  }

  public final int getType()
  {
    return EDGES_ADDED_TYPE;
  }

  public final FEdge[] getAddedEdges()
  {
    final FEdge[] returnThis = new FEdge[restoredEdges.length];
    System.arraycopy(restoredEdges, 0, returnThis, 0,
                     restoredEdges.length);
    return returnThis;
  }

}
