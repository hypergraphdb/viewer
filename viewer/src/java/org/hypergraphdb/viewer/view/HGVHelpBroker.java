package org.hypergraphdb.viewer.view;

import org.hypergraphdb.viewer.HGViewer;
import javax.help.*;
import java.net.*;

/**
 * This class creates the HGViewer Help Broker for managing the JavaHelp
 * system and help set access
 */
public class HGVHelpBroker {

  HelpBroker hb;
  HelpSet hs;

  public HGVHelpBroker() {

    hb = null;
    hs = null;
    URL hsURL = getClass().getResource("/org/hypergraphdb/viewer/help/org.hypergraphdb.viewer.hs");

    ClassLoader cl = HGViewer.class.getClassLoader();
    try {
	hs = new HelpSet(null, hsURL);
	hb = hs.createHelpBroker();
    } catch (Exception e) {
	System.out.println("HelpSet " + e.getMessage());
	System.out.println("HelpSet " + hs + " not found.");
    }
  }

  public HelpBroker getHelpBroker() { return hb; }
  public HelpSet getHelpSet() { return hs; }
  
}
