package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.GraphView;
import phoebe.PEdgeView;

public interface EdgePainter
{
	public void paintEdge(PEdgeView edgeView, GraphView network_view);
}
