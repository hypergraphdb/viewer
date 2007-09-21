package phoebe.event;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.util.*;

import java.util.*;
import phoebe.*;


public class PGrandTour {

  protected PGraphView graphView;

  public PGrandTour ( PGraphView view ) {
    this.graphView = view;
  }

  public void takeTour () {

    PCanvas canvas = graphView.getCanvas();
    PCamera camera = canvas.getCamera();

    PActivity last;

    Iterator nodes = graphView.getNodeViewsIterator();
    PNodeView to_node = ( PNodeView )nodes.next();
    PBounds to_bounds = to_node.getGlobalFullBounds();
    PActivity zoom_to_canvas = camera.animateViewToCenterBounds( canvas.getLayer().getGlobalFullBounds(), true, 2000 );
    PActivity pan_to_node = camera.animateViewToCenterBounds( to_bounds, false, 2000 );
    last = camera.animateViewToCenterBounds( to_bounds, true, 2000 );

    pan_to_node.startAfter( zoom_to_canvas );
    last.startAfter( pan_to_node );

    while ( nodes.hasNext() ) {
      to_node = ( PNodeView )nodes.next();
      to_bounds = to_node.getGlobalFullBounds();

      zoom_to_canvas = camera.animateViewToCenterBounds( canvas.getLayer().getGlobalFullBounds(), true, 2000 );
      zoom_to_canvas.startAfter( last );
      pan_to_node = camera.animateViewToCenterBounds( to_bounds, false, 2000 );
      last = camera.animateViewToCenterBounds( to_bounds, true, 2000 );

      pan_to_node.startAfter( zoom_to_canvas );
      last.startAfter( pan_to_node );

    }
  }

}
