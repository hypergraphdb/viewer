package org.hypergraphdb.viewer.giny;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.hypergraphdb.viewer.HGVNode;
import org.hypergraphdb.viewer.HGVKit;
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
	    		  HGVNetworkView v = (HGVNetworkView)((PNodeView) node).getGraphView();
	    		  HGUtils.expandNode(v.getNetwork().getHyperGraph(), 
	    				  (HGVNode) ((NodeView)node).getNode());
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
	    		 // int i = ((PNodeView) node).getRootGraphIndex ();
	    		 // NodeView view = HGVKit.getCurrentView().getNodeView(i);
	    		 // HGUtils.collapseNode(HGVKit.getCurrentNetwork().getHyperGraph(), 
	    		//		  (HGVNode)view.getNode());
	    		  HGVNetworkView v = (HGVNetworkView)((PNodeView) node).getGraphView();
	    		  HGUtils.collapseNode(v.getNetwork().getHyperGraph(), 
	    				  (HGVNode) ((NodeView)node).getNode());
	    		 
	    		  adjust(node);
	    	  }
	    	  });
	   }
	
	private static void adjust(PNode node)
	{
		//int i = ((PNodeView) node).getRootGraphIndex ();
		//NodeView nview = HGVKit.getCurrentNetworkView().getNodeView(i);
		HGVKit.getPreferedLayout().applyLayout();
		
		PGraphView view =(PGraphView) HGVKit.getCurrentView();
	    view.getCanvas().getCamera().animateViewToCenterBounds( 
		node.getFullBounds(), false, 1550l );
	}
	
	//static int def = 15;
	private static void removeExtraNodes(PNode node, int def)
	{
		if(HGVKit.getCurrentView().nodeCount() < def)
			return;
		TreeMap<Double, NodeView> nodes = new TreeMap<Double, NodeView>();
		Iterator it = HGVKit.getCurrentView().getNodeViewsIterator();
		java.awt.geom.Point2D n = node.getFullBounds().getCenter2D();
		while(it.hasNext())
		{
			NodeView view = (NodeView)it.next();
		    nodes.put(n.distance(view.getOffset()),view);
		}
		System.out.println("Node count: " + HGVKit.getCurrentView().nodeCount());
		int j = 0;
		for(int i = 0; nodes.size() - def > 0; i++)
		{
			Double key = nodes.lastKey();
			HGVNode nn = (HGVNode)nodes.get(key).getNode();
			j = HGUtils.removeNode(HGVKit.getCurrentNetwork().getHyperGraph(), nn);
			nodes.remove(key);
		}
	}
}
