//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------

import fing.model.FNode;
import java.util.*;
import org.hypergraphdb.viewer.HGVNetworkView;
import phoebe.PEdgeView;
import phoebe.PGraphView;
import phoebe.PNodeView;

//-------------------------------------------------------------------------
/**
 * Utility operations for selection and hiding/unhiding nodes and edges
 * in a Giny GraphView. Most operations are self-explanatory.
 */
public class GinyUtils {
    
    public static void hideSelectedNodes(HGVNetworkView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            // use GINY methods
            view.hideGraphObject( nview );
            
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.getGraphPerspective().getAdjacentEdgeIndicesArray(
                 nview.getGraphPerspectiveIndex(), true, true, true);
                if( edges != null )
                    //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
                    for ( int j = 0; j < edges.length; ++j ) {
                        // use GINY methods
                        view.hideGraphObject( view.getEdgeView( edges[j] ) );
                    }
            }
        }
    }
    
    public static void unHideSelectedNodes(HGVNetworkView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            view.showGraphObject( nview );
            
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.
                getGraphPerspective().getAdjacentEdgeIndicesArray( 
                		nview.getGraphPerspectiveIndex(), true, true, true );
                //if( edges != null )
                //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
                for ( int j = 0; j < edges.length; ++j ) {
                    view.showGraphObject( view.getEdgeView( edges[j] ) );
                }
            }
        }
    }
    
    public static void unHideAll(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            view.showGraphObject( nview );
        }
        for (Iterator ei = view.getEdgeViewsList().iterator(); ei.hasNext(); ) {
            PEdgeView eview =(PEdgeView) ei.next();
            view.showGraphObject( eview );
        }	
    }
    
    public static void unHideNodesAndInterconnectingEdges(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            FNode n = nview.getNode();
            
            view.showGraphObject( nview );
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.getGraphPerspective().getAdjacentEdgeIndicesArray( 
                		nview.getGraphPerspectiveIndex(), true, true, true);
                if( edges != null )
                for ( int j = 0; j < edges.length; ++j ) {
                    PEdgeView ev = view.getEdgeView( edges[j] );
                    view.showGraphObject( ev );
                } else {
                    //	System.out.println( "Ah" +ev.getClass().toString());		
                }
            }
        }
    }

    public static void hideSelectedEdges(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            view.hideGraphObject( eview );
        }
    }
    
    public static void unHideSelectedEdges(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            view.showGraphObject( eview );
        }
    }
    
    
    public static void invertSelectedNodes(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            nview.setSelected( !nview.isSelected() );
        }
    }
    
    public static void invertSelectedEdges(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            eview.setSelected( !eview.isSelected() );
        }
    }
    
//    public static void selectFirstNeighbors(GraphView view) {
//        if (view == null) {return;}
//        
//        GraphPerspective graphPerspective = view.getGraphPerspective();
//        Set nodeViewsToSelect = new HashSet();
//        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
//            PNodeView nview =(PNodeView) i.next();
//            FNode n = nview.getNode();
//            for (Iterator ni = graphPerspective.neighborsList(n).iterator(); ni.hasNext(); ) {
//                FNode neib =(FNode) ni.next();
//                PNodeView neibview = view.getNodeView(neib);
//                nodeViewsToSelect.add(neibview);
//            }
//        }
//        for (Iterator si = nodeViewsToSelect.iterator(); si.hasNext(); ) {
//            PNodeView nview = (PNodeView)si.next();
//            nview.setSelected(true);
//        }
//    }
    
    public static void selectAllNodes(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            nview.setSelected( true );
        }
    }
    
    public static void deselectAllNodes(HGVNetworkView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            PNodeView nview =(PNodeView) i.next();
            nview.setSelected( false );
        }
    }

    
    public static void selectAllEdges(PGraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            eview.setSelected( true );
        }
    }
    
    public static void deselectAllEdges(PGraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            eview.setSelected( false );
        }
    }
    
    public static void hideAllEdges(PGraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            view.hideGraphObject( eview );
        }
    }
    
    public static void unHideAllEdges(PGraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            PEdgeView eview =(PEdgeView) i.next();
            view.showGraphObject( eview );
        }
    }
}

