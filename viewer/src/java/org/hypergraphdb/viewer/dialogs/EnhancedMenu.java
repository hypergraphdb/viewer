/*
 * This file is part of the Scriba source distribution. This is free, open-source 
 * software. For full licensing information, please see the LicensingInformation file
 * at the root level of the distribution.
 *
 * Copyright (c) 2006-2007 Kobrix Software, Inc.
 */
/*
 * EnhancedMenu.java - jEdit menu
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.hypergraphdb.viewer.dialogs;


import javax.swing.event.*;
import javax.swing.*;

/**
 * Menu class capable to display DynamicMenuProvider 
 */
public class EnhancedMenu extends JMenu implements MenuListener
{
	private static final long serialVersionUID = 6798827747654062221L;
   
	protected DynamicMenuProvider provider;
	public EnhancedMenu()
	{
	    addMenuListener(this);
	}
	
	public EnhancedMenu(String name, DynamicMenuProvider prov)
	{
		this(name, name, prov);
	} 

	public EnhancedMenu(String name, String label, DynamicMenuProvider prov)
	{
		provider = prov;
		if(label == null)
			label = name;

		char mnemonic;
		int index = label.indexOf('$');
		if(index != -1 && label.length() - index > 1)
		{
			mnemonic = Character.toLowerCase(label.charAt(index + 1));
			label = label.substring(0,index).concat(label.substring(++index));
		}
		else
			mnemonic = '\0';

		setText(label);
		setMnemonic(mnemonic);
		if(provider != null && !provider.updateEveryTime())
		{
			removeAll(); 
			provider.update(this);
		}
		addMenuListener(this);
	} 

	public void menuSelected(MenuEvent e)
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
			}
		});
	} 

	public void menuDeselected(MenuEvent e) {}

	public void menuCanceled(MenuEvent e) {}

	public void init()  
	{
		if(provider == null)
			return;
		
		if(provider.updateEveryTime())
		{
			removeAll(); 
			provider.update(this);
		}
    }

	public DynamicMenuProvider getProvider()
	{
		return provider;
	}

	public void setProvider(DynamicMenuProvider provider)
	{
		this.provider = provider;
	} 
}
