package org.hypergraphdb.viewer.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;

import phoebe.PGraphView;

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
	
	public void actionPerformed(ActionEvent event)
    {
		layout.applyLayout();
		//PGraphView view = HGVKit.getCurrentView();
	    //view.getCanvas().getCamera().animateViewToCenterBounds( 
	    //		view.getCanvas().getLayer().getFullBounds(), false, 1550l );
    }
    
}
