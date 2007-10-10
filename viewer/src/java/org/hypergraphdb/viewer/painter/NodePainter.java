package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PNodeView;

/**
 * <p>
 * The interface 
 * </p>
 * 
 * @author Konstantin Vandev
 *
 */
public interface NodePainter
{
	public void paintNode(PNodeView nodeView, HGVNetworkView network_view);
}
