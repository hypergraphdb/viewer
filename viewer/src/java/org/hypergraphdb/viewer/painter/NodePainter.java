package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.GraphView;
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
	public void paintNode(PNodeView nodeView, GraphView network_view);
}
