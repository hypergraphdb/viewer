/*
 * BeanShellErrorDialog.java - BeanShell execution error dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
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

package org.hypergraphdb.viewer.beanshell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * A dialog box showing a stack trace. Perhaps badly named, since any error, not
 * just a BeanShell error can be shown.
 * @author Slava Pestov
 * @version $Id: BeanShellErrorDialog.java,v 1.2 2006/02/27 19:59:19 bizi Exp $
 */
public class BeanShellErrorDialog extends TextAreaDialog
{
	public BeanShellErrorDialog(Frame frame, Throwable t)
	{
		super(frame,"BeanShell Error", t);
	}
}
