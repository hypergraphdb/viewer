package org.hypergraphdb.viewer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.util.CreditScreen;

public class HelpAboutAction extends AbstractAction   {

    public HelpAboutAction() {
        super(ActionManager.ABOUT_ACTION);
    }
    public void actionPerformed(ActionEvent e) {

        /*
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
		          lines.append("\n HGVKit is a collaboration \n" );
                  lines.append("between the Institute for Systems\n" );
                  lines.append("Biology, University of California,\n" );
                  lines.append("San Diego, Memorial Sloan Kettering\n" );
                  lines.append("Cancer Center and the\n" );
                  lines.append("Institute Pasteur.\n" );
                  lines.append(" \n" );
                  lines.append("For more information, please see: \n" );
                  lines.append(" http://www.org.hypergraphdb.viewer.org\n" );
                  lines.append(" \n" );
                  lines.append("Cytosape 2.x Primary Developers:\n" );
                  lines.append(" Mark Anderson, Iliana Avila-Campillo,\n");
		  lines.append(" Ethan Cerami, Rowan Christmas,\n");
		  lines.append(" Ryan Kelley, Andrew Markiel,\n");
		  lines.append(" Nerius Landys, and Chris Workman\n" );
                  lines.append(" \n" );
                  lines.append("ISB: Hamid Bolouri (PI) \n" );
                  lines.append(" Paul Shannon, David Reiss, James\n" );
                  lines.append(" Taylor, Larissa KamenkoVich and \n" );
                  lines.append(" Paul Edlefsen ( GINY Library )\n" );
                  lines.append(" \n" );
                  lines.append("UCSD: Trey Ideker (PI) \n" );
                  lines.append(" Jonathan Wang,  Nada Amin, and \n" );
                  lines.append(" Owen Ozier\n" );
                  lines.append(" \n" );
                  lines.append("MSKCC: Chris Sander (PI) \n" );
                  lines.append(" Gary Bader,  Robert Sheridan\n" );
                  lines.append(" \n" );
                  lines.append("IP: Benno Shwikowski (PI) \n" );
                  lines.append(" \n" );
                  lines.append("Addional Collaborators\n" );

               CreditScreen.showCredits( getClass().getResource(
			"/org/hypergraphdb/viewer/images/CytoscapeCredits.png"),
			lines.toString() );
              }
          }
         
      );
         */
    } 
}

