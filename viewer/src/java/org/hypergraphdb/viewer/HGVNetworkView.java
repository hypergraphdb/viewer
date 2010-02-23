package org.hypergraphdb.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.viewer.event.HGVNetworkChangeEvent;
import org.hypergraphdb.viewer.event.HGVNetworkChangeListener;
import org.hypergraphdb.viewer.event.HGVNetworkEdgesAddedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkEdgesRemovedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkNodesAddedEvent;
import org.hypergraphdb.viewer.event.HGVNetworkNodesRemovedEvent;
import org.hypergraphdb.viewer.painter.DefaultEdgePainter;
import org.hypergraphdb.viewer.painter.EdgePainter;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.painter.SimpleLabelTooltipNodePainter;
import org.hypergraphdb.viewer.view.ContextMenuHelper;
import org.hypergraphdb.viewer.view.HGVMenus;
import org.hypergraphdb.viewer.visual.VisualStyle;
import org.hypergraphdb.viewer.visual.ui.DropDownButton;

import phoebe.PEdgeView;
import phoebe.PGraphView;
import phoebe.PNodeView;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.swing.PScrollPane;

/**
 * HGVNetworkView is responsible for actually getting a graph to show up on the
 * screen.<BR>
 * <BR>
 * HGVKit does not currently define specific classes for NodeViews and
 * EdgeViews, the defaults from the GINY graph library ( namely phoebe.PNodeView
 * and phoebe.PEdgeView ) are most commonly used. Making custom nodes is easy
 * and fun. One must inherit from edu.umd.cs.piccolo.PNode. The Piccolo project is 
 * what all of the painting is based on, and is very fast, flexible and powerful. 
 * Becoming acquainted with Piccolo is essential for build custom nodes.<BR>
 * <BR>
 * Fortunately, if you just want basic shapes and colors, it's all built into
 * the UI already, and you really need never even use this class. Just learn how
 * to use the VisualManager to accomplish your data to view mappings. The manual is
 * a good place to start.
 */

public class HGVNetworkView extends PGraphView
{
	 static EdgePainter def_edge_painter = new DefaultEdgePainter();
	 static NodePainter def_node_painter = new SimpleLabelTooltipNodePainter();
	
	protected VisualStyle style;
	VisualStyle self_style = new VisualStyle("self");
	protected PBasicInputEventHandler keyEventHandler;
	protected PCanvas canvas;
	
	public HGVNetworkView(HGVComponent comp, HyperGraph db, 
	        Collection<FNode> nodes, Collection<FEdge> edges)
	{
		super(db, nodes, edges);
		viewComponent = comp;
		initialize(nodes, edges);
	}
	


	public PCanvas getCanvas()
	{
		return canvas;
	}

	protected void initialize(Collection<FNode> nodes, Collection<FEdge> edges)
	{
		isInitialized = true;
		canvas = new Canvas();
		canvas.setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		canvas.setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		canvas.setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		getCanvas().getCamera().setPaint(DEFAULT_BACKGROUND_COLOR);
		// System.out.println("Phoebe just built a canvas: " + canvas);
		// Set up Layers
		nodeLayer = new PLayer() {
			public void addChild(int index, PNode child)
			{
				PNode oldParent = child.getParent();
				if (oldParent != null)
				{
					oldParent.removeChild(child);
				}
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
				if (oldParent != null)
				{
					oldParent.removeChild(child);
				}
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
		
		contextMenuStore = new HashMap(5);
		// only create the node/edge view if requested
		createViewableObjects(nodes, edges);
		enableNodeSelection();
	    disableEdgeSelection();
	}
	
	
	protected void initializeEventHandlers()
	{
		super.initializeEventHandlers();
		ContextMenuHelper ctxMenuHandler = new ContextMenuHelper(this);
        ctxMenuHandler.setEventFilter(new PInputEventFilter(
                InputEvent.BUTTON3_MASK));
        getCanvas().addInputEventListener(ctxMenuHandler);
        
        getCanvas().getZoomEventHandler().getEventFilter().setAndMask(
                InputEvent.BUTTON2_DOWN_MASK | InputEvent.SHIFT_MASK);
	}

	public void redrawGraph()
	{
		getCanvas().setInteracting(true);
		applyAppearances();
		getCanvas().setInteracting(false);
	}
	
	public void applyAppearances()
	{
		if(getVisualStyle()== null)
			setVisualStyle(VisualManager.getInstance().getDefaultVisualStyle());
		setBackgroundPaint(getVisualStyle().getBackgroundColor());
		applyNodeAppearances();
		applyEdgeAppearances();
	}
	
	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodePainter
	 * member of the current visual style.
	 */
	public void applyNodeAppearances()
	{
		for (PNodeView nodeView : getNodeViews())
		{
			if(nodeView == null)
			{
				System.out.println("VM - applyNodeAppearances - NULL NODE");
				continue;
			}
			FNode node = nodeView.getNode();
			HGHandle h = graph.getTypeSystem().getTypeHandle(node.getHandle());
			NodePainter p = self_style.getNodePainter(h);
			if(p == null)
				p = getVisualStyle().getNodePainter(h);
			if(p == null)
				p = def_node_painter;
			p.paintNode(nodeView, this);
		}
	}
	
	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgePainter
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances()
	{
		 for (Iterator i = getEdgeViewsIterator(); i.hasNext(); )
		 { 
			 PEdgeView edgeView = (PEdgeView)i.next();
			 if(edgeView == null)
			 {
				System.out.println("VMM -applyNodeAppearances - NULL NODE");
				continue;
			 }
			 FNode node = edgeView.getEdge().getSource();
			 HGHandle h = graph.getTypeSystem().getTypeHandle(node.getHandle());
			 EdgePainter p = self_style.getEdgePainter(h);
			 if(p == null)	getVisualStyle().getEdgePainter(h);
			 if(p == null)	p = def_edge_painter;
			 p.paintEdge(edgeView, this);
		}
	}

	public VisualStyle getVisualStyle()
	{
		return style;
	}

	public void setVisualStyle(VisualStyle style)
	{
		//System.out.println("setVisualStyle: " + style);
		this.style = style;
		//redrawGraph();
	}


	
	// -------------------------------//
	// Layouts and VizMaps
	// --------------------//
	// Convience Methods
	/**
	 * Sets the Given nodes Selected
	 */
	public boolean setSelected(FNode[] nodes)
	{
		return setSelected(convertToViews(nodes));
	}

	/**
	 * Sets the Given nodes Selected
	 */
	public boolean setSelected(PNodeView[] node_views)
	{
		for (int i = 0; i < node_views.length; ++i)
		{
			node_views[i].select();
		}
		return true;
	}

	/**
	 * Sets the Given edges Selected
	 */
	public boolean setSelected(FEdge[] edges)
	{
		return setSelected(convertToViews(edges));
	}

	/**
	 * Sets the Given edges Selected
	 */
	public boolean setSelected(PEdgeView[] edge_views)
	{
		for (int i = 0; i < edge_views.length; ++i)
		{
			edge_views[i].select();
		}
		return true;
	}

	protected PNodeView[] convertToViews(FNode[] nodes)
	{
		PNodeView[] views = new PNodeView[nodes.length];
		for (int i = 0; i < nodes.length; ++i)
		{
			views[i] = getNodeView(nodes[i]);
		}
		return views;
	}

	protected PEdgeView[] convertToViews(FEdge[] edges)
	{
		PEdgeView[] views = new PEdgeView[edges.length];
		for (int i = 0; i < edges.length; ++i)
		{
			views[i] = getEdgeView(edges[i]);
		}
		return views;
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
					//System.out.println("Action: " + name + ":" + key);
				}// else
				// System.out.println("Action: " + name + ":" + key);
			}
		}

		public HGVNetworkView getView()
		{
			return HGVNetworkView.this;
		}

		public void repaint(long a, int x, int y, int w, int h)
		{
			if (((x + w) > 1) || ((y + h) > 2)) super.repaint(a, x, y, w, h);
		}

		
		@Override
		public void addNotify()
		{
			super.addNotify();
		}

		@Override
		public void removeNotify()
		{
			super.removeNotify();
		}
	};

	 private Set<HGVNetworkChangeListener> view_listeners =
	        new HashSet<HGVNetworkChangeListener>();
	    public void addHGVNetworkChangeListener(
	            HGVNetworkChangeListener listener) 
	    {
	        view_listeners.add(listener);
	    }

	    public void removeHGVNetworkChangeListener(
	            HGVNetworkChangeListener listener) {
	        view_listeners.remove(listener);
	    }
	    
	    void fireNetworkChanged(HGVNetworkChangeEvent event)
	    {
	        for(HGVNetworkChangeListener l: view_listeners)
	            l.networkChanged(event);
	    }

	    
	    public int getNodeCount() {
	        return nodeViewMap.size();
	    }

	    public int getEdgeCount() {
	        return edgeViewMap.size();
	    }

	    public Iterator<FNode> nodesIterator() {
	        return nodeViewMap.keySet().iterator();
	    }

	    public Iterator<FEdge> edgesIterator() {
	        return edgeViewMap.keySet().iterator();
	    }

	    public void removeNodes(FNode[] nodes0)
	    {
	        for(FNode n: nodes0)
	            nodeViewMap.remove(n);
	        fireNetworkChanged(
	                new HGVNetworkNodesRemovedEvent(this, nodes0));
	    }

	  
	    public void removeEdges(FEdge[] res) {
	        for (FEdge e : res)
	            edgeViewMap.remove(e);
	        fireNetworkChanged(new HGVNetworkEdgesRemovedEvent(
	                this, res));
	    }

	  
	    
	    /**
	     * This will entirely remove a PNodeView/PEdgeView from the GraphView.
	     */
	    public PNodeView removeNodeView(FNode node)
	    {
	        if(!nodeViewMap.containsKey(node)) return null;
	        PNodeView node_view = getNodeView(node);
	        getNodeView(node).removeFromParent();
	              
            nodeViewMap.remove(node);
            nodeSelectionList.remove(node);
            fireNetworkChanged(new HGVNetworkNodesRemovedEvent(this, new FNode[] { node }));
	        return node_view;
	    }

	    /**
	     * This will entirely remove a PNodeView/PEdgeView from the GraphView.
	     */
	    public PEdgeView removeEdgeView(PEdgeView edge_view)
	    {
	        return removeEdgeView(edge_view.getEdge());
	    }

	    /**
	     * This will entirely remove a PNodeView/PEdgeView from the GraphView.
	     */
	    public PEdgeView removeEdgeView(FEdge e)
	    { 
	        if (!edgeViewMap.containsKey(e)) return null;
	        PEdgeView view = getEdgeView(e);
	        view.removeFromParent();
	        edgeSelectionList.remove(e);
	        edgeViewMap.remove(e);
	        fireNetworkChanged(new HGVNetworkEdgesRemovedEvent(
	                this, new FEdge[]{e}));
	        return view;
	    }
	    
	    /**
	     * @param node
	     *            the index of a node to have a view created for it
	     * @return a new PNodeView based on the node with the given index
	     */
	    public PNodeView addNodeView(FNode node)
	    {
	        if(nodeViewMap.containsKey(node))
	            return nodeViewMap.get(node);
	        
	        if (DEFAULT_X % 2000 == 0)
	        {
	            DEFAULT_Y += 100;
	            DEFAULT_X = 100;
	        }
	        else
	        {
	            DEFAULT_X += 100;
	        }
	        // Only need to Change the X and Y
	        NODE_DEFAULTS[0] = new Double(DEFAULT_X);
	        NODE_DEFAULTS[1] = new Double(DEFAULT_Y);
	        setAllNodePropertyData(node, (Object[]) NODE_DEFAULTS.clone());
	        PNodeView node_view = new PNodeView(node, this);
	        nodeViewMap.put(node, node_view);
	        addToNodeLayer(node_view);
	        fireNetworkChanged(new HGVNetworkNodesAddedEvent(
                    this, new FNode[] { node }));
	        return node_view;
	    }

	    public PEdgeView addEdgeView(FEdge edge)
	    {
	        if(edgeViewMap.containsKey(edge))
                return edgeViewMap.get(edge);
	        setAllEdgePropertyData(edge, (Object[]) EDGE_DEFAULTS.clone());
	        PEdgeView edge_view = new PEdgeView(edge, this);
	        addToEdgeLayer(edge_view);
	        edgeViewMap.put(edge, edge_view);
	        fireNetworkChanged(new HGVNetworkEdgesAddedEvent(
                    this, new FEdge[] { edge }));
	        return edge_view;
	    }

	     public FEdge[] getAdjacentEdges(FNode node, 
	            boolean incoming, boolean outgoing)
	    {
	        if (node == null || !nodeViewMap.containsKey(node))  return new FEdge[0];
	        HGHandle nH = node.getHandle();
	        IncidenceSet handles = graph.getIncidenceSet(nH);
	        Set<FEdge> res = new HashSet<FEdge>();
	        for (HGHandle h : handles) 
	        {
	            FNode incNode = new FNode(h);
	            if (!nodeViewMap.containsKey(incNode)) continue;
	            if(outgoing)
	            {
	               FEdge e = new FEdge(node, incNode);
	               if (edgeViewMap.containsKey(e))
	                   res.add(e);
	            }
	            
	            if(incoming)
	            {
	               FEdge e = new FEdge(incNode, node);
	               if (edgeViewMap.containsKey(e))
	                   res.add(e);
	            }
	        }
	        Object o = graph.get(nH);
	        if (o instanceof HGLink) {
	            HGLink link = ((HGLink) o);
	            for (int i = 0; i < link.getArity(); i++) 
	            {
	                FNode incNode = new FNode(link.getTargetAt(i));
	                if (!nodeViewMap.containsKey(incNode)) continue;
	                if(outgoing)
	                {
	                   FEdge e = new FEdge(incNode, node);
	                   if (edgeViewMap.containsKey(e))
	                       res.add(e);
	                }
	                if(incoming)
	                {
	                   FEdge e = new FEdge(node, incNode);
	                   if (edgeViewMap.containsKey(e))
	                       res.add(e);
	                }
	            }
	        }
	        return res.toArray(new FEdge[res.size()]);
	    }
	    
	    /**
	     * Hides an Object that is in the Graph by removing it from the Piccolo
	     * Scene Graph <B>Note:</B> The object must inherit from PNodeView or
	     * PEdgeView
	     */
	    public void hideGraphObject(PNode obj)
	    {
	        if (obj instanceof PNodeView) 
	            removeNodeView(((PNodeView) obj).getNode());
	        else if (obj instanceof PEdgeView) 
	            removeEdgeView(((PEdgeView) obj).getEdge());
	    }

	    /**
	     * Shows an Object that is in the Graph by adding it to the Piccolo Scene
	     * Graph
	     * 
	     * <B>Note:</B> The object must inherit from PNodeView or PEdgeView
	     */
	    public void showGraphObject(PNode object)
	    {
	        if (object == null) return;

	        if (object instanceof PNodeView)
	           addNodeView(((PNodeView) object).getNode());
	        else if (object instanceof PEdgeView)
	           addEdgeView(((PEdgeView) object).getEdge());
	    }

	    /**
	     * Hides a group of GraphObjects
	     */
	    public void hideGraphObjects(List<? extends PNode> objects)
	    {
	        for (PNode node: objects)
	            hideGraphObject(node);
	    }
	    
	    /**
	     * Returns the set of all flagged nodes in the referenced GraphPespective.<P>
	     *
	     * WARNING: the returned set is the actual data object, not a copy. Don't
	     * directly modify this set.
	     */
	    public Set<FNode> getFlaggedNodes() 
	    {
	        HashSet<FNode> res = new HashSet<FNode>();
	        for(FNode n : nodeSelectionList)
	            res.add(n);
	        return res;
	    }
	    /**
	     * Returns the set of all flagged edges in the referenced GraphPespective.<P>
	     *
	     * WARNING: the returned set is the actual data object, not a copy. Don't
	     * directly modify this set.
	     */
	    public Set<FEdge> getFlaggedEdges() {
	        HashSet<FEdge> res = new HashSet<FEdge>();
	        for(PNode n : selectionHandler.getSelection())
	            if(n instanceof PEdgeView)
	                res.add(((PEdgeView)n).getEdge());
	        return res; 
	    }
	    
	    
	    /**
	     * Returns true if the argument is a flagged FNode in the referenced
	     * GraphPerspective, false otherwise.
	     */
	    public boolean isFlagged(FNode node)
	    {
	        return getNodeView(node) != null && getNodeView(node).isSelected();
	    }
	    /**
	     * Returns true if the argument is a flagged FEdge in the referenced
	     * GraphPerspective, false otherwise.
	     */
	    public boolean isFlagged(FEdge edge)
	    {
	        return getEdgeView(edge) != null && getEdgeView(edge).isSelected();
	    }
	    
	    /**
	     * Sets the flagged state to true for all Nodes in the GraphPerspective.
	     */
	    public void flagAllNodes() {
	        Set<FNode> changes = new HashSet<FNode>();
	        for (PNodeView nv : nodeViewMap.values())
	        {
	            if (!nv.isSelected()) {
	                changes.add(nv.getNode());
	                nv.select();
	            }
	        }
//	        if (changes.size() > 0) 
//	        {
//	            fireEvent(changes, true);
//	         }
	    }
	    
	    /**
	     * Sets the flagged state to true for all Edges in the GraphPerspective.
	     */
	    public void flagAllEdges() {
	        Set<FEdge> changes = new HashSet<FEdge>();
	        for (PEdgeView nv : edgeViewMap.values())
	        {
	            if (!nv.isSelected()) {
	                changes.add(nv.getEdge());
	                nv.select();
	            }
	        }
	        //if (changes.size() > 0) {fireEvent(changes, true);}
	    }
	    
	    /**
	     * Sets the flagged state to false for all Nodes in the GraphPerspective.
	     */
	    public void unflagAllNodes() {
	        //TODO
//	        if (flaggedNodes.size() == 0) {return;}
//	        Set<FNode> changes = new HashSet<FNode>(flaggedNodes);
//	        flaggedNodes.clear();
//	        fireEvent(changes, false);
	    }
	    
	    /**
	     * Sets the flagged state to false for all Edges in the GraphPerspective.
	     */
	    public void unflagAllEdges() {
	        //TODO
//	        if (flaggedEdges.size() == 0) {return;}
//	        Set<FEdge> changes = new HashSet<FEdge>(flaggedEdges);
//	        flaggedEdges.clear();
//	        fireEvent(changes, false);
	    }
	    
	    
	    /**
	     * Sets the flagged state defined by the second argument for all Nodes
	     * contained in the first argument, which should be a Collection of FNode objects
	     * contained in the referenced GraphPerspective. One event will be fired
	     * for the full set of changes. This method does nothing if the first
	     * argument is null.
	     *
	     * @return a Set containing the objects for which the flagged state changed
	     * @throws ClassCastException  if the first argument contains objects other
	     *                             than giny.model.FNode objects
	     */
	    public Set<FNode> setFlaggedNodes(Collection<FNode> nodesToSet, boolean newState) {
	        
	      //System.out.println( "SettingFlaggedNodes" );
	      Set<FNode> returnSet = new HashSet<FNode>();
//	        if (nodesToSet == null) {return returnSet;}
//	        if (newState == true) {
//	            for (FNode node : nodesToSet) {
//	                //System.out.println( "Flagging node"+node );
//	                if (node == null)
//	                    continue;
//	                boolean setChanged = flaggedNodes.add(node);
//	                if (setChanged) {returnSet.add(node);}
//	            }
//	            if (returnSet.size() > 0) {fireEvent(returnSet, true);}
//	        } else {
//	            for (FNode node : nodesToSet) {
//	                //System.out.println( "UNFlagging node"+node );
//	                boolean setChanged = flaggedNodes.remove(node);
//	                if (setChanged) {
//	                  //System.out.println( setChanged+" Set Changed: "+node);
//	                  returnSet.add(node);
//	                }
//	            }
//	            if (returnSet.size() > 0) {fireEvent(returnSet, false);}
//	        }
	        return returnSet;
	    }
	    
	    /**
	     * Sets the flagged state defined by the second argument for all Edges
	     * contained in the first argument, which should be a Collection of FEdge objects
	     * contained in the referenced GraphPerspective. One event will be fired
	     * for the full set of changes. This method does nothing if the first
	     * argument is null.
	     *
	     * @return a Set containing the objects for which the flagged state changed
	     * @throws ClassCastException  if the first argument contains objects other
	     *                             than giny.model.FEdge objects
	     */
	    public Set<FEdge> setFlaggedEdges(Collection<FEdge> edgesToSet, boolean newState) {
	        Set<FEdge> returnSet = new HashSet<FEdge>();
//	        if (edgesToSet == null) {return returnSet;}
//	        if (newState == true) {
//	            for (Iterator<FEdge> i = edgesToSet.iterator(); i.hasNext(); ) {
//	                FEdge edge = i.next();
//	                boolean setChanged = flaggedEdges.add(edge);
//	                if (setChanged) {returnSet.add(edge);}
//	            }
//	            if (returnSet.size() > 0) {fireEvent(returnSet, true);}
//	        } else {
//	            for (Iterator<FEdge> i = edgesToSet.iterator(); i.hasNext(); ) {
//	                FEdge edge = i.next();
//	                boolean setChanged = flaggedEdges.remove(edge);
//	                if (setChanged) {returnSet.add(edge);}
//	            }
//	            if (returnSet.size() > 0) {fireEvent(returnSet, false);}
//	        }
	        return returnSet;
	    }
	    
	    

}


