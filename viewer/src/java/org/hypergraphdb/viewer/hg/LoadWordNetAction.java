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
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.AppConfig;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetwork;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.VisualManager;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.painter.NodePainter;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;
import org.hypergraphdb.viewer.view.HGVDesktop;
import org.hypergraphdb.viewer.visual.VisualStyle;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import edu.umd.cs.piccolo.PCanvas;

/**
  */
public class LoadWordNetAction extends HGVAction
{
	private static final String WORDNET_PATH_PROP = "WORDNET_PATH_PROP";
	private static String PATH = "F:\\kosta\\hg\\wordnet";
    
    /**
     * ConstructorLink.
     */
    public LoadWordNetAction()
    {
        super(ActionManager.LOAD_WORD_NET_ACTION);
        setAcceleratorCombo(java.awt.event.KeyEvent.VK_W,
        ActionEvent.CTRL_MASK);
    }
    
      
    /**
     * User Initiated Request.
     *
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e)
    {
       String p = (String)AppConfig.getInstance().getProperty(
    		   WORDNET_PATH_PROP, PATH);
    	loadHyperGraph(new File(p));
    }
    
    public static void loadHyperGraph(File file)
    {
    	if(file == null) throw new NullPointerException("HG file is null");
    	HGVNetwork network = HGVKit.getNetworkByFile(file);
    	if (network == null)
       {
            //  Create LoadNetwork Task
            LoadTask task = new LoadTask(file);
            //  Execute Task in New Thread;  pops open JTask Dialog Box.
            TaskManager.executeTask(task, null); 
        }
    	//else
    	//	HGVKit.getDesktop().setFocus(network);
    }
    
       
    
    /**
     * Task to Load New Network Data.
     */
    static class LoadTask implements Task
    {
        private File db;
        private HGVNetwork cyNetwork;
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
            taskMonitor.setStatus("Reading in Network Data...");
            Thread.currentThread().setContextClassLoader(AppConfig.getInstance().getClassLoader());
            try
            {
            	if(!db.isDirectory())
            		throw new IOException("No such DB: " + db.getAbsolutePath());
            	HGVNetworkView view = createNetwork();
                cyNetwork = (view!= null) ? view.getNetwork(): null;
                
                if (cyNetwork != null)
                {
                    informUserOfGraphStats(view.getNetwork());
                } else
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not read network from DB: " + db.getAbsolutePath());
                    sb.append("\nThis directory may not be a valid DB.");
                    taskMonitor.setException(new IOException(sb.toString()),
                    sb.toString());
                }
                taskMonitor.setPercentCompleted(100);
            } catch (IOException e)
            {
                taskMonitor.setException(e, "Unable to load network file.");
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
        
        private HGVNetworkView createNetwork() throws IOException
        {
        	
            final HGWNReader reader = new HGWNReader(db);
            open(reader);
                       
            //  Get the RootGraph indices of the nodes and
            //  Edges that were just created
            final int[] nodes = reader.getNodeIndicesArray();
            final int[] edges = reader.getEdgeIndicesArray();
            
            final String title = db.getAbsolutePath();
            
            // Create a new cytoscape.data.HGVNetwork from these nodes and edges
            //taskMonitor.setStatus("Creating HG Network...");
            
            //  Create the HGVNetwork
            //  First, set the view threshold to 0.  By doing so, we can disable
            //  the auto-creating of the HGVNetworkView.
            int realThreshold =AppConfig.getInstance().getViewThreshold();
            AppConfig.getInstance().setViewThreshold(0);
            HGVNetwork network = HGVKit.createNetwork(nodes, edges,
            		reader.getHyperGraph());
            
            network.setTitle(title);
           // System.out.println("Network: " + network + " file_name: " + network.getFileName());
            //  Reset back to the real View Threshold
            AppConfig.getInstance().setViewThreshold(realThreshold);
            
            if (network.getNodeCount() < AppConfig.getInstance().getViewThreshold()  )
            {
            	final HGVNetworkView view = createNetworkView(network);
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        PCanvas pCanvas = view.getCanvas();
                        pCanvas.setVisible(true);
                    }
                });
                setVisualStyle(view);
                return view;
            }
           return null;
        }
        private static final String WN_STYLE = "wordnet_style";
        private void setVisualStyle(HGVNetworkView view){
        	VisualStyle vs = VisualManager.getInstance().getVisualStyle(WN_STYLE);
        	if(vs != null) {
        		System.out.println("WordNet Style already in HG");
        		view.setVisualStyle(vs);
        		view.redrawGraph();
        		return;
        	}
        	vs = new VisualStyle(WN_STYLE);
        	HyperGraph hg = view.getNetwork().getHyperGraph();
        	ClassLoader cl = Thread.currentThread().getContextClassLoader();
        	addNodePainter(hg, vs, cl,
        			"org.hypergraphdb.app.wordnet.data.NounSynsetLink",
        			"org.hypergraphdb.app.wordnet.viewer.SynsetNodePainter");
        	addNodePainter(hg, vs, cl,
        			"org.hypergraphdb.app.wordnet.data.VerbSynsetLink",
        			"org.hypergraphdb.app.wordnet.viewer.SynsetNodePainter");
        	addNodePainter(hg, vs, cl,
        			"org.hypergraphdb.app.wordnet.data.VerbFrame",
        			"org.hypergraphdb.app.wordnet.viewer.VerbFrameNodePainter");
        	addNodePainter(hg, vs, cl,
        			"org.hypergraphdb.app.wordnet.data.Word",
        			"org.hypergraphdb.app.wordnet.viewer.WordNodePainter");
        	VisualManager.getInstance().addVisualStyle(vs);
        	view.setVisualStyle(vs);
        	
       }
        
        private void addNodePainter(HyperGraph hg, VisualStyle vs,
        		ClassLoader cl, String t_cls, String p_cls){
        	try{
        		Class clazz = cl
				.loadClass(t_cls);
        		HGPersistentHandle h = hg.getPersistentHandle(
        				hg.getTypeSystem().getTypeHandle(clazz));
        		 NodePainter p = (NodePainter) cl.loadClass(p_cls).newInstance();
        	     vs.addNodePainter(h, p);
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        }
        
        public void open(HGWNReader reader) throws IOException
    	{
    		NotifyDescriptor d = new NotifyDescriptor.InputLine(
    				GUIUtilities.getFrame(), "",
    				"Specify the word", NotifyDescriptor.PLAIN_MESSAGE,
    				NotifyDescriptor.OK_CANCEL_OPTION);
    		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
    		{
    			String lemma = ((NotifyDescriptor.InputLine) d).getInputText();
    			if (lemma == null || lemma.equals("")) return;
    			HyperGraph hg = reader.getHyperGraph();
        		reader.read(getWordHandle(hg, lemma), 2, (HGAtomPredicate)null);
    		}
    	}
        
        private HGHandle getWordHandle(HyperGraph hg, String lemma) throws IOException
    	{
        	HGHandle word_handle = 
    			HGVUtils.lookup(hg, "word", "lemma",	lemma);
    		if (word_handle == null){
    			hg.close();
    			throw new IOException("No such word: " + lemma + " in the DB.");
    		}
    		return word_handle;
    	}
        
        /**
         * Creates the HGVNetworkView.
         * Most of this code is copied directly from HGVKit.createHGVNetworkView.
         * However, it requires a bit of a hack to actually hide the network
         * view from the user, and I didn't want to use this hack in the core
         * HGVKit.java class.
         */
        private HGVNetworkView createNetworkView(HGVNetwork network)
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
            
            HGVKit.getPreferedLayout().applyLayout();
			
            //  Instead of calling fitContent(), access PGraphView directly.
            view.getCanvas().getCamera().animateViewToCenterBounds
            (view.getCanvas().getLayer().getFullBounds(), true, 0);
            return view;
        }
    }
     
}