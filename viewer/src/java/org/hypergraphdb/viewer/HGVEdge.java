package org.hypergraphdb.viewer;

import giny.model.*;
import org.hypergraphdb.viewer.giny.HGVRootGraph;

public class HGVEdge implements giny.model.Edge
{
     // Variables specific to public get/set methods.
	HGVRootGraph m_rootGraph = null;
    int m_rootGraphIndex = 0;
	
	public HGVEdge(RootGraph root, int rootGraphIndex) {
		this.m_rootGraph = (HGVRootGraph) root;
		this.m_rootGraphIndex = rootGraphIndex;
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
		return getSource().getIdentifier() + "/" + getTarget().getIdentifier();
	}

	public boolean setIdentifier(String new_id) {
		return true;
	}

}