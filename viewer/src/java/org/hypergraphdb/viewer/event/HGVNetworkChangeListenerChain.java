package org.hypergraphdb.viewer.event;



// Package visible.
// Analagous to java.awt.AWTEventMulticaster for chaining together
// giny.model.GraphPerspectiveChangeListener objects.  Example usage:
//
// public class Foo implements GraphPerspective
// {
//   private GraphPerspectiveChangeListener lis = null;
//   public void addGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.add(lis, l); }
//   public void removeGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.remove(lis, l); }
//   ...
// }

public class HGVNetworkChangeListenerChain
  implements GraphViewChangeListener
{

  private final GraphViewChangeListener a, b;

  private HGVNetworkChangeListenerChain(GraphViewChangeListener a,
                                              GraphViewChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void graphChanged(GraphViewChangeEvent evt)
  {
    a.graphChanged(evt);
    b.graphChanged(evt);
  }

  public static GraphViewChangeListener add(GraphViewChangeListener a,
                                            GraphViewChangeListener b)
  {
    if (a == null) return b;
    if (b == null) return a;
    return new HGVNetworkChangeListenerChain(a, b);
  }

  public static GraphViewChangeListener remove(
    GraphViewChangeListener l, GraphViewChangeListener oldl)
  {
    if (l == oldl || l == null) return null;
    else if (l instanceof HGVNetworkChangeListenerChain)
      return ((HGVNetworkChangeListenerChain) l).remove(oldl);
    else return l;
  }

  private GraphViewChangeListener remove(
    GraphViewChangeListener oldl)
  {
    if (oldl == a) return b;
    if (oldl == b) return a;
    GraphViewChangeListener a2 = remove(a, oldl);
    GraphViewChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) return this;
    return add(a2, b2);
  }

}
