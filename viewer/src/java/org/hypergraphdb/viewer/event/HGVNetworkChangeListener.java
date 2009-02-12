package org.hypergraphdb.viewer.event;
import java.util.EventListener;
public interface HGVNetworkChangeListener extends EventListener {
   public void networkChanged ( HGVNetworkChangeEvent event );
} 
