package org.hypergraphdb.viewer.layout;

import java.awt.event.ActionEvent;
import java.util.List;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PNodeView;
import edu.umd.cs.piccolo.util.PBounds;

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
		List<PNodeView> sel = view.getSelectedNodes();
		PBounds b = (sel.size() > 0) ? sel.get(0).getFullBounds() :
        	view.getCanvas().getLayer().getFullBounds();
	    view.getCanvas().getCamera().animateViewToCenterBounds(b, false, 0 );
    }
    
}
