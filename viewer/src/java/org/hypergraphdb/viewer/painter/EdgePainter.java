package org.hypergraphdb.viewer.painter;

import giny.view.EdgeView;
import org.hypergraphdb.viewer.HGVNetworkView;

public interface EdgePainter
{
	public void paintEdge(EdgeView edgeView, HGVNetworkView network_view);
}
