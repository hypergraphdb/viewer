package fing.model;


// This class is currently not being used.  Thus its constructor is private.
final class RootGraphNodesCreatedEvent extends RootGraphChangeEventAdapter
{

  private final int[] m_createdNodeInx;

  // Note that no copy of the array createdNodeInx is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.
  private RootGraphNodesCreatedEvent(FRootGraph rootGraph, int[] createdNodeInx)
  {
    super(rootGraph);
    m_createdNodeInx = createdNodeInx;
  }

  public final int getType()
  {
    return NODES_CREATED_TYPE;
  }

  
  // If this system of listeners and events is to be used publicly (outside
  // of this package, that is), then we need to make a copy of this array
  // and return that copy.
  public final int[] getCreatedNodeIndices()
  {
    return m_createdNodeInx;
  }

}
