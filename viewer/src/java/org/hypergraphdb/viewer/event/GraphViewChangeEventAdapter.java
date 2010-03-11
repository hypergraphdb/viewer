package org.hypergraphdb.viewer.event;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;



public abstract class GraphViewChangeEventAdapter
  extends GraphViewChangeEvent
{

  GraphViewChangeEventAdapter(GraphView source)
  {
    super(source);
  }

  // This is the only abstract method on this class; whatever the type of
  // event, make sure to override the appropriate getXXX() methods - those
  // methods all return null in this implementation.
  public abstract int getType();

  public final boolean isNodesAddedType() {
    return (getType() & NODES_ADDED_TYPE) != 0; }
  public final boolean isEdgesAddedType() {
    return (getType() & EDGES_ADDED_TYPE) != 0; }
  public final boolean isNodesRemovedType() {
    return (getType() & NODES_REMOVED_TYPE) != 0; }
  public final boolean isEdgesRemovedType() {
    return (getType() & EDGES_REMOVED_TYPE) != 0; }
  public final boolean isNodesSelectedType() {
    return (getType() & NODES_SELECTED_TYPE) != 0; }
  public final boolean isNodesUnselectedType() {
    return (getType() & NODES_UNSELECTED_TYPE) != 0; }
  public final boolean isEdgesSelectedType() {
    return (getType() & EDGES_SELECTED_TYPE) != 0; }
  public final boolean isEdgesUnselectedType() {
    return (getType() & EDGES_UNSELECTED_TYPE) != 0; }

  public FNode[] getAddedNodes() { return null; }
  public FEdge[] getAddedEdges() { return null; }
  public FNode[] getRemovedNodes() { return null; }
  public FEdge[] getRemovedEdges() { return null; }
  public int[] getSelectedNodeIndices() { return null; }
  public int[] getUnselectedNodeIndices() { return null; }
  public int[] getSelectedEdgeIndices() { return null; }
  public int[] getUnselectedEdgeIndices() { return null; }

}
