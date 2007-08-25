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

package org.hypergraphdb.viewer.props.editors;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.hypergraphdb.viewer.props.*;
//import org.openide.explorer.propertysheet.editors.XMLPropertyEditor;
//import org.openide.nodes.Node;

/** A property editor for array of Strings.
 * @author  Ian Formanek
 */
public class StringArrayEditor implements PropertyEditor, StringArrayCustomizable
{

    // constants for XML persistence
    private static final String XML_STRING_ARRAY = "StringArray"; // NOI18N
    private static final String XML_STRING_ITEM = "StringItem"; // NOI18N
    private static final String ATTR_COUNT = "count"; // NOI18N
    private static final String ATTR_INDEX = "index"; // NOI18N
    private static final String ATTR_VALUE = "value"; // NOI18N

    // private fields
    private String[] strings;
    private PropertyChangeSupport support;
    private boolean editable = true;
    private String separator = ",";

    public StringArrayEditor() {
        support = new PropertyChangeSupport (this);
    }

    public Object getValue () {
        return strings;
    }

    public void setValue (Object value) {
        strings = (String[]) value;
        support.firePropertyChange("", null, null); // NOI18N
    }

    // -----------------------------------------------------------------------------
    // StringArrayCustomizable implementation

    /** Used to acquire the current value from the PropertyEditor
    * @return the current value of the property
    */
    public String[] getStringArray () {
        return (String[])getValue ();
    }

    /** Used to modify the current value in the PropertyEditor
    * @param value the new value of the property
    */
    public void setStringArray (String[] value) {
        setValue (value);
    }

    // end of StringArrayCustomizable implementation

    protected final String getStrings(boolean quoted) {
        if (strings == null) return "null"; // NOI18N

        StringBuffer buf = new StringBuffer ();
        for (int i = 0; i < strings.length; i++) {
            // XXX handles in-string escapes if quoted
            buf.append(quoted ? "\""+strings[i]+"\"" : strings[i]); // NOI18N
            if (i != strings.length - 1) {
                buf.append (separator); 
                buf.append (' '); // NOI18N
            }
        }

        return buf.toString ();
    }

    public String getAsText () {
        return getStrings(false);
    }

    public void setAsText (String text) {
        if (text.equals("null")) { // NOI18N
            setValue(null);
            return;
        }
        StringTokenizer tok = new StringTokenizer(text, separator);
        java.util.List list = new LinkedList();
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            list.add(s.trim());
        }
        String [] a = (String[])list.toArray(new String[list.size()]);
        setValue(a);
    }

    public String getJavaInitializationString () {
        if (strings == null) return "null"; // NOI18N
        // [PENDING - wrap strings ???]
        StringBuffer buf = new StringBuffer ("new String[] {"); // NOI18N
        buf.append (getStrings(true));
        buf.append ("}"); // NOI18N
        return buf.toString ();
    }

    public String[] getTags () {
        return null;
    }

    public boolean isPaintable () {
        return false;
    }

    public void paintValue (Graphics g, Rectangle rectangle) {
    }

    public boolean supportsCustomEditor () {
        //Don't show custom editor if it's just going to show
        //an empty component
        if (!editable && (strings==null || strings.length==0)) {
            return false;
        } else {
            return true;
        }
    }

    public Component getCustomEditor () {
        if (editable) {
            return new StringArrayCustomEditor(this);
        } else {
            return new JScrollPane(new JList(getStringArray()));
        }
    }

    public void addPropertyChangeListener (PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener (propertyChangeListener);
    }

    public void removePropertyChangeListener (PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener (propertyChangeListener);
    }

}
