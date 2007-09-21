package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PEdgeView;

public interface EdgePainter
{
	public void paintEdge(PEdgeView edgeView, HGVNetworkView network_view);
}
