package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * Base class that encapsulates some of the basic functionality needed to draw
 * an icon. This class should be extended by any new icon. If this class is
 * instantiated directly, it will not draw anything and will not appear in any
 * way on the screen.
 */
public class PEdgeEndIcon extends PPath  {
    /**
     * The source node point.
     */
    protected Point2D source;

    /**
     * The target node point. The icon is always drawn at the this point.  To
     * draw the icon at the opposite node, instantiate the child class with
     * the nodes swapped.
     */
    protected Point2D target;

    /**
     * The new X position for the end of the edge. Since we don't (usually)
     * want the edge as it is currently drawn to  be drawn through the icon,
     * we have to adjust the endpoint of the edge to accomodate the icon.
     * This point must be updated (usually by drawIcon) when the icon gets
     * drawn.
     */
    protected double newX;

    /**
     * The new X position for the end of the edge. Since we don't (usually)
     * want the edge as it is currently drawn to  be drawn through the icon,
     * we have to adjust the endpoint of the edge to accomodate the icon.
     * This point must be updated (usually by drawIcon) when the icon gets
     * drawn.
     */
    protected double newY;

    /**
     * The length of the edge.
     */
    protected double lineLen;

    /**
     * Constructor.
     *
     * @param s The source node point.
     * @param t The target node point.
     */
    public PEdgeEndIcon(
        Point2D s,
        Point2D t) {

      super( null, null );
        source = s;
        target = t;
        calcLineLen();
        newX = target.getX();
        newY = target.getY();
    }

    /**
     * Draws the icon. This method should be overridden by any child class to
     * actually draw the icon.
     *
     * @param s The source node point.
     * @param t The target node point.
     */
    public void drawIcon(
        Point2D s,
        Point2D t) {
        source = s;
        target = t;
        newX = target.getX();
        newY = target.getY();
    }

    /**
     * Calculates the length of the edge using the pythagorean theorem.
     */
    protected void calcLineLen() {
        double x1 = target.getX();
        double y1 = target.getY();

        double x2 = source.getX();
        double y2 = source.getY();

        // pythagorean theorem
        lineLen = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
    }

    /**
     * Returns the new X position of the updated end point of the edge. Since
     * we don't (usually) want the edge as it is currently drawn to  be drawn
     * through the icon, we have to adjust the endpoint of the edge to
     * accomodate the icon.
     */
    public double getNewX() {
        return newX;
    }

    /**
     * Returns the new Y position of the updated end point of the edge. Since
     * we don't (usually) want the edge as it is currently drawn to  be drawn
     * through the icon, we have to adjust the endpoint of the edge to
     * accomodate the icon.
     */
    public double getNewY() {
        return newY;
    }

   //  protected void paint(PPaintContext paintContext) {
//         double s = paintContext.getScale();

//         //if (s > phoebe.UI.BasicPNodeUI.GRAPHIC_DETAIL_LEVEL) {
//         super.paint(paintContext);
//         //}
//     }

  public String toString () {
    return "FEdge End";
  }
  

}
