package org.hypergraphdb.viewer;

import giny.view.EdgeView;
import giny.view.NodeView;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGSystemFlags;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.viewer.painter.DefaultEdgePainter;
import org.hypergraphdb.viewer.painter.DefaultNodePainter;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.view.HGVNetworkView;
import org.hypergraphdb.viewer.visual.VisualStyle;


public class VisualManager
{
	public static final String DEFAULT_STYLE_NAME = "default";
	private static final String APPCONFIG_KEY = "visualManagerStyles";
	  
	private static VisualManager instance;
	
	protected Map<String,VisualStyle> visualStylesMap
	 = new HashMap<String,VisualStyle>();
    protected Set<ChangeListener> listeners = new HashSet<ChangeListener>(); 
    private EdgePainter def_edge_painter = new DefaultEdgePainter();
    private NodePainter def_node_painter = new DefaultNodePainter();
    
    public Set<String> getVisualStyleNames() {
        return visualStylesMap.keySet();
    }
    
    public Collection<VisualStyle> getVisualStyles() {
        return visualStylesMap.values();
    }
    
    public void addVisualStyle(VisualStyle vs) {
        if (vs == null) {return;}
        String name = vs.toString();
        //check for duplicate names
        if (visualStylesMap.keySet().contains(name)) {
            String s = "Duplicate visual style name " + name;
            throw new RuntimeException(s);
        }
        visualStylesMap.put(name, vs);
        fireStateChanged();
    }
    
    public void removeVisualStyle(String name) {
    	if(visualStylesMap.containsKey(name))
    	{
           visualStylesMap.remove(name);
    	   fireStateChanged();
    	}
    }
    public VisualStyle getVisualStyle(String name) {
    	 return (VisualStyle)visualStylesMap.get(name);
    }
    
	public ChangeListener[] getChangeListeners() {
		return listeners.toArray(new ChangeListener[listeners.size()]);
	}
	
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
    
	protected void fireStateChanged(){
		for(ChangeListener l: listeners)
			l.stateChanged(new ChangeEvent(this));
	}
	
	public VisualStyle getDefaultVisualStyle() {
		System.out.println("VM: " + visualStylesMap);
		return getVisualStyle(DEFAULT_STYLE_NAME);
	}

	public VisualManager()
	{
		if (instance != null)
			throw new RuntimeException("Can't construct VisualManager twice...it's a singleton.");
	}
   
	
	public static VisualManager getInstance(){
		if(instance == null){
			HyperGraph graph = AppConfig.getInstance().getGraph();
            instance = (VisualManager)
               hg.getOne(graph, hg.type(VisualManager.class));
            if (instance == null)
            {
            	instance = new VisualManager();
            	instance.addVisualStyle(new VisualStyle(DEFAULT_STYLE_NAME));
             	graph.add(instance, HGSystemFlags.MUTABLE);
            }
//			Map vs = (Map) AppConfig.getInstance().getProperty(APPCONFIG_KEY);
//			if(vs == null)
//			{
//			   instance.visualStylesMap = new HashMap<String,VisualStyle>();
//			   instance.addVisualStyle(new VisualStyle(DEFAULT_STYLE_NAME));
//			  // VisualStyle temp = new VisualStyle("ff");
//			   //VisualStyle[] set = new VisualStyle[]{};
//			   System.out.println("VM - store VisualStyle");
//				//	   AppConfig.getInstance().getProperty("temp66", new VisualStyle("mm")));
//			  // AppConfig.getInstance().setProperty(APPCONFIG_KEY, instance.visualStyles);
//			  // AppConfig.getInstance().setProperty("temp66", temp);
//    		// HyperGraph h =	 AppConfig.hg; 
//    		 //hg.getOne(h, hg.type(AppConfig.class));
//			}
		}
		return instance;
	}
	
	public void applyAppearances(HGVNetworkView network_view)
	{
		//Date start = new Date();
		if(network_view.getVisualStyle()== null)
			network_view.setVisualStyle(
					VisualManager.getInstance().getDefaultVisualStyle());
		network_view.setBackgroundPaint(
				network_view.getVisualStyle().getBackgroundColor());
		/** first apply the node appearance to all nodes */
		applyNodeAppearances(network_view);
		/** then apply the edge appearance to all edges */
		applyEdgeAppearances(network_view);
		/** now apply global appearances */
		/** we rely on the caller to redraw the graph as needed */
		//Date stop = new Date();
		// System.out.println("Time to apply node styles: " + (stop.getTime() -
		// start.getTime()));
	}
	
	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances(HGVNetworkView network_view)
	{
		for (Iterator i = network_view.getNodeViewsIterator(); i.hasNext();)
		{
			NodeView nodeView = (NodeView) i.next();
			if(nodeView == null)
			{
				System.out.println("VM - applyNodeAppearances - NULL NODE");
				continue;
			}
			HGVNode node = (HGVNode) nodeView.getNode();
			HyperGraph hg = network_view.getNetwork().getHyperGraph();
			HGPersistentHandle h = hg.getPersistentHandle(
					hg.getTypeSystem().getTypeHandle(node.getHandle()));
			NodePainter p = network_view.getVisualStyle().getNodePaintersMap().get(h);
			if(p == null)
				p = def_node_painter;
			p.paintNode(nodeView, network_view);
		}
	}
	
	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances(HGVNetworkView network_view)
	{
		 for (Iterator i = network_view.getEdgeViewsIterator(); i.hasNext(); )
		 { 
			 EdgeView edgeView = (EdgeView)i.next();
			 if(edgeView == null)
			 {
				System.out.println("VMM -applyNodeAppearances - NULL NODE");
				continue;
			 }
			 HGVNode node = (HGVNode) ((HGVEdge) edgeView.getEdge()).getSource();
			 HyperGraph hg = network_view.getNetwork().getHyperGraph();
			 HGPersistentHandle h = hg.getPersistentHandle(
						hg.getTypeSystem().getTypeHandle(node.getHandle()));
			 EdgePainter p = network_view.getVisualStyle().getEdgePaintersMap().get(h);
			 if(p == null)
				p = def_edge_painter;
			 p.paintEdge(edgeView, network_view);
		}
	}

	public Map<String, VisualStyle> getVisualStylesMap()
	{
		return visualStylesMap;
	}

	public void setVisualStylesMap(Map<String, VisualStyle> visualStylesMap)
	{
		this.visualStylesMap = visualStylesMap;
	}
}
