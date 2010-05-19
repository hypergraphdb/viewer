package org.hypergraphdb.viewer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;
import org.hypergraphdb.viewer.phoebe.event.BirdsEyeView;
import org.hypergraphdb.viewer.phoebe.event.PEdgeHandler;
import org.hypergraphdb.viewer.phoebe.event.PEdgeSelectionHandler;
import org.hypergraphdb.viewer.phoebe.event.PNodeSelectionHandler;
import org.hypergraphdb.viewer.phoebe.event.PToolTipHandler;
import org.hypergraphdb.viewer.phoebe.event.SquiggleEventHandler;
import org.hypergraphdb.viewer.visual.VisualStyle;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * GraphView is the main Piccolo component which displays the graph on the
 * screen using  {@link org.hypergraphdb.viewer.phoebe.PNodeView} and
 * {@link org.hypergraphdb.viewer.phoebe.PEdgeView} instances to visually represent nodes and edges.
 * For its internal model GraphView uses {@link FNode} and {@link FEdge} classes. FNode simply encapsulate a HGHandle
 * form the viewed HG and FEdge represents a relation between two FNodes e.g. a HGLink(source node) and one of 
 * its target set members(target node). Based on this model GraphView creates and displays above mentioned
 * PNodeViews and PEdgeViews. 
 * GraphView has some pre-built features as: zooming, few selection modes, hand-drawing mode
 * (squiggle), visual styling support and customizable node/edge painting.
 * <h3>Zooming</h3>
 * <li>Right Mouse Button Down + Drag Left OR Alt + Up = Zoom In</li>
 * <li>Right Mouse Button Down + Drag Right OR Alt + Down = Zoom Out</li> 
 * <h3>Selection modes</h3>   
 *  GraphView has 3 selection modes:
 * <li>Node selection - only nodes are selectable either by left click or mouse drag</li>
 * <li>Edge selection - only edges are selectable</li> 
 * <li>Nodes & Edges selection - nodes and edges are selectable</li> 
 * <h3>Squiggle</h3> 
 *  You can draw by hand over the graph to add some comments or emphasize on some node, edge etc.
 *  To switch to this mode you could use menu command, call <code>HGVKit.setSquiggleState(true)</code> - 
 *  which works for all opened viewers or use the GraphView's own squiggleHandler. 
 *  <h3>Visual Appearance</h3>
 *  GraphView displays PNodeView and PEdgeView by using instances of  {@link org.hypergraphdb.viewer.painter.DefaultNodePainter} and
 *  {@link org.hypergraphdb.viewer.painter.DefaultEdgePainter}. You can define your own painters according to the type of the node's handle - 
 *  <code>graph.getTypeSystem().getTypeHandle(graph.get(node.getHandle()).getClass())</code>. Note that edge painters are defined according the edge's source node. 
 *  Those painters are stored in a {@link org.hypergraphdb.viewer.visual.VisualStyle} and styles are stored in a {@link VisualManager}.
 *  By default GraphView uses a style named "default", but you could simply define your own styles and switch between them.
 *   
 */
public class GraphView
{
    // if set, checks the edge node for consistency e.g 
    //source node should be a link and should point to target node
    private static boolean check_edge_consistency = false;

    static EdgePainter def_edge_painter = new DefaultEdgePainter();
    static NodePainter def_node_painter = new SimpleLabelTooltipNodePainter();

    protected Set<GraphViewChangeListener> view_listeners = new HashSet<GraphViewChangeListener>();
    protected Set<SelectionListener> selection_listeners = new HashSet<SelectionListener>();
    
    protected VisualStyle style;
    protected PBasicInputEventHandler keyEventHandler;
    // The Piccolo PCanvas that we will draw on
    protected PCanvas canvas;

    public boolean updateEdges = true;

    protected Map<FNode, PNodeView> nodeViewMap;
    protected Map<FEdge, PEdgeView> edgeViewMap;
    // PCS support
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    // A JPanel which contains the Canvas in the middle, and some other stuff
    // around
    protected HGViewer viewComponent;
    // Piccolo Stuff for this class
    protected PNodeSelectionHandler nodeSelectionHandler;
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

    protected static boolean firePiccoloEvents = true;
    protected String identifier;
    protected HyperGraph graph;

    /**
     * Constructor
     * 
     * @param comp
     *            HGViewer in which this GraphView is dispalyed
     * @param db
     *            HyperGraph to be viewed
     * @param nodes
     *            Nodes to be shown
     * @param edges
     *            Edges to be shown
     */
    public GraphView(HGViewer comp, HyperGraph db, Collection<FNode> nodes,
            Collection<FEdge> edges)
    {
        this.graph = db;
        this.setIdentifier(db.getLocation());
        nodeViewMap = new HashMap<FNode, PNodeView>(nodes.size());
        edgeViewMap = new HashMap<FEdge, PEdgeView>(edges.size());
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
        nodeSelectionHandler = new PNodeSelectionHandler(this, getCanvas()
                .getLayer(), getNodeLayer(), getCanvas().getCamera());
        nodeSelectionHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON1_MASK));
        // Add an FEdge Selection Handler
        edgeSelectionHandler = new PEdgeSelectionHandler(this, 
                getCanvas().getLayer(), getEdgeLayer(), getCanvas().getCamera());
        edgeSelectionHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON1_MASK));

        // Only allow panning via Middle Mouse button
        getCanvas().getPanEventHandler().setEventFilter(
                new PInputEventFilter(InputEvent.BUTTON2_MASK));

        PZoomEventHandler zoomer = new MyZoomEventHandler();
        zoomer.setMinScale(.15);
        zoomer.setMaxScale(7);
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
    }

    /**
     * Redraws the graph by reapplying visual properties
     */
    public void redrawGraph()
    {
       getCanvas().setInteracting(true);
       applyAppearances();
       getCanvas().setInteracting(false);
    }

    /**
     * Returns the viewed HyperGraph
     */
    public HyperGraph getHyperGraph()
    {
        return graph;
    }

    /**
     * Returns PEdgeHandler which deals with bended edges 
     */
    public PEdgeHandler getEdgeHandler()
    {
        return edgeHandler;
    }

     /**
     * Returns the selected nodes  
     * @return a list of the selected PNodeView
     */
    public Collection<PNodeView> getSelectedNodes()
    {
       return getNodeSelectionHandler().getSelection();
    }

    
    /**
     * Shortcut method which returns the first selected PNodeView or null
     * @return 
     */
    public PNodeView getSelectedNodeView()
    {
        if(!isNodeSelectionEnabled()) return null;
        Collection<PNodeView> res = getSelectedNodes();
        return (res.size() == 0) ? null : res.iterator().next();
    }

    /**
     * Returns the selected nodes  
     * @return a list of the selected PEdgeView
     */
    public Collection<PEdgeView> getSelectedEdges()
    {
        return getEdgeSelectionHandler().getSelection();
    }

    /**
     * Sets the background color of this GraphView
     * @param the
     *            new Paint for the background
     */
    public void setBackgroundPaint(Paint paint)
    {
        getCanvas().getCamera().setPaint(paint);
    }

    /**
     * Returns the background color of this GraphView
     * @return the backgroundPaint
     */
    public Paint getBackgroundPaint()
    {
        return getCanvas().getCamera().getPaint();
    }

    /**
     * Returns true if node selection is enabled
     * @return
     */
    public boolean isNodeSelectionEnabled()
    {
        return nodeSelection;
    }

    /**
     * Returns true if edge selection is enabled
     * @return
     */
    public boolean isEdgeSelectionEnabled()
    {
        return edgeSelection;
    }

    /**
     * Enable or disable node selection based on the passed in parameter
     * @param on
     */
    public void setNodeSelection(boolean on)
    {
        if (!nodeSelection && on)
            getCanvas().addInputEventListener(getNodeSelectionHandler());
        if (nodeSelection && !on)
            getCanvas().removeInputEventListener(getNodeSelectionHandler());
        nodeSelection = on;
    }

    /**
     * Returns the node selection handler
     * @return the PNodeSelectionHandler
     */
    public PNodeSelectionHandler getNodeSelectionHandler()
    {
        return nodeSelectionHandler;
    }

    /**
     * Enable or disable edge selection based on the passed in parameter
     * @param on
     */
    public void setEdgeSelection(boolean on)
    {
        if (!edgeSelection && on)
            getCanvas().addInputEventListener(getEdgeSelectionHandler());
        if (edgeSelection && !on)
            getCanvas().removeInputEventListener(getEdgeSelectionHandler());
        edgeSelection = on;
    }

    /**
     * Returns the edge selection handler
     * @return the PEdgeSelectionHandler
     */
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
        System.out.println("Create Nodes took: "
                + (System.currentTimeMillis() - time));
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
     * Returns the NodeViews present in this GraphView
     */
    public Collection<PNodeView> getNodeViews()
    {
        return nodeViewMap.values();
    }

    /**
     * Returns a copy of all NodeViews present in this GraphView
     */
    public Collection<PNodeView> getNodeViewsCopy()
    {
        ArrayList<PNodeView> list = new ArrayList<PNodeView>(getNodeViewCount());
        for (FNode i : nodeViewMap.keySet())
            list.add(nodeViewMap.get(i));
        return list;
    }

    /**
     * Returns the number of NodeViews present in this GraphView
     */
    public int getNodeViewCount()
    {
        return nodeViewMap.size();
    }

    /**
     * Returns the number of EdgeViews present in this GraphView
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

    /**
     * Returns the EdgeViews present in this GraphView
     */
    public Collection<PEdgeView> getEdgeViews()
    {
        return edgeViewMap.values();
    }

    /**
     * Returns a copy of all EdgeViews present in this GraphView
     */
    public Collection<PEdgeView> getEdgeViewsCopy()
    {
        ArrayList<PEdgeView> list = new ArrayList<PEdgeView>(getEdgeViewCount());
        for (FEdge i : edgeViewMap.keySet())
            list.add(edgeViewMap.get(i));
        return list;
    }

    /**
     * @param edge
     *            The FEdge whose view is requested
     * @return The PEdgeView for the given FEdge
     * 
     */
    public PEdgeView getEdgeView(FEdge edge)
    {
        return edgeViewMap.get(edge);
    }

    /**
     * Recalculates and reapplies all the node and edge appearances. 
     */
    public void applyAppearances()
    {
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
            p.paintNode(nodeView);
        }
    }

    private NodePainter getPainter(HGHandle h, boolean return_default)
    {
        NodePainter p = getVisualStyle(true).getNodePainter(h);
        if (p == null && return_default) p = def_node_painter;
        if (p != null) return p;
        try
        {
            HGHandle typeH = graph.getTypeSystem().getTypeHandle(h);
            return getPainter(typeH, true);
        }
        catch (HGException ex)
        {
            // do nothing
            // the node handle pointed to some statically
            // defined HGHandleFactory.makeHandle()
            // with no value
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
            p.paintEdge(edgeView);
        }
    }

    VisualStyle getVisualStyle(boolean return_default)
    {
        return getVisualStyle() != null ? getVisualStyle() : VisualManager
                .getInstance().getDefaultVisualStyle();
    }

    /**
     * Returns the current visual style of this GraphView 
     * @return the style 
     */
    public VisualStyle getVisualStyle()
    {
        if(style == null) 
           style = VisualManager.getInstance().getDefaultVisualStyle();
        return style;
    }

    /**
     * Sets new visual style for this GraphView 
     * @param style The style
     */
    public void setVisualStyle(VisualStyle style)
    {
        this.style = style;
        redrawGraph();
    }

    /**
     * Adds a GraphViewChangeListener
     * @param listener
     */
    public void addGraphViewChangeListener(GraphViewChangeListener listener)
    {
        view_listeners.add(listener);
    }

    /**
     * Removes a GraphViewChangeListener
     * @param listener
     */
    public void removeGraphViewChangeListener(GraphViewChangeListener listener)
    {
        view_listeners.remove(listener);
    }

    protected void fireGraphChanged(GraphViewChangeEvent event)
    {
        for (GraphViewChangeListener l : view_listeners)
            l.graphChanged(event);
    }

    /**
     * Adds a SelectionListener
     * @param listener
     */
    public void addSelectionListener(SelectionListener listener)
    {
        selection_listeners.add(listener);
    }

    /**
     * Removes a SelectionListener
     * @param listener
     */
    public void removeSelectionListener(SelectionListener listener)
    {
        selection_listeners.remove(listener);
    }

    public void fireSelectionChanged()
    {
        for (SelectionListener l : selection_listeners)
            l.selectionChanged();
    }

    /**
     * Returns the number of nodes in this view
     * @return
     */
    public int getNodeCount()
    {
        return nodeViewMap.size();
    }

    /**
     * Returns the number of edges in this view
     * @return
     */
    public int getEdgeCount()
    {
        return edgeViewMap.size();
    }

    /**
     * Removes a PNodeView given its FNode from the GraphView.
     * @param node the node
     * @return the removed view or null if no such view
     */
    public PNodeView removeNodeView(FNode node)
    {
        if (!nodeViewMap.containsKey(node)) return null;
        PNodeView node_view = getNodeView(node);
        if(isNodeSelectionEnabled())
           getNodeSelectionHandler().unselect(node_view);
        getNodeLayer().removeChild(node_view);
        nodeViewMap.remove(node);
        
        fireGraphChanged(new GraphViewNodesRemovedEvent(this,
                new FNode[] { node }));
        return node_view;
    }

    /**
     * Removes a PEdgeView from the GraphView.
     * @param edge_view the view
     * @return the removed view or null if no such view
     */
    public PEdgeView removeEdgeView(PEdgeView edge_view)
    {
        return removeEdgeView(edge_view.getEdge());
    }

    /**
     * Removes a PEdgeView given its FEdge from the GraphView.
     * @param edge the edge
     * @return the removed view or null if no such view
     */
    public PEdgeView removeEdgeView(FEdge e)
    {
        if (!edgeViewMap.containsKey(e)) return null;
        PEdgeView view = getEdgeView(e);
        view.removeFromParent(); 
       
        if(isEdgeSelectionEnabled())
           getEdgeSelectionHandler().unselect(view);
        edgeViewMap.remove(e);
        fireGraphChanged(new GraphViewEdgesRemovedEvent(this, new FEdge[] { e }));
        return view;
    }
    
    void removeAll()
    {
        getNodeSelectionHandler().unselectAll();
        getEdgeSelectionHandler().unselectAll();
        edgeViewMap.clear();
        edgeLayer.removeAllChildren();
        nodeViewMap.clear();
        nodeLayer.removeAllChildren();
        squiggleLayer.removeAllChildren();
    } 

    /**
     * Adds a PNodeView given a FNode 
     * @param node  the node 
     * @return a new PNodeView or existing one
     */
    public PNodeView addNodeView(FNode node)
    {
        if (nodeViewMap.containsKey(node)) return nodeViewMap.get(node);

        PNodeView node_view = new PNodeView(node, this);
        nodeViewMap.put(node, node_view);
        addToNodeLayer(node_view);
        fireGraphChanged(new GraphViewNodesAddedEvent(this,
                new FNode[] { node }));
        def_node_painter.paintNode(node_view);
        return node_view;
    }

    /**
     * Adds a PEdgeView given a FEdge 
     * @param edge  the edge 
     * @return a new PEdgeView or existing one
     */
    public PEdgeView addEdgeView(FEdge edge)
    {
        if (edgeViewMap.containsKey(edge)) return edgeViewMap.get(edge);
        if (check_edge_consistency)
        {
            Object s = graph.get(edge.getSource().getHandle());
            if (!(s instanceof HGLink))
                System.err
                        .println("Impossible PEdgeView - source is not HGLink: "
                                + edge);
            if (!graph.getIncidenceSet(edge.getTarget().getHandle()).contains(
                    edge.getSource().getHandle()))
                System.err
                        .println("Impossible PEdgeView - target is not pointed by source: "
                                + edge);
        }
        PEdgeView edge_view = new PEdgeView(edge, this);
        addToEdgeLayer(edge_view);
        edgeViewMap.put(edge, edge_view);
        fireGraphChanged(new GraphViewEdgesAddedEvent(this,
                new FEdge[] { edge }));
        return edge_view;
    }

    /**
     * Returns an array with the adjacent edges of a given nodes 
     * @param node The node
     * @param incoming if true, includes all incoming edges
     * @param outgoing if true, includes all  outgoing edges
     * @return
     */
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
     * Selects all nodes in the GraphView.
     */
    public void selectAllNodes()
    {
        if(isNodeSelectionEnabled())
        for (PNodeView nv : nodeViewMap.values())
            nv.setSelected(true);
    }

    /**
     * Selects all edges in the GraphView.
     */
    public void selectAllEdges()
    {
        if(isEdgeSelectionEnabled())
          for (PEdgeView nv : edgeViewMap.values())
             nv.setSelected(true);
    }

    class Canvas extends PCanvas
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

 
    static class MyZoomEventHandler extends PZoomEventHandler
    {
        PInputEvent original = null;

        @Override
        public void mousePressed(PInputEvent e)
        {
            if (accept((MouseEvent) e.getSourceSwingEvent())) original = e;
        }

        @Override
        public void mouseReleased(PInputEvent e)
        {
            e.getComponent().setInteracting(false);
            original = null;
        }

        private boolean accept(MouseEvent e)
        {
            return e.getButton() == MouseEvent.BUTTON3 && !e.isControlDown()
                    && !e.isShiftDown() && !e.isAltDown();
        }

        @Override
        public void mouseDragged(PInputEvent e)
        {
            // some node is under the mouse
            if (e.getPath().getNodeStackReference().size() > 1) return;
            e.getComponent().setInteracting(true);
            super.mouseDragged(e);
            if (original != null) doZoom(original, e);
        }

        protected void doZoom(PInputEvent ref, PInputEvent now)
        {
            PCamera camera = ((PCanvas) ref.getComponent()).getCamera();
            Point2D zoomPt = ref.getCanvasPosition();

            double dx = ((MouseEvent) ref.getSourceSwingEvent()).getX()
                    - ((MouseEvent) now.getSourceSwingEvent()).getX();
            double scaleDelta = (1.0 + (0.001 * dx));
            double currentScale = camera.getViewScale();
            double newScale = currentScale * scaleDelta;

            if (newScale < getMinScale())
                scaleDelta = getMinScale() / currentScale;

            if ((getMaxScale() > 0) && (newScale > getMaxScale()))
                scaleDelta = getMaxScale() / currentScale;

            camera
                    .scaleViewAboutPoint(scaleDelta, zoomPt.getX(), zoomPt
                            .getY());
            original = now;
        }
    }
    
    /**
     * Listens to selection changes
     */
    public static interface SelectionListener
    {
        /**
         * Called by GraphView when node/edge selection change
         */
        void selectionChanged();
    }

}
