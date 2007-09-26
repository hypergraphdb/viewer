package phoebe.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import phoebe.util.PLabel;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;
import fing.model.FNode;
import fing.model.FRootGraph;

import phoebe.*;

/**
 * @author Rowan Christmas
 */
public class P3DNode extends PPath
  implements PropertyChangeListener
{


  PPath red, blue;

  /**
   * The index of this node in the RootGraph
   * note that this is always a negative number.
   */
  protected int rootGraphIndex;

  /**
   * The View to which we belong.
   */
  protected PGraphView view;
  
  /**
   * Our label 
   * TODO: more extendable
   */
  protected PLabel label;

  /**
   * Our Selection toggle
   */
  protected boolean selected;

  /**
   * Our Visibility
   */
  protected boolean visible;

  /**
   * A boolean that tells us if we are updated to the current 
   * position, i.e. after a layout
   */
  protected boolean notUpdated;

  //----------------------------------------//
  // Constructors and Initialization
  //----------------------------------------//

  public P3DNode ( int        node_index,
                     PGraphView view ) {
    this ( node_index,
           view,
           Double.MAX_VALUE,
           Double.MAX_VALUE,
           Integer.MAX_VALUE,
           ( Paint )null,
           ( Paint )null,
           ( Paint )null,
           Float.MAX_VALUE,
           Double.MAX_VALUE,
           Double.MAX_VALUE,
           ( String )null );
  }

  /**
   * Create a new PNodeView with the given physical attributes.
   * @param node_index The RootGraph Index of this node 
   * @param view the PGraphVIew that we belong to
   * @param x_positon the x_positon desired for this node
   * @param y_positon the y_positon desired for this node
   * @param shape the shape type
   * @param paint the Paint for this node
   * @param selection_paint the Paint when this node is selected
   * @param border_paint the boder Paint
   * @param border_width the width of the border
   * @param width the width of the node
   * @param height the height of the node
   * @param label the String to display on the label
   */
  public P3DNode ( int        node_index,
                     PGraphView view,
                     double     x_positon,
                     double     y_positon,
                     int        shape,
                     Paint      paint,
                     Paint      selection_paint,
                     Paint      border_paint,
                     float      border_width,
                     double     width,
                     double     height,
                     String     label ) {
    // Call PNode Super Constructor
    super();

    // Set the PGraphView that we belong to
    if ( view == null ) {
      throw new IllegalArgumentException( "A PNodeView must belong to a PGraphView" );
    }
    this.view = view;

    // Set the Index
    if ( node_index == Integer.MAX_VALUE ) {
    throw new IllegalArgumentException( "A node_index must be passed to create a PNodeView" ); }
    
    if ( node_index >= 0 ) {
      this.rootGraphIndex = view.getGraphPerspective().getRootGraphNodeIndex( node_index );
    } else {
      this.rootGraphIndex = node_index;
    }
        
    
    // set NODE_X_POSITION
    if ( x_positon != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_X_POSITION,
                                  x_positon );
    }
    
    // set NODE_Y_POSITION
    if ( y_positon != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_Y_POSITION,
                                  y_positon );
    }
    
    // set NODE_SHAPE
    if ( shape != Integer.MAX_VALUE ) {
    view.setNodeIntProperty( rootGraphIndex,
                             PGraphView.NODE_SHAPE,
                             shape );
    }
    
    // set NODE_PAINT
    if ( paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_PAINT,
                                  paint );
    }
    
    // set NODE_SELECTION_PAINT
    if ( paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_SELECTION_PAINT,
                                  selection_paint );
    }
    
    // set NODE_BORDER_PAINT
    if ( border_paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_BORDER_PAINT,
                                  border_paint );
    }
    
    // set NODE_BORDER_WIDTH
    if ( border_width != Float.MAX_VALUE ) {
      view.setNodeFloatProperty( rootGraphIndex,
                                 PGraphView.NODE_BORDER_WIDTH,
                                 border_width );
    }
    
    // set NODE_WIDTH
    if ( width != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_WIDTH,
                                  width );
    }
    
    // set NODE_HEIGHT
    if ( height != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty ( rootGraphIndex,
                                   PGraphView.NODE_HEIGHT,
                                   height );
    }
    
    // set NODE_LABEL
    if ( label != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_LABEL,
                                  label );
				
    }

    initializeNodeView();

  }

  protected void initializeNodeView () {
    
    // set the FNode Position
    setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
               view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
   

    float z = (float)view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Z_POSITION );

    red = PPath.createEllipse( (float)getX(), (float)getY(), z, z );
    addChild( red );
    if ( z > 50 ) {
      red.offset( -10, 0 );
    } else {
      red.offset( -5, 0 );
    }

    blue = PPath.createEllipse( (float)getX(), (float)getY(), z, z );
    addChild( blue );
    if ( z > 50 ) {
      blue.offset( 5, 0 );
    } else {
      blue.offset( 0 , 0 );
    }

    red.setPaint( java.awt.Color.red );
    blue.setPaint( java.awt.Color.cyan );
    //red.setTransparency( .5f );
    //blue.setTransparency( .5f );

    // TODO: Remove?
    this.visible = true;
    this.selected = false;
    this.notUpdated = false;
    setPickable(true);
    invalidatePaint();

  }

  /**
   * 
   */
  public int getIndex () {
    return rootGraphIndex;
  }

  public String toString () {
    //TODO: add identifer to the NodeObjectProperty
    return ( "FNode: "+rootGraphIndex );
  }


  /**
   * @return the view we are in
   */
  public PGraphView getGraphView() {
    return view;
  }

  /**
   * @return The FNode we are a view on
   */
  public FNode getNode () {
	  FRootGraph rootGraph = view.getGraphPerspective().getRootGraph();
    return rootGraph.getNode( rootGraphIndex );
  }

  /**
   * @return the index of this node in the perspective to which we are in a view on.
   */
  public int getGraphPerspectiveIndex () {
    return view.getGraphPerspective().getRootGraphNodeIndex( rootGraphIndex );
  }

  /**
   * @return the index of this node in the root graph to which we are in a view on.
   */
  public int getRootGraphIndex () {
    return rootGraphIndex;
  }

   //------------------------------------------------------//
  // Get and Set Methods for all Common Viewable Elements
  //------------------------------------------------------//

  /**
   * Shape is currently defined via predefined variables in 
   * the PNodeView1 interface. To get the actual java.awt.Shape
   * use getPathReference()
   * @return the current int-tpye shape
   */
  public int getShape () {
    return view.getNodeIntProperty( rootGraphIndex,
                                    PGraphView.NODE_SHAPE );
  }
  
  /**
   * This sets the Paint that will be used by this node
   * when it is painted as selected.
   * @param paint The Paint to be used
   */
  public void setSelectedPaint (Paint paint) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_SELECTION_PAINT,
                                paint );
    if ( selected ) {
      setPaint( paint );
    }
  }

  /**
   * @return the currently set selection Paint
   */
  public Paint getSelectedPaint () {
    return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                PGraphView.NODE_SELECTION_PAINT );
  }
 
   
  public void setUnselectedPaint ( Paint paint ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_PAINT,
                                paint );
    if ( !selected ) {
      //System.out.println(  "UN-Selected, drawing: value of selection is"+selected );
      setPaint( paint );
    }
  }

   /**
    * @return the currently set paint
    */
   public Paint getUnselectedPaint () {
     return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                 PGraphView.NODE_PAINT );
   }

  /**
   * @param b_paint the paint the border will use
   */ 
  public void setBorderPaint ( Paint b_paint ) { 
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_BORDER_PAINT,
                                b_paint );
    super.setStrokePaint( b_paint );
  }
  
  /**
   * @return the currently set BOrder Paint
   */
  public Paint getBorderPaint () {
    return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                PGraphView.NODE_BORDER_PAINT );
  }

  /**
   * @param border_width The width of the border.
   */
  public void setBorderWidth ( float border_width ) {
    view.setNodeFloatProperty( rootGraphIndex,
                               PGraphView.NODE_BORDER_WIDTH,
                               border_width );
    super.setStroke( new BasicStroke( border_width ) );
  }

  /**
   * @return the currently set Border width
   */
  public float getBorderWidth () {
    return view.getNodeFloatProperty( rootGraphIndex,
                                      PGraphView.NODE_BORDER_WIDTH );
  }


  /**
   * @param stroke the new stroke for the border
   */
  public void setBorder ( Stroke stroke ) {
    super.setStroke( stroke );
  }


  /** 
   * @return the current border
   */
  public Stroke getBorder () {
    return super.getStroke();
  }


  /**
   * TODO: Reconcile with Border Methods
   * @param width the currently set width of this node
   */
  public boolean setWidth ( double width ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_WIDTH,
                               width );
    //setBounds( getX(), getY(), width, getHeight() );
    super.setWidth( width );
    return true;
  }

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set width of this node
   */
  public double getWidth () {
    return super.getWidth();
  }

  /**
   * TODO: Reconcile with Border Methods
   * @param height the currently set height of this node
   */
  public boolean setHeight ( double height ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_HEIGHT,
                               height );
    //setBounds( getX(), getY(), getWidth(), height );
    super.setHeight( height );
    return true;
  }

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set height of this node
   */
  public double getHeight () {
    return super.getHeight();
  }

  /**
 //   * @param label the new value to be displayed by the Label
//    */
//   public void setLabel ( String label ) {
//     view.setNodeObjectProperty( rootGraphIndex,
//                                 PGraphView.NODE_LABEL,
//                                 label );
//     if( label != null ) {
//       this.label.setText( label );
//     }
//     addClientProperty("tooltip",label);
//   }
  
  /**
   * @return The Value of the label
   */
  public PLabel getLabel () {
    // return ( String )view.getNodeObjectProperty( rootGraphIndex,
    //      PGraphView.NODE_LABEL);
    if ( label == null ) {
      label = new PLabel( null, this );
      label.setPickable(false);
      addChild(label);
      label.updatePosition();
    }
    return label;
  }


  /**
   * public void actionPerformed(ActionEvent e) { if (
   * view.getSelectionHandler().isSelected( this ) ) {
   * view.getSelectionHandler().unselect( this ); } else {
   * view.getSelectionHandler().select( this ); }}
   */
  /**
   * We want to be able to hear when a FNode changes a property, like  its
   * identifier or selected state. If the identifier changes the Label
   * chages to the new value If selection changes, this PNodeView1 draws
   * itself as selected.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    // if (evt.getPropertyName()
    //         .equals("identifier")) {
//       if (label != null) {
//         //pcs.firePropertyChange("identifier", null, evt.getNewValue() );
//         //label.setText(getNode().getIdentifier());

//         // PBounds lBounds = label.getBounds();
//         //                 float lWidth = new Double(lBounds.getWidth()).floatValue();
//         //                 float lHeight = new Double(lBounds.getHeight()).floatValue();
//         //                 setPathToRectangle(0f, 0f, lWidth + 5f, lHeight + 5f);

//         //                 //fitShapeToLabel();
//         //                 moveToFront();
//       }
//     } else if (evt.getPropertyName()
//                .equals("selected")) {
//       //System.out.println("Selction being changed");
//       Boolean bool = (Boolean) evt.getNewValue();

//       if (bool.booleanValue()) {
//         view.getSelectionHandler()
//           .select(this);
//       } else {
//         view.getSelectionHandler()
//           .unselect(this);
//       }

//       // set selection
//     }
  }

  /**
   * Set the location of this node
   */
  public void setOffset(
                        double x,
                        double y) {
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_X_POSITION,
                                x );
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_Y_POSITION,
                                y );
    super.setOffset(x, y);
    // firePropertyChange("Offset", null, this);
  }

  /**
   *  Set the location of this node
   */
  public void setOffset(java.awt.geom.Point2D point) {
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_X_POSITION,
                                point.getX() );
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_Y_POSITION,
                                point.getY() );
    super.setOffset(point);
    // firePropertyChange("Offset", null, this);
  }

  /**
   * Move this node relative to its current location
   */
  public void offset(
                     double dx,
                     double dy) {
    super.offset(dx, dy);
    Point2D p = getOffset();
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_X_POSITION,
                                p.getX() );
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_Y_POSITION,
                                p.getY() );
    // firePropertyChange("Offset", null, this);

  }

 
  /**
   * @param the new_x_position for this node
   */
  public void setXPosition(double new_x_position) {
    setXPosition( new_x_position, true );
  }

  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_x_position for this node
   * @param  update if this is true, the node will move immediatly. 
  */
  public void setXPosition ( double new_x_position, boolean update ) {
    
    //System.out.println( "AA OLD x_positon: "+view.getNodeDoubleProperty( rootGraphIndex,
    //                                                                   PGraphView.NODE_X_POSITION )+
    //                      " NEW x_positon: "+new_x_position );


    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_X_POSITION,
                                new_x_position );


    // System.out.println( "AA Confirm: "+view.getNodeDoubleProperty( rootGraphIndex,
    //                                                                 PGraphView.NODE_X_POSITION ) );
    

    if ( update ) {
      setNodePosition( false );
    } else {
      notUpdated = true;
    }
  }

  /**
   * @return the current x position of this node
   * @see setXPosition
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   */
  public double getXPosition() {
    
    // Note that this is rather sneaky, hehe.
    // The reason is that to allow for nodes to be moved around by the 
    // mouse using common Piccolo methods, like the PSelectionEventHandler
    // it is necessary to return where the node was moved to

    // if (notUpdated) {
    //  return view.getNodeDoubleProperty( rootGraphIndex,
    //                                     PGraphView.NODE_X_POSITION );
    //} else {
      return getOffset().getX();
      //}
  }

  /**
   * @param the new_y_position for this node
   */
  public void setYPosition(double new_y_position) {
    setYPosition( new_y_position, true );
    
  }

  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_y_position for this node
   * @param  update if this is true, the node will move immediatly. 
  */
  public void setYPosition ( double new_y_position, boolean update ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_Y_POSITION,
                                new_y_position );
    if ( update ) {
      setNodePosition( false );
    } else {
      notUpdated = true;
    }
  }

  /**
   * @return the current y position of this node
   * @see setYPosition
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   */
  public double getYPosition() {
    
    // Note that this is rather sneaky, hehe.
    // The reason is that to allow for nodes to be moved around by the 
    // mouse using common Piccolo methods, like the PSelectionEventHandler
    // it is necessary to return where the node was moved to

  //   if (notUpdated) {
//       return view.getNodeDoubleProperty( rootGraphIndex,
//                                          PGraphView.NODE_Y_POSITION );
//     } else {
      return getOffset().getY();
      // }
  }

  /**
   * moves this node to its stored x and y locations.
   */
  public void setNodePosition(boolean animate) {
    //if (notUpdated) {
    if (animate) {
      PTransformActivity activity = 
        this.
        animateToPositionScaleRotation( 
                                       view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
                                       view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ),
                                       1, 0, 2000);
      //animate the movement to the new position
    } else {
      setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
                 view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
    
    }

    // firePropertyChange("Offset", null, this);
    notUpdated = false;
  }
  



  /**
   * This draws us as selected
   */
  public void select () {
    selected = true;
    super.setPaint( ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                   PGraphView.NODE_SELECTION_PAINT ) );
    //view.nodeSelected( this );
  }

  /**
   * This draws us as unselected
   */
  public void unselect () {
    selected = false;
    super.setPaint( ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                   PGraphView.NODE_PAINT ) );
    //view.nodeUnselected( this );
  }

  /**
   *
   */
  public boolean isSelected () {
    return selected;
  }

  /**
   *
   */
  public boolean setSelected ( boolean selected ) {
    if (selected) {
      //select();
      view.getSelectionHandler().select( this );

    } else {
      //unselect();
      view.getSelectionHandler().unselect( this );

    }
    return this.selected;
  }
 

  //****************************************************************
  // Painting
  //****************************************************************

  /**
   *
   */
  protected void paint ( PPaintContext paintContext ) {
    super.paint( paintContext );
    // This Might be a good place to do some overriding
  }

  /**
   * Overridden method so that this node is aware of its bounds being changed
   * so  that it can tell its label and edges to change their position
   * accordingly.
   */
  public boolean setBounds (
                           double x,
                           double y,
                           double width,
                           double height) {
    boolean b = super.setBounds(x, y, width, height);

    //  System.out.println( "Bounds Changed for: "+rootGraphIndex );

    //  try {
//       int[] i = new int[0];
//       i[2] = 1;
//     } catch ( Exception e ) {
//       e.printStackTrace();
//     }

    firePropertyChange(0, "BoundsChanged", null, this);
    if ( label != null ) 
      label.updatePosition();
    return b;
  }

  /**
   * Set a new shape for the FNode, based on one of the pre-defined shapes
   * <B>Note:</B> calling setPathTo( Shape ), allows one to define their own
   * java.awt.Shape ( i.e. A picture of Johnny Cash )
   */
  public void setShape(int shape) {
  
    float x = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
                                         PGraphView.NODE_WIDTH ) ) ).floatValue();

    float y = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
                                         PGraphView.NODE_HEIGHT) ) ).floatValue();


    x = ( float )getWidth();
    y = ( float )getHeight();

    java.awt.geom.Point2D offset = getOffset();

    view.setNodeIntProperty( rootGraphIndex,
                             PGraphView.NODE_SHAPE,
                             shape );


    if (shape == PNodeView.TRIANGLE) {
      //make a trianlge
      setPathTo( ( PPath.createPolyline(
                                        new float[] {0f * x, 2f * x, 1f * x, 0f * x},
                                        new float[] {2f * y, 2f * y, 0f * y, 2f * y}))
                 .getPathReference());

    } else if ( shape == PNodeView.ROUNDED_RECTANGLE ) {

      GeneralPath path = new GeneralPath();
      path.moveTo( 1, 0 );
      path.lineTo( 2, 0 );
      path.quadTo( 3, 0, 3, 1 );
      path.lineTo( 3, 2 );
      path.quadTo( 3, 3, 2, 3 );
      path.lineTo( 1, 3 );
      path.quadTo( 0, 3, 0, 2 );
      path.lineTo( 0, 1 );
      path.quadTo( 0, 0, 1, 0 );
      path.closePath();
      setPathTo( path );

    } else if (shape == PNodeView.DIAMOND) {
      setPathTo( (PPath.createPolyline(
                                       new float[] {1f * x, 2f * x, 1f * x, 0f * x, 1f * x },
                                       new float[] {0f * y, 1f * y, 2f * y, 1f * y, 0f * y   }))
                 .getPathReference());
    } else if (shape == PNodeView.ELLIPSE) {
      setPathTo(
                (PPath.createEllipse((float) getBounds().getX(),
                                     (float) getBounds().getY(), (float) getBounds().getWidth(),
                                     (float) getBounds().getHeight())).getPathReference());
    } else if (shape == PNodeView.HEXAGON) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {0f * x, 1f * x, 2f * x, 3f * x, 2f * x, 1f * x, 0f * x },
                                      new float[] {1f * y, 2f * y, 2f * y, 1f * y, 0f * y, 0f * y, 1f * y }))
                .getPathReference());
    } else if (shape == PNodeView.OCTAGON) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        0f * x,
                                        0f * x,
                                        1f * x,
                                        2f * x,
                                        3f * x,
                                        3f * x,
                                        2f * x,
                                        1f * x,
                                        0f * x
                                      },
                                      new float[] {
                                        1f * y,
                                        2f * y,
                                        3f * y,
                                        3f * y,
                                        2f * y,
                                        1f * y,
                                        0f * y,
                                        0f * y,
                                        1f * y
                                      })).getPathReference());
    } else if (shape == PNodeView.PARALELLOGRAM) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        0f * x,
                                        1f * x,
                                        3f * x,
                                        2f * x,
                                        0f * x
                                      },
                                      new float[] {
                                        0f * y,
                                        1f * y,
                                        1f * y,
                                        0f * y,
                                        0f * y
                                      })).getPathReference());
    } else if (shape == PNodeView.RECTANGLE) {
      setPathTo(
                (PPath.createRectangle((float) getBounds().getX(),
                                       (float) getBounds().getY(), (float) getBounds().getWidth(),
                                       (float) getBounds().getHeight())).getPathReference());
    }

    setOffset( offset );
    setHeight( x );
    setWidth( y );


    if ( label != null ) 
      label.updatePosition();
  }

  /**
   * Set the new shape of the node, with a given
   * new height and width
   *
   * @param shape the shape type
   * @param width the new width
   * @param height the new height
   */
                public void setShape(
                       int shape,
                       double width,
                       double height) {
    setWidth( width );
    setHeight( height );
    setShape( shape );
  }



  /**
   * @see PNodeView1#setLabel( String ) setLabel
   * <B>Note:</B> this replaces: <I>NodeLabel nl = nr.getLabel();
   *    nl.setFont(na.getFont());</I>
   */
  public void setFont ( Font font ) {
    label.setFont( font );
  }

  /**
   *
   * @see phoebe.PNodeView#addClientProperty( String, String ) setToolTip
   */
  public void setToolTip ( String tip ) {
    addClientProperty( "tooltip", tip );
  }

  
 
  /**
   * @deprecated
   */
  public  void 	moveBy(double dx, double dy) {
    offset( dx, dy );
  }
  /**
   * @deprecated
   */
  public void 	setCenter(double x, double y) {
    setOffset( x, y );
  }
  /**
   * @deprecated
   */    
  public  void 	setLocation(double x, double y) {
    setOffset( x, y );
  }

  /**
   * @deprecated
   */
  public  void 	setSize(double w, double h) {
    setHeight( h );
    setWidth( w );
  }

  /**
   * @deprecated
   */
  public String getLabelText () {
    return label.getText();
  }


  /**
   * @deprecated
   */
  public void  setLabelText ( String newL ) {
    label.setText( newL );
  }
            


} //class PNodeView
