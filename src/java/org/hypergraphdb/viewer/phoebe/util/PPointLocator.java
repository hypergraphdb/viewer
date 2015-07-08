package org.hypergraphdb.viewer.phoebe.util;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.util.PNodeLocator;


/**
 * A PNodeLocator that locates itself based on a known point.  Generally
 * defeats the purpose of a locator.
 */
public class PPointLocator extends PNodeLocator {
    /**
     * The location of everything is based on.
     */
    protected Point2D location;

    /**
    	 *
    	 */
    public PPointLocator(
        PNode n,
        Point2D loc) {
        super(n);
        location = loc;
    }

    /**
     * Locates the input point on the known location point.
     */
    public Point2D locatePoint(Point2D aDstPoint) {
        if (aDstPoint == null) {
            aDstPoint = new Point2D.Double();
        }

        aDstPoint.setLocation(
            location.getX(),
            location.getY());

        return aDstPoint;
    }

    /**
     * Returns the x location of the input point.  I have no idea why this
     * method would be useful.  Perhaps it should go away?
     */
    public double locateXOn(Point2D aNode) {
        System.out.println("  locateX   " + aNode.toString());

        return aNode.getX();
    }

    /**
     * Returns the x location of the input point.  I have no idea why this
     * method would be useful.  Perhaps it should go away?
     */
    public double locateYOn(Point2D aNode) {
        System.out.println("  locateY   " + aNode.toString());

        return aNode.getY();
    }

    /**
     * Updates the point based on offsets.
     *
     * @param xOffset Offset across.
     * @param yOffset Offset down.
     */
    public void update(
        double xOffset,
        double yOffset) {
        location.setLocation(location.getX() + xOffset,
            location.getY() + yOffset);
    }

    /**
     * Returns the point around which this locator revolves.
     */
    public Point2D getLocation() {
        return location;
    }
}
