//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------

import java.util.*;

import org.hypergraphdb.viewer.FEdge;
import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PEdgeView;
import phoebe.PNodeView;

//-------------------------------------------------------------------------
/**
 * Utility operations for selection and hiding/unhiding nodes and edges in a
 * Giny GraphView. Most operations are self-explanatory.
 */
public class GinyUtils
{

    public static void hideSelectedNodes(HGVNetworkView view)
    {
        // hides nodes and edges between them
        if (view == null) return;
        Set<FEdge> del_edges = new HashSet<FEdge>(); 
        List<PNodeView> sel_nodes = view.getSelectedNodes();
        for (PNodeView nview : sel_nodes)
        {
           FEdge[] edges = view.getAdjacentEdges(nview.getNode(),
                    true, true);
          for (int j = 0; j < edges.length; j++)
               del_edges.add(edges[j]);
        }
        for(FEdge e: del_edges)
            view.removeEdgeView(e);
        for (PNodeView nview : sel_nodes)
            view.removeNodeView(nview.getNode()); 
    }

  
    public static void hideSelectedEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PEdgeView eview : view.getSelectedEdges())
            view.removeEdgeView(eview);
    }

   
    public static void invertSelectedNodes(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PNodeView nview : view.getSelectedNodes())
            nview.setSelected(!nview.isSelected());
    }

    public static void invertSelectedEdges(HGVNetworkView view)
    {
        if (view == null) { return; }
        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(!eview.isSelected());
        
    }

    public static void selectAllNodes(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PNodeView nview : view.getSelectedNodes())
            nview.setSelected(true);
    }

    public static void deselectAllNodes(HGVNetworkView view)
    {
        if (view == null) return;

        for (PNodeView nview : view.getNodeViews())
            nview.setSelected(false);
    }

    public static void selectAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(true);
    }

    public static void deselectAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PEdgeView eview : view.getEdgeViews())
            eview.setSelected(false);
    }

    public static void hideAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PEdgeView eview : view.getEdgeViews())
           view.removeEdgeView(eview);
    }
}
