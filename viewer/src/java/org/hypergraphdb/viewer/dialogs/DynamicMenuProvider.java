/*
 * This file is part of the Scriba source distribution. This is free, open-source 
 * software. For full licensing information, please see the LicensingInformation file
 * at the root level of the distribution.
 *
 * Copyright (c) 2006-2007 Kobrix Software, Inc.
 */
/*
 * DynamicMenuProvider.java - API for dynamic plugin menus
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

import java.io.Serializable;

import javax.swing.JMenu;

/**
 * Interface for a menu whose contents are determined at runtime.<p>
 *
 */
public interface DynamicMenuProvider extends Serializable
{
	/**
	 * Returns true if the menu should be updated each time it is shown.
	 * Otherwise, it will only be updated when the menu is first created,
	 */
	boolean updateEveryTime();

	/**
	 * Adds the menu items to the given menu.
	 * @param menu The menu
	 */
	void update(JMenu menu);
}
