package org.hypergraphdb.viewer.layout;

import java.awt.event.ActionEvent;

import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.phoebe.PNodeView;
import org.hypergraphdb.viewer.util.HGVAction;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Action that calls specific layout on the current GraphView 
 *
 */
public class LayoutAction extends HGVAction
{
	protected Layout layout;
	static int zero = java.awt.event.KeyEvent.VK_0;
	public LayoutAction(Layout layout, int i)
	{
		super(layout.getName());
		this.layout = layout;
		setAcceleratorCombo(zero + i, ActionEvent.CTRL_MASK);
	}
	
	public void action(HGViewer viewer) throws Exception
    {
		GraphView view = viewer.getView();
		layout.applyLayout(view);
		PNodeView sel = view.getSelectedNodeView();
		PBounds b = (sel != null) ? sel.getFullBounds() :
        	view.getCanvas().getLayer().getFullBounds();
	    view.getCanvas().getCamera().animateViewToCenterBounds(b, false, 0 );
    }
    
}
