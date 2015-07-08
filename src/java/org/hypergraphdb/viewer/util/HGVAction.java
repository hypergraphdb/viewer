package org.hypergraphdb.viewer.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.hypergraphdb.viewer.HGViewer;

public abstract class HGVAction extends AbstractAction
{
    public HGVAction()
    {
        super();
    }

    public HGVAction(String name)
    {
        super(name);
    }

    public HGVAction(String name, Icon icon)
    {
        super(name, icon);
    }

    abstract protected void action(HGViewer viewer) throws Exception;

    public void actionPerformed(ActionEvent e)
    {
        if (!(this.isEnabled())) return;
        HGViewer viewer = getHGViewer(e);
        if (viewer == null) return;
        try
        {
            action(viewer);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            UIManager.getLookAndFeel().provideErrorFeedback(viewer);
        }
    }

//    public boolean isEnabled()
//    {
//        HGViewer viewer = HGViewer.getFocusedComponent();
//        if (viewer == null) return false;
//        return isEnabled(viewer);
//    }
//
//    public boolean isEnabled(HGViewer viewer)
//    {
//        return true;
//    }

    public void setAcceleratorCombo(int key_code, int key_mods)
    {
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key_code,
                key_mods));

    }

    protected final HGViewer getHGViewer(ActionEvent e)
    {
        if (e != null)
        {
            Object o = e.getSource();
            if (o instanceof HGViewer) return (HGViewer) o;
        }
        return HGViewer.getFocusedComponent();
    }
}
