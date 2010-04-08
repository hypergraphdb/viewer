package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.phoebe.PEdgeView;

/**
 * The interface responsible for applying visual stuff to a EdgeView (visual representation of the FEdge).
 * Normally(see {@link DefaultEdgePainter}) works in conjunction with the {@link PaintEdgeInfo} which describes the most common and mutable aspects of a EdgeView.
 * Of course you could avoid PaintEdgeInfo and DefaultEdgePainter altogether and work directly with NodePainter and modify the passed in EdgeView whatever way you like.
 * For example: 
 * <code><pre>
&nbsp; public class MyEdgePainter implements EdgePainter
&nbsp; {
&nbsp;    public void paintEdge(PEdgeView v)
&nbsp;    {
&nbsp;       //draws the label's background in white
&nbsp;       //which is not supported by default painter
&nbsp;       v.getLabel().setPaint(Color.WHITE);
&nbsp;    }
&nbsp; }
 *</pre></code>
 * 
 */
public interface EdgePainter
{
    /**
     * Paints the given PEdgeView
     * @param edgeView the edge
     */
	public void paintEdge(PEdgeView edgeView);
}
