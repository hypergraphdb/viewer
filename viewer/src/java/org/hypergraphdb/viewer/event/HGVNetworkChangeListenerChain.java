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
  implements HGVNetworkChangeListener
{

  private final HGVNetworkChangeListener a, b;

  private HGVNetworkChangeListenerChain(HGVNetworkChangeListener a,
                                              HGVNetworkChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void networkChanged(HGVNetworkChangeEvent evt)
  {
    a.networkChanged(evt);
    b.networkChanged(evt);
  }

  public static HGVNetworkChangeListener add(HGVNetworkChangeListener a,
                                            HGVNetworkChangeListener b)
  {
    if (a == null) return b;
    if (b == null) return a;
    return new HGVNetworkChangeListenerChain(a, b);
  }

  public static HGVNetworkChangeListener remove(
    HGVNetworkChangeListener l, HGVNetworkChangeListener oldl)
  {
    if (l == oldl || l == null) return null;
    else if (l instanceof HGVNetworkChangeListenerChain)
      return ((HGVNetworkChangeListenerChain) l).remove(oldl);
    else return l;
  }

  private HGVNetworkChangeListener remove(
    HGVNetworkChangeListener oldl)
  {
    if (oldl == a) return b;
    if (oldl == b) return a;
    HGVNetworkChangeListener a2 = remove(a, oldl);
    HGVNetworkChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) return this;
    return add(a2, b2);
  }

}
