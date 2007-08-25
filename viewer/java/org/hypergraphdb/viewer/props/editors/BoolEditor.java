/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * BoolEditor.java
 *
 * Created on February 28, 2003, 1:13 PM
 */

package org.hypergraphdb.viewer.props.editors;
import org.hypergraphdb.viewer.props.*;
import java.beans.*;
//import org.openide.explorer.propertysheet.ExPropertyEditor;
//import org.openide.util.NbBundle;
/** Replacement editor for boolean primitive values which supports
 *  internationalization and alternate string values that
 *  can be supplied to the property editor via adding an array
 *  returning an array of two Strings (false then true) from
 *  <code>env.getFeatureDescriptor().getValue()</code>.  These
 *  string values will then be used for getAsText, setAsText, and getTags.
 *  These strings should be correctly internationalized if supplied
 *  by a module.  String value matching in setAsText is non-case-sensitive
 *  ("TRUE" and "tRue" are equivalent).
 *
 * @author  Tim Boudreau
 */
public class BoolEditor extends  PropertyEditorSupport {
    String[] stringValues = null;
    /** Creates a new instance of BoolEditor */
    public BoolEditor() {
    }
    
      
    private String getStringRep(boolean val) {
        if (stringValues != null) {
            return stringValues [val ? 0 : 1];
        }
        String result;
        if (val) {
            result = "true";
        } else {
            result = "false";
        }
        return result;
    }
    
    /** Returns Boolean.TRUE, Boolean.FALSE or null in the case of an
     *  unrecognized string. */
    private Boolean stringVal(String val) {
        String valToTest = val.trim().toUpperCase();
        String test = getStringRep(true).toUpperCase();
        if (test.equals(valToTest)) return Boolean.TRUE;
        test = getStringRep(false).toUpperCase();
        if (test.equals(valToTest)) return Boolean.FALSE;
        return null;
    }
    
    public String getJavaInitializationString() {
        Boolean val = (Boolean) getValue();
        if (val == null) return "null"; //NOI18N
        return Boolean.TRUE.equals(getValue()) ? "true" : "false"; //NOI18N
    }
    
    public String[] getTags() {
        return new String[] {
            getStringRep(true), getStringRep(false)
        };
    }
    
    public String getAsText() {
        Boolean val = (Boolean) getValue();
        if (val == null) return "null"; //NbBundle.getMessage(BoolEditor.class, "NULL");
        return getStringRep(Boolean.TRUE.equals(getValue()));
    }
    
    public void setAsText(String txt) {
        Boolean val = stringVal(txt);
        boolean newVal = val == null ? false : val.booleanValue();
        setValue(newVal ? Boolean.TRUE : Boolean.FALSE);
    }
}
