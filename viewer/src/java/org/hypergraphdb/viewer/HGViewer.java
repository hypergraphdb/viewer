package org.hypergraphdb.viewer;

import giny.model.RootGraph;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.awt.Component;
import java.io.IOException;
import java.util.Iterator;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.visual.VisualStyle;

public class HGViewer
{
	private HyperGraph hg;
	private int depth;
	private HGVNetworkView view;
	private HGHandle foc_handle;

	public HGViewer(HyperGraph hg)
	{
		this.hg = hg;
	}
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	public Component focus(HGHandle handle){
		this.foc_handle = handle;
		view = HGVKit.getStandaloneView(hg, foc_handle, depth, null);
		Component c = view.getComponent();
		c.setPreferredSize(new java.awt.Dimension(600,400));
		layout();
		return c;
	}
	
    public void refresh()
    {
    	if(view == null) return;
    	clearView();
    	refreshView();
    }
    
    public void setPainter(HGPersistentHandle typeHandle, NodePainter painter){
    	VisualStyle vs = view.getVisualStyle();
        vs.addNodePainter(typeHandle, painter);
        if(view != null)
        	view.redrawGraph();
    }

	public HGVNetworkView getView()
	{
		return view;
	} 
	    
    private void refreshView()
	{
		try
		{
			final HGWNReader reader = new HGWNReader(hg);
			reader.read(foc_handle, depth, null);
			final int[] nodes = reader.getNodeIndicesArray();
			final int[] edges = reader.getEdgeIndicesArray();
			HGVNetwork net = view.getNetwork();
			for(int i = 0; i < nodes.length; i++)
				net.restoreNode(nodes[i]);
			for(int i = 0; i < edges.length; i++)
				net.restoreEdge(edges[i]);
			layout();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
    
    private void clearView(){
    	RootGraph g = view.getNetwork().getRootGraph();
    	for(Iterator it = view.getEdgeViewsIterator(); it.hasNext();){
    		EdgeView nv = (EdgeView) it.next();
    		view.removeEdgeView(nv.getEdge().getRootGraphIndex());
    		g.removeEdge(nv.getEdge());
    	}
    	for(Iterator it = view.getNodeViewsIterator(); it.hasNext();){
    		NodeView nv = (NodeView) it.next();
    		view.removeNodeView(nv.getNode().getRootGraphIndex());
    		g.removeNode(nv.getNode());
    	}
    }
    
    private void layout(){
    	HGVKit.getPreferedLayout().applyLayout();
		//view.getCanvas().getCamera().animateViewToCenterBounds( 
		//		view.getCanvas().getLayer().getFullBounds(), true, 50l );
	}
    
}
