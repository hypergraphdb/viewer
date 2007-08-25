package org.hypergraphdb.viewer.giny;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.hypergraphdb.viewer.HGVNode;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.actions.DeselectAllAction;
import org.hypergraphdb.viewer.actions.HideSelectedAction;
import org.hypergraphdb.viewer.actions.SelectFirstNeighborsAction;
import org.hypergraphdb.viewer.hg.HGUtils;
import org.hypergraphdb.viewer.layout.GEM;
import org.hypergraphdb.viewer.view.HGVNetworkView;

import phoebe.PGraphView;
import phoebe.PNodeView;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import giny.view.NodeView;

public class ContextMenuHelper {

	public static JMenuItem expandNodeAction(Object[] args, final PNode node) {

	    return new JMenuItem( new  AbstractAction("Expand"){
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		  int i = ((PNodeView) node).getRootGraphIndex ();
	    		  NodeView view = HGViewer.getCurrentView().getNodeView(i);
	    		  HGUtils.expandNode(HGViewer.getCurrentNetwork().getHyperGraph(), 
	    				  (HGVNode)view.getNode());
	    		  int def = 50;
	    		  removeExtraNodes(node, def);
	    		  adjust(node);
	    	  }
	    	  });
	   }
	
	public static JMenuItem collapseNodeAction(Object[] args, final PNode node) {

	    return new JMenuItem( new AbstractAction("Collapse"){
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		  int i = ((PNodeView) node).getRootGraphIndex ();
	    		  NodeView view = HGViewer.getCurrentView().getNodeView(i);
	    		  HGUtils.collapseNode(HGViewer.getCurrentNetwork().getHyperGraph(), 
	    				  (HGVNode)view.getNode());
	    		  adjust(node);
	    	  }
	    	  });
	   }
	
	private static void adjust(PNode node)
	{
		//int i = ((PNodeView) node).getRootGraphIndex ();
		//NodeView nview = HGViewer.getCurrentNetworkView().getNodeView(i);
		HGViewer.getPreferedLayout().applyLayout();
		
		PGraphView view =(PGraphView) HGViewer.getCurrentView();
	    view.getCanvas().getCamera().animateViewToCenterBounds( 
		node.getFullBounds(), false, 1550l );
	}
	
	//static int def = 15;
	private static void removeExtraNodes(PNode node, int def)
	{
		if(HGViewer.getCurrentView().nodeCount() < def)
			return;
		TreeMap<Double, NodeView> nodes = new TreeMap<Double, NodeView>();
		Iterator it = HGViewer.getCurrentView().getNodeViewsIterator();
		java.awt.geom.Point2D n = node.getFullBounds().getCenter2D();
		while(it.hasNext())
		{
			NodeView view = (NodeView)it.next();
		    nodes.put(n.distance(view.getOffset()),view);
		}
		System.out.println("Node count: " + HGViewer.getCurrentView().nodeCount());
		int j = 0;
		for(int i = 0; nodes.size() - def > 0; i++)
		{
			Double key = nodes.lastKey();
			HGVNode nn = (HGVNode)nodes.get(key).getNode();
			j = HGUtils.removeNode(HGViewer.getCurrentNetwork().getHyperGraph(), nn);
			nodes.remove(key);
		}
	}
}
