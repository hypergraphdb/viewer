package org.hypergraphdb.viewer.foo;

import java.util.Iterator;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.FNode;



/**
 * This class extends MutableGraphLayout to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 **/
public interface MutablePolyEdgeGraphLayout 
{

	public Iterator<FEdge> edgesIterator();
    public Iterator<FNode> nodesIterator();
     
        
    
   /**
   * Creates a new edge anchor point.<p>
   * The creation of an anchor point is accomplished such that the ordering
   * of existing anchor points stays the same.  An existing anchor point
   * [belonging to specified edge] with index greater
   * than or equal to anchorIndex will be assigned a new index
   * equal to its previous index plus one; an existing anchor point with index
   * less than anchorIndex will keep its index.<p>
   * A new anchor point P's X,Y position is the midpoint along the segment
   * whose end points are P's neighbors in the edge poly-line definition;
   * X,Y positions of existing anchor points and nodes are unchanged.
   *
   * @param edge new anchor point will be created on specified edge.
   * @param anchorIndex new anchor point will have index
   *   anchorIndex within specified edge.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge)].
   * @exception UnsupportedOperationException if specified edge
   *   source and target nodes that are both non-movable.
   **/
  public void createAnchor(FEdge edge, int anchorIndex);

  /**
   * Sets the X,Y position of an edge anchor point.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of getAnchorPosition() - that is, if we call
   * <blockquote><code>setAnchorPosition(edge, aInx, x, y)</code></blockquote>
   * then the subsequent expressions
   * <blockquote>
   * <nobr><code>x == getAnchorPosition(edge, aInx, true)</code></nobr><br />
   * <nobr><code>y == getAnchorPosition(edge, aInx, false)</code></nobr>
   * </blockquote>
   * both evaluate to true.
   *
   * @param edge the edge to which the anchor point to be
   *   positioned belongs.
   * @param anchorIndex the index of anchor point, within specified edge,
   *   which we're trying to position.
   * @param xPosition the desired X position of specified edge anchor point.
   * @param yPosition the desired Y position of specified edge anchor point.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge) - 1].
   * @exception IllegalArgumentException if specified X position or
   *   specified Y position falls outside of [0.0, getMaxWidth()] and
   *   [0.0, getMaxHeight()], respectively.
   * @exception UnsupportedOperationException if specified edge
   *   has source and target nodes that are both non-movable.
   **/
  public void setAnchorPosition(FEdge edge,
                                int anchorIndex,
                                double xPosition,
                                double yPosition);
  
   /**
   * Sets the X,Y position of a node.
   * This is a hook for layout algorithms to actually set locations of
   * nodes.  Layout algorithms should call this method.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of getNodePosition() -- that is, if we call
   * <blockquote><code>setNodePosition(node, x, y)</code></blockquote>
   * then the subsequent expressions
   * <blockquote>
   * <nobr><code>x == getNodePosition(node, true)</code></nobr><br />
   * <nobr><code>y == getNodePosition(node, false)</code></nobr>
   * </blockquote>
   * both evaluate to true.<p>
   * Layout algorithms are encouraged to set node positions such that
   * their X and Y values use the full range of allowable values, including
   * the boundary values 0, getMaxWidth(), and
   * getMaxHeight().  Any notion of node thickness, graph
   * border on perimeter, etc. should be predetermined by the application
   * using a layout algorithm; getMaxWidth() and
   * getMaxHeight() should be defined accordingly by the
   * application using a layout algorithm.
   *
   * @exception IllegalArgumentException if xPos or yPos are out of
   *   allowable range [0.0, getMaxWidth()] and [0.0, getMaxHeight()].
   *   respectively.
   *   <nobr><code>xPos < 0.0</code></nobr>, if
   * @exception IllegalArgumentException if specified node is not
   *   a node in this graph.
   * @exception UnsupportedOperationException if
   *   isMovableNode(node) returns false.
   *
   * @see #getMaxWidth()
   * @see #getMaxHeight()
   * @see #getNodePosition(int, boolean)
   * @see #isMovableNode(int)
   */
  public void setNodePosition(FNode node, double xPos, double yPos);

  
  /**
   * Returns the maximum allowable value of X positions of nodes.
   * All X positions of nodes in this graph will lie in the interval
   * [0.0, getMaxWidth()].
   *
   * @see #getNodePosition(int, boolean)
   **/
  public double getMaxWidth();

  /**
   * Returns the maximum allowable value of Y positions of nodes.
   * All Y positions of nodes in this graph will lie in the interval
   * [0.0, getMaxHeight()].
   *
   * @see #getNodePosition(int, boolean)
   **/
  public double getMaxHeight();

  /**
   * Returns the X or Y position of a node.
   *
   * @param node the node whose position we're seeking.
   * @param xPosition if true, return X position; if false, return Y position.
   * @return the X or Y position of node.
   * @exception IllegalArgumentException if specified node is not
   *   a node in this graph.
   **/
  public double getNodePosition(FNode node, boolean xPosition);

  /**
   * Returns the number of anchor points belonging to an edge.
   * In other methods of this
   * interface an anchor point is referenced by the edge to which
   * the anchor point belongs along with the anchor point's index within that
   * edge.  Indices of anchor points within an edge E
   * start at 0 and end at getNumAnchors(E) - 1, inclusive.
   *
   * @return the number of edge anchor points belonging to specified ege.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   **/
  public int getNumAnchors(FEdge edge);

  /**
   * Returns the X or Y position of an edge anchor point.
   *
   * @param edge the edge to which the anchor point whose
   *   position we're seeking belongs.
   * @param anchorIndex the index of anchor point, within specified edge,
   *   whose position we're seeking.
   * @param xPosition if true, return X position of anchor point;
   *   if false, return Y position of anchor point.
   * @return the X or Y position of anchor point with index
   *   anchorIndex within specified edge;
   *   X return values are within the interval
   *   [0.0, getMaxWidth()] and Y return values
   *   are within the interval [0.0, getMaxHeight()].
   *
   * @exception IllegalArgumentException if specified edge is not an
   *   edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge) - 1].
   **/
  public double getAnchorPosition(FEdge edge,
                                  int anchorIndex,
                                  boolean xPosition);


}