package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;


/**
 * Draws a diamond icon at the target node.
 */
public class PDiamondIcon extends PEdgeEndIcon {
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
     * @param l The length of an edge in the diamond.
     */
    public PDiamondIcon(
        Point2D s,
        Point2D t,
        double l) {
        super(s, t);
        length = l * Math.sqrt(2);
        calcPositions();
    }

    /**
     * Calculates the points needed to draw the diamond.
     */
    protected void calcPositions() {
        double x1 = target.getX();
        double y1 = target.getY();

        double x2 = source.getX();
        double y2 = source.getY();

        // find the sin and cos of the edge angle
        //
        // Recall: sin/cos gives you the amount an angled line travels 
        // up/across, given an angle theta in terms of the unit 
        // circle (radius=1).
        //
        sinTheta = (y1 - y2) / lineLen;
        cosTheta = (x1 - x2) / lineLen;

        // find the updated edge end point
        newY = y1 - (sinTheta * length);
        newX = x1 - (cosTheta * length);

        // find the midpoint between the orig edge end point and the new
        // edge end point
        midY = y1 - ((sinTheta * length) / 2);
        midX = x1 - ((cosTheta * length) / 2);

        // get the angle
        double theta = Math.acos(cosTheta);

        // add 90 degrees to the angles
        double topTheta = theta + (Math.PI / 2);
        double botTheta = theta - (Math.PI / 2);

        // take sin/cos of new angles
        double sinTopTheta = Math.sin(topTheta);
        double cosTopTheta = Math.cos(topTheta);

        double sinBotTheta = Math.sin(botTheta);
        double cosBotTheta = Math.cos(botTheta);

        // now calculate the top points and bottom points
        if (y2 > y1) {
            topY = midY + ((sinTopTheta * length) / 2);
            botY = midY + ((sinBotTheta * length) / 2);
        } else {
            topY = midY - ((sinTopTheta * length) / 2);
            botY = midY - ((sinBotTheta * length) / 2);
        }

        topX = midX - ((cosTopTheta * length) / 2);
        botX = midX - ((cosBotTheta * length) / 2);
    }

    /**
     * Draws the icon at the new point.
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
                (float) target.getX(),
                (float) topX,
                (float) newX,
                (float) botX,
                (float) target.getX()
            };
        float[] ys = new float[] {
                (float) target.getY(),
                (float) topY,
                (float) newY,
                (float) botY,
                (float) target.getY()
            };

        setPathToPolyline(xs, ys);
    }
}
