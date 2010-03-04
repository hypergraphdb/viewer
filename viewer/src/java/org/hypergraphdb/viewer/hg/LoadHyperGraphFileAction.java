/*
 * Created on 2005-9-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hypergraphdb.viewer.hg;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.type.Top;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.util.FileUtil;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

/**
 */
public class LoadHyperGraphFileAction extends HGVAction
{
    /**
     * ConstructorLink.
     * 
     */
    public LoadHyperGraphFileAction()
    {
        super(ActionManager.LOAD_HYPER_GRAPH_ACTION);
        setAcceleratorCombo(java.awt.event.KeyEvent.VK_L, ActionEvent.CTRL_MASK);
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
        if (file != null) loadHyperGraph(file);
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
         HGViewer comp = HGVKit.createHGViewer(reader
                .getHyperGraph(), reader.getNodes(), reader.getEdges());
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
            try{
                h = HGHandleFactory.makeHandle(hh);
            }catch(Throwable t)
            {
                NotifyDescriptor e = new NotifyDescriptor.Exception(GUIUtilities
                        .getFrame(), t);
                DialogDisplayer.getDefault().notify(e);
                h = reader.getHyperGraph().getTypeSystem().getTypeHandle(Top.class);
            }
            reader.read(h, 2, (HGAtomPredicate) null);
        }
    }
}