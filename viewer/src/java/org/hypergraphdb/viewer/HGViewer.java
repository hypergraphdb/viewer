package org.hypergraphdb.viewer;

import edu.umd.cs.piccolo.util.PBounds;
import fing.model.FNode;
import fing.model.FRootGraph;
import java.awt.Component;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.util.CloseMe;
import org.hypergraphdb.viewer.hg.HGWNReader;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.visual.VisualStyle;
import phoebe.PEdgeView;
import phoebe.PGraphView;
import phoebe.PNodeView;

public class HGViewer implements Serializable
{
	private transient HyperGraph hg;
	private transient int depth;
	private transient HGVNetworkView view;
	private transient HGHandle foc_handle;
	private transient VisualStyle tmp_style = new VisualStyle("tmp");
	private HGALGenerator generator = null;
		
	private HGViewer()
	{
		
	}
	
	public HGViewer(HyperGraph hg)
	{
		this.hg = hg;
	}
	
	public void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public HGALGenerator getGenerator()
	{
		if (generator == null)
			generator = new DefaultALGenerator(hg, null, null);
		return generator;
	}
	
	public void setGenerator(HGALGenerator generator)
	{
		if (this.generator != null && this.generator instanceof CloseMe)
			((CloseMe)this.generator).close();
		this.generator = generator;		
	}
	
	public Component focus(HGHandle handle, HGALGenerator generator)
	{
		
		//we've been already focused
		if(view != null)
			tmp_style = view.self_style;
		this.foc_handle = handle;
		HGWNReader reader = new HGWNReader(hg);
		reader.read(handle, depth, generator); 
		view = HGVKit.getStandaloneView(hg, reader);
		for(HGPersistentHandle h: tmp_style.getNodePaintersMap().keySet())
			view.self_style.addNodePainter(h, tmp_style.getNodePainter(h));
		for(HGPersistentHandle h: tmp_style.getEdgePaintersMap().keySet())
			view.self_style.addEdgePainter(h, tmp_style.getEdgePainter(h));
		for(HGPersistentHandle h: tmp_style.getNonPersistentNodePaintersMap().keySet())
			view.self_style.addNodePainter(h, tmp_style.getNodePainter(h));
		for(HGPersistentHandle h: tmp_style.getNonPersistentEdgePaintersMap().keySet())
			view.self_style.addEdgePainter(h, tmp_style.getEdgePainter(h));
		
		Component c = view.getComponent();
		c.setPreferredSize(new java.awt.Dimension(600,400));
		layout();
		view.redrawGraph();
		FNode node = HGVKit.getHGVNode(handle, false); 
		view.getNodeView(node).setSelected(true);
		view.getCanvas().getCamera().animateViewToCenterBounds( 
				view.getNodeView(node).getFullBounds(), false, 1550l );
		return c;		
	}
	
	public Component focus(HGHandle handle)
	{
		return focus(handle, getGenerator());
		//we've been already focused
/*		if(view != null)
			tmp_style = view.self_style;
		this.foc_handle = handle;
		view = HGVKit.getStandaloneView(hg, foc_handle, depth, null);
		for(HGPersistentHandle h: tmp_style.getNodePaintersMap().keySet())
			view.self_style.addNodePainter(h, tmp_style.getNodePainter(h));
		for(HGPersistentHandle h: tmp_style.getEdgePaintersMap().keySet())
			view.self_style.addEdgePainter(h, tmp_style.getEdgePainter(h));
		for(HGPersistentHandle h: tmp_style.getNonPersistentNodePaintersMap().keySet())
			view.self_style.addNodePainter(h, tmp_style.getNodePainter(h));
		for(HGPersistentHandle h: tmp_style.getNonPersistentEdgePaintersMap().keySet())
			view.self_style.addEdgePainter(h, tmp_style.getEdgePainter(h));
		
		Component c = view.getComponent();
		c.setPreferredSize(new java.awt.Dimension(600,400));
		layout();
		view.redrawGraph();
		FNode node = HGVKit.getHGVNode(handle, false); 
		view.getNodeView(node).setSelected(true);
		view.getCanvas().getCamera().animateViewToCenterBounds( 
				view.getNodeView(node).getFullBounds(), false, 1550l );
		return c; */
	}
	
    public void refresh()
    {
    	if(view == null) return;
    	clearView();
    	refreshView();
    }
    
    public void setPainter(HGHandle typeHandle, NodePainter painter)
    {
    	if(view != null){
    	  view.self_style.addNodePainter(hg.getPersistentHandle(typeHandle), painter);
          view.redrawGraph();
        }else 
        	tmp_style.addNodePainter(hg.getPersistentHandle(typeHandle), painter);
    }
    
    public void setStyle(VisualStyle vs){
    	if(view == null) {
    		tmp_style = vs;
    		return;
    	}
    	view.setVisualStyle(vs);
        view.redrawGraph();
    }
    
    public VisualStyle getStyle(){
    	return (view == null) ? tmp_style: view.getVisualStyle();
    }

	public HGVNetworkView getView()
	{
		return view;
	} 
	
	public void setDefaultNodePainter(NodePainter p){
		if(p != null)
			HGVNetworkView.def_node_painter = p;
	}
	
	public void setDefaultEdgePainter(EdgePainter p){
		if(p != null)
			HGVNetworkView.def_edge_painter = p;
	}
	
	public NodePainter getDefaultNodePainter(){
		return HGVNetworkView.def_node_painter;
	}
	
	public EdgePainter getDefaultEdgePainter(){
		return	HGVNetworkView.def_edge_painter;
	}
	    
    private void refreshView()
	{
		final HGWNReader reader = new HGWNReader(hg);
		reader.read(foc_handle, depth, getGenerator());
		final int[] nodes = reader.getNodeIndicesArray();
		final int[] edges = reader.getEdgeIndicesArray();
		HGVNetwork net = view.getNetwork();
		for(int i = 0; i < nodes.length; i++)
			net.restoreNode(nodes[i]);
		for(int i = 0; i < edges.length; i++)
			net.restoreEdge(edges[i]);
		layout();
	}
    
    private void clearView(){
    	FRootGraph g = view.getNetwork().getRootGraph();
    	for(Iterator<PEdgeView> it = view.getEdgeViewsIterator(); it.hasNext();){
    		PEdgeView nv = (PEdgeView) it.next();
    		view.removeEdgeView(nv.getEdge().getRootGraphIndex());
    		g.removeEdge(nv.getEdge());
    	}
    	for(Iterator<PNodeView> it = view.getNodeViewsIterator(); it.hasNext();){
    		PNodeView nv = (PNodeView) it.next();
    		view.removeNodeView(nv.getNode().getRootGraphIndex());
    		g.removeNode(nv.getNode());
    	}
    }
    
    private void layout(){
    	HGVKit.getPreferedLayout().applyLayout();
	}
    
    private void writeObject(java.io.ObjectOutputStream s)
    throws java.io.IOException
	{
    	s.writeInt(depth);
    	s.writeObject(hg.getStore().getDatabaseLocation());
    	s.writeObject(hg.getPersistentHandle(foc_handle));
    }
    
    private void readObject(java.io.ObjectInputStream s)
    throws java.lang.ClassNotFoundException,
	     java.io.IOException{
    	depth = s.readInt();
    	String loc = (String) s.readObject();
    	HGPersistentHandle h = (HGPersistentHandle) s.readObject();
    	hg = new HyperGraph(loc);
    	this.focus(h);
    }

}
