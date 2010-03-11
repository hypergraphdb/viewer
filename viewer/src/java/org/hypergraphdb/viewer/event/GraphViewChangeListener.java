package org.hypergraphdb.viewer.event;
import java.util.EventListener;
public interface GraphViewChangeListener extends EventListener {
   public void graphChanged ( GraphViewChangeEvent event );
} 
