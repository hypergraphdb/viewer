package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;



public final class GraphViewNodesAddedEvent
  extends GraphViewChangeEventAdapter
{

  private final FNode[] restoredNodes;

  // Note that no copy of the array restoredNodeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  public GraphViewNodesAddedEvent(GraphView persp,
		  FNode[] nodes)
  {
    super(persp);
    restoredNodes = nodes;
  }

  public final int getType()
  {
    return NODES_ADDED_TYPE;
  }

  public final FNode[] getAddedNodes()
  {
    final FNode[] returnThis = new FNode[restoredNodes.length];
    System.arraycopy(restoredNodes, 0, returnThis, 0,
                     restoredNodes.length);
    return returnThis;
  }

}
