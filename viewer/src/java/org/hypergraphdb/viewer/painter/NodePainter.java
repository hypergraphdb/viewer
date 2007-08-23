package org.hypergraphdb.viewer.painter;

import giny.view.NodeView;
import org.hypergraphdb.viewer.view.HGVNetworkView;

public interface NodePainter
{
	public void paintNode(NodeView nodeView, HGVNetworkView network_view);
}
