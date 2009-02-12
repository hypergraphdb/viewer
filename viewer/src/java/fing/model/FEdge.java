package fing.model;

public class FEdge {

	// Variables specific to public get/set methods.
	FNode source = null;
	FNode target;
	String m_identifier = null;

	// Package visible constructor.
	FEdge() {
	}

	public FEdge(FNode source, FNode target) {
		this.source = source;
		this.target = target;
	}

	public FNode getSource() {
		return source;
	}

	public FNode getTarget() {
		return target;
	}

	public String getIdentifier() {
		return m_identifier;
	}

	public boolean setIdentifier(String new_id) {
		m_identifier = new_id;
		return true;
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

}
