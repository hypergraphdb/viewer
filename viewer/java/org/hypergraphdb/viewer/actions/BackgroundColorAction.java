//-------------------------------------------------------------------------
// $Revision: 1.2 $
// $Date: 2006/02/15 15:33:52 $
// $Author: bizi $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;

//-------------------------------------------------------------------------

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.visual.VisualStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//-------------------------------------------------------------------------
/**
 * Prompts User for New Background Color.
 */
public class BackgroundColorAction extends HGVAction {

   
    /**
     * ConstructorLink.
     */
    public BackgroundColorAction () {
        super(ActionManager.BACKGROUND_COLOR_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_B, ActionEvent.ALT_MASK) ;
    }

    /**
     * Captures User Menu Selection.
     * @param ev Action Event.
     */
    public void actionPerformed(ActionEvent ev) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JColorChooser color = new JColorChooser();
                Color newPaint = color.showDialog( 
                		HGViewer.getCurrentView().getComponent(),
                                                  "Choose a Background Color",
                                                  (java.awt.Color)HGViewer.getCurrentView().
                                                  getBackgroundPaint() );

            //  Update the Current Background Color
            //  and Synchronize with current Visual Style
                HGViewer.getCurrentView().setBackgroundPaint(newPaint);
            synchronizeVisualStyle(newPaint);
            }
        });
    }//action performed

    /**
     * Synchronizes the New Background Color with the Current Visual Style.
     * @param newColor New Color
     */
    private void synchronizeVisualStyle(Color newColor) {
       VisualStyle style = HGViewer.getCurrentView().getVisualStyle();
       style.setBackgroundColor(newColor);
    }
}
