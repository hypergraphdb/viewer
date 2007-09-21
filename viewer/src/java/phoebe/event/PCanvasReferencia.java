package phoebe.event;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.nodes.P3DRect;
import edu.umd.cs.piccolox.util.PNodeLocator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** 
 * Canvas for View reference
 * 
 * @version 1.0.1  2004/02/24
 * @author 1.0.0 from BirdsEyeViewExample by Rowan Christmas   
 */

public class PCanvasReferencia extends PCanvas implements PropertyChangeListener {

  PNode areaVisiblePNode; 
  PCanvas viewedCanvas; 
  PHandle h;
  boolean bCambioDesdeNode = false;
  boolean bCambioDesdeView = false;
    
  public PCanvasReferencia( PCanvas canvas, 
                            PLayer layer ) {

    this.viewedCanvas = canvas; 
    bCambioDesdeNode = false;
    bCambioDesdeView = false;
    h = null;

    PropertyChangeListener changeListener   = new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          updateFromViewed();
        }
      };
    viewedCanvas.getCamera().addPropertyChangeListener( changeListener );
    
    getCamera().addLayer( 0, layer ); 
    
    areaVisiblePNode = new P3DRect(); 
    areaVisiblePNode.setPaint( new Color(128,128,255) ); 
    areaVisiblePNode.setTransparency( .8f ); 
    areaVisiblePNode.setBounds( 0, 0, 100, 100 ); 
    getCamera().addChild( areaVisiblePNode );
    
    adjustHandles();
    
    removeInputEventListener( getPanEventHandler()); 
    removeInputEventListener( getZoomEventHandler()); 


    areaVisiblePNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS , new PropertyChangeListener () { 
        public void propertyChange(PropertyChangeEvent evt) { 
          updateFromView(); 
        } 
      } ); 
    areaVisiblePNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS , new PropertyChangeListener () { 
        public void propertyChange(PropertyChangeEvent evt) { 
          updateFromView(); 
        } 
      } ); 
    areaVisiblePNode.addPropertyChangeListener( PNode.PROPERTY_TRANSFORM , new PropertyChangeListener () { 
        public void propertyChange(PropertyChangeEvent evt) { 
          updateFromView(); 
        } 
      } );
  }
  
  public void addLayer ( PLayer new_layer ) { 
    getCamera().addLayer( new_layer ); 
  } 
     
  public void removeLayer ( PLayer old_layer ) { 
    getCamera().removeLayer( old_layer ); 
  } 



  /** 
   * This method will get called when the viewed canvas changes 
   */ 
  public void propertyChange ( PropertyChangeEvent event ) { 
    updateFromViewed(); 
  } 


  /**
   * This method gets the state of the BirdsEyeViewer and modifies the
   * viewed canvas.
   */
  public void updateFromView () {

    double dragHeight;
    double dragWidth;
    double dragX;
    double dragY;
    double viewHeight;
    double viewWidth;

    double canvasHeight;
    double canvasWidth;
    double viewedX;
    double viewedY;
    double viewedHeight;
    double viewedWidth;

    if ( bCambioDesdeView == true ) {
      bCambioDesdeView = false;
      return;
    }
        
    Rectangle2D drag_box_bounds = this.getCamera().localToGlobal( areaVisiblePNode.getBoundsReference() );
    
    dragHeight = drag_box_bounds.getHeight();
    dragWidth = drag_box_bounds.getWidth() ;
    dragX = drag_box_bounds.getX() ;
    dragY = drag_box_bounds.getY() ;
    
    //    viewHeight = this.getLayer().getFullBounds().getHeight();
    //  viewWidth = this.getLayer().getFullBounds().getWidth();

    //canvasHeight = viewedCanvas.getLayer().getFullBounds().getHeight();
    //canvasWidth = viewedCanvas.getLayer().getFullBounds().getWidth();

    viewHeight = this.getCamera().getFullBounds().getHeight();
    viewWidth = this.getCamera().getFullBounds().getWidth();

    canvasHeight = viewedCanvas.getCamera().getFullBounds().getHeight();
    canvasWidth = viewedCanvas.getCamera().getFullBounds().getWidth();
    

    viewedX = ( canvasWidth * dragX ) / viewWidth;
    viewedY = ( canvasHeight * dragY ) / viewHeight;
    viewedHeight = ( canvasHeight * dragHeight ) / viewHeight;
    viewedWidth = ( canvasWidth * dragWidth ) / viewWidth;

    // set the viewed canvas to look at the appropriate view.

    double zoom = getCamera().getViewScale();
    //    System.out.println( "Zoom: "+zoom);

    bCambioDesdeNode = true; //DLR_CHANGE
    viewedCanvas.getCamera().animateViewToCenterBounds( 
                                                       new Rectangle2D.Double( viewedX / zoom,
                                                                               viewedY / zoom ,
                                                                               viewedWidth / zoom ,
                                                                               viewedHeight / zoom),
                                                       true,
                                                       0 );

    
  }

  /**
   * This method gets the state of the viewed canvas and updates the
   * BirdsEyeViewer
   * This can be called from outside code
   */
  public void updateFromViewed () {
      
    double dragHeight;
    double dragWidth;
    double dragX;
    double dragY;
    double viewHeight;
    double viewWidth;

    double canvasHeight;
    double canvasWidth;
    double viewedX;
    double viewedY;
    double viewedHeight;
    double viewedWidth;

    if ( bCambioDesdeNode == true )
      {
        bCambioDesdeNode = false;
        return;
      }
      
    // viewHeight = this.getLayer().getFullBounds().getHeight();
//     viewWidth = this.getLayer().getFullBounds().getWidth();

//     canvasHeight = viewedCanvas.getLayer().getFullBounds().getHeight();
//     canvasWidth = viewedCanvas.getLayer().getFullBounds().getWidth();

    viewHeight = this.getCamera().getFullBounds().getHeight();
    viewWidth = this.getCamera().getFullBounds().getWidth();

    canvasHeight = viewedCanvas.getCamera().getFullBounds().getHeight();
    canvasWidth = viewedCanvas.getCamera().getFullBounds().getWidth();


    viewedX = viewedCanvas.getCamera().getViewBounds().getX();
    viewedY = viewedCanvas.getCamera().getViewBounds().getY();
    viewedHeight = viewedCanvas.getCamera().getViewBounds().getHeight();
    viewedWidth = viewedCanvas.getCamera().getViewBounds().getWidth();

    dragX = ( viewWidth * viewedX ) / canvasWidth;
    dragY = ( viewHeight * viewedY ) / canvasHeight;
    dragWidth = ( viewWidth * viewedWidth ) / canvasWidth;
    dragHeight = ( viewHeight * viewedHeight ) / canvasHeight;

    double zoom = getCamera().getViewScale();
    //  System.out.println( "Zoom: "+zoom);

    bCambioDesdeView = true; //DLR_CHANGE
    areaVisiblePNode.setBounds( dragX * zoom, dragY  * zoom, dragWidth * zoom, dragHeight * zoom );
    
    //DLR_CHANGE I invoke adjustHandles for adjust handles to new Bounds
    adjustHandles();
  }
  
  /* //DLR_CHANGE
   * Adjust PNode Handles to the new bounds
   */
  public void adjustHandles()
  {
    if ( h != null )
      {
        PBoundsHandle.removeBoundsHandlesFrom(areaVisiblePNode);
        areaVisiblePNode.removeChild( h );
        h = null;
      }
      
    h = new PHandle( new PNodeLocator( areaVisiblePNode ) ) { 
        /**
         * This method *should* be able to use "translate" or "offset"
         * but, it doesn;t work.
         */
        public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
          double w = areaVisiblePNode.getWidth();
          double h = areaVisiblePNode.getHeight();
          double x = areaVisiblePNode.getX();
          double y = areaVisiblePNode.getY();
          areaVisiblePNode.setBounds( x + aLocalDimension.getWidth(),
                                      y + aLocalDimension.getHeight(),
                                      w,
                                      h );
        }

      }; 
    areaVisiblePNode.addChild( h );
    PBoundsHandle.addBoundsHandlesTo( areaVisiblePNode ); 
  }
}
