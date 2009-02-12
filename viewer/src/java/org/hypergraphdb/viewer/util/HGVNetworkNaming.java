package org.hypergraphdb.viewer.util;

import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVKit;

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
    for (HGVNetwork net: HGVKit.getNetworkMap().keySet()) 
      if (titleCandidate.equals(net.getTitle()))
          return true; 
    return false;
  }

}
