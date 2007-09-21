package fing.model;


/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.
 **/
public interface FingEdgeDepot
{

  /**
   * This either instantiates a new edge or gets one from the recyclery,
   * initializing it with the parameters specified.
   **/
  public FEdge getEdge(FRootGraph root, int index, String id);

  /**
   * Recycles an edge.  Implementations may choose to do nothing in this
   * method and instantiate a new edge in each call to getEdge().  This method
   * is simply a hook for Fing to tell the depository "I'm done using this edge
   * object -- it's no longer part of a RootGraph".
   **/
  public void recycleEdge(FEdge node);

}
