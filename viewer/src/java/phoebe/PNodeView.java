package phoebe;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import phoebe.util.PLabel;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PBounds;
import fing.model.FNode;
import fing.model.FRootGraph;

/**
 * @author Rowan Christmas
 */
public class PNodeView extends PPath
{
	public static final int TRIANGLE = 0;
	public static final int DIAMOND = 1;
	public static final int ELLIPSE = 2;
	public static final int HEXAGON = 3;
	public static final int OCTAGON = 4;
	public static final int PARALELLOGRAM = 5;
	public static final int RECTANGLE = 6;
	public static final int ROUNDED_RECTANGLE = 7;
	/**
	 * The index of this node in the RootGraph note that this is always a
	 * negative number.
	 */
	protected int rootGraphIndex;
	/**
	 * The View to which we belong.
	 */
	protected PGraphView view;
	/**
	 * Our label
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
	 * A boolean that tells us if we are updated to the current position, i.e.
	 * after a layout
	 */
	protected boolean sandboxed;

	// ----------------------------------------//
	// Constructors and Initialization
	// ----------------------------------------//
	public PNodeView(int node_index, PGraphView view)
	{
		this(node_index, view, Double.MAX_VALUE, Double.MAX_VALUE,
				Integer.MAX_VALUE, (Paint) null, (Paint) null, (Paint) null,
				Float.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
				(String) null);
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
	public PNodeView(int node_index, PGraphView view, double x_positon,
			double y_positon, int shape, Paint paint, Paint selection_paint,
			Paint border_paint, float border_width, double width,
			double height, String label)
	{
		// Call PNode Super Constructor
		super();
		// Set the PGraphView that we belong to
		if (view == null)
		{
			throw new IllegalArgumentException(
					"A PNodeView must belong to a PGraphView");
		}
		this.view = view;
		// Set the Index
		if (node_index == Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException(
					"A node_index must be passed to create a PNodeView");
		}
		if (node_index >= 0)
		{
			this.rootGraphIndex = view.getGraphPerspective()
					.getRootGraphNodeIndex(node_index);
		} else
		{
			this.rootGraphIndex = node_index;
		}
		// set NODE_X_POSITION
		if (x_positon != Double.MAX_VALUE)
		{
			view.setNodeDoubleProperty(rootGraphIndex,
					PGraphView.NODE_X_POSITION, x_positon);
		}
		// set NODE_Y_POSITION
		if (y_positon != Double.MAX_VALUE)
		{
			view.setNodeDoubleProperty(rootGraphIndex,
					PGraphView.NODE_Y_POSITION, y_positon);
		}
		// set NODE_SHAPE
		if (shape != Integer.MAX_VALUE)
		{
			view.setNodeIntProperty(rootGraphIndex, PGraphView.NODE_SHAPE,
					shape);
		}
		// set NODE_PAINT
		if (paint != null)
		{
			view.setNodeObjectProperty(rootGraphIndex, PGraphView.NODE_PAINT,
					paint);
		}
		// set NODE_SELECTION_PAINT
		if (paint != null)
		{
			view.setNodeObjectProperty(rootGraphIndex,
					PGraphView.NODE_SELECTION_PAINT, selection_paint);
		}
		// set NODE_BORDER_PAINT
		if (border_paint != null)
		{
			view.setNodeObjectProperty(rootGraphIndex,
					PGraphView.NODE_BORDER_PAINT, border_paint);
		}
		// set NODE_BORDER_WIDTH
		if (border_width != Float.MAX_VALUE)
		{
			view.setNodeFloatProperty(rootGraphIndex,
					PGraphView.NODE_BORDER_WIDTH, border_width);
		}
		// set NODE_WIDTH
		if (width != Double.MAX_VALUE)
		{
			view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_WIDTH,
					width);
		}
		// set NODE_HEIGHT
		if (height != Double.MAX_VALUE)
		{
			view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_HEIGHT,
					height);
		}
		// set NODE_LABEL
		if (label != null)
		{
			view.setNodeObjectProperty(rootGraphIndex, PGraphView.NODE_LABEL,
					label);
		}
		initializeNodeView();
	}

	/**
	 * This does a default paint and positioning of the PNodeView based on the
	 * values passed on its initial construction.
	 */
	protected void initializeNodeView()
	{
		// all x/y is done using offset as that operates on the nodes transform,
		// and affects
		// the nodes children
		setOffset(view.getNodeDoubleProperty(rootGraphIndex,
				PGraphView.NODE_X_POSITION), view.getNodeDoubleProperty(
				rootGraphIndex, PGraphView.NODE_Y_POSITION));
		// all w/h is done in the nodes local coordinate system
		setHeight(view.getNodeDoubleProperty(rootGraphIndex,
				PGraphView.NODE_HEIGHT));
		setWidth(view.getNodeDoubleProperty(rootGraphIndex,
				PGraphView.NODE_WIDTH));
		setStrokePaint(Color.black);
		setPaint(Color.white);
		this.visible = true;
		this.selected = false;
		this.sandboxed = false;
		setPickable(true);
		invalidatePaint();
	}

	/**
	 * @return the RootGraphIndex of the FNode we are associated with
	 */
	public int getIndex()
	{
		return rootGraphIndex;
	}

	/**
	 * @return this currently returns the RootGraphIndex of the FNode we are
	 * associated with
	 */
	public String toString()
	{
		return ("FNode: " + rootGraphIndex);
	}

	/**
	 * @return the view we are in
	 */
	public PGraphView getGraphView()
	{
		return view;
	}

	/**
	 * @return The FNode we are a view on
	 */
	public FNode getNode()
	{
		FRootGraph rootGraph = view.getGraphPerspective().getRootGraph();
		return rootGraph.getNode(rootGraphIndex);
	}

	/**
	 * @return the index of this node in the perspective to which we are in a
	 * view on.
	 */
	public int getGraphPerspectiveIndex()
	{
		return (rootGraphIndex);
	}

	/**
	 * @return the index of this node in the root graph to which we are in a
	 * view on.
	 */
	public int getRootGraphIndex()
	{
		return rootGraphIndex;
	}

	/**
	 * @return The list of EdgeViews connecting these two nodes. Possibly null.
	 */
	public java.util.List getEdgeViewsList(PNodeView otherNode)
	{
		return view.getEdgeViewsList(getNode(), otherNode.getNode());
	}

	// ------------------------------------------------------//
	// Get and Set Methods for all Common Viewable Elements
	// ------------------------------------------------------//
	/**
	 * Shape is currently defined via predefined variables in the PNodeView
	 * interface. To get the actual java.awt.Shape use getPathReference()
	 * @return the current int-tpye shape
	 */
	public int getShape()
	{
		return view.getNodeIntProperty(rootGraphIndex, PGraphView.NODE_SHAPE);
	}

	/**
	 * This sets the Paint that will be used by this node when it is painted as
	 * selected.
	 * @param paint The Paint to be used
	 */
	public void setSelectedPaint(Paint paint)
	{
		view.setNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_SELECTION_PAINT, paint);
		if (selected)
		{
			setPaint(paint);
		}
	}

	/**
	 * @return the currently set selection Paint
	 */
	public Paint getSelectedPaint()
	{
		return (Paint) view.getNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_SELECTION_PAINT);
	}

	public void setUnselectedPaint(Paint paint)
	{
		view
				.setNodeObjectProperty(rootGraphIndex, PGraphView.NODE_PAINT,
						paint);
		if (!selected)
		{
			// System.out.println( "UN-Selected, drawing: value of selection
			// is"+selected );
			setPaint(paint);
		}
	}

	/**
	 * @return the currently set paint
	 */
	public Paint getUnselectedPaint()
	{
		return (Paint) view.getNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_PAINT);
	}

	/**
	 * @param b_paint the paint the border will use
	 */
	public void setBorderPaint(Paint b_paint)
	{
		view.setNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_BORDER_PAINT, b_paint);
		super.setStrokePaint(b_paint);
	}

	/**
	 * @return the currently set BOrder Paint
	 */
	public Paint getBorderPaint()
	{
		return (Paint) view.getNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_BORDER_PAINT);
	}

	/**
	 * @param border_width The width of the border.
	 */
	public void setBorderWidth(float border_width)
	{
		view.setNodeFloatProperty(rootGraphIndex, PGraphView.NODE_BORDER_WIDTH,
				border_width);
		super.setStroke(new BasicStroke(border_width));
	}

	/**
	 * @return the currently set Border width
	 */
	public float getBorderWidth()
	{
		return view.getNodeFloatProperty(rootGraphIndex,
				PGraphView.NODE_BORDER_WIDTH);
	}

	/**
	 * @param stroke the new stroke for the border
	 */
	public void setBorder(Stroke stroke)
	{
		super.setStroke(stroke);
	}

	/**
	 * @return the current border
	 */
	public Stroke getBorder()
	{
		return super.getStroke();
	}

	/**
	 * Width is a property in the nodes local coordinate system.
	 * @param width the currently set width of this node
	 */
	public boolean setWidth(double width)
	{
		double old_width = getWidth();
		view
				.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_WIDTH,
						width);
		super.setWidth(width);
		// keep the node centered
		offset(old_width / 2 - width / 2, 0);
		return true;
	}

	/**
	 * Width is a property in the nodes local coordinate system.
	 * @return the currently set width of this node
	 */
	public double getWidth()
	{
		return super.getWidth();
	}

	/**
	 * Height is a property in the nodes local coordinate system.
	 * @param height the currently set height of this node
	 */
	public boolean setHeight(double height)
	{
		double old_height = getHeight();
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_HEIGHT,
				height);
		super.setHeight(height);
		// keep the node centered
		offset(0, old_height / 2 - height / 2);
		return true;
	}

	/**
	 * Height is a property in the nodes local coordinate system.
	 * @return the currently set height of this node
	 */
	public double getHeight()
	{
		return super.getHeight();
	}

	/**
	 * @param label_text the new value to be displayed by the Label
	 */
	public void setLabelText(String label_text)
	{
		getLabel().setText(label_text);
	}

	/**
	 * @return The label that is also a Child of this node
	 */
	public PLabel getLabel()
	{
		if (label == null)
		{
			label = new PLabel(null, this);
			label.setPickable(false);
			addChild(label);
			label.updatePosition();
		}
		return label;
	}


	/**
	 * X and Y are a Global coordinate system property of this node, and affect
	 * the nodes children
	 * 
	 * setOffset moves the node to a specified location, offset increments the
	 * node by a specified amount
	 */
	public void setOffset(double x, double y)
	{
		// setOffset automatically centers the node based on its width
		x -= getWidth() / 2;
		y -= getHeight() / 2;
		// System.out.println( "setOffset called x y: "+x+" "+y );
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_X_POSITION,
				x);
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_Y_POSITION,
				y);
		// this operates on the node's AffineTransform
		super.setOffset(x, y);
	}

	/**
	 * X and Y are a Global coordinate system property of this node, and affect
	 * the nodes children
	 * 
	 * setOffset moves the node to a specified location, offset increments the
	 * node by a specified amount
	 */
	public void offset(double dx, double dy)
	{
		// System.out.println( "offset called dx dy: "+dx+" "+dy );
		// apply the change to the node's transform directly, otherwise
		// setOffset will get called
		// getTransformReference(true).setOffset(getTransformReference(true).getTranslateX()
		// + dx,
		// getTransformReference(true).getTranslateY() + dy );
		// System.out.println( "XOffset: "+ getXOffset() +"YOffset:
		// "+getYOffset() );
		// set the stored values to the new values from the transform
		// view.setNodeDoubleProperty( rootGraphIndex,
		// PGraphView.NODE_X_POSITION,
		// getXOffset() - getWidth()/2 );
		// view.setNodeDoubleProperty( rootGraphIndex,
		// PGraphView.NODE_Y_POSITION,
		// getYOffset() - getHeight()/2 );
		// fire the appropriate events
		// invalidatePaint();
		// invalidateFullBounds();
		// super.firePropertyChange( PNode.PROPERTY_CODE_TRANSFORM,
		// PNode.PROPERTY_TRANSFORM, null, getTransformReference(true) );
		double new_x_position = getXOffset() + getWidth() / 2 + dx;
		new_x_position -= getWidth() / 2;
		double new_y_position = getYOffset() + getHeight() / 2 + dy;
		new_y_position -= getHeight() / 2;
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_X_POSITION,
				new_x_position);
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_Y_POSITION,
				new_y_position);
		getTransformReference(true).setOffset(new_x_position, new_y_position);
		invalidatePaint();
		invalidateFullBounds();
		super.firePropertyChange(PNode.PROPERTY_CODE_TRANSFORM,
				PNode.PROPERTY_TRANSFORM, null, getTransformReference(true));
		// setXPosition( getXPosition() + dx );
		// setYPosition( getYPosition() + dy );
	}

	/**
	 * @return if sandboxing is being used
	 */
	public boolean isSandboxed()
	{
		return sandboxed;
	}

	/**
	 * This immediatly moves the node to a new X coordinate.
	 * @see{setOffset}
	 * @param the new_x_position for this node
	 */
	public void setXPosition(double new_x_position)
	{
		// System.out.println( "Setxposition called: "+new_x_position );
		setXPosition(new_x_position, true);
	}

	/**
	 * Passing "false" to this method will use the built-in sandbox for storing
	 * node positions. If using the sandbox, getXPosition will return the
	 * sandbox value. After calling setNodePosition the sandboxed values will be
	 * applied to the PNodeView.<BR>
	 * <BR>
	 * Sandbox usage:<BR>
	 * 1) setNodeX/YPosition( value, <b>false</b> );<BR>
	 * 2) getNodeX/YPosition();<BR>
	 * repeat... 3) setNodePosition( boolean animate ); // true will animate the
	 * node to the sandboxed position.<BR>
	 * 
	 * @param new_x_position for this node
	 * @param no_sandbox if this is true, the node will move immediatly.
	 */
	public void setXPosition(double new_x_position, boolean no_sandbox)
	{
		new_x_position -= getWidth() / 2;
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_X_POSITION,
				new_x_position);
		if (no_sandbox)
		{
			setNodePosition(false);
		} else
		{
			sandboxed = true;
		}
	}

	/**
	 * If the nodes sandbox is being used, then this method will return the
	 * sandboxed postion.
	 * @return the current x position of this node, or sandboxed position
	 * @see setXPosition
	 */
	public double getXPosition()
	{
		if (sandboxed)
		{
			// System.out.println( "sandboxedm returninf sandbox x pos" );
			return view.getNodeDoubleProperty(rootGraphIndex,
					PGraphView.NODE_X_POSITION);
		} else
		{
			// System.out.println( "Not sandboxed: getXOffset: "+getXOffset() );
			return getXOffset() + getWidth() / 2;
		}
	}

	/**
	 * This immediatly moves the node to a new Y coordinate.
	 * @see{setOffset}
	 * @param the new_y_position for this node
	 */
	public void setYPosition(double new_y_position)
	{
		setYPosition(new_y_position, true);
	}

	/**
	 * Passing "false" to this method will use the built-in sandbox for storing
	 * node positions. If using the sandbox, getYPosition will return the
	 * sandbox value. After calling setNodePosition the sandboxed values will be
	 * applied to the PNodeView.<BR>
	 * <BR>
	 * Sandbox usage:<BR>
	 * 1) setNodeX/YPosition( value, <b>false</b> );<BR>
	 * 2) getNodeX/YPosition();<BR>
	 * repeat... 3) setNodePosition( boolean animate ); // true will animate the
	 * node to the sandboxed position.<BR>
	 * 
	 * @param new_y_position for this node
	 * @param no_sandbox if this is true, the node will move immediatly.
	 */
	public void setYPosition(double new_y_position, boolean no_sandbox)
	{
		new_y_position -= getHeight() / 2;
		view.setNodeDoubleProperty(rootGraphIndex, PGraphView.NODE_Y_POSITION,
				new_y_position);
		if (no_sandbox)
		{
			setNodePosition(false);
		} else
		{
			sandboxed = true;
		}
	}

	/**
	 * If the nodes sandbox is being used, then this method will return the
	 * sandboxed postion.
	 * @return the current y position of this node, or sandboxed position
	 * @see setYPosition
	 */
	public double getYPosition()
	{
		if (sandboxed)
		{
			return view.getNodeDoubleProperty(rootGraphIndex,
					PGraphView.NODE_Y_POSITION);
		} else
		{
			return getYOffset() + getHeight() / 2;
		}
	}

	/**
	 * moves this node to its stored x and y locations.
	 */
	public void setNodePosition(boolean animate)
	{
		// if (sandboxed) {
		if (animate)
		{
			// animate the movement to the new position
			PTransformActivity activity = this.animateToPositionScaleRotation(
					view.getNodeDoubleProperty(rootGraphIndex,
							PGraphView.NODE_X_POSITION), view
							.getNodeDoubleProperty(rootGraphIndex,
									PGraphView.NODE_Y_POSITION), 1, 0, 2000);
		} else
		{
			// do a manual setOffset
			getTransformReference(true).setOffset(
					view.getNodeDoubleProperty(rootGraphIndex,
							PGraphView.NODE_X_POSITION),
					view.getNodeDoubleProperty(rootGraphIndex,
							PGraphView.NODE_Y_POSITION));
			invalidatePaint();
			invalidateFullBounds();
			super
					.firePropertyChange(PNode.PROPERTY_CODE_TRANSFORM,
							PNode.PROPERTY_TRANSFORM, null,
							getTransformReference(true));
		}
		// }
		// not sandboxed, therefore we are up to date
		firePropertyChange(0, "Offset", null, this);
		sandboxed = false;
	}

	/**
	 * This draws us as selected
	 */
	public void select()
	{
		selected = true;
		super.setPaint((Paint) view.getNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_SELECTION_PAINT));
		view.nodeSelected(this);
	}

	/**
	 * This draws us as unselected
	 */
	public void unselect()
	{
		selected = false;
		super.setPaint((Paint) view.getNodeObjectProperty(rootGraphIndex,
				PGraphView.NODE_PAINT));
		view.nodeUnselected(this);
	}

	/**
	 * 
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * 
	 */
	public boolean setSelected(boolean selected)
	{
		if (selected)
		{
			// select();
			view.getSelectionHandler().select(this);
		} else
		{
			// unselect();
			view.getSelectionHandler().unselect(this);
		}
		return this.selected;
	}

	// ****************************************************************
	// Painting
	// ****************************************************************
	/**
	 * 
	 */
	protected void paint(PPaintContext paintContext)
	{
		super.paint(paintContext);
	}

	/**
	 * Overridden method so that this node is aware of its bounds being changed
	 * so that it can tell its label and edges to change their position
	 * accordingly.
	 */
	public boolean setBounds(double x, double y, double width, double height)
	{
		boolean b = super.setBounds(x, y, width, height);
		// System.out.println( "Bounds Changed for: "+rootGraphIndex );
		// try {
		// int[] i = new int[0];
		// i[2] = 1;
		// } catch ( Exception e ) {
		// e.printStackTrace();
		// }
		firePropertyChange(PNode.PROPERTY_CODE_BOUNDS, "BoundsChanged", null,
				this);
		if (label != null) label.updatePosition();
		return b;
	}

	/**
	 * Set a new shape for the FNode, based on one of the pre-defined shapes
	 * <B>Note:</B> calling setPathTo( Shape ), allows one to define their own
	 * java.awt.Shape ( i.e. A picture of Johnny Cash )
	 */
	public void setShape(int shape)
	{
		// float x = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
		// PGraphView.NODE_WIDTH ) ) ).floatValue();
		// float y = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
		// PGraphView.NODE_HEIGHT) ) ).floatValue();
		PBounds bounds = getBounds();
		float x = (float) getWidth();
		float y = (float) getHeight();
		java.awt.geom.Point2D offset = getOffset();
		view.setNodeIntProperty(rootGraphIndex, PGraphView.NODE_SHAPE, shape);
		if (shape == TRIANGLE)
		{
			// make a trianlge
			setPathTo((PPath.createPolyline(new float[] { 0f * x, 2f * x,
					1f * x, 0f * x }, new float[] { 2f * y, 2f * y, 0f * y,
					2f * y })).getPathReference());
		} else if (shape == ROUNDED_RECTANGLE)
		{
			GeneralPath path = new GeneralPath();
			path.moveTo(1, 0);
			path.lineTo(2, 0);
			path.quadTo(3, 0, 3, 1);
			path.lineTo(3, 2);
			path.quadTo(3, 3, 2, 3);
			path.lineTo(1, 3);
			path.quadTo(0, 3, 0, 2);
			path.lineTo(0, 1);
			path.quadTo(0, 0, 1, 0);
			path.closePath();
			setPathTo(path);
		} else if (shape == DIAMOND)
		{
			setPathTo((PPath.createPolyline(new float[] { 1f * x, 2f * x,
					1f * x, 0f * x, 1f * x }, new float[] { 0f * y, 1f * y,
					2f * y, 1f * y, 0f * y })).getPathReference());
		} else if (shape == ELLIPSE)
		{
			setPathTo((PPath.createEllipse((float) getBounds().getX(),
					(float) getBounds().getY(), (float) getBounds().getWidth(),
					(float) getBounds().getHeight())).getPathReference());
		} else if (shape == HEXAGON)
		{
			setPathTo((PPath.createPolyline(new float[] { 0f * x, 1f * x,
					2f * x, 3f * x, 2f * x, 1f * x, 0f * x }, new float[] {
					1f * y, 2f * y, 2f * y, 1f * y, 0f * y, 0f * y, 1f * y }))
					.getPathReference());
		} else if (shape == OCTAGON)
		{
			setPathTo((PPath.createPolyline(new float[] { 0f * x, 0f * x,
					1f * x, 2f * x, 3f * x, 3f * x, 2f * x, 1f * x, 0f * x },
					new float[] { 1f * y, 2f * y, 3f * y, 3f * y, 2f * y,
							1f * y, 0f * y, 0f * y, 1f * y }))
					.getPathReference());
		} else if (shape == PARALELLOGRAM)
		{
			setPathTo((PPath.createPolyline(new float[] { 0f * x, 1f * x,
					3f * x, 2f * x, 0f * x }, new float[] { 0f * y, 1f * y,
					1f * y, 0f * y, 0f * y })).getPathReference());
		} else if (shape == RECTANGLE)
		{
			setPathToRectangle((float) getX(), (float) getY(), x, y);
		}
		// setOffset( offset );
		// setHeight( x );
		// setWidth( y );
		setX(bounds.getX());
		setY(bounds.getY());
		setWidth(bounds.getWidth());
		setHeight(bounds.getHeight());
		if (label != null) label.updatePosition();
		firePropertyChange(0, "Offset", null, this);
	}

	/**
	 * Set the new shape of the node, with a given new height and width
	 * 
	 * @param shape the shape type
	 * @param width the new width
	 * @param height the new height
	 */
	public void setShape(int shape, double width, double height)
	{
		setWidth(width);
		setHeight(height);
		setShape(shape);
		firePropertyChange(0, "Offset", null, this);
	}

	public void firePropertyChange(java.lang.String propertyName,
			java.lang.Object oldValue, java.lang.Object newValue)
	{
		super.firePropertyChange(0, propertyName, oldValue, newValue);
	}

	/**
	 * @see PNodeView#setLabel( String ) setLabel <B>Note:</B> this replaces:
	 * <I>NodeLabel nl = nr.getLabel(); nl.setFont(na.getFont());</I>
	 */
	public void setFont(Font font)
	{
		label.setFont(font);
	}

	/**
	 * 
	 * @see phoebe.PNodeView#addClientProperty( String, String ) setToolTip
	 */
	public void setToolTip(String tip)
	{
		addClientProperty("tooltip", tip);
	}

	/**
	 * @deprecated
	 */
	public void moveBy(double dx, double dy)
	{
		offset(dx, dy);
	}

	/**
	 * 
	 */
	public void setCenter(double x, double y)
	{
		setOffset(x - getWidth() / 2, y - getHeight() / 2);
	}

	public Point2D getCenter()
	{
		return new Point2D.Double(getXOffset() + getWidth() / 2, getYOffset()
				+ getHeight() / 2);
	}

	/**
	 * 
	 */
	public void setLocation(double x, double y)
	{
		setOffset(x, y);
	}

	/**
	 * 
	 */
	public void setSize(double w, double h)
	{
		setHeight(h);
		setWidth(w);
	}

	/**
	 * 
	 */
	public String getLabelText()
	{
		return label.getText();
	}
} // class PNodeView
