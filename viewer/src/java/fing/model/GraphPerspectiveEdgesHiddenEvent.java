package fing.model;


final class GraphPerspectiveEdgesHiddenEvent
  extends GraphPerspectiveChangeEventAdapter
{

  private final int[] m_hiddenEdgeInx;

  // Note that no copy of the array hiddenEdges is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  
  GraphPerspectiveEdgesHiddenEvent(Object source,
                                   int[] hiddenEdges)
  {
    super(source);
    m_hiddenEdgeInx = hiddenEdges;
  }

  public final int getType()
  {
    return EDGES_HIDDEN_TYPE;
  }

  public final int[] getHiddenEdgeIndices()
  {
    final int[] returnThis = new int[m_hiddenEdgeInx.length];
    System.arraycopy(m_hiddenEdgeInx, 0, returnThis, 0,
                     m_hiddenEdgeInx.length);
    return returnThis;
  }

}
