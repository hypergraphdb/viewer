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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGIndex;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.GraphView;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.util.FileUtil;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.visual.VisualStyle;

import org.hypergraphdb.viewer.util.cytoscape.task.Task;
import org.hypergraphdb.viewer.util.cytoscape.task.TaskMonitor;
import org.hypergraphdb.viewer.util.cytoscape.task.util.TaskManager;


/**
 * Action that loads a Wordnet HG
 */
public class LoadWordNetAction extends AbstractAction
{
    private static final String WORDNET_PATH_PROP = "WORDNET_PATH_PROP";
   // private static String PATH = "F:\\kosta\\hg\\wordnet";

    /**
     * ConstructorLink.
     */
    public LoadWordNetAction()
    {
        super(ActionManager.LOAD_WORD_NET_ACTION);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    }

    /**
     * User Initiated Request.
     * 
     * @param e
     *            Action Event.
     */
    public void actionPerformed(ActionEvent e)
    {
        String p = (String) AppConfig.getInstance().getProperty(
               WORDNET_PATH_PROP);
        File file = (p != null) ? new File(p) : FileUtil.getFile("Load Wordnet HyperGraph", FileUtil.LOAD, null,
                null, null, true);
        if (file != null)
        {
            loadHyperGraph(file);
            AppConfig.getInstance().setProperty(
                WORDNET_PATH_PROP, file.getAbsolutePath());
        }
    }

    public static void loadHyperGraph(File file)
    {
        if (file == null) throw new NullPointerException("HG file is null");
        // Create LoadNetwork Task
        LoadTask task = new LoadTask(file);
        // Execute Task in New Thread; pops open JTask Dialog Box.
        TaskManager.executeTask(task, null);
    }

    /**
     * Task to Load New Network Data.
     */
    static class LoadTask implements Task
    {
        private File db;
        private TaskMonitor taskMonitor;

        public LoadTask(File db)
        {
            this.db = db;
        }

        /**
         * Executes Task.
         */
        public void run()
        {
            taskMonitor.setStatus("Reading in HG Data...");
            try
            {
                if (!db.isDirectory())
                    throw new IOException("No such DB: " + db.getAbsolutePath());
                HGViewer viewer = createViewer();
                if (viewer != null)
                {
                    informUserOfGraphStats(viewer.getView());
                }
                else
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not read network from DB: "
                            + db.getAbsolutePath());
                    sb.append("\nThis directory may not be a valid DB.");
                    taskMonitor.setException(new IOException(sb.toString()), sb
                            .toString());
                }
                taskMonitor.setPercentCompleted(100);
            }
            catch (IOException e)
            {
                taskMonitor.setException(e, "Unable to load network file.");
            }
        }

        /**
         * Inform User of Network Stats.
         */
        private void informUserOfGraphStats(GraphView newNetwork)
        {
            NumberFormat formatter = new DecimalFormat("#,###,###");
            StringBuffer sb = new StringBuffer();

            // Give the user some confirmation
            sb.append("Succesfully loaded network from DB:  "
                    + db.getAbsolutePath());
            sb.append("\n\nNetwork contains "
                    + formatter.format(newNetwork.getNodeCount()));
            sb.append(" nodes and "
                    + formatter.format(newNetwork.getEdgeCount()));
            sb.append(" edges.\n\n");

            if (newNetwork.getNodeCount() < AppConfig.getInstance()
                    .getViewThreshold())
            {
                sb.append("Network is under "
                        + AppConfig.getInstance().getViewThreshold()
                        + " nodes.  A view will be automatically created.");
            }
            else
            {
                sb.append("Network is over "
                        + AppConfig.getInstance().getViewThreshold()
                        + " nodes.  A view will not been created."
                        + "  If you wish to view this network, use "
                        + "\"Create View\" from the \"Edit\" menu.");
            }
            taskMonitor.setStatus(sb.toString());
        }

        /**
         * Halts the Task: Not Currently Implemented.
         */
        public void halt()
        {
            // Task can not currently be halted.
        }

        /**
         * Sets the Task Monitor.
         * 
         * @param taskMonitor
         *            TaskMonitor Object.
         */
        public void setTaskMonitor(TaskMonitor taskMonitor)
                throws IllegalThreadStateException
        {
            this.taskMonitor = taskMonitor;
        }

        /**
         * Gets the Task Title.
         * 
         * @return Task Title.
         */
        public String getTitle()
        {
            return new String("Loading Network");
        }

        private HGViewer createViewer() throws IOException
        {

            final HGWNReader reader = new HGWNReader(db);
            open(reader);
            HGViewer comp = HGVKit.createHGViewer(reader.getHyperGraph(),
                    reader.getNodes(), reader.getEdges());
            setVisualStyle(comp.getView());
            return comp;
        }

        private static final String WN_STYLE = "wordnet_style";

        private void setVisualStyle(GraphView view)
        {
            VisualStyle vs = VisualManager.getInstance().getVisualStyle(
                    WN_STYLE);
            if (vs != null)
            {
                System.out.println("WordNet Style already in HG");
                view.setVisualStyle(vs);
                view.redrawGraph();
                return;
            }
            vs = new VisualStyle(WN_STYLE);
//            HyperGraph hg = view.getHyperGraph();
//            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            addNodePainter(hg, vs, cl,
//                    "org.hypergraphdb.app.wordnet.data.NounSynsetLink",
//                    "org.hypergraphdb.app.wordnet.viewer.SynsetNodePainter");
//            addNodePainter(hg, vs, cl,
//                    "org.hypergraphdb.app.wordnet.data.VerbSynsetLink",
//                    "org.hypergraphdb.app.wordnet.viewer.SynsetNodePainter");
//            addNodePainter(hg, vs, cl,
//                    "org.hypergraphdb.app.wordnet.data.VerbFrame",
//                    "org.hypergraphdb.app.wordnet.viewer.VerbFrameNodePainter");
//            addNodePainter(hg, vs, cl,
//                    "org.hypergraphdb.app.wordnet.data.Word",
//                    "org.hypergraphdb.app.wordnet.viewer.WordNodePainter");
            VisualManager.getInstance().addVisualStyle(vs);
            view.setVisualStyle(vs);

        }

        private void addNodePainter(HyperGraph hg, VisualStyle vs,
                ClassLoader cl, String t_cls, String p_cls)
        {
            try
            {
                Class clazz = cl.loadClass(t_cls);
                HGHandle h = hg.getTypeSystem().getTypeHandle(clazz);
                NodePainter p = (NodePainter) cl.loadClass(p_cls).newInstance();
                vs.addNodePainter(h, p);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        public void open(HGWNReader reader) throws IOException
        {
            NotifyDescriptor d = new NotifyDescriptor.InputLine(GUIUtilities
                    .getFrame(), "", "Enter a word: ",
                    NotifyDescriptor.PLAIN_MESSAGE,
                    NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
            {
                String lemma = ((NotifyDescriptor.InputLine) d).getInputText();
                if (lemma == null || lemma.equals("")) return;
                HyperGraph hg = reader.getHyperGraph();
                reader
                        .read(getWordHandle(hg, lemma), 2,
                                (HGAtomPredicate) null);
            }
        }

        private HGHandle getWordHandle(HyperGraph hg, String lemma)
                throws IOException
        {
            HGHandle word_handle = lookup(hg, "word", "lemma", lemma);
            if (word_handle == null)
            {
                hg.close();
                throw new IOException("No such word: " + lemma + " in the DB.");
            }
            return word_handle;
        }
        
        private static HGHandle lookup(HyperGraph hg, String typeAlias,
                String keyProperty, Object keyValue)
        {
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(typeAlias);
            ByPartIndexer byProperty = new ByPartIndexer(typeHandle, new String[] { keyProperty });
            HGIndex<String, HGHandle> index = hg.getIndexManager().register(byProperty);
            return index.findFirst((String)keyValue);
        }
    }

}