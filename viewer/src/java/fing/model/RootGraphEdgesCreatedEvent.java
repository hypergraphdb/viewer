package fing.model;


// This class is not currently being used.  Thus its constructor is private.
final class RootGraphEdgesCreatedEvent extends RootGraphChangeEventAdapter
{

  private final int[] m_createdEdgeInx;

  // Note that no copy of the array createdEdgeInx is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.
  private RootGraphEdgesCreatedEvent(FRootGraph rootGraph, int[] createdEdgeInx)
  {
    super(rootGraph);
    m_createdEdgeInx = createdEdgeInx;
  }

  public final int getType()
  {
    return EDGES_CREATED_TYPE;
  }

  public final int[] getCreatedEdgeIndices()
  {
    return m_createdEdgeInx;
  }

}
