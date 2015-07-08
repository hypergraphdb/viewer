package org.hypergraphdb.viewer.phoebe.util;

import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PNodeLocator;


/**
 * A little wrapper class that adds a name to a PHandle. Used primarily for
 * debugging purposes.
 */
public class PNamedHandle extends PHandle {
    /**
     * Handle name.
     */
    protected String name;

    /**
     * Constructor.
     *
     * @param name The name of the handle.
     * @param locator The locator for this handle.
     */
    public PNamedHandle(
        String name,
        PNodeLocator locator) {
        super(locator);
        this.name = name;
    }

    /**
     * Returns the name of the handle.
     */
    public String getName() {
        return name;
    }
}
