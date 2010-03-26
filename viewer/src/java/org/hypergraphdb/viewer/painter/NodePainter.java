package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 * <p>
 * The interface responsible for applying visual stuff to node
 * </p>
 * 
 */
public interface NodePainter
{
    /**
     * Paints the given NodeView
     * @param nodeView
     */
	public void paintNode(PNodeView nodeView);
}
