/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
 //----------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:42 $
// $Author: bobo $
//----------------------------------------------------------------------------
package org.hypergraphdb.viewer.painter;
//----------------------------------------------------------------------------
import org.hypergraphdb.viewer.phoebe.PNodeView;
//----------------------------------------------------------------------------
/**
 * This class is a replacement for the yFiles Shape class.
 * It defines byte constants specifying shape types.
 */
public class Shape {
    
    public static final byte RECT = (byte)0;
    public static final byte ROUND_RECT = (byte)1;
    public static final byte RECT_3D = (byte)2;
    public static final byte TRAPEZOID = (byte)3;
    public static final byte TRAPEZOID_2 = (byte)4;
    public static final byte TRIANGLE = (byte)5;
    public static final byte PARALLELOGRAM = (byte)6;
    public static final byte DIAMOND = (byte)7;
    public static final byte ELLIPSE = (byte)8;
    public static final byte HEXAGON = (byte)9;
    public static final byte OCTAGON = (byte)10;
    
    public static Byte parseNodeShapeTextIntoByte(String text) {
        return new Byte(parseNodeShapeText(text));
    }
    
    public static byte parseNodeShapeText(String text) {
        String nstext = text.trim();
        nstext = nstext.replaceAll("_",""); // ditch all underscores
        
        if(nstext.equalsIgnoreCase("rect")) {
            return Shape.RECT;
        } else if(nstext.equalsIgnoreCase("roundrect")) {
            return Shape.ROUND_RECT;
        } else if(nstext.equalsIgnoreCase("rect3d")) {
            return Shape.RECT_3D;
        } else if(nstext.equalsIgnoreCase("trapezoid")) {
            return Shape.TRAPEZOID;
        } else if(nstext.equalsIgnoreCase("trapezoid2")) {
            return Shape.TRAPEZOID_2;
        } else if(nstext.equalsIgnoreCase("triangle")) {
            return Shape.TRIANGLE;
        } else if(nstext.equalsIgnoreCase("parallelogram")) {
            return Shape.PARALLELOGRAM;
        } else if(nstext.equalsIgnoreCase("diamond")) {
            return Shape.DIAMOND;
        } else if(nstext.equalsIgnoreCase("ellipse") || nstext.equalsIgnoreCase("circle")) {
            return Shape.ELLIPSE;
        } else if(nstext.equalsIgnoreCase("hexagon")) {
            return Shape.HEXAGON;
        } else if(nstext.equalsIgnoreCase("octagon")) {
            return Shape.OCTAGON;
        } else {
            return Shape.RECT;
        }
    }
    
    public static String getNodeShapeText(byte shape) {
        if(shape == RECT){return "rect";}
        if(shape == ROUND_RECT){return "roundrect";}
        if(shape == RECT_3D){return "rect3d";}
        if(shape == TRAPEZOID){return "trapezoid";}
        if(shape == TRAPEZOID_2){return "trapezoid2";}
        if(shape == TRIANGLE){return "triangle";}
        if(shape == PARALLELOGRAM){return "parallelogram";}
        if(shape == DIAMOND){return "diamond";}
        if(shape == ELLIPSE){return "ellipse";}
        if(shape == HEXAGON){return "hexagon";}
        if(shape == OCTAGON){return "octagon";}
        
        return "rect";
    }
    
    public static int getGinyShape(byte byteShape) {
        if (byteShape == TRIANGLE) {
            return PNodeView.TRIANGLE;
        } else if (byteShape == PARALLELOGRAM) {
            return PNodeView.PARALELLOGRAM;
        } else if (byteShape == DIAMOND) {
            return PNodeView.DIAMOND;
        } else if (byteShape == ELLIPSE) {
            return PNodeView. ELLIPSE;
        } else if (byteShape == HEXAGON) {
            return PNodeView.HEXAGON;
        } else if (byteShape == OCTAGON) {
            return PNodeView.OCTAGON;
        } else if (byteShape == ROUND_RECT ) {
          return PNodeView.ROUNDED_RECTANGLE;
        } else {//rectangle, or unknown shape
            return PNodeView.RECTANGLE;
        }
    }
}

