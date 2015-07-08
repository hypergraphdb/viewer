/*
 * This code is based on the SpringLayout of JUNG
 */
package org.hypergraphdb.viewer.layout;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.viewer.GraphView;

import org.hypergraphdb.viewer.phoebe.PEdgeView;
import org.hypergraphdb.viewer.phoebe.PNodeView;

/**
 *  SpringLayout implementation.
 */
class JUNGSpringLayout extends AbstractLayout
{
    private LengthFunction lengthFunction;
    public static int RANGE = 100;
    private double FORCE_CONSTANT = 1.0 / 3.0;
    private Map<PNodeView, SpringVertexData> nodeIndexToDataMap;
    private Map<PEdgeView, SpringEdgeData> edgeIndexToDataMap;
    double increment;
    double NUM_INCRMENTS = 100;
    long relaxTime = 0;
    public static int STRETCH = 70;
    public static final LengthFunction UNITLENGTHFUNCTION = new UnitLengthFunction(
            30);

    /**
     * Constructor for a SpringLayout for a raw graph with associated
     * dimension--the input knows how big the graph is. Defaults to the unit
     * length function.
     */
    public JUNGSpringLayout(GraphView g)
    {
        this(g, UNITLENGTHFUNCTION);
    }

    /**
     * Constructor for a SpringLayout for a raw graph with associated component.
     * 
     * @param g
     *            the input Graph
     * @param f
     *            the length function
     */
    public JUNGSpringLayout(GraphView g, LengthFunction f)
    {
        super(g);
        this.lengthFunction = f;
        nodeIndexToDataMap = new HashMap<PNodeView, SpringVertexData>(graphView
                .getNodeViewCount());
        edgeIndexToDataMap = new HashMap<PEdgeView, SpringEdgeData>(graphView
                .getEdgeViewCount());
    }

    public void doLayout()
    {
        initialize(null);
        while (!incrementsAreDone())
        {
            advancePositions();
            // System.out.println( increment +" "+getStatus() );
        }
       // for (PNodeView n : graphView.getNodeViews())
       //     n.setNodePosition(true);
    }

    protected void initialize_local()
    {
        increment = 0;
        for (PEdgeView e : graphView.getEdgeViews())
        {
            SpringEdgeData sed = getSpringData(e);
            if (sed == null)
            {
                sed = new SpringEdgeData(e);
                edgeIndexToDataMap.put(e, sed);
            }
            calcEdgeLength(sed, lengthFunction);
        }
    }

    protected void initialize_local_node_view(PNodeView v)
    {
        SpringVertexData vud = getSpringData(v);
        if (vud == null)
        {
            vud = new SpringVertexData();
            nodeIndexToDataMap.put(v, vud);
        }
    }

    protected void calcEdgeLength(SpringEdgeData sed, LengthFunction f)
    {
        sed.length = f.getLength(sed.e);
    }

    /**
     * Relaxation step. Moves all nodes a smidge.
     */
    public void advancePositions()
    {
        increment++;
        for (PNodeView v : graphView.getNodeViews())
        {
            SpringVertexData svd = getSpringData(v);
            if (svd == null)
            {
                System.out.println("How confusing!");
                continue;
            }
            svd.dx /= 4;
            svd.dy /= 4;
            svd.edgedx = svd.edgedy = 0;
            svd.repulsiondx = svd.repulsiondy = 0;
        }
        relaxEdges();
        calculateRepulsion();
        moveNodes();
    }

    private void relaxEdges()
    {
        for (PEdgeView e : graphView.getEdgeViews())
        {
            PNodeView source = graphView.getNodeView(e.getEdge().getSource());
            PNodeView target = graphView.getNodeView(e.getEdge().getTarget());
            double source_x = source.getX();
            double source_y = source.getY();
            double target_x = target.getX();
            double target_y = target.getY();
            double vx = source_x - target_x;
            double vy = source_y - target_y;
            double len = Math.sqrt(vx * vx + vy * vy);
            double desiredLen = getLength(e);
            len = (len == 0) ? .0001 : len;
            // force factor: optimal length minus actual length,
            // is made smaller as the current actual length gets larger.
            // why?
            // System.out.println("Desired : " + getLength( e ));
            double f = FORCE_CONSTANT * (desiredLen - len) / len;
            int deg1 = graphView.getAdjacentEdges(source.getNode(), true, true).length;
            int deg2 = graphView.getAdjacentEdges(target.getNode(), true, true).length;
            f = f * Math.pow(STRETCH / 100.0, (deg1 + deg2 - 2));
            // the actual movement distance 'dx' is the force multiplied by the
            // distance to go.
            double dx = f * vx;
            double dy = f * vy;

            SpringVertexData v1D = getSpringData(source);
            SpringVertexData v2D = getSpringData(target);
            SpringEdgeData sed = getSpringData(e);
            sed.f = f;
            if (v1D != null)
            {
                v1D.edgedx += dx;
                v1D.edgedy += dy;
            }
            if (v2D != null)
            {
                v2D.edgedx += -dx;
                v2D.edgedy += -dy;
            }
        }
    }

    private void calculateRepulsion()
    {
        for (PNodeView v : graphView.getNodeViews())
        {
            if (dontMove(v)) continue;
            SpringVertexData svd = getSpringData(v);
            double dx = 0, dy = 0;
            for (PNodeView v2 : graphView.getNodeViews())
            {
                if (v == v2) continue;
                double v_x = v.getX();
                double v_y = v.getY();
                double v2_x = v2.getX();
                double v2_y = v2.getY();
                double vx = v_x - v2_x;
                double vy = v_y - v2_y;
                double distance = vx * vx + vy * vy;
                if (distance == 0)
                {
                    dx += Math.random();
                    dy += Math.random();
                }
                else if (distance < RANGE * RANGE)
                {
                    double factor = 1;
                    dx += factor * vx / Math.pow(distance, 2);
                    dy += factor * vy / Math.pow(distance, 2);
                }
            }
            double dlen = dx * dx + dy * dy;
            if (dlen > 0)
            {
                dlen = Math.sqrt(dlen) / 2;
                svd.repulsiondx += dx / dlen;
                svd.repulsiondy += dy / dlen;
            }
        }
    }

    protected void moveNodes()
    {
        synchronized (getCurrentSize())
        {
            for (PNodeView v : graphView.getNodeViews())
            {
                if (dontMove(v)) continue;
                SpringVertexData vd = getSpringData(v);
                double v_x = v.getX();
                double v_y = v.getY();
                vd.dx += vd.repulsiondx + vd.edgedx;
                vd.dy += vd.repulsiondy + vd.edgedy;
                // keeps nodes from moving any faster than 5 per time unit
                v.offset(v_x + (Math.max(-5, Math.min(5, vd.dx))), 
                       v_y  + (Math.max(-5, Math.min(5, vd.dy))));
               
                int width = getCurrentSize().width;
                int height = getCurrentSize().height;
                if (v_x < 0)
                    v.setOffset(0, 0);
                else if (v_x > width)
                    v.setOffset(width,0);
                if (v_y < 0)
                    v.setOffset(0, 0);
                else if (v_y > height)
                   v.setOffset(0, height);
            }
        }
    }

    public SpringVertexData getSpringData(PNodeView v)
    {
        return nodeIndexToDataMap.get(v);
    }

    public SpringEdgeData getSpringData(PEdgeView e)
    {
        return edgeIndexToDataMap.get(e);
    }

    public double getLength(PEdgeView e)
    {
        return edgeIndexToDataMap.get(e).length;
    }

    /* ---------------Length Function------------------ */
    /**
     * If the edge is weighted, then override this method to show what the
     * visualized length is.
     * 
     * @author Danyel Fisher
     */
    public interface LengthFunction
    {
        public double getLength(PEdgeView e);
    }

    private static final class UnitLengthFunction implements LengthFunction
    {
        int length;

        public UnitLengthFunction(int length)
        {
            this.length = length;
        }

        public double getLength(PEdgeView e)
        {
            return length;
        }
    }

    /* ---------------User Data------------------ */
    static class SpringVertexData
    {
        public double edgedx;
        public double edgedy;
        public double repulsiondx;
        public double repulsiondy;

        public SpringVertexData()
        {
        }

        /** movement speed, x */
        public double dx;
        /** movement speed, y */
        public double dy;
    }

    static class SpringEdgeData
    {
        public double f;
        PEdgeView e;
        double length;

        public SpringEdgeData(PEdgeView e)
        {
            this.e = e;
        }
    }

    /* ---------------Resize handler------------------ */
    public class SpringDimensionChecker extends ComponentAdapter
    {
        public void componentResized(ComponentEvent e)
        {
            resize(e.getComponent().getSize());
        }
    }

    /**
     * This one is an incremental visualization
     */
    public boolean isIncremental()
    {
        return true;
    }

    /**
     * For now, we pretend it never finishes.
     */
    public boolean incrementsAreDone()
    {
        if (increment < NUM_INCRMENTS) { return false; }
        return true;
    }
}
