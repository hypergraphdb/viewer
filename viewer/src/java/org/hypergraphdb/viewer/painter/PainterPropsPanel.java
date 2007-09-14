package org.hypergraphdb.viewer.painter;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class PainterPropsPanel extends PropertySheetPanel
{
	Object painter;
	public PainterPropsPanel(){
		setPreferredSize(new Dimension(250,200));
		setDescriptionVisible(true);
		setSortingCategories(true);
		setSortingProperties(true);
		setRestoreToggleStates(true);
	}
	
	public void init()
	{
		new BeanBinder(painter, this);
	}

	public Object getPainter()
	{
		return painter;
	}

	public void setPainter(Object painter)
	{
		this.painter = painter;
		init();
	}

	

}
