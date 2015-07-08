package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;


/**
 * Draws a T icon at the target end of the edge.
 */
public class PNullIcon extends PEdgeEndIcon {
    protected double length;
    protected double sinTheta;
    protected double cosTheta;
    protected double midX;
    protected double midY;
    protected double botX;
    protected double botY;
    protected double topX;
    protected double topY;

    /**
     * Constructor.
     *
     * @param s The source node point.
     * @param t The target node point.
     * @param l The length of the cross of the T.
     */
    public PNullIcon (
        Point2D s,
        Point2D t,
        double l) {
        super(s, t);
        length = l;
        calcPositions();
    }

    /**
     * Calculates the points needed to draw the T.
     */
    protected void calcPositions() {
        newX = target.getX();
        newY = target.getY();
       
    }

    /**
     * Draws the icon.
     *
     * @param s The source node point.
     * @param t The target node point.
     */
    public void drawIcon(
        Point2D s,
        Point2D t) {
        source = s;
        target = t;
        calcLineLen();
        calcPositions();

        float[] xs = new float[] {
                (float) newX,
                (float) ( newX + .01 )
            };
        float[] ys = new float[] {
                (float) newY,
                (float) ( newY + .01 )
            };

        setPathToPolyline(xs, ys);
    }
}
