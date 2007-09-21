package fing.model;


final class GraphPerspectiveEdgesRestoredEvent
  extends GraphPerspectiveChangeEventAdapter
{

  private final FGraphPerspective m_persp;
  private final int[] m_restoredEdgeInx;

  // Note that no copy of the array restoredEdgeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  GraphPerspectiveEdgesRestoredEvent(FGraphPerspective persp,
                                     int[] restoredEdgeInx)
  {
    super(persp);
    m_persp = persp;
    m_restoredEdgeInx = restoredEdgeInx;
  }

  public final int getType()
  {
    return EDGES_RESTORED_TYPE;
  }

  public final FEdge[] getRestoredEdges()
  {
    final FEdge[] returnThis = new FEdge[m_restoredEdgeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_persp.getRootGraph().getEdge(m_restoredEdgeInx[i]);
    return returnThis;
  }

  public final int[] getRestoredEdgeIndices()
  {
    final int[] returnThis = new int[m_restoredEdgeInx.length];
    System.arraycopy(m_restoredEdgeInx, 0, returnThis, 0,
                     m_restoredEdgeInx.length);
    return returnThis;
  }

}
