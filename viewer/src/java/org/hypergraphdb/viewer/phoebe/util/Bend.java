package org.hypergraphdb.viewer.phoebe.util;

import org.hypergraphdb.viewer.phoebe.*;
import java.awt.geom.*;
import java.util.*;

import org.hypergraphdb.viewer.GraphView;

import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.util.*;

/**
 * A class that encapsulates the representation of the bend used for a
 * particular PEdgeView.
 * 
 * @author Mike Smoot (mes5k)
 */
public class Bend 
{
	/**
	 * The list of points associated with the handles. If any handles exist,
	 * then points must exist. The points are what are actually used to define
	 * the curve of the Bend.
	 */
	protected Vector<Point2D> handlePointList;
	/**
	 * The list of handles added to the edge. If the PEdgeView is not selected
	 * then the handles aren't displayed and thus this list is of size 0. Actual
	 * handles exist in this list only when the PEdgeView is selected.
	 */
	protected Vector<PNamedHandle> handleList;
	/**
	 * The source point of the PEdgeView. Defines one of the endpoints of the
	 * edge.
	 */
	protected Point2D sourcePoint;
	/**
	 * The target point of the PEdgeView. Defines one of the endpoints of the
	 * edge.
	 */
	protected Point2D targetPoint;
	/**
	 * The PEdgeView this Bend is associated with.
	 */
	protected PEdgeView edgeView;
	/**
	 * The GraphView this Bend is associated with.
	 */
	protected GraphView view;

	/**
	 * Creates a generic Bend with no curve or handles.
	 * @param sourcePoint The Point2D of the source node.
	 * @param targetPoint The Point2D of the target node.
	 * @param edgeView The associated PEdgeView.
	 */
	public Bend(Point2D sourcePoint, Point2D targetPoint, PEdgeView edgeView)
	{
		commonInit(sourcePoint, targetPoint, edgeView);
	}

	/**
	 * Creates a Bend with handles/handlePoints at the points specified.
	 * @param sourcePoint The Point2D of the source node.
	 * @param targetPoint The Point2D of the target node.
	 * @param edgeView The associated PEdgeView.
	 * @param bendPoints A list of Point2Ds used to define a curve.
	 */
	public Bend(Point2D sourcePoint, Point2D targetPoint, PEdgeView edgeView,
			List<Point2D> bendPoints)
	{
		commonInit(sourcePoint, targetPoint, edgeView);
		for (int i = 0; i < bendPoints.size(); i++)
			addHandle(i, bendPoints.get(i));
	}

	/**
	 * A method used to initialize things that are common to all constructors.
	 * Too bad we can't have default params...
	 */
	private void commonInit(Point2D sourcePoint, Point2D targetPoint,
			PEdgeView edgeView)
	{
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		this.edgeView = edgeView;
		handleList = new Vector<PNamedHandle>();
		handlePointList = new Vector<Point2D>();
	}

	/**
	 * Given a list of points removes all existing handles/handlePoints and adds
	 * new ones for those specified in the List.
	 * @param bendPoints A list of Point2Ds to create new handles.
	 */
	public void setHandles(List<Point2D> bendPoints)
	{
		if (handleList.size() > 0)
			for (int i = 0; i < handleList.size(); i++)
				removeHandle(i);
		else
			handlePointList.removeAllElements();
		for (int i = 0; i < bendPoints.size(); i++)
			addHandle(i, bendPoints.get(i));
	}

	/**
	 * Returns a (new) List of clones of the Point2Ds that locate the handles.
	 */
	public List<Point2D> getHandles()
	{
		List<Point2D> l = new ArrayList<Point2D>();
		for (int i = 0; i < handlePointList.size(); i++)
		{
			Point2D pt = (Point2D) handlePointList.get(i).clone();
			l.add(pt);
		}
		return l;
	}

	/**
	 * Moves the handle specified at the given index to the given point.
	 * @param i Index of the handle to move.
	 * @param pt Point2D to which to move the specified handle.
	 */
	public void moveHandle(int i, Point2D pt)
	{
		moveHandle_internal(i, pt);
		edgeView.updateEdgeView();
	}

	public void moveHandle_internal(int i, Point2D pt)
	{
		// System.out.println( "i: "+i+"BEND: "+ handlePointList.size() +
		//		 "is empty : "+handlePointList.isEmpty() );
		if (i == 0 && handlePointList.isEmpty())
		{
			//System.out.println( "Adding handle by hand" );
			handlePointList.add(pt);
		}
		// if ( i != 0 || i >= handlePointList.size() || i < 0 ) {
		if (handlePointList.isEmpty() || i < 0)
		{
			//System.out.println("No point exists at given index.");
			return;
		}
		// System.out.println( "Moving to:"+ pt);
		handlePointList.setElementAt(pt, i);
		if (handleList.size() > 0)
		{
			PHandle h = handleList.get(i);
			PLocator l = h.getLocator();
			l.locatePoint(pt);
		}
	}

	/**
	 * Returns the handle Point2D closest to the source node.
	 */
	public Point2D getSourceHandlePoint()
	{
		// If there are no points in the list, use the point opposite
		// to the sourcePoint... the targetPoint.
		if (handlePointList.size() == 0)
			return targetPoint;
		// If there are points, then use the one closest to the sourcePoint.
		else
			return handlePointList.get(0);
	}

	/**
	 * Returns the handle Point2D closest to the target node.
	 */
	public Point2D getTargetHandlePoint()
	{
		// If there are no points in the list, use the point opposite
		// to the targetPoint... the sourcePoint.
		if (handlePointList.size() == 0)
			return sourcePoint;
		// If there are points, then use the one closest to the targetPoint.
		else
			return handlePointList.get(handlePointList.size() - 1);
	}

	/**
	 * Add a PHandle to the edge at the point specified. Acts as an interface to
	 * actuallyAddHandle() which does the actual adding.
	 * 
	 * @param pt The point at which to draw the PHandle and to which the PHandle
	 * will be attached via the locator.
	 */
	public void addHandle(Point2D pt)
	{
		int index = CubicGenerator.getListIndex(pt, getDrawPoints(), edgeView
				.getLineType());
		actuallyAddHandle(index, pt);
	}

	/**
	 * Add a PHandle to the edge at the point and index specified. Acts as an
	 * interface to actuallyAddHandle() which does the actual adding.
	 * 
	 * @param insertIndex The index at which to add the PHandle to the list of
	 * handles.
	 * @param pt The point at which to draw the PHandle and to which the PHandle
	 * will be attached via the locator.
	 */
	public void addHandle(int insertIndex, Point2D pt)
	{
		if (insertIndex >= 0 && insertIndex <= handlePointList.size())
		{
			actuallyAddHandle(insertIndex, pt);
		} else
		{
			System.out.println("Couldn't insert handle at index: "
					+ insertIndex);
		}
	}

	/**
	 * Actually add a PHandle to the edge at the point and index specified.
	 * Creates the handle at the specified index and its it, as appropriate, to
	 * the list.
	 * 
	 * @param insertIndex The index at which to add the PHandle to the list of
	 * handles.
	 * @param pt The point at which to draw the PHandle and to which the PHandle
	 * will be attached via the locator.
	 */
	private void actuallyAddHandle(int insertIndex, Point2D pt)
	{
		if (edgeView.isSelected())
		{
			PNamedHandle h = createHandle(pt);
			h.addInputEventListener(edgeView.getEdgeHandler());
			edgeView.addChild(h);
			handleList.insertElementAt(h, insertIndex);
		}
		handlePointList.insertElementAt(pt, insertIndex);
		edgeView.updateEdgeView();
	}

	/**
	 * Creates a PHandle based on the input point.
	 */
	protected PNamedHandle createHandle(Point2D pt)
	{
		return new PNamedHandle(pt.toString(), new PPointLocator(edgeView, pt)) {
			public void dragHandle(PDimension dim, PInputEvent aEvent)
			{
				edgeView.localToParent(dim);
				((PPointLocator) getLocator()).update(dim.getWidth(), dim
						.getHeight());
				edgeView.updateEdgeView();
			}
		};
	}

	/**
	 * Removes the PHandle at the specified point.
	 * 
	 * @param pt If this point intersects an existing PHandle, then remove that
	 * PHandle.
	 */
	public void removeHandle(Point2D pt)
	{
		for (int i = 0; i < handleList.size(); i++)
		{
			Rectangle2D rect = new Rectangle2D.Double(pt.getX(), pt.getY(),
					1.0, 1.0);
			if (handleList.get(i).intersects(rect))
			{
				removeHandle(i);
				break;
			}
		}
	}

	/**
	 * Removes the specified PHandle.
	 * 
	 * @param h PHandle to remove.
	 */
	public void removeHandle(PHandle h)
	{
		for (int i = 0; i < handleList.size(); i++)
		{
			if (handleList.get(i).equals(h))
			{
				removeHandle(i);
				break;
			}
		}
	}

	/**
	 * Removes the PHandle at the given index.
	 * 
	 * @param i The index of the PHandle to remove.
	 */
	public void removeHandle(int i)
	{
		if (i >= 0 && i < handleList.size())
		{
			edgeView.removeChild(handleList.get(i));
			handleList.removeElementAt(i);
			handlePointList.removeElementAt(i);
			System.out.println("HANDLE Removed at index: " + i);
		} else
		{
			System.out.println("Couldn't remove handle at index: " + i);
		}
	}

	/**
	 * Removes all handles
	 */
	public void removeAllHandles()
	{
		// drawSelected();
		// System.out.println( "Bend has: "+handleList.size() +" handles or
		// "+handlePointList.size() );
		// for ( int i = 0; i < handlePointList.size(); ++i ) {
		// System.out.println( "removing handle: "+i+" of "+handleList.size() );
		// //removeHandle( ( Point2D )handlePointList.get(i) );
		// //removeHandle( i );
		// handlePointList.removeElementAt(i);
		// }
		handlePointList.removeAllElements();
		drawUnselected();
	}

	/**
	 * Checks to see if a PHandle already exists for the given point.
	 * 
	 * @param pt If this point intersects a currently existing PHandle, then
	 * return true, else return false.
	 */
	public boolean handleAlreadyExists(Point2D pt)
	{
		for (int i = 0; i < handleList.size(); i++)
		{
			Rectangle2D rect = new Rectangle2D.Double(pt.getX(), pt.getY(),
					1.0, 1.0);
			if (handleList.get(i).intersects(rect))
				return true;
		}
		return false;
	}

	/**
	 * Draws any handles previously added.
	 */
	public void drawSelected()
	{
		// For each point in handlePointList, add a handle.
		for (int i = 0; i < handlePointList.size(); i++)
		{
			PNamedHandle h = createHandle(handlePointList.get(i));
			h.addInputEventListener(edgeView.getEdgeHandler());
			edgeView.addChild(h);
			handleList.add(h);
		}
	}

	/**
	 * Removes any handles from the display.
	 */
	public void drawUnselected()
	{
		// Remove each handle, but don't remove the associated point.
		for (int i = 0; i < handlePointList.size(); i++)
		{
			if (i > handleList.size())
			{
				continue;
			}
			edgeView.removeChild(handleList.get(i));
		}
		handleList.removeAllElements();
	}

	/**
	 * Returns a list of points that define what gets drawn and hence what is
	 * visible to the user.
	 */
	public Point2D[] getDrawPoints()
	{
		Point2D[] pts = new Point2D[handlePointList.size() + 2];
		pts[0] = sourcePoint;
		for (int i = 0; i < handlePointList.size(); i++)
			pts[i + 1] = handlePointList.get(i);
		pts[handlePointList.size() + 1] = targetPoint;
		return CubicGenerator.getPoints(pts, edgeView.getLineType());
	}
}
