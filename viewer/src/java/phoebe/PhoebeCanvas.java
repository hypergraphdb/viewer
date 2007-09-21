package phoebe;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTarget;
import java.io.IOException;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.swing.PScrollPane;
import edu.umd.cs.piccolo.activities.PTransformActivity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

/*
 * Created on Jun 9, 2005
 *
 * Drag/drop-enabled canvas.
 * Handles drag enter and drop events and also maintains the store of PhoebeCanvasDropListener objects.  
 * Delegates a PhoebeCanvasDropEvent in response to drop event, invoking itemDropped() method on listeners.
 * 
 */

/**
 * @author Allan Kuchinsky
 *
 * 
 */
public class PhoebeCanvas extends PCanvas implements java.awt.dnd.DropTargetListener,
                                                     PhoebeCanvasDroppable
													 {
	private DropTarget dropTarget;
	
	private String CANVAS_DROP = "CanvasDrop";
	
	public Vector listeners = new Vector();

	/**
	 * Creates an instance of the PhoebeCanvas.
	 * Adds a drop target.
	 * 
	 */
	public PhoebeCanvas ()
	{
		super();
		dropTarget = new DropTarget(this, // component
				DnDConstants.ACTION_COPY, // actions
				this); // DropTargetListener

	}
	
    /**
     * default dragEnter handler.  Accepts the drag.
     * @param dte the DropTargetDragEvent
     * 
     */
	public void dragEnter (java.awt.dnd.DropTargetDragEvent dte)
	{

		dte.acceptDrag(DnDConstants.ACTION_COPY);
	}
	
	
    /**
     * default dragExit handler.  Does nothing, can be overridden.
     * @param dte the DropTargetDragEvent
     * 
     */
	public void dragExit (java.awt.dnd.DropTargetEvent dte)
	{

	}
	
    /**
     * default dropActionChanged handler.  Does nothing, can be overridden.
     * @param dte the DropTargetDragEvent
     * 
     */	public void dropActionChanged (java.awt.dnd.DropTargetDragEvent dte)
	{

	}

     /**
      * default dragOver handler.  Does nothing, can be overridden.
      * @param dte the DropTargetDragEvent
      * 
      */	public void dragOver (java.awt.dnd.DropTargetDragEvent dte)
	{

	}
      
      
      /**
       * default drop handler.  Accepts drop, builds a transferable, creates and
       * fires a PhoebeCanvasDropEvent, then calls dropComplete().
       * @param dte the DropTargetDragEvent
       * 
       */	
	public void drop (java.awt.dnd.DropTargetDropEvent dte)
	{
		dte.acceptDrop(DnDConstants.ACTION_COPY);
		
		Transferable t = dte.getTransferable();

		Point pt = dte.getLocation();
		
		PhoebeCanvasDropEvent event = 
			new PhoebeCanvasDropEvent (this,   // we are the event source
					t, // item dropped
					pt // location
					);
		processPhoebeCanvasDropEvent (event);
		
		dte.dropComplete(true);		
	}
	
    /**
     * adds a listener to the store of PhoebeCanvasDropTargetListeners
     * @param l the PhoebeCanvasDropTargetListener
     * 
     */	
	public void addPhoebeCanvasDropListener (PhoebeCanvasDropListener l)
	{
		listeners.addElement(l);
	}
	
    /**
     * removes a listener from the store of PhoebeCanvasDropTargetListeners
     * @param l the PhoebeCanvasDropTargetListener
     * 
     */		public void removePhoebeCanvasDropListener (PhoebeCanvasDropListener l)
	{
		listeners.removeElement(l);
	}
		
     /**
      * handles a PhoebeCanvasDropEvent.  For each listerner, calls its itemDropped() method
      * @param event the PhoebeCanvasDropEvent
      * 
      */	
     protected synchronized void processPhoebeCanvasDropEvent (PhoebeCanvasDropEvent event)
	{
		Enumeration e = listeners.elements();
		while (e.hasMoreElements())
		{
			PhoebeCanvasDropListener l = (PhoebeCanvasDropListener) e.nextElement();
			l.itemDropped(event);
		}
	}

}
