package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.phoebe.PEdgeView;

/**
 * <p>
 * The interface responsible for applying visual stuff to edge
 * </p>
 */
public interface EdgePainter
{
    /**
     * Paints the given PEdgeView
     * @param edgeView the edge
     */
	public void paintEdge(PEdgeView edgeView);
}
