//----------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:42 $
// $Author: bobo $
//----------------------------------------------------------------------------
package org.hypergraphdb.viewer.visual.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * PopupIconChooser displays a popup window for the user to select an icon.
 */
public class PopupIconChooser {
    private String title, objectName;
    int selectedIndex;
    private JDialog mainDialog;
    private Frame parentDialog;
    private JList iconList;
    private boolean alreadyConstructed = false;

    /**
     * Create a PopupIconChooser with the supplied attributes.
     *
     * @param	title		title to display in the popup dialog
     * @param	objectName	name/description of icon being set
     * @param	icons		icons to choose from
     * @param	startIconObject	initially selected icon
     * @param	parentDialog	parent dialog of the selection popup
     */
    public PopupIconChooser(String title,
			    String objectName,
			    ImageIcon[] icons,
			    int selectedIndex,
			    Frame parentDialog) {
	this.parentDialog = parentDialog;
	this.title = title;
	this.objectName = objectName;

	this.iconList = new JList(icons);
	this.selectedIndex = selectedIndex;
	}

    public int showDialog() {
	if(!alreadyConstructed) {
	    mainDialog = new JDialog(parentDialog, this.title, true);

	    JPanel mainPanel = new JPanel(new GridLayout(0,1));

	    // create buttons
	    final JButton setButton = new JButton("Apply");
	    JButton cancelButton = new JButton("Cancel");
	    setButton.addActionListener    (new ApplyIconAction());
	    cancelButton.addActionListener (new CancelIconAction());

	    // create list
	    iconList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	    iconList.setVisibleRowCount(1);
	    //iconList.setFixedCellHeight(35);
	    //iconList.setFixedCellWidth(35);
	    iconList.setBackground(Color.WHITE);
	    iconList.setSelectionBackground(Color.RED);
	    iconList.setSelectionForeground(Color.RED);
	    iconList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    iconList.addMouseListener( new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) setButton.doClick();
		    }
		});
	    JScrollPane listScroller = new JScrollPane(iconList) ;
	    listScroller.setPreferredSize(new Dimension(150, 50));
	    listScroller.setMinimumSize(new Dimension(150,50));
	    listScroller.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	    listScroller.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
	    iconList.ensureIndexIsVisible(iconList.getSelectedIndex());

	    // Create a container so that we can add a title around the scroll pane
	    JPanel listPane = new JPanel();
	    listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
	    JLabel label = new JLabel("Set " + objectName);
	    label.setLabelFor(iconList);
	    listPane.add(label);
	    listPane.add(Box.createRigidArea(new Dimension(0,5)));
	    listPane.add(listScroller);
	    listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	    // Lay out the buttons from left to right.
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
	    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	    buttonPane.add(Box.createHorizontalGlue());
	    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
	    buttonPane.add(setButton);
	    buttonPane.add(cancelButton);

	    // add everything
	    mainPanel.add(listPane, BorderLayout.CENTER);
	    mainPanel.add(buttonPane, BorderLayout.SOUTH);
	    mainDialog.setContentPane (mainPanel);
	    alreadyConstructed = true;
	}
	if(selectedIndex >=0)
	  iconList.setSelectedIndex(selectedIndex);
	  mainDialog.pack ();
	  mainDialog.setLocationRelativeTo (parentDialog);
  	  mainDialog.show(); // blocks until user makes selection
  	 return selectedIndex;
    }

    public class ApplyIconAction extends AbstractAction{
	public void actionPerformed(ActionEvent e){
	    //setIcon((String) iconList.getSelectedValue());
	    selectedIndex = iconList.getSelectedIndex();
	    mainDialog.dispose();
	}
    }
    public class CancelIconAction extends AbstractAction{
	CancelIconAction(){super ("");}
	public void actionPerformed (ActionEvent e){
		selectedIndex = -1;
	    mainDialog.dispose();
	}
    }
}
