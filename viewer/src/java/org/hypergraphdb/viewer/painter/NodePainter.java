package org.hypergraphdb.viewer.painter;

import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 * <p>
 * The interface responsible for applying visual stuff to a NodeView (visual representation of the FNode).
 * Normally(see {@link DefaultNodePainter}) works in conjunction with the {@link PaintNodeInfo} which describes the most common and mutable aspects of a NodeView.
 * Of course you could avoid PaintNodeInfo and DefaultNodePainter altogether and work directly with NodePainter and modify the passed in NodeView whatever way you like.
 * </p>
 * 
 * For example the following painter will draw an additional label on the NodeView: 
 * <code> <pre>
&nbsp;  import org.hypergraphdb.viewer.phoebe.util.PLabel;
&nbsp;  public class MyNodePainter implements  NodePainter
&nbsp;  {
&nbsp;     public void paintNode(PNodeView v)
&nbsp;     {
&nbsp;        if(v.getChildrenCount() == 1)
&nbsp;        {
&nbsp;           PLabel l = new PLabel("Under Label", v);
&nbsp;           l.setLabelLocation(PLabel.SOUTH);
&nbsp;           v.addChild(l);
&nbsp;        }
&nbsp;     }
&nbsp;  }
 *</pre></code>
 */
public interface NodePainter
{
    /**
     * Paints the given NodeView
     * @param nodeView
     */
	public void paintNode(PNodeView nodeView);
}
