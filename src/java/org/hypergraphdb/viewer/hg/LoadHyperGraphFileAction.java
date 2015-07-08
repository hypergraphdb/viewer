/*
 * Created on 2005-9-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hypergraphdb.viewer.hg;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.HGTypeSystem;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.type.Top;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.util.FileUtil;
import org.hypergraphdb.viewer.util.GUIUtilities;

/**
 * Action to choose and load a HyperGraph
 */
public class LoadHyperGraphFileAction extends AbstractAction
{
    /**
     * ConstructorLink.
     * 
     */
    public LoadHyperGraphFileAction()
    {
        super(ActionManager.LOAD_HYPER_GRAPH_ACTION);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L,
                ActionEvent.CTRL_MASK));
    }

    /**
     * User Initiated Request.
     * 
     * @param e
     *            Action Event.
     */
    public void actionPerformed(ActionEvent e)
    {
        // Get the file name
        File file = FileUtil.getFile("Load Network File", FileUtil.LOAD, null,
                null, null, true);
        if (file != null)
            loadHyperGraph(file);
    }

    public static void loadHyperGraph(File file)
    {
        if (file == null)
            throw new NullPointerException("HG file is null");
        try
        {
            createHGViewer(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static HGViewer createHGViewer(File db) throws IOException
    {
        HGWNReader reader = new HGWNReader(db);
        // Have the GraphReader read the given file
        open(reader);
        HGViewer comp = HGVKit.createHGViewer(reader.getHyperGraph(), reader
                .getNodes(), reader.getEdges());
        return comp;
    }

    static void open(HGWNReader reader) throws IOException
    {
        NotifyDescriptor d = new NotifyDescriptor.InputLine(GUIUtilities
                .getFrame(), "", "Enter a handle: ",
                NotifyDescriptor.PLAIN_MESSAGE,
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
        {
            String hh = ((NotifyDescriptor.InputLine) d).getInputText();
            // if (hh == null || hh.equals("")) return;
            HGHandle h = null;
            try
            {
                h = reader.getHyperGraph().getHandleFactory().makeHandle(hh);
            }
            catch (Throwable t)
            {
                NotifyDescriptor e = new NotifyDescriptor.Exception(
                        GUIUtilities.getFrame(), t);
                DialogDisplayer.getDefault().notify(e);
                h = reader.getHyperGraph().getTypeSystem().getTypeHandle(
                        Top.class);
            }
            reader.read(h, 2, (HGAtomPredicate) null);
        }
        else
            reader.read(reader.getHyperGraph().getTypeSystem().getTop(), 2, (HGAtomPredicate) null);
    }
}