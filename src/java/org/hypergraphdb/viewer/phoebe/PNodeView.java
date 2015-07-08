package org.hypergraphdb.viewer.phoebe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.hypergraphdb.viewer.FNode;
import org.hypergraphdb.viewer.GraphView;

import org.hypergraphdb.viewer.phoebe.util.PLabel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * This class extends a PNode and does most of what PPath would do but lets the
 * painting get done by NodePainter
 * @author Rowan Christmas
 */
public class PNodeView extends PPath
{
    // Default FNode Paint
    public static Paint DEFAULT_NODE_PAINT = java.awt.Color.white;
    // Default FNode Selection Paint
    public static Paint DEFAULT_NODE_SELECTION_PAINT = java.awt.Color.yellow;
    // Default Border Paint
    public static Paint DEFAULT_BORDER_PAINT = java.awt.Color.black;

    public static Stroke DEFAULT_BORDER_STROKE = new BasicStroke(1);

    public static final int TRIANGLE = 0;
    public static final int DIAMOND = 1;
    public static final int ELLIPSE = 2;
    public static final int HEXAGON = 3;
    public static final int OCTAGON = 4;
    public static final int PARALELLOGRAM = 5;
    public static final int RECTANGLE = 6;
    public static final int ROUNDED_RECTANGLE = 7;
    /**
     * The underlying node.
     */
    protected FNode node;
    /**
     * The View to which we belong.
     */
    protected GraphView view;
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

    protected Paint selectedPaint = DEFAULT_NODE_SELECTION_PAINT;

    protected Paint unselectedPaint = DEFAULT_NODE_PAINT;
    protected int shape = PNodeView.OCTAGON;

    // ----------------------------------------//
    // Constructors and Initialization
    // ----------------------------------------//
    public PNodeView(FNode node, GraphView view)
    {
        this.node = node;
        this.view = view;
        initializeNodeView();
    }

 
    protected void initializeNodeView()
    {
        // all x/y is done using offset as that operates on the nodes transform,
        // and affects the nodes children
        //setOffset(100, 100);
        // all w/h is done in the nodes local coordinate system
        setSize(30, 30);
        setStrokePaint(Color.black);
        setPaint(Color.white);
        this.visible = true;
        this.selected = false;
        setPickable(true);
        String tip = "" + view.getHyperGraph().get(node.getHandle());
        this.setToolTip(tip);
        if(tip.length() > 15)
        {
            Object a = view.getHyperGraph().get(node.getHandle()); 
            tip = a.getClass().getSimpleName();
        }
        this.setLabelText(tip);
        invalidatePaint();
    }

    /**
     * @return this currently returns the FNode we are
     *         associated with
     */
    public String toString()
    {
        return ("FNode: " + node);
    }

    /**
     * @return the view we are in
     */
    public GraphView getGraphView()
    {
        return view;
    }

    /**
     * @return The FNode we are a view on
     */
    public FNode getNode()
    {
        return node;
    }

     /**
     * Shape is currently defined via predefined variables in the PNodeView
     * interface. To get the actual java.awt.Shape use getPathReference()
     * 
     * @return the current int-type shape
     */
    public int getShape()
    {
        return shape;
    }

    /**
     * @return the currently set selection Paint
     */
    public Paint getSelectedPaint()
    {
        return selectedPaint;
    }

    /**
     * This sets the Paint that will be used by this node when it is painted as
     * selected.
     * 
     * @param paint
     *            The Paint to be used
     */
    public void setSelectedPaint(Paint paint)
    {
        selectedPaint = paint;
        if (selected) setPaint(selectedPaint);
    }

    /**
     * @return the currently set selection Paint
     */
    public Paint getUnselectedPaint()
    {
        return unselectedPaint;
    }

    /**
     * This sets the Paint that will be used by this node when it is painted as
     * selected.
     * 
     * @param paint
     *            The Paint to be used
     */
    public void setUnselectedPaint(Paint paint)
    {
        unselectedPaint = paint;
        if (!selected) 
            setPaint(unselectedPaint);
    }

    /**
     * @param b_paint
     *            the paint the border will use
     */
    public void setBorderPaint(Paint b_paint)
    {
        super.setStrokePaint(b_paint);
    }

    /**
     * @return the currently set BOrder Paint
     */
    public Paint getBorderPaint()
    {
        return getStrokePaint();
    }

    /**
     * @param stroke
     *            the new stroke for the border
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
     * 
     * @param width
     *            the currently set width of this node
     */
    public boolean setWidth(double width)
    {
        double old_width = getWidth();
        super.setWidth(width);
        // keep the node centered
        offset(old_width / 2 - width / 2, 0);
        return true;
    }

    /**
     * Width is a property in the nodes local coordinate system.
     * 
     * @return the currently set width of this node
     */
    public double getWidth()
    {
        return super.getWidth();
    }

    /**
     * Height is a property in the nodes local coordinate system.
     * 
     * @param height
     *            the currently set height of this node
     */
    public boolean setHeight(double height)
    {
        double old_height = getHeight();
        super.setHeight(height);
        // keep the node centered
        offset(0, old_height / 2 - height / 2);
        return true;
    }

    /**
     * Height is a property in the nodes local coordinate system.
     * 
     * @return the currently set height of this node
     */
    public double getHeight()
    {
        return super.getHeight();
    }

    /**
     * @param label_text
     *            the new value to be displayed by the Label
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
        double new_x_position = getXOffset() + getWidth() / 2 + dx;
        new_x_position -= getWidth() / 2;
        double new_y_position = getYOffset() + getHeight() / 2 + dy;
        new_y_position -= getHeight() / 2;
        getTransformReference(true).setOffset(new_x_position, new_y_position);
        invalidatePaint();
        invalidateFullBounds();
        super.firePropertyChange(PNode.PROPERTY_CODE_TRANSFORM,
                PNode.PROPERTY_TRANSFORM, null, getTransformReference(true));
        // setXPosition( getXPosition() + dx );
        // setYPosition( getYPosition() + dy );
    }

    /**
     * This immediatly moves the node to a new X coordinate.
     * 
     * @see{setOffset
     * @param the
     *            new_x_position for this node
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
     * @param new_x_position
     *            for this node
     * @param no_sandbox
     *            if this is true, the node will move immediatly.
     */
    public void setXPosition(double new_x_position, boolean no_sandbox)
    {
        new_x_position -= getWidth() / 2;
        offset(new_x_position, 0);
    }

    /**
     * @return the current x position of this node, or sandboxed position
     * @see setXPosition
     */
    public double getXPosition()
    {
        return getXOffset() + getWidth() / 2;
    }

    /**
     * This immediately moves the node to a new Y coordinate.
     * 
     * @see{setOffset
     * @param the
     *            new_y_position for this node
     */
     public void setYPosition(double new_y_position)
    {
        new_y_position -= getHeight() / 2;
        offset(0, new_y_position);
    }

    /**
     * 
     * @return the current y position of this node
     * @see setYPosition
     */
    public double getYPosition()
    {
        return getYOffset() + getHeight() / 2;
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
    public void setSelected(boolean selected)
    {
        this.selected = selected;
        if (selected) 
            view.getNodeSelectionHandler().select(this);
        else
            view.getNodeSelectionHandler().unselect(this);
       super.setPaint(selected ? getSelectedPaint(): getUnselectedPaint());
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
        PBounds bounds = getBounds();
        float x = (float) getWidth();
        float y = (float) getHeight();
        Point2D offset = getOffset();
        this.shape = shape;
        if (shape == TRIANGLE)
        {
            // make a trianlge
            setPathTo((PPath.createPolyline(new float[] { 0f * x, 2f * x,
                    1f * x, 0f * x }, new float[] { 2f * y, 2f * y, 0f * y,
                    2f * y })).getPathReference());
        }
        else if (shape == ROUNDED_RECTANGLE)
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
        }
        else if (shape == DIAMOND)
        {
            setPathTo((PPath.createPolyline(new float[] { 1f * x, 2f * x,
                    1f * x, 0f * x, 1f * x }, new float[] { 0f * y, 1f * y,
                    2f * y, 1f * y, 0f * y })).getPathReference());
        }
        else if (shape == ELLIPSE)
        {
            setPathTo((PPath.createEllipse((float) getBounds().getX(),
                    (float) getBounds().getY(), (float) getBounds().getWidth(),
                    (float) getBounds().getHeight())).getPathReference());
        }
        else if (shape == HEXAGON)
        {
            setPathTo((PPath.createPolyline(new float[] { 0f * x, 1f * x,
                    2f * x, 3f * x, 2f * x, 1f * x, 0f * x }, new float[] {
                    1f * y, 2f * y, 2f * y, 1f * y, 0f * y, 0f * y, 1f * y }))
                    .getPathReference());
        }
        else if (shape == OCTAGON)
        {
            setPathTo((PPath.createPolyline(new float[] { 0f * x, 0f * x,
                    1f * x, 2f * x, 3f * x, 3f * x, 2f * x, 1f * x, 0f * x },
                    new float[] { 1f * y, 2f * y, 3f * y, 3f * y, 2f * y,
                            1f * y, 0f * y, 0f * y, 1f * y }))
                    .getPathReference());
        }
        else if (shape == PARALELLOGRAM)
        {
            setPathTo((PPath.createPolyline(new float[] { 0f * x, 1f * x,
                    3f * x, 2f * x, 0f * x }, new float[] { 0f * y, 1f * y,
                    1f * y, 0f * y, 0f * y })).getPathReference());
        }
        else if (shape == RECTANGLE)
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
     * @param shape
     *            the shape type
     * @param width
     *            the new width
     * @param height
     *            the new height
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
     * @see PNodeView#setLabel(String ) setLabel <B>Note:</B> this replaces:
     *      <I>NodeLabel nl = nr.getLabel(); nl.setFont(na.getFont());</I>
     */
    public void setFont(Font font)
    {
        label.setFont(font);
    }

    /**
     * 
     * @see phoebe.PNodeView#addClientProperty(String, String ) setToolTip
     */
    public void setToolTip(String tip)
    {
        addClientProperty("tooltip", tip);
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
