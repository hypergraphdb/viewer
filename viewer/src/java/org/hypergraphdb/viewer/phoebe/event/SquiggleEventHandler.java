package org.hypergraphdb.viewer.phoebe.event;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import org.hypergraphdb.viewer.GraphView;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The Squiggle Event Handler
 */
public class SquiggleEventHandler extends PDragSequenceEventHandler {

  private GraphView view;
  private PLayer squiggle_layer;
  private PCanvas canvas;
  private PPath squiggle;
  private Stroke CURRENT_STROKE = new BasicStroke( 2 );
  private Paint CURRENT_PAINT = Color.red;

  /**
   * Must pass the layer to Squiggle on, as well the canvas.
   */
  public SquiggleEventHandler ( PLayer layer, PCanvas canvas, GraphView view  ) {
    this.squiggle_layer = layer;
    this.canvas = canvas;
    this.view = view;
    setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
  }

  /**
   * Starts or resumes squiggling
   */
  public void beginSquiggling () {
    canvas.getLayer().addChild(  squiggle_layer );
    canvas.removeInputEventListener( view.getNodeSelectionHandler() );
    canvas.addInputEventListener( this );
  }

  /**
   * Called by the User to stop squiggling
   */
  public void stopSquiggling () {
    canvas.getLayer().removeChild( squiggle_layer );
    canvas.addInputEventListener( view.getNodeSelectionHandler() );
    canvas.removeInputEventListener( this );
  }

  /**
   * Clears the Layer of all squiggles
   */
  public void clearSquiggles () {
    squiggle_layer.removeAllChildren();
     
  }

  public void startDrag(PInputEvent e) {
    super.startDrag(e); 		
				
    Point2D p = e.getPosition();
      
    squiggle = new PPath();
    squiggle.addClientProperty( "no_menu", "yes" );
    //squiggle.seStroke( CURRENT_STROKE );
    squiggle.setStrokePaint( CURRENT_PAINT );
    squiggle.moveTo((float)p.getX(), (float)p.getY());
    squiggle.setStroke(new BasicStroke((float)( 2 / e.getCamera().getViewScale())));
    squiggle_layer.addChild(squiggle);
  }
			
  public void drag(PInputEvent e) {
    super.drag(e);				
    updateSquiggle(e);
  }
		
  public void endDrag(PInputEvent e) {
    super.endDrag(e);
    updateSquiggle(e);
    squiggle = null;
  }	
				
  public void updateSquiggle(PInputEvent aEvent) {
    Point2D p = aEvent.getPosition();
    squiggle.lineTo((float)p.getX(), (float)p.getY());
  }
}
