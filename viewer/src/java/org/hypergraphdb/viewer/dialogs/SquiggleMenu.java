package org.hypergraphdb.viewer.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGVKit;

/**
 * Dynamic menu for Squiggle options: enable/disable, clear. 
 */
public class SquiggleMenu implements DynamicMenuProvider
{
    public boolean updateEveryTime()
    {
        return true;
    }

    public void update(final JMenu menu)
    {
        final boolean b = HGVKit.isSquiggleEnabled();
        String text = b ? "Disable" : "Enable";

        JMenuItem squiggleMode = new JMenuItem(new AbstractAction(text) {
            public void actionPerformed(ActionEvent e)
            {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                       HGVKit.setSquiggleState(!b);
                    }
                });
            }
        });
        menu.add(squiggleMode);
        squiggleMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));

        JMenuItem clearSquiggle = new JMenuItem(new AbstractAction("Clear") {
            public void actionPerformed(ActionEvent e)
            {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                        GraphView view = HGVKit.getCurrentView();
                        view.getSquiggleHandler().clearSquiggles();
                    }
                });
            }
        });
        clearSquiggle.setEnabled(b);
        menu.add(clearSquiggle);
    }
}
