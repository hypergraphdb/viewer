/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * IntEditor.java
 *
 * Created on February 28, 2003, 2:15 PM
 */

package org.hypergraphdb.viewer.props.editors;
import java.beans.*;
import java.util.Arrays;
//import org.openide.ErrorManager;
import org.hypergraphdb.viewer.props.*;


public class IntEditor extends PropertyEditorSupport {
    
    String[] code = null;
        
    /** Creates a new instance of IntEditor */
    public IntEditor() {
    }
    
    public String getAsText() {
        Integer i = (Integer) getValue();
        String result;
        if (i != null) {
            result = getValue().toString();
        } else {
            result = "null"; 
        }
        return result;
    }
    
    private void doSetAsText(String s) {
        try {
            setValue(new Integer(Integer.parseInt(s)));
        } catch (NumberFormatException nfe) {
            String msg = "ILLEGAL VALUE TEXT: " + s; 
            RuntimeException iae = new IllegalArgumentException(msg); 
            //FIXME:
            //ErrorManager.getDefault().annotate(iae, ErrorManager.USER, msg,
            //msg, nfe, new java.util.Date());
            throw iae;
        }
    }
    
    public void setAsText(String s) {
        s = s.trim();
        doSetAsText(s);
    }
    
    public Object getValue () {
        Integer v = (Integer) super.getValue();
        return v;
    }
    
    public void setValue (Object value) {
        if ((value instanceof Integer) || (value == null)) {
            super.setValue (value);
        } else {
            throw new IllegalArgumentException (
                "Argument to IntEditor.setValue() must be Integer, but was " + //NOI18N
                value.getClass().getName() + "(=" +  //NOI18N
                value.toString() + ")"); //NOI18N
        }
    }
    
    public String getJavaInitializationString() {
        String result;
        if (code == null) {
            result = getValue().toString();
        } else {
            result = code[((Integer) getValue()).intValue()];
        }
        return result;
    }
}
