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

//----------------------------------------------------------------------------
/**
 * This class encapsulates a full set of visual mapping specifications for
 * org.hypergraphdb.viewer. Currently this is implemented by holding a reference
 * to three appearance calculators, one for nodes, one for edges, and one for
 * global visual attributes.
 */
public class VisualStyle implements Cloneable
{

    String name = "default";
    private Color backgroundColor = Color.white;
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
     * Simple constructor, creates default node/edge/global appearance
     * calculators.
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

    public Map<HGHandle, EdgePainter> getEdgePaintersMap()
    {
        return edgePaintersMap;
    }

    public void setEdgePaintersMap(Map<HGHandle, EdgePainter> edgePaintersMap)
    {
        this.edgePaintersMap = edgePaintersMap;
    }

    public Map<HGHandle, NodePainter> getNodePaintersMap()
    {
        return nodePaintersMap;
    }

    public Map<HGHandle, NodePainter> getNonPersistentNodePaintersMap()
    {
        return this.npNodePaintersMap;
    }

    public Map<HGHandle, EdgePainter> getNonPersistentEdgePaintersMap()
    {
        return npEdgePaintersMap;
    }

    public void setNodePaintersMap(Map<HGHandle, NodePainter> nodePaintersMap)
    {
        this.nodePaintersMap = nodePaintersMap;
    }

    public NodePainter getNodePainter(HGHandle h)
    {
        NodePainter p = nodePaintersMap.get(h);
        return (p == null) ? npNodePaintersMap.get(h) : p;
    }

    public EdgePainter getEdgePainter(HGHandle h)
    {
        EdgePainter p = edgePaintersMap.get(h);
        return (p == null) ? npEdgePaintersMap.get(h) : p;
    }

    public void addNodePainter(HGHandle h, NodePainter p)
    {
        if (p.getClass().getPackage() == null) 
            npNodePaintersMap.put(h, p);
        else
            nodePaintersMap.put(h, p);
    }

    public void addEdgePainter(HGHandle h, EdgePainter p)
    {
        if (p.getClass().getPackage() == null) 
            npEdgePaintersMap.put(h, p);
        else
            edgePaintersMap.put(h, p);
    }

    public void removeNodePainter(HGHandle h)
    {
        npEdgePaintersMap.remove(h);
        nodePaintersMap.remove(h);
    }

    public void removeEdgePainter(HGHandle h)
    {
        npEdgePaintersMap.remove(h);
        edgePaintersMap.remove(h);
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }
}
