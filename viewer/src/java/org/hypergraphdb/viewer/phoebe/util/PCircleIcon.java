package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;


/**
 * Draws a circle icon at the target node.
 */
public class PCircleIcon extends PEdgeEndIcon {
    protected double diameter;
    protected double sinTheta;
    protected double cosTheta;
    protected double midX;
    protected double midY;

    /**
     * Constructor.
     *
     * @param s The source node point.
     * @param t The target node point.
     * @param d The diameter of the circle to draw.
     */
    public PCircleIcon(
        Point2D s,
        Point2D t,
        double d) {
        super(s, t);
        setStroke( null );
        diameter = d;
        calcPositions();
    }

    /**
     * Calculates the points needed to draw the circle. The difficulty is that
     * the point that defines where a circle is to be drawn is the top left
     * corner of the imaginary box surrounding the circle.
     */
    protected void calcPositions() {
        double x1 = target.getX();
        double y1 = target.getY();

        double x2 = source.getX();
        double y2 = source.getY();

        sinTheta = (y1 - y2) / lineLen;
        cosTheta = (x1 - x2) / lineLen;

        newY = y1 - (sinTheta * diameter);
        newX = x1 - (cosTheta * diameter);

        midY = y1 - ((sinTheta * diameter) / 2);
        midX = x1 - ((cosTheta * diameter) / 2);
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

        double ellipseX = midX - (diameter / 2);
        double ellipseY = midY - (diameter / 2);

        setPathToEllipse((float) ellipseX, (float) ellipseY, (float) diameter,
            (float) diameter);
    }
}
