package org.hypergraphdb.viewer.layout;

import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.foo.GraphConverter;
import org.hypergraphdb.viewer.util.GUIUtilities;
import cytoscape.graph.legacy.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.legacy.layout.impl.SpringEmbeddedLayouter2;
import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class SpringLayout implements Layout
{
	public String getName()
	{
		   return "Spring Embedded";
	}
	
	public void applyLayout()
	{
		final MutablePolyEdgeGraphLayout nativeGraph = GraphConverter
				.getGraphCopy(0.0d, false, false);
		Task task = new SpringEmbeddedLayouter2(nativeGraph);
		// ////////////////////////////////////////////////////////
		// BEGIN: The thread and process related code starts here.
		// ////////////////////////////////////////////////////////
		// Configure UI Options.
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setOwner(GUIUtilities.getFrame());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayStatus(true);
		// Execute Task
		// This method will block until Pop-up Dialog Box is diposed/closed.
		boolean success = TaskManager.executeTask(task, jTaskConfig);
		// Whatever you do, make sure that task.run() is finished by the time
		// we exit out of this code block. This may require a synchronized
		// block with Object.wait() if we call task.run() from another thread.
		// ////////////////////////////////////////////////////
		// END: The thread and process related code ends here.
		// ////////////////////////////////////////////////////
		// Update the UI only if Task was Successful.
		if (success)
		{
			GraphConverter.updateCytoscapeLayout(nativeGraph);
		}
	}
}
