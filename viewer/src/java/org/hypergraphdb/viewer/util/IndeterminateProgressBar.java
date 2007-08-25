/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
// IndeterminateProgressBar.java
//-----------------------------------------------------------------
// $Date: 2005/12/25 01:22:42 $
// $Author: iliana
//-----------------------------------------------------------------
package org.hypergraphdb.viewer.util;
//-----------------------------------------------------------------
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.border.*;
import javax.swing.BoxLayout;
//-----------------------------------------------------------------
/**
 * This class creates a dialog with a JProgressBar in indeterminate state.
 * Showing a bar in indeterminate state is useful when a long task is running,
 * the task's running time can't be approximated (if it can, then it is 
 * better to use csplugins.util.HGVProgressMonitor), and the client
 * wants to let the user know that something is happening (instead of giving the 
 * impression of a frozen program).
 */
public class IndeterminateProgressBar extends JDialog {
  
  JPanel mainPanel,labelPanel,barPanel;
  JLabel label;
  String labelText;
  JProgressBar pBar;

  /**
   * Constructs an initially invisible, non-modal Dialog with no owner, the given title and label.
   */
  public IndeterminateProgressBar (String title, String label){
    super();
    labelText = label;
    setTitle(title);
  }//cons
  
  /**
   * Constructs an initially invisible, non-modal Dialog with the specified owner dialog, title and label.
   */
  public IndeterminateProgressBar (Dialog owner, 
                                   String title, String label) {
    super(owner,title);
    labelText = label;
    create();
  }//cons
  
  /**
   * Constructs an initially invisible, non-modal Dialog with the specified owner frame, title and label.
   */
  public IndeterminateProgressBar(Frame owner,
                                  String title, String label){
    super(owner,title);
    labelText = label;
    create();
  }//cons

  protected void create () {
    mainPanel = new JPanel();
    mainPanel.setLayout (new BoxLayout (mainPanel, BoxLayout.Y_AXIS));
    labelPanel = new JPanel();
    label = new JLabel(labelText);
    labelPanel.add(label);
    mainPanel.add(labelPanel);
    barPanel = new JPanel();
    pBar = new JProgressBar(JProgressBar.HORIZONTAL);
    pBar.setIndeterminate(true);
    barPanel.add(pBar);
    mainPanel.add(barPanel);
    getContentPane().add(mainPanel);
  }//create

  public void setLabelText(String label_text){
    labelText = label_text;
    label.setText(labelText);
  }//setLabelText

    
}//IndeterminateProgressBar
