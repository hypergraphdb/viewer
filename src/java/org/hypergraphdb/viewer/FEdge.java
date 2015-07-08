package org.hypergraphdb.viewer;

/**
 * Foundation class in the HGViewer model representing an "edge"
 * e.g. relation between a source and a target FNode. 
 * Source node's HGHandle always represents an instance of HGLink and target node's 
 * HGHandle is contained in the target set of the source node. 
 */
public class FEdge {

	// Variables specific to public get/set methods.
	FNode source;
	FNode target;
	
	
    public FEdge() 
    {
    }

    public FEdge(FNode source, FNode target) {
        this.source = source;
        this.target = target;
    }

    /**
     *  Sets source FNode
     */
	public void setSource(FNode source)
    {
        this.source = source;
    }

	/**
     *  Sets target FNode
     */
    public void setTarget(FNode target)
    {
        this.target = target;
    }

    /**
     *  Returns source FNode
     */
    public FNode getSource() {
		return source;
	}

    /**
     *  Returns target FNode
     */
	public FNode getTarget() {
		return target;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		final FEdge other = (FEdge) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

    @Override
    public String toString()
    {
       return "" + source.getHandle() + "/" + target.getHandle();
    }

}
