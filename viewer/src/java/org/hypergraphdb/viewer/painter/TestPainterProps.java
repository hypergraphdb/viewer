package org.hypergraphdb.viewer.painter;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.viewer.HGViewer;

import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

/**
 * Testing class to be removed 
 */
public class TestPainterProps extends PropertySheetPanel
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
        JFrame f = new JFrame();
//	    Map<String, Object> l = new HashMap<String, Object>();
//	    l.put("First", "First");
//	    l.put("Sec",  new JButton("Test"));
//	   // Object[] l = new Object[]{ "First", new JButton("Test")};
//	    Object obj = l;//new JButton("Test");
//	    ObjectInspector outline = new ObjectInspector(l);
//	    outline.setModelObject(obj);
//        outline.setPreferredSize(new Dimension(400, 200));
//        DialogDescriptor dd = new DialogDescriptor(f, outline,
//              "Properties");
//       DialogDisplayer.getDefault().notify(dd);
//       if(true) return;
       
		final HyperGraph graph = new HyperGraph("F:/temp/xxx4");
		HGHandle h = graph.getTypeSystem().getTypeHandle(HGStats.class);
		HGViewer viewer = new HGViewer(graph, h, 1, null);
		f.getContentPane().add(viewer);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addWindowListener(new WindowAdapter(){
           public void windowClosing(WindowEvent e)
            {
               graph.close();
            }
		    
		});
		f.setMinimumSize(new Dimension(600, 400));
		f.setVisible(true);
		//new org.hypergraphdb.viewer.dialogs.PhoebeNodeControl(viewer.getView());
	}
	
	public static HGViewer getViewer(HyperGraph graph){
		HGHandle h = graph.getTypeSystem().getTypeHandle(HGStats.class);
		HGViewer c = null;
			
		
		if(c == null)
		{
		  c = new HGViewer(graph, h, 1, null);
		
		}
		else
		    System.out.println("Viewer retrieved from HG");
		
		//c.setPreferredSize(new java.awt.Dimension(600,400));
		
		//c = new HGVComponent(hg, (Collection<FNode>) new ArrayList<FNode>(), 
		//         (Collection<FEdge>) new ArrayList<FEdge>());
		     //c.setPreferredSize(new java.awt.Dimension(600,400));
		//c.getView().redrawGraph();
		
		
		return c;
	}
	public void init(Object p)
	{
		setPreferredSize(new Dimension(250,200));
		//setLayout(LookAndFeelTweaks.createVerticalPercentLayout());
		//JTextArea message = new JTextArea();
		//message.setText("Java Formatter Properties");
		//LookAndFeelTweaks.makeMultilineLabel(message);
		//add(message);
		setDescriptionVisible(true);
		setSortingCategories(true);
		setSortingProperties(true);
		setRestoreToggleStates(true);
		//add(sheet, "*");
		new BeanBinder(p, this);
		addPropertySheetChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt)
					{
						//Utilities.resetFormatter(nbui, 5);
						//Utilities.formatCell(nbui, 5);
					}
				});
		//Utilities.formatCell(nbui, 5);
	}

	
//	private void init(){
//		PropertySheetTable table = new PropertySheetTable();
//		//ArrayList<DefaultProperty> data = new ArrayList<DefaultProperty> ();
//		//data.add(0, new RtProperty(ClassRepository.getInstance().getRtDocInfo()));
//		//table.setModel(new MyTableModel(data));
//		setTable(table);
//		setDescriptionVisible(true);
//		//setToolBarVisible(false);
//		//table.getEditorRegistry().
//		//    registerEditor(File.class, DirectoryPropertyEditor.class);
//	}
}
