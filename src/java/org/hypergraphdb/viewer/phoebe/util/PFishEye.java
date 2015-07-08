package org.hypergraphdb.viewer.phoebe.util;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;


/**
 * The idea of this fisheye lens is that it will zoom
 */
public class PFishEye extends PNode {
    //DOCUMENT ME!
    public static double LENS_DRAGBAR_HEIGHT = 20;

    //DOCUMENT ME!
    public static Paint DEFAULT_DRAGBAR_PAINT = Color.DARK_GRAY;

    //DOCUMENT ME!
    public static Paint DEFAULT_LENS_PAINT = Color.LIGHT_GRAY;
    private PPath handle;
    private PCamera camera;
    private PPath lens;
    private PDragEventHandler lensDragger;
    private PCanvas canvas;

    /**
     * Creates a new PFishEye object.
     *
     * @param canvas DOCUMENT ME!
     */
    public PFishEye(PCanvas canvas) {
        // call PNode Constructor
        super();

        handle = new PPath();
        handle.setPaint(DEFAULT_DRAGBAR_PAINT);
        handle.setPickable(false); // This forces drag events to percolate up to PLens object
        handle.setPathToPolyline(
            new Point2D[] {
                new Point2D.Double(45, 50),
                new Point2D.Double(50, 45),
                new Point2D.Double(90, 85),
                new Point2D.Double(85, 90),
                new Point2D.Double(45, 50)
            });
        addChild(handle);

        lens = new PClip();
        lens.setPathToEllipse(0, 0, 60, 60);

        camera = new PCamera();
        camera.setPaint(DEFAULT_LENS_PAINT);
        camera.addLayer(
            0,
            canvas.getLayer());
        camera.setBounds(0, 0, 60, 60);
        lens.addChild(camera);
        addChild(lens);

        // create an event handler to drag the lens around. Note that this event
        // handler consumes events in case another conflicting event handler has been
        // installed higher up in the heirarchy.
        //lensDragger = new PFishEyeEventHandler( canvas );
        lensDragger = new PDragEventHandler();
        lensDragger.getEventFilter()
                   .setMarksAcceptedEventsAsHandled(true);
        addInputEventListener(lensDragger);

        // When this PLens is dragged around adjust the cameras view transform. 	
        addPropertyChangeListener(
            "transform",
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    double scale = camera.getViewScale();
                    camera.setViewTransform(getInverseTransform());
                    camera.scaleViewAboutPoint(
                        scale,
                        camera.getViewBounds().getX(),
                        camera.getViewBounds().getY());
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PPath getDragBar() {
        return handle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PDragEventHandler getLensDraggerHandler() {
        return lensDragger;
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     * @param layer DOCUMENT ME!
     */
    public void addLayer(
        int index,
        PLayer layer) {
        camera.addLayer(index, layer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param layer DOCUMENT ME!
     */
    public void removeLayer(PLayer layer) {
        camera.removeLayer(layer);
    }

    // when the lens is resized this method gives us a chance to layout the lenses
    // camera child appropriately.
    protected void layoutChildren() {
        //dragBar.setPathToRectangle((float)getX(), (float)getY(), (float)getWidth(), (float)LENS_DRAGBAR_HEIGHT);
        //camera.setBounds(getX(), getY() + LENS_DRAGBAR_HEIGHT, getWidth(), getHeight() - LENS_DRAGBAR_HEIGHT);
    }
}
