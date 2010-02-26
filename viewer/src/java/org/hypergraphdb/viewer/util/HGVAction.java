package org.hypergraphdb.viewer.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public abstract class HGVAction extends AbstractAction
{
    protected String consoleName;

    public HGVAction()
    {
        super();
    }

    public HGVAction(String name)
    {
        super(name);
        this.consoleName = name;
        consoleName = consoleName.replaceAll(":. \'", "");
    }

    public HGVAction(String name, javax.swing.Icon icon)
    {
        super(name, icon);
        this.consoleName = name;
        consoleName = consoleName.replaceAll(" ", "");
    }

    public void setName(String name)
    {
        this.consoleName = name;
    }

    public String getName()
    {
        return consoleName;
    }

    // implements AbstractAction
    public abstract void actionPerformed(ActionEvent e);

    public void setAcceleratorCombo(int key_code, int key_mods)
    {
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key_code,
                key_mods));

    }
} 
