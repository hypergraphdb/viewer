// MiscGB.java:  miscellaneous static GridBagLayout utilities

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//--------------------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//--------------------------------------------------------------------------------------
package org.hypergraphdb.viewer.dialogs;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;

import org.hypergraphdb.viewer.util.Misc;
import org.hypergraphdb.viewer.util.MutableColor;
import org.hypergraphdb.viewer.dialogs.GridBagGroup;

//------------------------------------------------------------------------------
public class MiscGB {

    // sets GridBagConstraints.
    public static void pad(GridBagConstraints c, int padx, int pady) {
	c.ipadx = padx;	c.ipady = pady;
    }
    public static void inset(GridBagConstraints c, int b, int l, int r, int t) {
	c.insets = new Insets(b,l,r,t);
    }
    public static void inset(GridBagConstraints c, int s) {
	inset(c,s,s,s,s);
    }
    public static void set(GridBagConstraints c,
			   int x, int y, int w, int h,
			   int weightx, int weighty, int f) {
	c.gridx = x;	c.gridy = y;
	c.gridwidth = w;	c.gridheight = h;
	c.weightx = weightx;    c.weighty = weighty;
	c.fill = f;
    }
    public static void set(GridBagConstraints c,
			   int x, int y, int w, int h) {
	set(c,x,y,w,h,0,0,GridBagConstraints.NONE);
    }
    public static void set(GridBagConstraints c,
			   int x, int y) {
	set(c,x,y,1,1,0,0,GridBagConstraints.NONE);
    }

    // inserts a component into a panel with a GridBagLayout.
    public static void insert (JPanel panel,
			       Component comp,
			       GridBagLayout bag,
			       GridBagConstraints c) {
	if(bag==null) System.out.println("bag is null");
	if(comp==null) System.out.println("comp is null");
	if(c==null) System.out.println("c is null");
	if(panel==null) System.out.println("panel is null");
	bag.setConstraints(comp,c);
	panel.add(comp);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y) {
	set(gbg.constraints, x, y);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y, int w, int h) {
	set(gbg.constraints, x, y, w, h);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y, int w, int h, int f) {
	set(gbg.constraints, x, y, w, h, 0, 0, f);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }
    public static void insert(GridBagGroup gbg,
			      Component comp,
			      int x, int y, int w, int h,
			      int weightx, int weighty, int f) {
	set(gbg.constraints, x, y, w, h, weightx, weighty, f);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static JLabel createColorLabel(Color c) {
	JLabel label = new JLabel("    ");
	label.setOpaque(true);
	label.setBackground(c);
	return label;
    }

    public static JButton buttonAndColor(JDialog parent, MutableColor mc,
					 JLabel l, String bTitle) {
	JButton jb = new JButton(bTitle);
	jb.addActionListener
	    (new GeneralColorDialogListener
		(parent,mc,l,"Choose a " + bTitle));
	return jb;
    }

}



