package org.hypergraphdb.viewer;

import giny.model.*;
import org.hypergraphdb.*;
import org.hypergraphdb.viewer.giny.HGViewerFingRootGraph;


// Package visible class.
public class HGVNode implements HGVBaseElement, giny.model.Node 
{

  // Variables specific to public get/set methods.
  HGViewerFingRootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  HGHandle handle = null;
 
   
  public HGVNode(RootGraph root,
         int rootGraphIndex) { 
    this.m_rootGraph = (HGViewerFingRootGraph) root;
    this.m_rootGraphIndex = rootGraphIndex;
  }


  public GraphPerspective getGraphPerspective()
  {
    return m_rootGraph.createGraphPerspective
      (m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
       m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex));
  }

  public boolean setGraphPerspective(GraphPerspective gp)
  {
    if (gp.getRootGraph() != m_rootGraph) return false;
    final int[] nodeInx = gp.getNodeIndicesArray();
    final int[] edgeInx = gp.getEdgeIndicesArray();
    for (int i = 0; i < nodeInx.length; i++)
      m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);
    for (int i = 0; i < edgeInx.length; i++)
      m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);
    return true;
  }

  public RootGraph getRootGraph()
  {
    return m_rootGraph;
  }

  public int getRootGraphIndex()
  {
    return m_rootGraphIndex;
  }

  public String getIdentifier()
  {
     return "" + handle;
  }
  
  public boolean setIdentifier(String new_id){
	  m_rootGraph.setNodeIdentifier(handle, m_rootGraphIndex); 
	  return true;
  }

  /**
   * Getter for property dataObject.
   * @return Value of property dataObject.
   */
  public HGHandle getHandle()
  {
      return this.handle;
  }
  
  /**
   * Setter for property dataObject.
   * @param dataObject New value of property dataObject.
   */
  public void setHandle(HGHandle h)
  {
      handle = h;
      m_rootGraph.setNodeIdentifier(handle, m_rootGraphIndex);
  }
   
} 
