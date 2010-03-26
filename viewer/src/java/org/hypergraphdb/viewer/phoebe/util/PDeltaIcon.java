package org.hypergraphdb.viewer.phoebe.util;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;


/**
 * Draws an arrow icon at the target point.
 */
public class PDeltaIcon extends PEdgeEndIcon {
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
     * @param l The length of the arrow edges.
     */
    public PDeltaIcon(
        Point2D s,
        Point2D t,
        double l) {
        super(s, t);
        length = l * Math.sqrt(2);
        //setStroke( new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        setStroke( null );
        calcPositions();
    }

    /**
     * Calculates the points used to draw the shape. To adjust the shape of the
     * arrow (narrower, wider, etc.),  adjust the length in different
     * placements.
     */
    protected void calcPositions() {
        double x1 = target.getX();
        double y1 = target.getY();

        double x2 = source.getX();
        double y2 = source.getY();

        sinTheta = (y1 - y2) / lineLen;
        cosTheta = (x1 - x2) / lineLen;

        //newY = y1;
        //newX = x1;

        newY = y1 - ((sinTheta * length));
        newX = x1 - ((cosTheta * length));

        
        //midY = y1 - ((sinTheta * length) / 2);
        // midX = x1 - ((cosTheta * length) / 2);

        midY = y1 - ((sinTheta * length) / .7);
        midX = x1 - ((cosTheta * length) / .7);

       

        double theta = Math.acos(cosTheta);

        double topTheta = theta + (Math.PI / 2);
        double botTheta = theta - (Math.PI / 2);

        double sinTopTheta = Math.sin(topTheta);
        double cosTopTheta = Math.cos(topTheta);

        double sinBotTheta = Math.sin(botTheta);
        double cosBotTheta = Math.cos(botTheta);

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
     * Draws the icon. The icon is aways drawn at the target point. To change
     * the orientation, swap the points in how the constructor and this method
     * are called.
     *
     * @param s The source node point.
     * @param t The target node point (icon drawn here).
     */
    public void drawIcon(
        Point2D s,
        Point2D t) {
        source = s;
        target = t;
        calcLineLen();
        calcPositions();

        float[] xs = new float[] {
                (float) topX,
                (float) target.getX(),
                (float) botX,
                (float) topX

            };
        float[] ys = new float[] {
                (float) topY,
                (float) target.getY(),
                (float) botY,
                (float) topY,
            };

        setPathToPolyline(xs, ys);
    }
}
