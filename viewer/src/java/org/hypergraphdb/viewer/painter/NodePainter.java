package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 * <p>
 * The interface responsible for applying visual stuff to node
 * </p>
 * 
 * @author Konstantin Vandev
 *
 */
public interface NodePainter
{
	public void paintNode(PNodeView nodeView);
}
