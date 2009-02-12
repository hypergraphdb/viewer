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
import javax.swing.SwingUtilities;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.util.FileUtil;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.view.HGVDesktop;
import phoebe.PGraphView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import edu.umd.cs.piccolo.PCanvas;
import fing.model.FEdge;
import fing.model.FNode;

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
        setAcceleratorCombo(java.awt.event.KeyEvent.VK_L,
        ActionEvent.CTRL_MASK);
    }
    
     /**
     * User Initiated Request.
     *
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e)
    {
        // Get the file name
        File  file = FileUtil.getFile("Load Network File",
        FileUtil.LOAD, null,null,null,true);
        if(file != null)
           loadHyperGraph(file);
    }
    
    public static void loadHyperGraph(File file)
    {
    	if(file == null) throw new NullPointerException("HG file is null");
    	HGVNetwork network = HGVKit.getNetworkByFile(file);
    	if (network == null)
        {
            //  Create LoadNetwork Task
            LoadHGNetworkTask task = new LoadHGNetworkTask(file);
            
            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(GUIUtilities.getFrame());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);
            
            //  Execute Task in New Thread;  pops open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);
        }
    	//else
    	//	HGVKit.getDesktop().setFocus(network);
    }
    
    
    /**
     * Task to Load New Network Data.
     */
    static class LoadHGNetworkTask implements Task
    {
        private File db;
        private HGVNetwork cyNetwork;
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
                cyNetwork = this.createNetwork();
                
                if (cyNetwork != null)
                {
                    informUserOfGraphStats(cyNetwork);
                } else
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not read network from DB: " + db.getAbsolutePath());
                    sb.append("\nThis file may not be a valid GML or SIF file.");
                    taskMonitor.setException(new IOException(sb.toString()),
                    sb.toString());
                }
                taskMonitor.setPercentCompleted(100);
            } catch (IOException e)
            {
                taskMonitor.setException(e, "Unable to load network file.");
                e.printStackTrace();
            }
        }
        
        /**
         * Inform User of Network Stats.
         */
        private void informUserOfGraphStats(HGVNetwork newNetwork)
        {
            NumberFormat formatter = new DecimalFormat("#,###,###");
            StringBuffer sb = new StringBuffer();
            
            //  Give the user some confirmation
            sb.append("Succesfully loaded network from DB:  " + db.getAbsolutePath());
            sb.append("\n\nNetwork contains " + formatter.format
            (newNetwork.getNodeCount()));
            sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
            sb.append(" edges.\n\n");
            
            if (newNetwork.getNodeCount() < AppConfig.getInstance().getViewThreshold())
            {
                sb.append("Network is under "
                + AppConfig.getInstance().getViewThreshold()
                + " nodes.  A view will be automatically created.");
            } else
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
         * Halts the Task:  Not Currently Implemented.
         */
        public void halt()
        {
            //   Task can not currently be halted.
        }
        
        /**
         * Sets the Task Monitor.
         *
         * @param taskMonitor TaskMonitor Object.
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
         * Creates a HGVNetwork from a file.
         * This operation may take a long time to complete.
         *
         * @param location      the location of the file
         */
        private HGVNetwork createNetwork() throws IOException
        {
            
            HGReader reader = new HGReader(db);
            reader.taskMonitor = taskMonitor;
            
            //Have the GraphReader read the given file
            reader.read();
            
            //  Get the RootGraph indices of the nodes and
            //  Edges that were just created
            final FNode[] nodes = reader.getNodeIndicesArray();
            final FEdge[] edges = reader.getEdgeIndicesArray();
            
            final String title = db.getAbsolutePath();
            
            // Create a new cytoscape.data.HGVNetwork from these nodes and edges
            taskMonitor.setStatus("Creating HG Network...");
            
            //  Create the HGVNetwork
            //  First, set the view threshold to 0.  By doing so, we can disable
            //  the auto-creating of the HGVNetworkView.
            int realThreshold = AppConfig.getInstance().getViewThreshold();
            AppConfig.getInstance().setViewThreshold(0);
            HGVNetwork network = HGVKit.createNetwork(nodes, edges,
            		reader.getHyperGraph());
            
            network.setTitle(title);
            System.out.println("Network: " + network + " file_name: " + 
            		network.getHyperGraph().getStore().getDatabaseLocation());
            
            //  Reset back to the real View Threshold
            AppConfig.getInstance().setViewThreshold(realThreshold);
            
            //  Conditionally, Create the HGVNetworkView
            if (network.getNodeCount() < AppConfig.getInstance().getViewThreshold()  )
            {
            	createNetworkView(network);
                //  Lastly, make the GraphView Canvas Visible.
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        PGraphView view =(PGraphView)
                        HGVKit.getCurrentView();
                        PCanvas pCanvas = view.getCanvas();
                        pCanvas.setVisible(true);
                    }
                });
            }
            return network;
        }
        
        /**
         * Creates the HGVNetworkView.
         * Most of this code is copied directly from HGVKit.createHGVNetworkView.
         * However, it requires a bit of a hack to actually hide the network
         * view from the user, and I didn't want to use this hack in the core
         * HGVKit.java class.
         */
        private void createNetworkView(HGVNetwork network)
        {
        	final HGVNetworkView view = new HGVNetworkView(network,
            network.getTitle());
           
            //  Start of Hack:  Hide the View
            PCanvas pCanvas = view.getCanvas();
            pCanvas.setVisible(false);
            //  End of Hack
            
            HGVKit.getNetworkMap().put(network, view);
            
            // if Squiggle function enabled, enable squiggling on the created view
            if (HGVKit.isSquiggleEnabled())
            {
                view.getSquiggleHandler().beginSquiggling();
            }
            
            // set the selection mode on the view
            HGVKit.setSelectionMode(HGVKit.getSelectionMode(), view);
            
            HGVKit.firePropertyChange
              (HGVDesktop.NETWORK_VIEW_CREATED, null, view);
                    	
            //  Instead of calling fitContent(), access PGraphView directly.
            view.getCanvas().getCamera().animateViewToCenterBounds
            (view.getCanvas().getLayer().getFullBounds(), true, 0);
         
        }
    }
    
}