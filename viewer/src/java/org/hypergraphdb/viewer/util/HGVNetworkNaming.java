package org.hypergraphdb.viewer.util;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;

public class HGVNetworkNaming
{

  public static String getSuggestedSubnetworkTitle(HGVNetworkView view)
  {
    for (int i = 0; true; i++) {
      String nameCandidate =
        view.getIdentifier() + "->child" + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(nameCandidate)) 
          return nameCandidate; }
  }

  public static String getSuggestedNetworkTitle(String desiredTitle)
  {
    for (int i = 0; true; i++) {
      String titleCandidate = desiredTitle + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(titleCandidate)) return titleCandidate; }
  }

  private static boolean isNetworkTitleTaken(String titleCandidate)
  {
    for (HGVNetworkView v: HGVKit.getNetworkViewsList()) 
      if (titleCandidate.equals(v.getIdentifier()))
          return true; 
    return false;
  }

}
