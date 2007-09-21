package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PNodeView;

public interface NodePainter
{
	public void paintNode(PNodeView nodeView, HGVNetworkView network_view);
}
