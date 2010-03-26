package org.hypergraphdb.viewer.layout;

import org.hypergraphdb.viewer.GraphView;

/**
 * Interface for every layout algorithm that wish to be applied onto GraphView
 */
public interface Layout
{

    /**
     * Returns layout name
     */
    public String getName();

    /**
     * Applies the layout to given GraphView
     * 
     * @param view
     *            The view
     */
    public void applyLayout(GraphView view);

}
