package org.hypergraphdb.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.hypergraphdb.HGSystemFlags;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.viewer.visual.VisualStyle;


/**
 * Singleton responsible for management of visual styles. 
 * @author Konstantin Vandev
 */
public class VisualManager
{
	public static final String DEFAULT_STYLE_NAME = "default";
	private static VisualManager instance;
	
	protected Map<String,VisualStyle> visualStylesMap
	 = new HashMap<String,VisualStyle>();
    protected Set<ChangeListener> listeners = new HashSet<ChangeListener>(); 
     
    
    /**
     * Returns the names of all defined visual styles 
     * @return string collection
     */
    public Collection<String> getVisualStyleNames() {
        return visualStylesMap.keySet();
    }
    
    /**
     * Returns all defined visual styles 
     * @return collection
     */
    public Collection<VisualStyle> getVisualStyles() {
        return visualStylesMap.values();
    }
    
    /**
     * Adds a VisualStyle, throws RuntimeException if a style 
     * with the same name already exists  
     * @param vs  The style to be added
     */
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
    
    /**
     * Removes a VisualStyle given its name
     * @param vs  The style name
     */
    public void removeVisualStyle(String name) {
    	if(visualStylesMap.containsKey(name))
    	{
           visualStylesMap.remove(name);
    	   fireStateChanged();
    	}
    }
    
    /**
     * Returns a VisualStyle given its name
     * @param vs  The style name
     */
    public VisualStyle getVisualStyle(String name) {
    	 return visualStylesMap.get(name);
    }
    
    
	/**
	 * Returns an array with all attached change listeners 
	 * @return The array
	 */
	public ChangeListener[] getChangeListeners() {
		return listeners.toArray(new ChangeListener[listeners.size()]);
	}
	
	/**
	 * Adds a change listener
	 * @param l The listener
	 */
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
	/**
     * Removes a change listener
     * @param l The listener
     */
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
    
	protected void fireStateChanged()
	{
		for(ChangeListener l: listeners)
			l.stateChanged(new ChangeEvent(this));
		save();
	}
	
	
	/**
	 * Persist the VisualManager it the configuration HG
	 * See AppConfig for more details. 
	 */
	public void save()
	{
	   // System.out.println("VisualManager saving styles: " + this);
	    try{
	       HyperGraph graph = AppConfig.getInstance().getGraph();
           graph.update(this);
        }catch(Throwable t)
        {
            t.printStackTrace();
        }
	}
	
	 /**
     * Returns the  default VisualStyle
     */
	public VisualStyle getDefaultVisualStyle() {
		//System.out.println("VM: " + visualStylesMap);
		return getVisualStyle(DEFAULT_STYLE_NAME);
	}

	
	/**
	 * Don't call this constructor. It will throw RuntimeException.
	 */
	public VisualManager()
	{
		if (instance != null)
			throw new RuntimeException("Can't construct VisualManager twice...it's a singleton.");
	}
   
	
	/**
	 * Returns the one and only instance of the VisualManager
	 */
	public static VisualManager getInstance(){
		if(instance == null){
			HyperGraph graph = AppConfig.getInstance().getGraph();
            instance = (VisualManager)
               hg.getOne(graph, hg.type(VisualManager.class));
            if (instance == null)
            {
            	instance = new VisualManager();
            	graph.add(instance, HGSystemFlags.MUTABLE);
            	instance.addVisualStyle(new VisualStyle(DEFAULT_STYLE_NAME));
            }
		}
		return instance;
	}
	
	/**
     * Returns the map with all styles and their names
     */
	public Map<String, VisualStyle> getVisualStylesMap()
	{
		return visualStylesMap;
	}

	/**
     * Sets the map with all styles and their names
     */
	public void setVisualStylesMap(Map<String, VisualStyle> visualStylesMap)
	{
		this.visualStylesMap = visualStylesMap;
	}
}
