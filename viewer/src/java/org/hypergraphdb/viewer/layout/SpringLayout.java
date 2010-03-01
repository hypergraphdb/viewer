package org.hypergraphdb.viewer.layout;

import org.hypergraphdb.viewer.GraphView;

public class SpringLayout implements Layout
{
    public String getName()
    {
        return "Spring";
    }

    public void applyLayout(GraphView view)
    {
        if (view == null) return;
        JUNGSpringLayout l = new JUNGSpringLayout(view);
        l.doLayout();
    }
}
