//----------------------------------------------------------------------------
// $Revision: 1.4 $
// $Date: 2006/02/17 17:42:21 $
// $Author: bizi $
//----------------------------------------------------------------------------
package org.hypergraphdb.viewer.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;

/**
 * This class contains a collection of node and edge painters used in HGViewer. 
 * Painters are automatically kept in two different maps, one of which is persistable. This allows
 * the use of anonymous inline painters and such provided through scripting.
 * */
/**
 * @author Konstantin Vandev
 *
 */
public class VisualStyle implements Cloneable
{

    String name = "default";
    private Color backgroundColor = new Color(255, 255, 204) ;
    protected int dupCount = 0;
    protected Map<HGHandle, NodePainter> nodePaintersMap = new HashMap<HGHandle, NodePainter>();
    protected Map<HGHandle, NodePainter> npNodePaintersMap = new HashMap<HGHandle, NodePainter>();

    protected Map<HGHandle, EdgePainter> edgePaintersMap = new HashMap<HGHandle, EdgePainter>();
    protected Map<HGHandle, EdgePainter> npEdgePaintersMap = new HashMap<HGHandle, EdgePainter>();

    /**
     * Perform deep copy of this VisualStyle.
     */
    public Object clone() throws CloneNotSupportedException
    {
        VisualStyle copy = new VisualStyle(name);
        String dupeFreeName;
        if (dupCount != 0)
        {
            int dupeCountIndex = name.lastIndexOf(new Integer(dupCount)
                    .toString());
            if (dupeCountIndex == -1) dupeFreeName = new String(name);
            else
                dupeFreeName = name.substring(0, dupeCountIndex);
        }
        else
            dupeFreeName = new String(name);
        copy.name = dupeFreeName;
        copy.dupCount++;
        copy.nodePaintersMap = new HashMap<HGHandle, NodePainter>(
                nodePaintersMap);
        copy.edgePaintersMap = new HashMap<HGHandle, EdgePainter>(
                edgePaintersMap);
        return copy;
    }

    public VisualStyle()
    {
    }

    /**
     * Simple constructor, creates named style.
     */
    public VisualStyle(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor with new name. Creates a default object if the first
     * argument is null, otherwise copies the members of the first argument. The
     * name of this new VisualStyle will be equal to the second argument; the
     * caller should ensure that this is a unique name.
     * 
     * @throws NullPointerException
     *             if the second argument is null
     */
    public VisualStyle(VisualStyle toCopy, String newName)
    {
        if (newName == null)
        {
            String s = "Unexpected null name in VisualStyle constructor";
            throw new NullPointerException(s);
        }
        setName(newName);
        if (toCopy == null) { return; }
    }

    /**
     * Returns the name of this object, as returned by getName.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of this object.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this visual style.
     * 
     * @param n
     *            the new name
     */
    public void setName(String n)
    {
        name = n;
    }

    /**
     * Returns the EdgePainter's map
     * @return the map
     */
    public Map<HGHandle, EdgePainter> getEdgePaintersMap()
    {
        return edgePaintersMap;
    }

    /**
     * Sets the EdgePainter's map
     * @param the new map
     */
    public void setEdgePaintersMap(Map<HGHandle, EdgePainter> edgePaintersMap)
    {
        this.edgePaintersMap = edgePaintersMap;
    }

    /**
     * Returns the NodePainter's map
     * @return the map
     */
    public Map<HGHandle, NodePainter> getNodePaintersMap()
    {
        return nodePaintersMap;
    }
    
    /**
     * Sets the NodePainter's map
     * @param the new map
     */
    public void setNodePaintersMap(Map<HGHandle, NodePainter> nodePaintersMap)
    {
        this.nodePaintersMap = nodePaintersMap;
    }

    /**
     * Returns the map of non-persistent node painters
     * @return the map
     */
    public Map<HGHandle, NodePainter> getNonPersistentNodePaintersMap()
    {
        return this.npNodePaintersMap;
    }
    
    /**
     * Returns the map of non-persistent edge painters
     * @return the map
     */
    public Map<HGHandle, EdgePainter> getNonPersistentEdgePaintersMap()
    {
        return npEdgePaintersMap;
    }
    
    /**
     * Returns the node painter for given type handle
     * @param h the type handle
     * @return the node painter
     */
    public NodePainter getNodePainter(HGHandle h)
    {
        NodePainter p = nodePaintersMap.get(h);
        return (p == null) ? npNodePaintersMap.get(h) : p;
    }

    /**
     * Returns the edge painter for given type handle
     * @param h the type handle
     * @return the edge painter
     */
    public EdgePainter getEdgePainter(HGHandle h)
    {
        EdgePainter p = edgePaintersMap.get(h);
        return (p == null) ? npEdgePaintersMap.get(h) : p;
    }

    /**
     * Adds a node painter for given type handle
     * @param h the type handle
     * @param p the node painter
     */
    public void addNodePainter(HGHandle h, NodePainter p)
    {
        if (p.getClass().getPackage() == null) 
            npNodePaintersMap.put(h, p);
        else
            nodePaintersMap.put(h, p);
    }

    /**
     * Adds an edge painter for given type handle
     * @param h the type handle
     * @param p the edge painter
     */
    public void addEdgePainter(HGHandle h, EdgePainter p)
    {
        if (p.getClass().getPackage() == null) 
            npEdgePaintersMap.put(h, p);
        else
            edgePaintersMap.put(h, p);
    }

    /**
     * Remove the node painter for given type handle
     * @param h the type handle
     */
    public void removeNodePainter(HGHandle h)
    {
        npEdgePaintersMap.remove(h);
        nodePaintersMap.remove(h);
    }

    /**
     * Remove the edge painter for given type handle
     * @param h the type handle
     */
    public void removeEdgePainter(HGHandle h)
    {
        npEdgePaintersMap.remove(h);
        edgePaintersMap.remove(h);
    }

    
    /**
     * Returns the color used for background 
     * @return the color
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Sets the color used for background 
     * @param the color
     */
    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
