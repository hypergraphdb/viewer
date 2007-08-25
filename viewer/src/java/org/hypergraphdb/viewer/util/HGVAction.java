package org.hypergraphdb.viewer.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.SwingPropertyChangeSupport;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

import java.beans.PropertyChangeListener;

public abstract class HGVAction
  extends AbstractAction {

  protected Integer menuIndex = new Integer( -1 );
  protected String consoleName;

 

  public HGVAction () {
    super();
  }

  public HGVAction ( String name ) {
    super( name );
    this.consoleName = name;
    consoleName = consoleName.replaceAll( ":. \'", "" );
  }

  public HGVAction ( String name, javax.swing.Icon icon ) {
    super( name, icon );
    this.consoleName = name;
    consoleName = consoleName.replaceAll( " ", "" );
  }

   
  public void setName ( String name ) {
    this.consoleName = name;
  }
  
  public String getName () {
    return consoleName;
  }

  public String actionHelp () {
    return "";
  }

  
  // implements AbstractAction
  public abstract void actionPerformed ( ActionEvent e );

 
  public Integer getPrefferedIndex () {
    return menuIndex;
  }
  
  public void setAcceleratorCombo ( int key_code, int key_mods ) {
    putValue(Action.ACCELERATOR_KEY, 
    		KeyStroke.getKeyStroke( key_code, key_mods));
   
  }
} // class HGVAction
