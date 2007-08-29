package org.hypergraphdb.viewer;

import giny.model.*;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.viewer.giny.HGVRootGraph;

public class HGVEdge implements HGVBaseElement, giny.model.Edge {

	// Variables specific to public get/set methods.
	HGVRootGraph m_rootGraph = null;
    int m_rootGraphIndex = 0;
	//String m_identifier = null;
	HGHandle type = null;

	public HGVEdge(RootGraph root, int rootGraphIndex) {
		this.m_rootGraph = (HGVRootGraph) root;
		this.m_rootGraphIndex = rootGraphIndex;
		//this.m_identifier = new Integer(m_rootGraphIndex).toString();
	}

	public giny.model.Node getSource() {
		return m_rootGraph.getNode(m_rootGraph
				.getEdgeSourceIndex(m_rootGraphIndex));
	}

	public giny.model.Node getTarget() {
		return m_rootGraph.getNode(m_rootGraph
				.getEdgeTargetIndex(m_rootGraphIndex));
	}

	public boolean isDirected() {
		return m_rootGraph.isEdgeDirected(m_rootGraphIndex);
	}

	public RootGraph getRootGraph() {
		return m_rootGraph;
	}

	public int getRootGraphIndex() {
		return m_rootGraphIndex;
	}

	public String getIdentifier() {
		return "" + type; //m_identifier;
	}

	public boolean setIdentifier(String new_id) {
		//m_identifier = new_id;
		//m_rootGraph.setEdgeIdentifier(type, m_rootGraphIndex);
		return true;
	}

	/**
	 * @return Returns the type.
	 */
	public HGHandle getHandle() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setHandle(HGHandle type) {
		this.type = type;
	}
}