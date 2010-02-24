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

        for (PNodeView nview : view.getSelectedNodes())
        {
           FEdge[] edges = view.getAdjacentEdges(nview.getNode(),
                    true, true);
           if (edges != null)
           for (int j = 0; j < edges.length; ++j)
              view.hideGraphObject(view.getEdgeView(edges[j]));
           view.hideGraphObject(nview);    
        }
    }

  
    public static void hideSelectedEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (PEdgeView eview : view.getSelectedEdges())
            view.hideGraphObject(eview);
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

        for (Iterator<PEdgeView> i = view.getEdgeViewsIterator(); i.hasNext();)
        {
            PEdgeView eview = i.next();
            eview.setSelected(true);
        }
    }

    public static void deselectAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (Iterator<PEdgeView> i = view.getEdgeViewsIterator(); i.hasNext();)
        {
            PEdgeView eview = i.next();
            eview.setSelected(false);
        }
    }

    public static void hideAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (Iterator<PEdgeView> i = view.getEdgeViewsIterator(); i.hasNext();)
        {
            PEdgeView eview = i.next();
            view.hideGraphObject(eview);
        }
    }

    public static void unHideAllEdges(HGVNetworkView view)
    {
        if (view == null) { return; }

        for (Iterator<PEdgeView> i = view.getEdgeViewsIterator(); i.hasNext();)
        {
            PEdgeView eview = i.next();
            view.showGraphObject(eview);
        }
    }
}
