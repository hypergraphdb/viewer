package fing.model;


final class GraphPerspectiveEdgesHiddenEvent
  extends GraphPerspectiveChangeEventAdapter
{

  private final FEdge[] m_hiddenEdges;
  private final int[] m_hiddenEdgeInx;

  // Note that no copy of the array hiddenEdges is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  Note that the FEdge objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the FEdge objects is not too important
  // because the getHiddenEdges() method has been deprecated.
  GraphPerspectiveEdgesHiddenEvent(Object source,
                                   FEdge[] hiddenEdges)
  {
    super(source);
    m_hiddenEdges = hiddenEdges;
    m_hiddenEdgeInx = new int[m_hiddenEdges.length];
    for (int i = 0; i < m_hiddenEdgeInx.length; i++)
      m_hiddenEdgeInx[i] = m_hiddenEdges[i].getRootGraphIndex();
  }

  public final int getType()
  {
    return EDGES_HIDDEN_TYPE;
  }

  // This method has been deprecated in the Giny API.
  public final FEdge[] getHiddenEdges()
  {
    final FEdge[] returnThis = new FEdge[m_hiddenEdges.length];
    System.arraycopy(m_hiddenEdges, 0, returnThis, 0, m_hiddenEdges.length);
    return returnThis;
  }

  public final int[] getHiddenEdgeIndices()
  {
    final int[] returnThis = new int[m_hiddenEdgeInx.length];
    System.arraycopy(m_hiddenEdgeInx, 0, returnThis, 0,
                     m_hiddenEdgeInx.length);
    return returnThis;
  }

}
