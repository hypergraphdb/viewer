package fing.model;


final class RootGraphNodesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final int[] m_removedNodes;

  // Note that no copy of the array removedNodes is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.  Note that the FNode objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the FNode objects is not too important
  // becuase the getRemovedNodes() method has been deprecated in both
  // GraphPerspective and RootGraph listener systems.
  RootGraphNodesRemovedEvent(FRootGraph rootGraph, int[] removedNodes)
  {
    super(rootGraph);
    m_removedNodes = removedNodes;
  }

  public final int getType()
  {
    return NODES_REMOVED_TYPE;
  }

  public final int[] getRemovedNodeIndices()
  {
    return m_removedNodes;
  }

}
