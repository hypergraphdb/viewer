/*
 * BeanShellAction.java - BeanShell action
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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

import bsh.*;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.hypergraphdb.viewer.util.Log;
import org.hypergraphdb.viewer.*;
import org.hypergraphdb.viewer.util.*;

/**
 * An action that evaluates BeanShell code when invoked. 
 *
 */
public class BeanShellAction extends HGVAction
{
    private boolean noRepeat;
    private boolean noRecord;
    private boolean noRememberLast;
    private String code;
    private String isSelected;
    private BshMethod cachedCode;
    private BshMethod cachedIsSelected;
    private String sanitizedName;
    
     public BeanShellAction(){
        super("Evaluate Bean Shell");
        setAcceleratorCombo(KeyEvent.VK_F4, 0);
     }
     
    public BeanShellAction(String name, String code, String isSelected,
    boolean noRepeat, boolean noRecord, boolean noRememberLast)
    {
        //super(name);
        this();
        
        this.code = code;
        this.isSelected = isSelected;
        this.noRepeat = noRepeat;
        this.noRecord = noRecord;
        this.noRememberLast = noRememberLast;
        
        /* Some characters that we like to use in action names
        * ('.', '-') are not allowed in BeanShell identifiers. */
        sanitizedName = name.replace('.','_').replace('-','_');
    } 
    
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        BeanShell.init();
        BeanShell.showEvaluateDialog();
       
    } 
    
    public boolean isSelected(Component comp)
    {
        if(isSelected == null)
            return false;
        
        NameSpace global = BeanShell.getNameSpace();
        
        try
        {
            if(cachedIsSelected == null)
            {
                String cachedIsSelectedName = "selected_" + sanitizedName;
                cachedIsSelected = BeanShell.cacheBlock(cachedIsSelectedName,
                isSelected,true);
            }
            
                        
            return Boolean.TRUE.equals(BeanShell.runCachedBlock(
            cachedIsSelected,
            new NameSpace(BeanShell.getNameSpace(),
            "BeanShellAction.isSelected()")));
        }
        catch(Throwable e)
        {
            Log.log(Log.ERROR,this,e);
            
            // so that in the future we don't see streams of
            // exceptions
            isSelected = null;
            
            return false;
        }
        finally
        {
            try
            {
                global.setVariable("_comp",null);
            }
            catch(UtilEvalError err)
            {
                Log.log(Log.ERROR,this,err);
            }
        }
    }
    
    public boolean noRepeat()
    {
        return noRepeat;
    } 
    
    public boolean noRecord()
    {
        return noRecord;
    } 
    
    /**
     * Returns if this edit action should not be remembered as the most
     * recently invoked action.
     */
    public boolean noRememberLast()
    {
        return noRememberLast;
    } 
    
    public String getCode()
    {
        return code.trim();
    }

}
