package org.hypergraphdb.viewer.layout;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;

public class SpringLayout implements Layout
{
	public String getName()
	{
		return "Spring";
	}
	
	public void applyLayout(){
		HGVNetworkView view = HGVKit.getCurrentView();
		if(view == null) return;
		JUNGSpringLayout l = new JUNGSpringLayout(view);
		l.doLayout();
	}
}
