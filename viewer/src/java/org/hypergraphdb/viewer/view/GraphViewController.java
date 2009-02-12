/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
/**
 * @author Iliana Avila-Campillo
 * @version %I%, %G%
 * @since 2.0
 */

package org.hypergraphdb.viewer.view;
import java.util.*;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.event.HGVNetworkChangeEvent;


public class GraphViewController 
  implements org.hypergraphdb.viewer.event.HGVNetworkChangeListener{
  
  protected Map<HGVNetworkView, GraphViewHandler> graphViewToHandler;  // a map of GraphViews to GraphViewHandlers
  public static final GraphViewHandler DEFAULT_GRAPH_VIEW_HANDLER =
    new BasicGraphViewHandler();
  
  /**
   * Empty constructor, initializes class members to empty HashMaps.
   */
  public GraphViewController (){
    this.graphViewToHandler = new HashMap<HGVNetworkView, GraphViewHandler>();
  }//GraphViewController
    
    
   /**
   * Gets an array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized with
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method
   *
   * @return an array of <code>giny.view.GraphView</code> objects
   */
  public HGVNetworkView []  getGraphViews (){
    Set<HGVNetworkView> keySet = graphViewToHandler.keySet();
    return (HGVNetworkView[])keySet.toArray(new HGVNetworkView[keySet.size()]);
  }//getGraphViews

 
  /**
   * Gets the <code>org.hypergraphdb.viewer.view.GraphViewHandler</code> for the given
   * <code>giny.view.GraphView</code>
   *
   * @return a <code>org.hypergraphdb.viewer.view.GraphViewHandler</code>, or null if this
   * <code>GraphViewController</code> does not control the given 
   * <code>giny.view.GraphView</code>
   */
  public GraphViewHandler getGraphViewHandler (HGVNetworkView graph_view){
    return (GraphViewHandler)this.graphViewToHandler.get(graph_view);
  }//getGraphViewHandler
  
  /**
   * If this <code>GraphViewController</code> contains the given 
   * <code>giny.view.GraphView</code>, then it is removed from it, and it no longer listens 
   * for change events from the removed <code>giny.view.GraphView</code>'s 
   * <code>giny.model.GraphPerspective</code>.
   *
   * @param graph_view the <code>giny.view.GraphView</code> that will be removed
   * @return the removed  <code>giny.view.GraphView</code>'s 
   * <code>org.hypergraphdb.viewer.view.GraphViewHandler</code>, or null if it is not in this 
   * <code>GraphViewController</code>
   */
  public GraphViewHandler removeGraphView (HGVNetworkView graph_view){
    if(this.graphViewToHandler.containsKey(graph_view)){
    	HGVNetwork graphPerspective = graph_view.getNetwork();
      graphPerspective.removeHGVNetworkChangeListener(this);
      GraphViewHandler gvHandler = 
        (GraphViewHandler)this.graphViewToHandler.remove(graph_view);
      return gvHandler;
    }// if containsKey
    return null;
  }//removeGraphView

  /**
   * Adds to the set of <code>giny.view.GraphView</code> objects that this 
   * <code>GraphViewController</code> keeps synchronized with their 
   * <code>giny.model.GraphPerspective</code> objects. 
   * DEFAULT_GRAPH_VIEW_HANDLER is used for the given <code>giny.view.GraphView</code>
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be added
   * @return true if succesfully added, false otherwise (if it was already added)
   * @see GraphViewController.setGraphViewHandler
   */
  public boolean addGraphView (HGVNetworkView graph_view){
    return addGraphView (graph_view, DEFAULT_GRAPH_VIEW_HANDLER);
  }//addGraphView
  
  /**
   * Adds to the set of <code>giny.view.GraphView</code> objects that this 
   * <code>GraphViewController</code> keeps synchronized to their 
   * <code>giny.model.GraphPerspective</code> objects. The given <code>GraphViewHandler</code>
   * is used for the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be added
   * @param gv_to_handler the <code>GraphViewHandler</code> that will handle
   * change events from <code>graph_view</code>'s <code>giny.model.GraphPerspective</code> member
   * @return true if succesfully added, false otherwise (if <code>graph_view</code> is
   * already in this controller)
   * @see #setGraphViewHandler(GraphView, GraphViewHandler) setGraphViewHandler
   */
  public boolean addGraphView (HGVNetworkView graph_view, GraphViewHandler gv_handler){
    if(this.graphViewToHandler.containsKey(graph_view)){
      // already contained
      return false;
    }
    HGVNetwork graphPerspective = graph_view.getNetwork();
    graphPerspective.addHGVNetworkChangeListener(this);
     this.graphViewToHandler.put(graph_view, gv_handler);
    return true;
  }//addGraphView

  /**
   * If the given <code>giny.view.GraphView</code> object belongs to this 
   * <code>GraphViewController</code>, then its <code>GraphViewHandler</code>
   * is set to the given one.
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be updated
   * @param gv_handler the <code>GraphViewHandler</code> that will handle
   * change events from <code>graph_view</code>'s <code>giny.model.GraphPerspective</code>
   * @return true if the method was successful, false otherwise (if <code>graph_view</code> 
   * is not in this controller)
   */
  public boolean setGraphViewHandler (HGVNetworkView graph_view, GraphViewHandler gv_handler){
    if(this.graphViewToHandler.containsKey(graph_view)){
      this.graphViewToHandler.put(graph_view, gv_handler);
      return true;
    }
    return false;
  }//setGraphViewHandler

  /**
   * Removes all of the current <code>giny.view.GraphView</code> objects that this
   * <code>GraphViewController</code> keeps synchronized to their corresponding
   * <code>giny.model.GraphPerspective</code> members. This <code>GraphViewController</code>
   * will no longer receive events from <code>giny.model.GraphPerspective</code>s after
   * this call.
   * 
   * @return the array of removed <code>giny.view.GraphView</code> objects
   */
  public HGVNetworkView [] removeAllGraphViews (){
	  HGVNetworkView [] gViews = getGraphViews();
    for(int i = 0; i < gViews.length; i++){
    	HGVNetwork graphPerspective = gViews[i].getNetwork();
      graphPerspective.removeHGVNetworkChangeListener(this);
    }//for i
   this.graphViewToHandler.clear();
    return gViews;
  }//removeAllGraphViews
  
  /**
   * Whether or not the given <code>giny.view.GraphView</code> is kept synchronized
   * with its <code>giny.model.GraphPerspective</code> member by this
   * <code>GraphViewController</code>.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object to test
   */ 
  public boolean containsGraphView (HGVNetworkView graph_view){
    return this.graphViewToHandler.containsKey(graph_view);
  }//containsGraphView

  /**
   * It temporarily removes this <code>GraphViewController</code> as a listener for
   * all <code>giny.model.GraphPerspective</code> objects that it currently
   * listens to
   *
   * @see #resumeListening() resumeListening
   */
  public void stopListening (){
	  HGVNetworkView [] graphViews = getGraphViews();
    for(int i = 0; i < graphViews.length; i++){
    	HGVNetwork graphPerspective = graphViews[i].getNetwork();
      graphPerspective.removeHGVNetworkChangeListener(this);
    }//for i
  }//stopListening
  
  /**
   * It temporarily removes this <code>GraphViewController</code> listener
   * from the <code>giny.model.GraphPerspective</code> object that the given
   * <code>giny.view.GraphView</code> views.
   *
   * @see #resumeListening(GraphView)
   */
  // TODO: Catch all change events even of stopListening has been called, and when
  // listening is resumed, update the graph view
  public void stopListening (HGVNetworkView graph_view){
	  HGVNetwork graphPerspective = graph_view.getNetwork();
    graphPerspective.removeHGVNetworkChangeListener(this);
  }//stopListening
  
  /**
   * It adds this <code>GraphViewController</code> as a listener for
   * all <code>giny.model.GraphPerspective</code> that were temporarily
   * "removed" by calling <code>stopListening()</code>, it updates the <code>GraphViews</code>
   * of the <code>GraphPerspectives</code> so that they are synchronized to reflect changes that
   * may have occured while not listening.
   *
   * @see #stopListening() stopListening
   */
  public void resumeListening (){
	HGVNetworkView [] graphViews = getGraphViews();
    for(int i = 0; i < graphViews.length; i++){
    	HGVNetwork graphPerspective = graphViews[i].getNetwork();
      GraphViewHandler handler = (GraphViewHandler)this.graphViewToHandler.get(graphViews[i]);
      //handler.updateGraphView(graphViews[i]);
      graphPerspective.addHGVNetworkChangeListener(this);
    }//for i
  }//resumeListening

  /**
   * It adds this <code>GraphViewController</code> listener to the
   * <code>giny.model.GraphPerspective</code> of the given <code>giny.view.GraphView</code>
   * that was temporarily "removed" by a call to <code>stopListening(GraphView)</code>, it updates
   * <code>graph_view</code> so that it's synchronized to its <code>GraphPerspective</code> 
   * due to changes that may have occured while not listening.
   *
   * @see #stopListening(GraphView)
   */
  public void resumeListening (HGVNetworkView graph_view){
    GraphViewHandler handler = (GraphViewHandler)this.graphViewToHandler.get(graph_view);
    //handler.updateGraphView(graph_view);
    HGVNetwork graphPerspective = graph_view.getNetwork();
    graphPerspective.addHGVNetworkChangeListener(this);
  }//resumeListening
  
  /**
   * Invoked when a graph change to any of the <code>giny.model.GraphPerspective</code>
   * objects accessed through <code>giny.view.GraphView.getGraphPerspective()</code> of 
   * this object's graphViews is made.
   *
   * @param event the event that was generated, contains the source 
   * <code>giny.model.GraphPerspective</code>
   */
  public void networkChanged (HGVNetworkChangeEvent event){
    HGVNetwork net = (HGVNetwork) event.getSource();
    HGVNetworkView graphView = HGVKit.getNetworkView(net);
    if(graphView == null)   return;
    GraphViewHandler gvHandler = graphViewToHandler.get(graphView);
    if(gvHandler == null) return;
    gvHandler.handleGraphPerspectiveEvent(event, graphView);
    if ( graphView instanceof HGVNetworkView )
      ( ( HGVNetworkView )graphView).updateStatusLabel();
  }//graphPerspectiveChanged

}//class GraphViewController
