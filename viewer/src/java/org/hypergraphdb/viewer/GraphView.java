package org.hypergraphdb.viewer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.viewer.event.GraphViewChangeEvent;
import org.hypergraphdb.viewer.event.GraphViewChangeListener;
import org.hypergraphdb.viewer.event.GraphViewEdgesAddedEvent;
import org.hypergraphdb.viewer.event.GraphViewEdgesRemovedEvent;
import org.hypergraphdb.viewer.event.GraphViewNodesAddedEvent;
import org.hypergraphdb.viewer.event.GraphViewNodesRemovedEvent;
import org.hypergraphdb.viewer.painter.DefaultEdgePainter;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.painter.SimpleLabelTooltipNodePainter;
import org.hypergraphdb.viewer.util.PrimeFinder;
import org.hypergraphdb.viewer.visual.VisualStyle;

import phoebe.PEdgeView;
import phoebe.PNodeView;
import phoebe.event.BirdsEyeView;
import phoebe.event.PEdgeHandler;
import phoebe.event.PEdgeSelectionHandler;
import phoebe.event.PSelectionHandler;
import phoebe.event.PToolTipHandler;
import phoebe.event.SquiggleEventHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * GraphView is responsible for actually getting a graph to show up on the
 * screen.<BR>
 * <BR>
 * HGVKit does not currently define specific classes for NodeViews and
 * EdgeViews, the defaults from the GINY graph library ( namely phoebe.PNodeView
 * and phoebe.PEdgeView ) are most commonly used. Making custom nodes is easy
 * and fun. One must inherit from edu.umd.cs.piccolo.PNode. The Piccolo project
 * is what all of the painting is based on, and is very fast, flexible and
 * powerful. Becoming acquainted with Piccolo is essential for build custom
 * nodes.<BR>
 * <BR>
 * Fortunately, if you just want basic shapes and colors, it's all built into
 * the UI already, and you really need never even use this class. Just learn how
 * to use the VisualManager to accomplish your data to view mappings. 
 */

public class GraphView
{
    static EdgePainter def_edge_painter = new DefaultEdgePainter();
    static NodePainter def_node_painter = new SimpleLabelTooltipNodePainter();

    protected VisualStyle style;
    protected PBasicInputEventHandler keyEventHandler;
    // The Piccolo PCanvas that we will draw on
    protected PCanvas canvas;

    public boolean updateEdges = true;
   
    protected Map<FNode, PNodeView> nodeViewMap;
    protected Map<FEdge, PEdgeView> edgeViewMap;
    // PCS support
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    // A JPanel which contains the Canvas in the middle, and some other stuff around
    protected HGViewer viewComponent;
    // Piccolo Stuff for this class
    protected PSelectionHandler selectionHandler;
    protected PEdgeSelectionHandler edgeSelectionHandler;
    protected PEdgeHandler edgeHandler;
    protected PToolTipHandler toolTipHandler;
    protected SquiggleEventHandler squiggleEventHandler;
    protected boolean edgeSelection = false;
    protected boolean nodeSelection = false;

    // layer
    protected PLayer nodeLayer;
    protected PLayer edgeLayer;
    protected PLayer objectLayer;
    protected PLayer squiggleLayer;
    protected Color DEFAULT_BACKGROUND_COLOR = new java.awt.Color(60, 98, 176);

    protected Set<FNode> nodeSelectionList = new HashSet<FNode>();
    protected Set<FEdge> edgeSelectionList = new HashSet<FEdge>();
    protected static boolean firePiccoloEvents = true;

    protected String identifier;

    protected HyperGraph graph;

    public GraphView(HGViewer comp, HyperGraph db,
            Collection<FNode> nodes, Collection<FEdge> edges)
    {
        this.graph = db;
        this.setIdentifier(db.getLocation());
        nodeViewMap = new HashMap<FNode, PNodeView>(PrimeFinder.nextPrime(nodes
                .size()));
        edgeViewMap = new HashMap<FEdge, PEdgeView>(PrimeFinder.nextPrime(edges
                .size()));
        viewComponent = comp;
        initialize(nodes, edges);
    }

    protected void initialize(Collection<FNode> nodes, Collection<FEdge> edges)
    {
        canvas = new Canvas();
        canvas
                .setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        canvas.setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        getCanvas().getCamera().setPaint(DEFAULT_BACKGROUND_COLOR);
        // Set up Layers
        nodeLayer = new PLayer() {
            public void addChild(int index, PNode child)
            {
                PNode oldParent = child.getParent();
                if (oldParent != null) oldParent.removeChild(child);
                child.setParent(this);
                getChildrenReference().add(index, child);
                child.invalidatePaint();
                invalidateFullBounds();
                if (firePiccoloEvents)
                    firePropertyChange(0, PROPERTY_CHILDREN, null,
                            getChildrenReference());
            }
        };
        edgeLayer = new PLayer() {
            public void addChild(int index, PNode child)
            {
                PNode oldParent = child.getParent();
                if (oldParent != null) oldParent.removeChild(child);
                child.setParent(this);
                getChildrenReference().add(index, child);
                child.invalidatePaint();
                invalidateFullBounds();
                if (firePiccoloEvents)
                    firePropertyChange(0, PROPERTY_CHILDREN, null,
                            getChildrenReference());
            }
        };
        objectLayer = new PLayer();
        squiggleLayer = new PLayer();
        getCanvas().getLayer().addChild(edgeLayer);
        getCanvas().getLayer().addChild(nodeLayer);
        getCanvas().getLayer().addChild(objectLayer);
        // Set up the the Piccolo Event Handlers
        initializeEventHandlers();

        // only create the node/edge view if requested
        createViewableObjects(nodes, edges);
        setNodeSelection(true);
        setEdgeSelection(false);
    }

    protected void initializeEventHandlers()
    {
        // Add a FNode Selection Handler
        selectionHandler = new PSelectionHandler(getCanvas().getLayer(),
                getNodeLayer(), getCanvas().getCamera());
        selectionHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON1_MASK));
        // Add an FEdge Selection Handler
        edgeSelectionHandler = new PEdgeSelectionHandler(
                getCanvas().getLayer(), getEdgeLayer(), getCanvas().getCamera());
        edgeSelectionHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON1_MASK));

        // Only allow panning via Middle Mouse button
        getCanvas().getPanEventHandler().setEventFilter(
                new PInputEventFilter(InputEvent.BUTTON2_MASK));
        PZoomEventHandler zoomer = new PZoomEventHandler() {
            public void dragActivityFinalStep(PInputEvent e)
            {
                // System.out.println( "Scale: "+e.getCamera().getViewScale() );
                if (e.getCamera().getViewScale() < .45)
                {
                    getCanvas().setDefaultRenderQuality(
                            PPaintContext.LOW_QUALITY_RENDERING);
                }
                else
                {
                    getCanvas().setDefaultRenderQuality(
                            PPaintContext.HIGH_QUALITY_RENDERING);
                }
            }
        };
        zoomer.setMinScale(.15);
        zoomer.setMaxScale(7);
        zoomer.setEventFilter(new PInputEventFilter(InputEvent.BUTTON3_MASK));
        getCanvas().setZoomEventHandler(zoomer);
        edgeHandler = new PEdgeHandler(this);
        getCanvas().addInputEventListener(edgeHandler);

        // add the tool tip handler
        toolTipHandler = new PToolTipHandler(getCanvas().getCamera());
        getCanvas().getCamera().addInputEventListener(toolTipHandler);
        // create the Squiggle handler
        squiggleEventHandler = new SquiggleEventHandler(squiggleLayer,
                getCanvas(), this);
        ContextMenuHelper ctxMenuHandler = new ContextMenuHelper(this);
        ctxMenuHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON3_MASK));
        getCanvas().addInputEventListener(ctxMenuHandler);

        getCanvas().getZoomEventHandler().getEventFilter().setAndMask(
                InputEvent.SHIFT_MASK);
    }

    public void redrawGraph()
    {
        getCanvas().setInteracting(true);
        applyAppearances();
        getCanvas().setInteracting(false);
    }

    public HyperGraph getHyperGraph()
    {
        return graph;
    }

    public PEdgeHandler getEdgeHandler()
    {
        return edgeHandler;
    }

    public void nodeSelected(PNodeView node)
    {
        nodeSelectionList.add(node.getNode());
        fireSelectionChanged();
    }

    public void nodeUnselected(PNodeView node)
    {
        nodeSelectionList.remove(node.getNode());
        fireSelectionChanged();
    }

    public void edgeSelected(PEdgeView edge)
    {
        edgeSelectionList.add(edge.getEdge());
        fireSelectionChanged();
    }

    public void edgeUnselected(PEdgeView edge)
    {
        edgeSelectionList.remove(edge.getEdge());
        fireSelectionChanged();
    }

    /**
     * @return a list of the selected PNodeView
     */
    public List<PNodeView> getSelectedNodes()
    {
        ArrayList<PNodeView> selected = new ArrayList<PNodeView>(
                nodeSelectionList.size());
        for (FNode n : nodeSelectionList)
            selected.add(getNodeView(n));
        return selected;
    }

    public PNodeView getSelectedNodeView()
    {
        List<PNodeView> res = getSelectedNodes();
        return (res.size() == 0) ? null : res.get(0);
    }

    /**
     * @return a list of the selected PEdgeView
     */
    public List<PEdgeView> getSelectedEdges()
    {
        ArrayList<PEdgeView> selected = new ArrayList<PEdgeView>(
                edgeSelectionList.size());
        for (FEdge idx : edgeSelectionList)
            selected.add(getEdgeView(idx));
        return selected;
    }

    /**
     * @param the
     *            new Paint for the background
     */
    public void setBackgroundPaint(Paint paint)
    {
        getCanvas().getCamera().setPaint(paint);
    }

    /**
     * @return the backgroundPaint
     */
    public Paint getBackgroundPaint()
    {
        return getCanvas().getCamera().getPaint();
    }

    // ----------------------------------------//
    // Event Handlers
    // ----------------------------------------//
    public boolean isNodeSelectionEnabled()
    {
        return nodeSelection;
    }

    public boolean isEdgeSelectionEnabled()
    {
        return edgeSelection;
    }

    public void setNodeSelection(boolean on)
    {
        if (!nodeSelection && on)
            getCanvas().addInputEventListener(getSelectionHandler());
        if(nodeSelection && !on)
            getCanvas().removeInputEventListener(getSelectionHandler());
        nodeSelection = on;
    }

    public PSelectionHandler getSelectionHandler()
    {
        return selectionHandler;
    }

    public void setEdgeSelection(boolean on)
    {
        if (!edgeSelection && on)
            getCanvas().addInputEventListener(getEdgeSelectionHandler());
        if (edgeSelection && !on)
            getCanvas().removeInputEventListener(getEdgeSelectionHandler());
        edgeSelection = on;
    }

    public PEdgeSelectionHandler getEdgeSelectionHandler()
    {
        return edgeSelectionHandler;
    }

     /**
     * @return the Squiggle Event Handler
     */
    public SquiggleEventHandler getSquiggleHandler()
    {
        return squiggleEventHandler;
    }

    /**
     * 
     * This BirdsEyeView only looks at the nodes.
     * 
     * @return The BirdsEyeView that needs to be put into a frame/dialog/pane
     * 
     */
    public JComponent getBirdsEyeView()
    {
        BirdsEyeView canvas = new BirdsEyeView();
        canvas.connect(getCanvas(), new PLayer[] { getCanvas().getLayer() });
        return canvas;
    }

    /**
     * @return HGViewer that contains this GraphView
     */
    public HGViewer getViewer()
    {
        return viewComponent;
    }

    /**
     * @return The Piccolo PCanvas that the Graph is Drawn on
     */
    public PCanvas getCanvas()
    {
        return canvas;
    }

    /**
     * @return The PLayer that all Nodes are added to
     */
    public PLayer getNodeLayer()
    {
        return nodeLayer;
    }

    /**
     * @return The PLayer that all Edges are added to
     */
    public PLayer getEdgeLayer()
    {
        return edgeLayer;
    }

    /**
     * @return The PLayer that all non-FNode and non-FEdge objects can be added
     *         to
     */
    public PLayer getObjectLayer()
    {
        return objectLayer;
    }

    private void addToNodeLayer(PNodeView node)
    {
        nodeLayer.addChild(node);
    }

    private void addToEdgeLayer(PEdgeView edge)
    {
        edgeLayer.addChild(edge);
    }

    /**
     * This method will create a Viewable PNode derivative for every node and
     * FEdge in the network
     */
    protected void createViewableObjects(Collection<FNode> nodes,
            Collection<FEdge> edges)
    {
        firePiccoloEvents = false;
        long time = System.currentTimeMillis();
        for (FNode node : nodes)
            addNodeView(node);
        System.out.println("Create Nodes took: "  + (System.currentTimeMillis() - time));
        for (FEdge edge : edges)
            addEdgeView(edge);
        firePiccoloEvents = true;
        System.out.println("Create Viewable Object took: "
                + (System.currentTimeMillis() - time));
    }

    /**
     * @return The Unique Identifier of this GraphView
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * 
     * @param new_identifier
     *            The New Identifier for this GraphView
     * 
     */
    public void setIdentifier(String new_identifier)
    {
        if (new_identifier != null)
            if (new_identifier.equals(identifier)) return;
        String old_identifier = identifier;
        identifier = new_identifier;
        pcs.firePropertyChange("identifier", old_identifier, new_identifier);
    }

    /**
     * @return The Current Zoom Level
     */
    public double getZoom()
    {
        return getCanvas().getCamera().getViewScale();
    }

    /**
     * @param d
     *            The New ZoomLevel
     */
    public void setZoom(double d)
    {
        Point2D point = getCanvas().getCamera().getBounds().getCenter2D();
        point = getCanvas().getCamera().localToView(point);
        getCanvas().getCamera().scaleViewAboutPoint(d, point.getX(),
                point.getY());
    }

    /**
     * Fits all Viewable elements onto the Graph
     */
    public void fitContent()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                getCanvas().getCamera().animateViewToCenterBounds(
                        getCanvas().getLayer().getFullBounds(), true, 500l);
            }
        });
    }

    /**
     * nodeViewsList only returns the NodeViews that are explicitly associated
     * with this GraphView
     */
    public List<PNodeView> getNodeViews()
    {
        ArrayList<PNodeView> list = new ArrayList<PNodeView>(getNodeViewCount());
        for (FNode i : nodeViewMap.keySet())
            list.add(nodeViewMap.get(i));
        return list;
    }

    /**
     * 
     */
    public int getNodeViewCount()
    {
        return nodeViewMap.size();
    }

    /**
     * 
     */
    public int getEdgeViewCount()
    {
        return edgeViewMap.size();
    }

    /**
     * @param node
     *            The FNode whose view is requested
     * @return The PNodeView of the given FNode
     * 
     */
    public PNodeView getNodeView(FNode node)
    {
        return nodeViewMap.get(node);
    }

    public List<PEdgeView> getEdgeViews()
    {
        ArrayList<PEdgeView> list = new ArrayList<PEdgeView>(getEdgeViewCount());
        for (FEdge i : edgeViewMap.keySet())
            list.add(edgeViewMap.get(i));
        return list;
    }

     // implements GraphView
    public PEdgeView getEdgeView(FEdge edge)
    {
        return edgeViewMap.get(edge);
    }

    public void applyAppearances()
    {
        if (getVisualStyle() == null)
            setVisualStyle(VisualManager.getInstance().getDefaultVisualStyle());
        setBackgroundPaint(getVisualStyle().getBackgroundColor());
        applyNodeAppearances();
        applyEdgeAppearances();
    }

    /**
     * Recalculates and reapplies all of the node appearances. The visual
     * attributes are calculated by delegating to the NodePainter member of the
     * current visual style.
     */
    public void applyNodeAppearances()
    {
        for (PNodeView nodeView : nodeViewMap.values())
        {
            FNode node = nodeView.getNode();
            HGHandle h = node.getHandle();
            NodePainter p = getPainter(h, false);
            p.paintNode(nodeView, this);
        }
    }
    
    private NodePainter getPainter(HGHandle h, boolean return_default)
    {
        NodePainter p = getVisualStyle(true).getNodePainter(h);
        if (p == null && return_default) 
            p = def_node_painter;
        if(p != null) return p;
        try
        {
           HGHandle typeH = graph.getTypeSystem().getTypeHandle(h);
           return getPainter(typeH, true);
        }catch(HGException ex)
        {
            //do nothing 
            //the node handle pointed to some statically
            //defined HGHandleFactory.makeHandle()
            //with no value
            return def_node_painter;
        }
    }

    /**
     * Recalculates and reapplies all of the edge appearances. The visual
     * attributes are calculated by delegating to the EdgePainter member of the
     * current visual style.
     */
    public void applyEdgeAppearances()
    {
        for (PEdgeView edgeView : edgeViewMap.values())
        {
            FNode node = edgeView.getEdge().getSource();
            HGHandle h = graph.getTypeSystem().getTypeHandle(node.getHandle());
            EdgePainter p = getVisualStyle(true).getEdgePainter(h);
            if (p == null) p = def_edge_painter;
            p.paintEdge(edgeView, this);
        }
    }

    public VisualStyle getVisualStyle(boolean return_default)
    {
        return getVisualStyle() != null ? getVisualStyle() : 
            VisualManager.getInstance().getDefaultVisualStyle();
    }
    
    public VisualStyle getVisualStyle()
    {
        return style;
    }

    public void setVisualStyle(VisualStyle style)
    {
        this.style = style;
        redrawGraph();
    }


    public class Canvas extends PCanvas
    {
        public Canvas()
        {
            super();
            addKeyBindings();
        }

        private void addKeyBindings()
        {
            for (Action a : ActionManager.getInstance().getActions())
            {
                KeyStroke key = (KeyStroke) a.getValue(Action.ACCELERATOR_KEY);
                if (key != null)
                {
                    String name = (String) a.getValue(Action.NAME);
                    getInputMap().put(key, name);
                    getActionMap().put(name, a);
                    // System.out.println("Action: " + name + ":" + key);
                }// else
                // System.out.println("Action: " + name + ":" + key);
            }
        }
        
        public GraphView getView()
        {
            return GraphView.this;
        }

        public void repaint(long a, int x, int y, int w, int h)
        {
            if (((x + w) > 1) || ((y + h) > 2)) super.repaint(a, x, y, w, h);
        }
    };

    private Set<GraphViewChangeListener> view_listeners = new HashSet<GraphViewChangeListener>();

    public void addHGVNetworkChangeListener(GraphViewChangeListener listener)
    {
        view_listeners.add(listener);
    }

    public void removeHGVNetworkChangeListener(GraphViewChangeListener listener)
    {
        view_listeners.remove(listener);
    }

    void fireGraphChanged(GraphViewChangeEvent event)
    {
        for (GraphViewChangeListener l : view_listeners)
            l.graphChanged(event);
    }

    public static interface SelectionListener
    {
        void selectionChanged();
    }

    private Set<SelectionListener> selection_listeners = new HashSet<SelectionListener>();

    public void addSelectionListener(SelectionListener listener)
    {
        selection_listeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener)
    {
        selection_listeners.remove(listener);
    }

    void fireSelectionChanged()
    {
        for (SelectionListener l : selection_listeners)
            l.selectionChanged();
    }

    public int getNodeCount()
    {
        return nodeViewMap.size();
    }

    public int getEdgeCount()
    {
        return edgeViewMap.size();
    }

     /**
     * This will entirely remove a PNodeView/PEdgeView from the GraphView.
     */
    public PNodeView removeNodeView(FNode node)
    {
        if (!nodeViewMap.containsKey(node)) return null;
        PNodeView node_view = getNodeView(node);
        getNodeView(node).removeFromParent();

        nodeViewMap.remove(node);
        nodeSelectionList.remove(node);
        fireGraphChanged(new GraphViewNodesRemovedEvent(this,
                new FNode[] { node }));
        return node_view;
    }

    /**
     * This will entirely remove a PEdgeView from the GraphView.
     */
    public PEdgeView removeEdgeView(PEdgeView edge_view)
    {
        return removeEdgeView(edge_view.getEdge());
    }

    /**
     * This will entirely remove a PEdgeView from the GraphView.
     */
    public PEdgeView removeEdgeView(FEdge e)
    {
        if (!edgeViewMap.containsKey(e)) return null;
        PEdgeView view = getEdgeView(e);
        view.removeFromParent();
        edgeSelectionList.remove(e);
        edgeViewMap.remove(e);
        fireGraphChanged(new GraphViewEdgesRemovedEvent(this,
                new FEdge[] { e }));
        return view;
    }

    /**
     * @param node
     *            the index of a node to have a view created for it
     * @return a new PNodeView based on the node with the given index
     */
    public PNodeView addNodeView(FNode node)
    {
        if (nodeViewMap.containsKey(node)) return nodeViewMap.get(node);
        
        PNodeView node_view = new PNodeView(node, this);
        nodeViewMap.put(node, node_view);
        addToNodeLayer(node_view);
        fireGraphChanged(new GraphViewNodesAddedEvent(this,
                new FNode[] { node }));
        def_node_painter.paintNode(node_view, this);
        return node_view;
    }

    public PEdgeView addEdgeView(FEdge edge)
    {
        if (edgeViewMap.containsKey(edge)) return edgeViewMap.get(edge);
        Object s = graph.get(edge.getSource().getHandle());
        if(!(s instanceof HGLink))
            System.err.println("Impossible PEdgeView - source is not HGLink: " + edge);
        if(!graph.getIncidenceSet(edge.getTarget().getHandle()).contains(
                edge.getSource().getHandle()))
            System.err.println("Impossible PEdgeView - target is not pointed by source: " + edge);
        
        PEdgeView edge_view = new PEdgeView(edge, this);
        addToEdgeLayer(edge_view);
        edgeViewMap.put(edge, edge_view);
        fireGraphChanged(new GraphViewEdgesAddedEvent(this,
                new FEdge[] { edge }));
        return edge_view;
    }

    public FEdge[] getAdjacentEdges(FNode node, boolean incoming,
            boolean outgoing)
    {
        if (node == null || !nodeViewMap.containsKey(node))
            return new FEdge[0];
        HGHandle nH = node.getHandle();
        IncidenceSet handles = graph.getIncidenceSet(nH);
        Set<FEdge> res = new HashSet<FEdge>();
        for (HGHandle h : handles)
        {
            FNode incNode = new FNode(h);
            if (!nodeViewMap.containsKey(incNode)) continue;
            if (outgoing)
            {
                FEdge e = new FEdge(node, incNode);
                if (edgeViewMap.containsKey(e)) res.add(e);
            }

            if (incoming)
            {
                FEdge e = new FEdge(incNode, node);
                if (edgeViewMap.containsKey(e)) res.add(e);
            }
        }
        Object o = graph.get(nH);
        if (o instanceof HGLink)
        {
            HGLink link = ((HGLink) o);
            for (int i = 0; i < link.getArity(); i++)
            {
                FNode incNode = new FNode(link.getTargetAt(i));
                if (!nodeViewMap.containsKey(incNode)) continue;
                if (outgoing)
                {
                    FEdge e = new FEdge(incNode, node);
                    if (edgeViewMap.containsKey(e)) res.add(e);
                }
                if (incoming)
                {
                    FEdge e = new FEdge(node, incNode);
                    if (edgeViewMap.containsKey(e)) res.add(e);
                }
            }
        }
        return res.toArray(new FEdge[res.size()]);
    }

    /**
     * Sets the flagged state to true for all Nodes in the GraphPerspective.
     */
    public void selectAllNodes()
    {
        Set<FNode> changes = new HashSet<FNode>();
        for (PNodeView nv : nodeViewMap.values())
        {
            if (!nv.isSelected())
            {
                changes.add(nv.getNode());
                nv.select();
            }
        }
    }

    /**
     * Sets the flagged state to true for all Edges in the GraphPerspective.
     */
    public void selectAllEdges()
    {
        Set<FEdge> changes = new HashSet<FEdge>();
        for (PEdgeView nv : edgeViewMap.values())
        {
            if (!nv.isSelected())
            {
                changes.add(nv.getEdge());
                nv.select();
            }
        }
    }

    /**
     * Sets the flagged state to false for all Nodes in the GraphPerspective.
     */
    public void unselectAllNodes()
    {
        for (PNodeView v : getSelectedNodes())
            v.unselect();
    }

    /**
     * Sets the flagged state to false for all Edges in the GraphPerspective.
     */
    public void unselectAllEdges()
    {
        for (PEdgeView v : getSelectedEdges())
            v.unselect();
    }

    /**
     * Sets the flagged state defined by the second argument for all Nodes
     * contained in the first argument, which should be a Collection of FNode
     * objects contained in the referenced GraphPerspective. One event will be
     * fired for the full set of changes. This method does nothing if the first
     * argument is null.
     * 
     * @return a Set containing the objects for which the flagged state changed
     */
    public Set<FNode> selectNodes(Collection<FNode> nodesToSet, boolean newState)
    {
        Set<FNode> returnSet = new HashSet<FNode>();
        if (nodesToSet == null) { return returnSet; }
        if (newState == true)
        {
            for (FNode node : nodesToSet)
            {
                if (node == null) continue;
                if (!nodeSelectionList.contains(node))
                {
                    returnSet.add(node);
                    getSelectionHandler().select(getNodeView(node));
                }
            }
        }
        else
        {
            for (FNode node : nodesToSet)
            {
                if (node == null) continue;
                if (nodeSelectionList.contains(node))
                {
                    returnSet.add(node);
                    getSelectionHandler().unselect(getNodeView(node));
                }
            }
        }
        if(returnSet.size() > 0)
           fireSelectionChanged();
        return returnSet;
    }

    /**
     * Sets the flagged state defined by the second argument for all Edges
     * contained in the first argument, which should be a Collection of FEdge
     * objects contained in the referenced GraphPerspective. One event will be
     * fired for the full set of changes. This method does nothing if the first
     * argument is null.
     * 
     * @return a Set containing the objects for which the flagged state changed
     */
    public Set<FEdge> selectEdges(Collection<FEdge> edgesToSet, boolean newState)
    {
        Set<FEdge> returnSet = new HashSet<FEdge>();
        if (edgesToSet == null) { return returnSet; }
        if (newState == true)
        {
            for (FEdge edge: edgesToSet)
            {
                if (!edgeSelectionList.contains(edge))
                {
                    returnSet.add(edge);
                    getSelectionHandler().select(getEdgeView(edge));
                }
            }
        }
        else
        {
            for (FEdge edge: edgesToSet)
            {
                if (edgeSelectionList.contains(edge))
                {
                    returnSet.add(edge);
                    getSelectionHandler().unselect(getEdgeView(edge));
                }
            }
        }
        if(returnSet.size() > 0)
            fireSelectionChanged();
        return returnSet;
    }

}
