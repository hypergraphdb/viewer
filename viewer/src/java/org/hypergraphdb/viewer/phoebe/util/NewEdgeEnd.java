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
public abstract class NewEdgeEnd {
 
  protected static double length;
  protected static double sinTheta;
  protected static double cosTheta;
  protected static double midX;
  protected static double midY;
  protected static double botX;
  protected static double botY;
  protected static double topX;
  protected static double topY;
  

  /**
   * The source node point.
   */
  protected static Point2D source;

  /**
   * The target node point. The icon is always drawn at the this point.  To
   * draw the icon at the opposite node, instantiate the child class with
   * the nodes swapped.
   */
  protected static Point2D target;

  /**
   * The new X position for the end of the edge. Since we don't (usually)
   * want the edge as it is currently drawn to  be drawn through the icon,
   * we have to adjust the endpoint of the edge to accomodate the icon.
   * This point must be updated (usually by drawIcon) when the icon gets
   * drawn.
   */
  protected static double newX;

  /**
   * The new X position for the end of the edge. Since we don't (usually)
   * want the edge as it is currently drawn to  be drawn through the icon,
   * we have to adjust the endpoint of the edge to accomodate the icon.
   * This point must be updated (usually by drawIcon) when the icon gets
   * drawn.
   */
  protected static double newY;

  /**
   * The length of the edge.
   */
  protected static double lineLen;

 

  /**
   * Calculates the length of the edge using the pythagorean theorem.
   */
  protected static void calcLineLen() {
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
  public static double getNewX() {
    return newX;
  }

  /**
   * Returns the new Y position of the updated end point of the edge. Since
   * we don't (usually) want the edge as it is currently drawn to  be drawn
   * through the icon, we have to adjust the endpoint of the edge to
   * accomodate the icon.
   */
  public static double getNewY() {
    return newY;
  }

  public static Shape drawT ( Point2D s,
                              Point2D t,
                              double l) {
    
    length = l;
    target = t;
    source = s;
    calcLineLen();
      
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
        
    float[] xs = new float[] {
      (float) topX,
      (float) botX
    };
    float[] ys = new float[] {
      (float) topY,
      (float) botY
    };

    GeneralPath path = new GeneralPath( GeneralPath.WIND_NON_ZERO, xs.length );
    path.moveTo( xs[0], ys[0]);
		for (int i = 1; i < xs.length; i++) {
			path.lineTo( xs[i], ys[i]);
		}
    return path;

  } // drawT
  
  public static Shape drawDiamond ( Point2D s,
                                    Point2D t,
                                    double l ) {
    length = l * Math.sqrt(2);
    target = t;
    source = s;
    calcLineLen();
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

    GeneralPath path = new GeneralPath( GeneralPath.WIND_NON_ZERO, xs.length );
    path.moveTo( xs[0], ys[0]);
		for (int i = 1; i < xs.length; i++) {
			path.lineTo( xs[i], ys[i]);
		}
    return path;
  } // drawDiamond


  public static Shape drawEllipse( Point2D s,
                                   Point2D t,
                                   double d) {
    source = s;
    target = t;
    calcLineLen();
      
    double diameter = d;
     
      
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

    double ellipseX = midX - (diameter / 2);
    double ellipseY = midY - (diameter / 2);
      
    return new Ellipse2D.Float(  (float)ellipseX, 
                                 (float)ellipseY, 
                                 (float)diameter,
                                 (float)diameter  );
  } // drawEllipse

  public static Shape drawDelta ( Point2D s,
                                  Point2D t,
                                  double l) {
    
    target = t;
    source = s;
    length = l * Math.sqrt(2);
    calcLineLen();

    double x1 = target.getX();
    double y1 = target.getY();
    
    double x2 = source.getX();
    double y2 = source.getY();
    
    sinTheta = (y1 - y2) / lineLen;
    cosTheta = (x1 - x2) / lineLen;
    
    newY = y1 - ((sinTheta * length));
    newX = x1 - ((cosTheta * length));
    
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
    
    GeneralPath path = new GeneralPath( GeneralPath.WIND_NON_ZERO, xs.length );
    path.moveTo( xs[0], ys[0]);
		for (int i = 1; i < xs.length; i++) {
			path.lineTo( xs[i], ys[i]);
		}
    return path;
  } // drawDelta


  public static Shape drawArrow (Point2D source,
                                 Point2D target,
                                 double l) {
    
   

    
    length = l * Math.sqrt(2);
    

    System.out.println( "T: "+target.getX()+" "+target.getY()+" S: "+source.getX()+" "+source.getY() );
  
    double x1 = target.getX();
    System.out.println( "x1: "+x1 + " "+target.getX());
    double y1 = target.getY();
    System.out.println( "y1: "+y1 + " "+target.getY());

    double x2 = source.getX();
    System.out.println( "x1: "+x1 + " "+source.getX());
    double y2 = source.getY();
    System.out.println( "y1: "+y1 + " "+source.getY());
    
    lineLen = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
    
    System.out.println(Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))) );

    
    if ( lineLen == 0 )
      return null;

    sinTheta = (y1 - y2) / lineLen;
    cosTheta = (x1 - x2) / lineLen;
    System.out.println( "sin: "+sinTheta+" cos: "+cosTheta );
    midY = y1 - ((sinTheta * length) / .7);
    midX = x1 - ((cosTheta * length) / .7);
    System.out.println( "X: "+midX+" Y: "+midY );
    newY = y1 - ((sinTheta * length) / 1.2);
    newX = x1 - ((cosTheta * length) / 1.2);

    System.out.println( "X: "+newX+" Y: "+newY );

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

    System.out.println( "X: "+botX+" Y: "+botY );
   

    float[] xs = new float[] {
      (float) topX,
      (float) target.getX(),
      (float) botX,
      (float) newX,
      (float) topX
      
    };
    float[] ys = new float[] {
      (float) topY,
      (float) target.getY(),
      (float) botY,
      (float) newY,
      (float) topY,
    };
    
    GeneralPath path = new GeneralPath();
    path.moveTo( xs[0], ys[0]);
    System.out.println( "X: "+xs[0]+" Y: "+ys[0] );
		for (int i = 1; i < xs.length; i++) {
			path.lineTo( xs[i], ys[i]);
      System.out.println( "X: "+xs[i]+" Y: "+ys[i] );
    }
    path.closePath();
    return path;

  } // drawArrow


  



}
