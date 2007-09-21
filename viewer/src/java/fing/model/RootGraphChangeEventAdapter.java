package fing.model;


abstract class RootGraphChangeEventAdapter extends RootGraphChangeEvent
{

  RootGraphChangeEventAdapter(FRootGraph rootGraph)
  {
    super(rootGraph);
  }

  // This is the only abstract method on this class; whatever the type of
  // event, make sure to override the appropriate getXXX() methods - those
  // methods all return null in this implementation.
  public abstract int getType ();

  public final boolean isNodesCreatedType () {
    return (getType() & NODES_CREATED_TYPE) != 0; }
  public final boolean isEdgesCreatedType () {
    return (getType() & EDGES_CREATED_TYPE) != 0; }
  public final boolean isNodesRemovedType () {
    return (getType() & NODES_REMOVED_TYPE) != 0; }
  public final boolean isEdgesRemovedType () {
    return (getType() & EDGES_REMOVED_TYPE) != 0; }
 
  public int[] getCreatedNodeIndices() { return null; }
  public int[] getCreatedEdgeIndices() { return null; }
  public int[] getRemovedNodeIndices() { return null; }
  public int[] getRemovedEdgeIndices() { return null; }
 
}
