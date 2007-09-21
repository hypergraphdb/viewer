package fing.model;


final class RootGraphEdgesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final int[] m_removedEdges;

  // Note that no copy of the array removedEdges is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.  Note that the FEdge objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the FEdge objects is not too important
  // because the getRemovedEdges() method has been deprecated in both
  // GraphPerspective and RootGraph listener systems.
  RootGraphEdgesRemovedEvent(FRootGraph rootGraph, int[] removedEdges)
  {
    super(rootGraph);
    m_removedEdges = removedEdges;
  }

  public final int getType()
  {
    return EDGES_REMOVED_TYPE;
  }

  public final int[] getRemovedEdgeIndices()
  {
	  return m_removedEdges;
  }

}
