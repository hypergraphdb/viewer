package org.hypergraphdb.viewer.visual.ui;

import java.net.URL;
import javax.swing.*;
import org.hypergraphdb.viewer.painter.Shape;
import org.hypergraphdb.viewer.visual.Arrow;
import org.hypergraphdb.viewer.visual.LineType;

/**
 * Static class with few utility methods used in various editors
 */
public class EditorConstants
{
	private static ImageIcon[] arrowIcons = new ImageIcon[16];
	private static Arrow[] arrows = new Arrow[16];
	static
	{
		arrowIcons[0] = new ImageIcon(locateImage("edgeEnds/arrow_none.jpg"),
				"NONE");
		arrowIcons[1] = new ImageIcon(
				locateImage("edgeEnds/WHITE_DIAMOND.jpg"), "WHITE_DIAMOND");
		arrowIcons[2] = new ImageIcon(
				locateImage("edgeEnds/BLACK_DIAMOND.jpg"), "BLACK_DIAMOND");
		arrowIcons[3] = new ImageIcon(
				locateImage("edgeEnds/COLOR_DIAMOND.jpg"), "COLOR_DIAMOND");
		arrowIcons[4] = new ImageIcon(locateImage("edgeEnds/WHITE_DELTA.jpg"),
				"WHITE_DELTA");
		arrowIcons[5] = new ImageIcon(locateImage("edgeEnds/BLACK_DELTA.jpg"),
				"BLACK_DELTA");
		arrowIcons[6] = new ImageIcon(locateImage("edgeEnds/COLOR_DELTA.jpg"),
				"COLOR_DELTA");
		arrowIcons[7] = new ImageIcon(locateImage("edgeEnds/WHITE_CIRCLE.jpg"),
				"WHITE_CIRCLE");
		arrowIcons[8] = new ImageIcon(locateImage("edgeEnds/BLACK_CIRCLE.jpg"),
				"BLACK_CIRCLE");
		arrowIcons[9] = new ImageIcon(locateImage("edgeEnds/COLOR_CIRCLE.jpg"),
				"COLOR_CIRCLE");
		arrowIcons[10] = new ImageIcon(locateImage("edgeEnds/WHITE_ARROW.jpg"),
				"WHITE_ARROW");
		arrowIcons[11] = new ImageIcon(locateImage("edgeEnds/BLACK_ARROW.jpg"),
				"BLACK_ARROW");
		arrowIcons[12] = new ImageIcon(locateImage("edgeEnds/COLOR_ARROW.jpg"),
				"COLOR_ARROW");
		arrowIcons[13] = new ImageIcon(locateImage("edgeEnds/WHITE_T.jpg"),
				"WHITE_T");
		arrowIcons[14] = new ImageIcon(locateImage("edgeEnds/BLACK_T.jpg"),
				"BLACK_T");
		arrowIcons[15] = new ImageIcon(locateImage("edgeEnds/COLOR_T.jpg"),
				"COLOR_T");
		
		arrows[0] =Arrow.NONE;
		arrows[1] = Arrow.WHITE_DIAMOND;
		arrows[2] = Arrow.BLACK_DIAMOND;
		arrows[3] = Arrow.COLOR_DIAMOND;
		arrows[4] = Arrow.WHITE_DELTA;
		arrows[5] = Arrow.BLACK_DELTA;
		arrows[6] = Arrow.COLOR_DELTA;
		arrows[7] = Arrow.WHITE_CIRCLE;
		arrows[8] = Arrow.BLACK_CIRCLE;
		arrows[9] = Arrow.COLOR_CIRCLE;
		arrows[10] = Arrow.WHITE_ARROW;
		arrows[11] = Arrow.BLACK_ARROW;
		arrows[12] = Arrow.COLOR_ARROW;
		arrows[13] = Arrow.WHITE_T;
		arrows[14] = Arrow.BLACK_T;
		arrows[15] = Arrow.COLOR_T;
	}

	public static ImageIcon[] getArrowIcons()
	{
		return arrowIcons;
	}
	
	public static Arrow[] getArrows()
	{
		return arrows;
	}
	
	public static int getArrowIndex(Arrow t)
	{
		for (int i = 0; i < arrows.length; i++)
			if (t.equals(arrows[i])) return i;
		return 0;
	}
	
	private static final ImageIcon[] shapeIcons = new ImageIcon[11];
	static
	{
		// Array of icons for the list
		shapeIcons[Shape.ELLIPSE] = new ImageIcon(
				locateImage("ellipse.jpg"), "ELLIPSE");
		shapeIcons[Shape.ROUND_RECT] = new ImageIcon(
				locateImage("round_rect.jpg"), "ROUND_RECT");
		shapeIcons[Shape.RECT_3D] = new ImageIcon(
				locateImage("rect_3d.jpg"), "RECT_3D");
		shapeIcons[Shape.RECT] = new ImageIcon(
				locateImage("rect.jpg"), "RECTANGLE");
		shapeIcons[Shape.DIAMOND] = new ImageIcon(
				locateImage("diamond.jpg"), "DIAMOND");
		shapeIcons[Shape.HEXAGON] = new ImageIcon(
				locateImage("hexagon.jpg"), "HEXAGON");
		shapeIcons[Shape.OCTAGON] = new ImageIcon(
				locateImage("octagon.jpg"), "OCTAGON");
		shapeIcons[Shape.TRAPEZOID] = new ImageIcon(
				locateImage("trapezoid.jpg"), "TRAPEZOID");
		shapeIcons[Shape.TRAPEZOID_2] = new ImageIcon(
				locateImage("trapezoid_2.jpg"), "TRAPEZOID_2");
		shapeIcons[Shape.PARALLELOGRAM] = new ImageIcon(
				locateImage("parallelogram.jpg"), "PARALLELOGRAM");
		shapeIcons[Shape.TRIANGLE] = new ImageIcon(
				locateImage("triangle.jpg"), "TRIANGLE");
	}

	public static ImageIcon[] getShapeIcons()
	{
		return shapeIcons;
	}
	private static final ImageIcon[] lineTypeIcons = new ImageIcon[12];
	private static final LineType[] lineTypes = new LineType[12];
	static
	{
		lineTypeIcons[0] = new ImageIcon(locateImage("line_1.jpg"), "LINE_1");
		lineTypeIcons[1] = new ImageIcon(locateImage("line_2.jpg"), "LINE_2");
		lineTypeIcons[2] = new ImageIcon(locateImage("line_3.jpg"), "LINE_3");
		lineTypeIcons[3] = new ImageIcon(locateImage("line_4.jpg"), "LINE_4");
		lineTypeIcons[4] = new ImageIcon(locateImage("line_5.jpg"), "LINE_5");
		lineTypeIcons[5] = new ImageIcon(locateImage("line_6.jpg"), "LINE_6");
		lineTypeIcons[6] = new ImageIcon(locateImage("line_7.jpg"), "LINE_7");
		lineTypeIcons[7] = new ImageIcon(locateImage("dashed_1.jpg"),
				"DASHED_1");
		lineTypeIcons[8] = new ImageIcon(locateImage("dashed_2.jpg"),
				"DASHED_2");
		lineTypeIcons[9] = new ImageIcon(locateImage("dashed_3.jpg"),
				"DASHED_3");
		lineTypeIcons[10] = new ImageIcon(locateImage("dashed_4.jpg"),
				"DASHED_4");
		lineTypeIcons[11] = new ImageIcon(locateImage("dashed_5.jpg"),
				"DASHED_5");
		lineTypes[0] = LineType.LINE_1;
		lineTypes[1] = LineType.LINE_2;
		lineTypes[2] = LineType.LINE_3;
		lineTypes[3] = LineType.LINE_4;
		lineTypes[4] = LineType.LINE_5;
		lineTypes[5] = LineType.LINE_6;
		lineTypes[6] = LineType.LINE_7;
		lineTypes[7] = LineType.DASHED_1;
		lineTypes[8] = LineType.DASHED_2;
		lineTypes[9] = LineType.DASHED_3;
		lineTypes[10] = LineType.DASHED_4;
		lineTypes[11] = LineType.DASHED_5;
	}

	public static ImageIcon[] getLineTypeIcons()
	{
		return lineTypeIcons;
	}

	public static LineType[] getLineTypes()
	{
		return lineTypes;
	}

	public static int getLineTypeIndex(LineType t)
	{
		for (int i = 0; i < lineTypes.length; i++)
			if (t.equals(lineTypes[i])) return i;
		return -1;
	}

	/**
	 * Get the image from the .jar file
	 */
	private static URL locateImage(String imageFilename)
	{
		URL url = EditorConstants.class.getClassLoader().getResource(
				"org/hypergraphdb/viewer/images/" + imageFilename);
		if (url == null)
			url = EditorConstants.class.getClassLoader()
					.getResource(
							"org/hypergraphdb/viewer/visual/ui/images/"
									+ imageFilename);
		if (url == null)
			System.out.println("EditorConstants - locateImage: Could not find: "
					+ imageFilename);
		return url;
	}
}
