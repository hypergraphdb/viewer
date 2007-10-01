package org.hypergraphdb.viewer.giny;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.hg.HGUtils;

import phoebe.PGraphView;
import phoebe.PNodeView;
import phoebe.PNodeView;

import edu.umd.cs.piccolo.PNode;
import fing.model.FNode;

public class ContextMenuHelper {

	public static JMenuItem expandNodeAction(Object[] args, final PNode node) {

	    return new JMenuItem( new  AbstractAction("Expand"){
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		  HGVNetworkView v = (HGVNetworkView)((PNodeView) node).getGraphView();
	    		  HGUtils.expandNode(v.getNetwork().getHyperGraph(), 
	    				  ((PNodeView)node).getNode());
	    		  int def = 150;
	    		  removeExtraNodes(node, def);
	    		  adjust(node);
	    	  }
	    	  });
	   }
	
	public static JMenuItem collapseNodeAction(Object[] args, final PNode node) {

	    return new JMenuItem( new AbstractAction("Collapse"){
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		  HGVNetworkView v = (HGVNetworkView)((PNodeView) node).getGraphView();
	    		  HGUtils.collapseNode(v.getNetwork().getHyperGraph(), 
	    				  ((PNodeView)node).getNode());
	    		  adjust(node);
	    	  }
	    	  });
	   }
	
	private static void adjust(PNode node)
	{
		HGVKit.getPreferedLayout().applyLayout();
		PGraphView view = HGVKit.getCurrentView();
	    view.getCanvas().getCamera().animateViewToCenterBounds( 
		node.getFullBounds(), false, 1550l );
	}
	
	//static int def = 15;
	private static void removeExtraNodes(PNode node, int def)
	{
		if(HGVKit.getCurrentView().getNodeViewCount() < def)
			return;
		TreeMap<Double, PNodeView> nodes = new TreeMap<Double, PNodeView>();
		java.awt.geom.Point2D n = node.getFullBounds().getCenter2D();
		
		for(Iterator<PNodeView> it = HGVKit.getCurrentView().getNodeViewsIterator();it.hasNext();)
		{
			PNodeView view = it.next();
		    nodes.put(n.distance(view.getOffset()),view);
		}
		//System.out.println("FNode count: " + HGVKit.getCurrentView().nodeCount());
		for(int i = 0; nodes.size() - def > 0; i++)
		{
			Double key = nodes.lastKey();
			FNode nn = (FNode)nodes.get(key).getNode();
			HGUtils.removeNode(HGVKit.getCurrentNetwork().getHyperGraph(), nn, true);
			nodes.remove(key);
		}
	}
}
