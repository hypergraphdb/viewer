package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;



public final class GraphViewNodesRemovedEvent
  extends GraphViewChangeEventAdapter
{

  private final FNode[] nodes;

  // Note that no copy of the array hiddenNodes is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  Note that the FNode objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the FNode objects is not too important
  // because the getHiddenNodes() method has been deprecated.
  public GraphViewNodesRemovedEvent(GraphView source,
		  FNode[] hiddenNodes)
  {
    super(source);
    nodes = hiddenNodes;
  }

  public final int getType()
  {
    return NODES_REMOVED_TYPE;
  }

  public final FNode[] getRemovedNodes()
  {
    final FNode[] returnThis = new FNode[nodes.length];
    System.arraycopy(nodes, 0, returnThis, 0,
                     nodes.length);
    return returnThis;
  }

}
