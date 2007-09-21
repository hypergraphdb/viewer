package org.hypergraphdb.viewer;

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
import org.hypergraphdb.viewer.visual.VisualStyle;
import phoebe.PEdgeView;


public class VisualManager
{
	public static final String DEFAULT_STYLE_NAME = "default";
	private static VisualManager instance;
	
	protected Map<String,VisualStyle> visualStylesMap
	 = new HashMap<String,VisualStyle>();
    protected Set<ChangeListener> listeners = new HashSet<ChangeListener>(); 
     
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
		}
		return instance;
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
