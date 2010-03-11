/*
 * GUIUtilities.java - Various GUI utility functions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
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

package org.hypergraphdb.viewer.util;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;



/**
 * Various GUI functions.<p>
 */
public class GUIUtilities
{
    private GUIUtilities()
    {}
    
    public static Point adjustPointInPicollo(JComponent c, Point pt)
    {
        PNode ps = getPSwingNode(c);
        if (ps == null) return pt;
        PCanvas canvas = getTopCanvas(c);
        if (canvas == null) return pt;
        PBounds r1c = ps.getBounds();
        ps.localToGlobal(r1c);
        canvas.getCamera().globalToLocal(r1c);
        Rectangle2D r = canvas.getCamera().getViewTransform().createTransformedShape(r1c).getBounds2D();
        return new Point((int) (r.getX() + pt.x), (int) (r.getY() + pt.y));
    }
    
    public static PCanvas getTopCanvas(JComponent c)
    {
        PCanvas canvas = null;
        Component par = c.getParent();
        while(par != null)
        {
            if(par instanceof PCanvas)
                canvas = (PCanvas) par;
            par = par.getParent();
        }
        return canvas;
    }
    
    public static PNode getPSwingNode(JComponent c)
    {
        if (c == null) return null;
        PNode ps = (PNode) c
                .getClientProperty(PSwing.PSWING_PROPERTY);
        if (ps != null) return ps;
        if (c.getParent() instanceof JComponent)
            return getPSwingNode((JComponent) c.getParent());
        return null;
    }

    
    /**
     * Displays a dialog box.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     */
    public static void message(Component comp, String title, String message)
    {
        JOptionPane.showMessageDialog(comp, message,title,
        JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays an error dialog box.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property. The message
     * is formatted by the property manager with <code>args</code> as
     * positional parameters.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     */
    public static void error(Component comp, String title, String message, Object[] args)
    {
        JOptionPane.showMessageDialog(comp,
        message,
        title,
        JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an input dialog box and returns any text the user entered.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param def The text to display by default in the input field
     */
    public static String input(Component comp, String title, String message, Object def)
    {
        return input(comp, title, title, null,def);
    }
    
   
    /**
     * Displays an input dialog box and returns any text the user entered.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param def The text to display by default in the input field
     * @param args Positional parameters to be substituted into the
     * message text
     * @since jEdit 3.1pre3
     */
    public static String input(Component comp, String title, String message,
    Object[] args, Object def)
    {
        String retVal = (String)JOptionPane.showInputDialog(comp,
        message, title,
       JOptionPane.QUESTION_MESSAGE,null,null,def);
       return retVal;
    }
    
       
    /**
     * Displays a confirm dialog box and returns the button pushed by the
     * user. The title of the dialog is fetched from the
     * <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     * @param buttons The buttons to display - for example,
     * JOptionPane.YES_NO_CANCEL_OPTION
     * @param type The dialog type - for example,
     * JOptionPane.WARNING_MESSAGE
     * @since jEdit 3.1pre3
     */
    public static int confirm(Component comp, String title, String message, int buttons, int type)
    {
        return JOptionPane.showConfirmDialog(comp,
        message, title, buttons,type);
    }
    
    /**
     * Displays a confirm dialog box and returns the button pushed by the
     * user. The dialog
     * also shows a list of entries given by the <code>listModel</code>
     * parameter.
     * @since jEdit 4.3pre1
     */
    public static int listConfirm(Component comp, String title, String message,
    Object[] listModel)
    {
        JList list = new JList(listModel);
        list.setVisibleRowCount(8);
        
        Object[] message1 =
        {
            message,
            new JScrollPane(list)
        };
        
        return JOptionPane.showConfirmDialog(comp,
        message1,
        title,
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    }
    
    
    /**
     * Centers the given window on the screen. This method is needed because
     * JDK 1.3 does not have a <code>JWindow.setLocationRelativeTo()</code>
     * method.
     * @since jEdit 4.2pre3
     */
    public static void centerOnScreen(Window win)
    {
        GraphicsDevice gd = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice();
        Rectangle gcbounds = gd.getDefaultConfiguration().getBounds();
        int x = gcbounds.x + (gcbounds.width - win.getWidth()) / 2;
        int y = gcbounds.y + (gcbounds.height - win.getHeight()) / 2;
        win.setLocation(x,y);
    }
    
   
    /**
     * Focuses on the specified component as soon as the window becomes
     * active.
     * @param win The window
     * @param comp The component
     */
    public static void requestFocus(final Window win, final Component comp)
    {
        win.addWindowListener(new WindowAdapter()
        {
            public void windowActivated(WindowEvent evt)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        comp.requestFocus();
                    }
                });
                win.removeWindowListener(this);
            }
        });
    } 
    
    //{{{ isPopupTrigger() method
    /**
     * Returns if the specified event is the popup trigger event.
     * This implements precisely defined behavior, as opposed to
     * MouseEvent.isPopupTrigger().
     * @param evt The event
     * @since jEdit 3.2pre8
     */
    public static boolean isPopupTrigger(MouseEvent evt)
    {
        return isRightButton(evt.getModifiers());
    } 
    
    /**
     * @param modifiers The modifiers flag from a mouse event
     * @since jEdit 4.1pre9
     */
    public static boolean isMiddleButton(int modifiers)
    {
        /*
        if (OperatingSystem.isMacOS())
        {
            if((modifiers & MouseEvent.BUTTON1_MASK) != 0)
                return ((modifiers & MouseEvent.ALT_MASK) != 0);
            else
                return ((modifiers & MouseEvent.BUTTON2_MASK) != 0);
        }
        else
         */
            return ((modifiers & MouseEvent.BUTTON2_MASK) != 0);
    } 
    
    /**
     * @param modifiers The modifiers flag from a mouse event
     * @since jEdit 4.1pre9
     */
    public static boolean isRightButton(int modifiers)
    {
        /*
        if (OperatingSystem.isMacOS())
        {
            if((modifiers & MouseEvent.BUTTON1_MASK) != 0)
                return ((modifiers & MouseEvent.CTRL_MASK) != 0);
            else
                return ((modifiers & MouseEvent.BUTTON3_MASK) != 0);
        }
        else
         */
       return ((modifiers & MouseEvent.BUTTON3_MASK) != 0);
    } 
    
    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     * @param popup The popup menu
     * @param comp The component to show it for
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     * @since jEdit 4.0pre1
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp,
    int x, int y)
    {
        showPopupMenu(popup,comp,x,y,true);
    } 
    
    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     * @param popup The popup menu
     * @param comp The component to show it for
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     * @param point If true, then the popup originates from a single point;
     * otherwise it will originate from the component itself. This affects
     * positioning in the case where the popup does not fit onscreen.
     *
     * @since jEdit 4.1pre1
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp,
    int x, int y, boolean point)
    {
        int offsetX = 0;
        int offsetY = 0;
        
        int extraOffset = (point ? 1 : 0);
        
        Component win = comp;
        while(!(win instanceof Window || win == null))
        {
            offsetX += win.getX();
            offsetY += win.getY();
            win = win.getParent();
        }
        
        if(win != null)
        {
            Dimension size = popup.getPreferredSize();
            
            Rectangle screenSize = win.getGraphicsConfiguration()
            .getBounds();
            
            if(x + offsetX + size.width + win.getX() > screenSize.width
            && x + offsetX + win.getX() >= size.width)
            {
                //System.err.println("x overflow");
                if(point)
                    x -= (size.width + extraOffset);
                else
                    x = (win.getWidth() - size.width - offsetX + extraOffset);
            }
            else
            {
                x += extraOffset;
            }
            
            //System.err.println("y=" + y + ",offsetY=" + offsetY
            //	+ ",size.height=" + size.height
            //	+ ",win.height=" + win.getHeight());
            if(y + offsetY + size.height + win.getY() > screenSize.height
            && y + offsetY + win.getY() >= size.height)
            {
                if(point)
                    y = (win.getHeight() - size.height - offsetY + extraOffset);
                else
                    y = -size.height - 1;
            }
            else
            {
                y += extraOffset;
            }
            
            popup.show(comp,x,y);
        }
        else
            popup.show(comp,x + extraOffset,y + extraOffset);
        
    } 
    
    /**
     * Returns if the first component is an ancestor of the
     * second by traversing up the component hierarchy.
     *
     * @param comp1 The ancestor
     * @param comp2 The component to check
     * @since jEdit 4.1pre5
     */
    public static boolean isAncestorOf(Component comp1, Component comp2)
    {
        while(comp2 != null)
        {
            if(comp1 == comp2)
                return true;
            else
                comp2 = comp2.getParent();
        }
        
        return false;
    }
    
    /**
     * Traverses the given component's parent tree looking for an
     * instance of JDialog, and return it. If not found, return null.
     * @param c The component
     */
    public static JDialog getParentDialog(Component c)
    {
        Component p = c.getParent();
        while (p != null && !(p instanceof JDialog))
            p = p.getParent();
        
        return (p instanceof JDialog) ? (JDialog) p : null;
    } 
    
    /**
     * Finds a parent of the specified component.
     * @param comp The component
     * @param clazz Looks for a parent with this class (exact match, not
     * derived).
     * @since jEdit 4.2pre1
     */
    public static Component getComponentParent(Component comp, Class clazz)
    {
        for(;;)
        {
            if(comp == null)
                break;
            
            if(comp instanceof JComponent)
            {
                Component real = (Component)((JComponent)comp)
                .getClientProperty("KORTE_REAL_FRAME");
                if(real != null)
                    comp = real;
            }
            
            if(comp.getClass().equals(clazz))
                return comp;
            else if(comp instanceof JPopupMenu)
                comp = ((JPopupMenu)comp).getInvoker();
            else
                comp = comp.getParent();
        }
        return null;
    } 
    
    public static void showWarning(Frame frame, String msg)
    {
        NotifyDescriptor nd = new NotifyDescriptor.Message(frame, 
        msg, NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
    
    public static Frame getFrame(Component c)
    {
        Component p = c.getParent();
        while (p != null && !(p instanceof Frame))
        	p = p.getParent();
        
        return (p instanceof Frame) ? (Frame) p : null;
    } 
    
    public static Frame getFrame()
    {
    	if(!HGVKit.isEmbeded()) return HGVKit.getDesktop();
    	if(HGVKit.getCurrentView() == null)
    		return null; 
    	return getFrame(HGVKit.getCurrentView().getViewer());
    } 
 
}
