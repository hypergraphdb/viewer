package fing.model;

import org.hypergraphdb.HGHandle;

public class FNode
{
	protected FRootGraph m_rootGraph = null;
	protected int m_rootGraphIndex = 0;
	protected HGHandle handle = null;
	
	// Package visible constructor.
	FNode()
	{
	}

	public FNode(FRootGraph root, int rootGraphIndex)
	{
		this.m_rootGraph = root;
		this.m_rootGraphIndex = rootGraphIndex;
	}

	public FRootGraph getRootGraph()
	{
		return m_rootGraph;
	}

	public int getRootGraphIndex()
	{
		return m_rootGraphIndex;
	}
	
	 public HGHandle getHandle()
	  {
	      return this.handle;
	  }
	  
	  public void setHandle(HGHandle h)
	  {
	      handle = h;
	      getRootGraph().setNodeIdentifier(handle, getRootGraphIndex());
	 }
}
