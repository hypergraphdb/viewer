package org.hypergraphdb.viewer.giny;

import giny.model.Node;
import giny.model.RootGraph;
import fing.model.*;
import org.hypergraphdb.viewer.HGVNode;

final class HGVNodeDepot implements FingNodeDepot
{

  public Node getNode(RootGraph root, int index, String id)
  {
	 return new HGVNode(root, index);
  }

  public void recycleNode(Node node)
  {
  }

}
