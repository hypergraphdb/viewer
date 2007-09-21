package fing.model;


public class FEdge 
{

  // Variables specific to public get/set methods.
	FRootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  // Package visible constructor.
  FEdge() { }

  public FEdge(FRootGraph root, int rootGraphIndex) {
		this.m_rootGraph = root;
		this.m_rootGraphIndex = rootGraphIndex;
	}
  
  public FNode getSource()
  {
    return m_rootGraph.getNode
      (m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex));
  }

  public FNode getTarget()
  {
    return m_rootGraph.getNode
      (m_rootGraph.getEdgeTargetIndex(m_rootGraphIndex));
  }

  public FRootGraph getRootGraph()
  {
    return m_rootGraph;
  }

  public int getRootGraphIndex()
  {
    return m_rootGraphIndex;
  }

  public String getIdentifier()
  {
    return m_identifier;
  }

  public boolean setIdentifier(String new_id)
  {
    m_identifier = new_id;
    return true;
  }

}
