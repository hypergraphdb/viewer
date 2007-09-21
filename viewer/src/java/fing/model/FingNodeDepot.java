package fing.model;


/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.
 **/
public interface FingNodeDepot
{

  /**
   * This either instantiates a new node or gets one from the recyclery,
   * initializing it with the parameters specified.
   **/
  public FNode getNode(FRootGraph root, int index, String id);

  /**
   * Recycles a node.  Implementations may choose to do nothing in this
   * method and instantiate a new node in each call to getNode().  This method
   * is simply a hook for Fing to tell the depository "I'm done using this node
   * object -- it's no longer part of a RootGraph".
   **/
  public void recycleNode(FNode node);

}
