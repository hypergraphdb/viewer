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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVComponent;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.FileUtil;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

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
        if (file == null) throw new NullPointerException("HG file is null");
        // Create LoadNetwork Task
        LoadHGNetworkTask task = new LoadHGNetworkTask(file);

        // Configure JTask Dialog Pop-Up Box
        JTaskConfig taskConfig = new JTaskConfig();
        taskConfig.setOwner(GUIUtilities.getFrame());
        taskConfig.displayCloseButton(true);
        taskConfig.displayStatus(true);
        taskConfig.setAutoDispose(false);

        // Execute Task in New Thread; pops open JTask Dialog Box.
        TaskManager.executeTask(task, taskConfig);
    }

    /**
     * Task to Load New Network Data.
     */
    static class LoadHGNetworkTask implements Task
    {
        private File db;
        private HGVComponent comp;
        private TaskMonitor taskMonitor;

        public LoadHGNetworkTask(File db)
        {
            this.db = db;
        }

        /**
         * Executes Task.
         */
        public void run()
        {
            taskMonitor.setStatus("Reading in Network Data...");

            try
            {
                comp = createHGVComponent();
                if (comp != null) 
                {
                    informUserOfGraphStats(comp.getView());
                }
                else
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not read network from DB: "
                            + db.getAbsolutePath());
                    sb
                            .append("\nThis file may not be a valid GML or SIF file.");
                    taskMonitor.setException(new IOException(sb.toString()), sb
                            .toString());
                }
                taskMonitor.setPercentCompleted(100);
            }
            catch (IOException e)
            {
                taskMonitor.setException(e, "Unable to load network file.");
                e.printStackTrace();
            }
        }

        /**
         * Inform User of Network Stats.
         */
        private void informUserOfGraphStats(HGVNetworkView newNetwork)
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

        /**
         * Creates a HGVComponent from a file. This operation may take a long time
         * to complete.
         * 
         * @param location  the location of the file
         */
        private HGVComponent createHGVComponent() throws IOException
        {
            HGReader reader = new HGReader(db);
            reader.taskMonitor = taskMonitor;
            // Have the GraphReader read the given file
            reader.read();
            taskMonitor.setStatus("Creating HG Network...");

            HGVComponent comp = HGVKit.createHGVComponent(reader
                    .getHyperGraph(), reader.getNodes(), reader.getEdges());
           
            System.out.println("Network: " + comp.getView() + " file_name: "
                    + reader.getHyperGraph().getLocation());

            return comp;
        }
     
    }

}