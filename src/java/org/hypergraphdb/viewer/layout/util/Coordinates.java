package org.hypergraphdb.viewer.layout.util;


/**
 * Simple class to represent a x,y coordinate 
 */
public class Coordinates extends java.awt.geom.Point2D.Float
{

    public Coordinates()
    {
    }

    public Coordinates(double x, double y)
    {
        super((float)x, (float)y);
    }

    public Coordinates(Coordinates coordinates)
    {
        this(coordinates.getX(), coordinates.getY());
    }

    public void setX(double d)
    {
        setLocation(d, getY());
    }

    public void setY(double d)
    {
        setLocation(getX(), d);
    }

    public void add(double x, double y)
    {
        addX(x);
        addY(y);
    }

    public void addX(double d)
    {
        setX(getX() + d);
    }

    public void addY(double d)
    {
        setY(getY() + d);
    }

    public void mult(double x, double y)
    {
        multX(x);
        multY(y);
    }

    public void multX(double d)
    {
        setX(getX() * d);
    }

    public void multY(double d)
    {
        setY(getY() * d);
    }

    public double distance(Coordinates o)
    {
        return super.distance(o);
    }

    public Coordinates midpoint(Coordinates o)
    {
        double midX = (getX() + o.getX()) / 2D;
        double midY = (getY() + o.getY()) / 2D;
        Coordinates midpoint = new Coordinates(midX, midY);
        return midpoint;
    }
}
