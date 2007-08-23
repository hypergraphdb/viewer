package org.hypergraphdb.viewer.giny;

import giny.model.Edge;
import giny.model.RootGraph;
import org.hypergraphdb.viewer.HGVEdge;
import fing.model.*;

final class HGVEdgeDepot implements FingEdgeDepot
{

  public Edge getEdge(RootGraph root, int index, String id)
  {
    final HGVEdge returnThis = new HGVEdge(root, index);
    return returnThis;
  }

  public void recycleEdge(Edge edge)
  {
  }

}
