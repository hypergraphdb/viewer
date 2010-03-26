package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;
import java.util.Vector;

import org.hypergraphdb.viewer.phoebe.PEdgeView;


/**
 * A class to calculate the cubic spline curve based on input points.
 *
 * @author Originally written by Tim Lambert (lambert\@cse.unsw.edu.au).
 */
public class CubicGenerator {
    /**
     * The number of steps to break each curve into.
     */
    protected static int steps = 12;

    /**
     * Calculates the natural cubic spline.   It interpolates   y[0], y[1], ...
     * y[n] The first segment is returned as C[0].a + C[0].bu + C[0].cu^2 +
     * C[0].du^3  where 0 lte u lt 1 the other segments are in C[1], C[2], ...
     * C[n-1].
     */
    protected static Cubic[] calcNaturalCubic(
        int n,
        int[] x) {
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;

        //  We solve the equation
        //
        //  [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
        //  |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
        //  |  1 4 1   | | .  | = |      .         |
        //  |    ..... | | .  |   |      .         |
        //  |     1 4 1| | .  |   |3(x[n] - x[n-2])|
        //  [       1 2] [D[n]]   [3(x[n] - x[n-1])]
        //
        // by using row operations to convert the matrix to upper triangular
        // and then back sustitution. The D[i] are the derivatives 
        // at the knots.
        gamma[0] = 1.0f / 2.0f;

        for (i = 1; i < n; i++)
            gamma[i] = 1 / (4 - gamma[i - 1]);

        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x[1] - x[0]) * gamma[0];

        for (i = 1; i < n; i++)
            delta[i] = ((3 * (x[i + 1] - x[i - 1])) - delta[i - 1]) * gamma[i];

        delta[n] = ((3 * (x[n] - x[n - 1])) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];

        for (i = n - 1; i >= 0; i--)
            D[i] = delta[i] - (gamma[i] * D[i + 1]);

        // now compute the coefficients of the cubics 
        Cubic[] C = new Cubic[n];

        for (i = 0; i < n; i++)
            C[i] = new Cubic((float) x[i], D[i],
                    (3 * (x[i + 1] - x[i])) - (2 * D[i]) - D[i + 1],
                    (2 * (x[i] - x[i + 1])) + D[i] + D[i + 1]);

        return C;
    }

    /**
     * Used to determine the points fall on a straight line. If the points fall
     * on a straight straight line, then we don't want to calculate the cubic
     * splines.
     *
     * @param pts Array of points to check if they fall on a straigt line.
     */
    public static boolean pointsInLine(Point2D[] pts) {
        if (pts.length <= 2)
            return true;

        double epsilon = 0.05; // Seems to work. 

        double mainSlope = (pts[0].getY() - pts[pts.length - 1].getY()) / (pts[0].getX() -
            pts[pts.length - 1].getX());

        double testSlope = 0.0;

        for (int i = 1; i < (pts.length - 1); i++) {
            testSlope = (pts[0].getY() - pts[i].getY()) / (pts[0].getX() -
                pts[i].getX());

            //System.out.println("test slope:  " + testSlope  
            //				+ " main slope " + mainSlope + "  abs  " +
            //				Math.abs( testSlope - mainSlope ));
            if (Math.abs(testSlope - mainSlope) > epsilon)
                return false;
        }

        return true;
    }

    /**
     * Returns an array of points to draw a line, curved or otherwise. If the
     * input points are on a straight line, then just return the input.  If
     * the points don't fall on a straight line, then calculate the cubic
     * splines based on the input points and return a new array of points.
     *
     * @param points An array of points used to calculate line to draw.
     * @param ls Indicates the line style of line to draw.
     */
    public static Point2D[] getPoints(
        Point2D[] points,
        int lineStyle) {
        // If the lineStyle is straight or if the points are effectively
        // straight, make the lineStyle straight.
        if ((lineStyle == PEdgeView.STRAIGHT_LINES) || pointsInLine(points)) {
            return points;
        }

        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            Point2D pt = (Point2D) points[i];
            xPoints[i] = (int) pt.getX();
            yPoints[i] = (int) pt.getY();
        }

        Cubic[] X = calcNaturalCubic(points.length - 1, xPoints);
        Cubic[] Y = calcNaturalCubic(points.length - 1, yPoints);

        // very crude technique - 
        // just break each segment up into steps lines 
        Vector tempPoints = new Vector();
        tempPoints.add(
            new Point2D.Double((double) Math.round(X[0].eval(0)),
                (double) Math.round(Y[0].eval(0))));

        for (int i = 0; i < X.length; i++) {
            for (int j = 1; j <= steps; j++) {
                float u = j / (float) steps;
                tempPoints.add(
                    new Point2D.Double((double) Math.round(X[i].eval(u)),
                        (double) Math.round(Y[i].eval(u))));
            }
        }

        Point2D[] drawPoints = new Point2D[tempPoints.size()];

        for (int i = 0; i < drawPoints.length; i++)
            drawPoints[i] = (Point2D) tempPoints.get(i);

        return drawPoints;
    }

    /**
     * Finds the index of the given point. This method iterates over the  list
     * of points used to draw the curve/line and finds the one closest  to the
     * input point.  Based on that index we figure out where in the list of
     * curve input points (as opposed to drawing points) the  point in
     * question falls.
     *
     * @param pt The point to check.
     */
    public static int getListIndex(
        Point2D pt,
        Point2D[] drawPoints,
        int lineStyle) {
        if (lineStyle == PEdgeView.CURVED_LINES) {
            double minDist = Double.MAX_VALUE;
            int index = 0;

            // find the point closest to the input
            for (int i = 0; i < drawPoints.length; i++) {
                double tempDist = drawPoints[i].distanceSq(pt);

                if (tempDist < minDist) {
                    minDist = tempDist;
                    index = i;
                }
            }

            // Subtract 0.5 so that we effectively always round down, that is
            // a point between 1 and 2, say 1.98, will round down to 1.
            return Math.round(((float) index / (float) steps) - 0.5f);
        } else {
            // drawPoints should never have fewer than 2 points, so this
            // should be safe.
            for (int i = 0; i < (drawPoints.length - 1); i++) {
                // Create an array of the three points and see if they create
                // a line. If so, return that index.
                Point2D[] points = new Point2D[] {
                        drawPoints[i],
                        pt,
                        drawPoints[i + 1]
                    };

                if (pointsInLine(points) && isMidPoint(points))
                    return i;
            }

            //System.out.println("Point: " + pt.toString() +
            //    " not in line with anything");

            return 0;
        }
    }

    /**
     * Returns true if the midpoint in the array falls between the two
     * endpoints in the array, else false.
     */
    private static boolean isMidPoint(Point2D[] pts) {
        double maxX = Math.max(
                pts[0].getX(),
                pts[2].getX());
        double minX = Math.min(
                pts[0].getX(),
                pts[2].getX());

        double maxY = Math.max(
                pts[0].getY(),
                pts[2].getY());
        double minY = Math.min(
                pts[0].getY(),
                pts[2].getY());

        double midX = pts[1].getX();
        double midY = pts[1].getY();

        if ((midX <= maxX) && (midX >= minX) && (midY <= maxY) &&
                (midY >= minY))
            return true;
        else

            return false;
    }
}
