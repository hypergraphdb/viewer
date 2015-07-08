package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;


/**
 * Draws a T icon at the target end of the edge.
 */
public class PTIcon extends PEdgeEndIcon {
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
    public PTIcon(
        Point2D s,
        Point2D t,
        double l) {
        super(s, t);
        length = l;
        setStroke( new java.awt.BasicStroke( 3 ) );
        calcPositions();
    }

  public void setPaint ( java.awt.Paint new_paint ) {
    super.setStrokePaint( new_paint );
  }

    /**
     * Calculates the points needed to draw the T.
     */
    protected void calcPositions() {
        newX = target.getX();
        newY = target.getY();

        double x1 = target.getX();
        double y1 = target.getY();

        double x2 = source.getX();
        double y2 = source.getY();


        sinTheta = (y1 - y2) / lineLen;
        cosTheta = (x1 - x2) / lineLen;

        midY = y1 - ((sinTheta * length) / .7);
        midX = x1 - ((cosTheta * length) / .7);

        newY = y1 - ((sinTheta * length) / .7);
        newX = x1 - ((cosTheta * length) / .7);

        double theta = Math.acos(cosTheta);

        double topTheta = theta + (Math.PI / 2);
        double botTheta = theta - (Math.PI / 2);

        double sinTopTheta = Math.sin(topTheta);
        double cosTopTheta = Math.cos(topTheta);

        double sinBotTheta = Math.sin(botTheta);
        double cosBotTheta = Math.cos(botTheta);

        if (y2 > y1) {
            topY = midY + ((sinTopTheta * length) / .7);
            botY = midY + ((sinBotTheta * length) / .7);
        } else {
            topY = midY - ((sinTopTheta * length) / .7);
            botY = midY - ((sinBotTheta * length) / .7);
        }

        topX = midX - ((cosTopTheta * length) / .7);
        botX = midX - ((cosBotTheta * length) / .7);
        



      //   // don't ask, it works
//         if (y2 > y1)
//             cosTheta = (x2 - x1) / lineLen;
//         else
//             cosTheta = (x1 - x2) / lineLen;

//         double theta = Math.acos(cosTheta);

//         double topTheta = theta + (Math.PI / 2);
//         double botTheta = theta - (Math.PI / 2);

//         double sinTopTheta = Math.sin(topTheta);
//         double cosTopTheta = Math.cos(topTheta);

//         double sinBotTheta = Math.sin(botTheta);
//         double cosBotTheta = Math.cos(botTheta);

//         topY = y1 - ((sinTopTheta * length) / 2);
//         topX = x1 - ((cosTopTheta * length) / 2);
//         botY = y1 - ((sinBotTheta * length) / 2);
//         botX = x1 - ((cosBotTheta * length) / 2);
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
                (float) topX,
                (float) botX
            };
        float[] ys = new float[] {
                (float) topY,
                (float) botY
            };

        setPathToPolyline(xs, ys);
    }
}
