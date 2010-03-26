package org.hypergraphdb.viewer;

import org.hypergraphdb.HGHandle;

/**
 * Foundation class in the HGViewer model representing a given node
 * described by its HGHandle in the HyperGraphDB 
 */
public class FNode
{
	protected HGHandle handle = null;
	
	public FNode()
    {
    }

    public FNode(HGHandle h)
	{
		this.handle = h;
	}

    /**
     * Returns the node's handle
     */
	public HGHandle getHandle()
	{
	   return this.handle;
	}
	  

	/**
     * Sets the node's handle
     */
	public void setHandle(HGHandle h)
	{
	      handle = h;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((handle == null) ? 0 : handle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FNode other = (FNode) obj;
		if (handle == null) {
			if (other.handle != null)
				return false;
		} else if (!handle.equals(other.handle))
			return false;
		return true;
	}
}
