package org.hypergraphdb.viewer.util;

import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGViewer;
import java.util.Iterator;
import java.util.Set;

public class HGVNetworkNaming
{

  public static String getSuggestedSubnetworkTitle(HGVNetwork parentNetwork)
  {
    for (int i = 0; true; i++) {
      String nameCandidate =
        parentNetwork.getTitle() + "->child" + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(nameCandidate)) return nameCandidate; }
  }

  public static String getSuggestedNetworkTitle(String desiredTitle)
  {
    for (int i = 0; true; i++) {
      String titleCandidate = desiredTitle + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(titleCandidate)) return titleCandidate; }
  }

  private static boolean isNetworkTitleTaken(String titleCandidate)
  {
    Set existingNetworks = HGViewer.getNetworkSet();
    Iterator iter = existingNetworks.iterator();
    while (iter.hasNext()) {
      HGVNetwork existingNetwork = (HGVNetwork) iter.next();
      if (existingNetwork.getTitle().equals(titleCandidate))
        return true; }
    return false;
  }

}
