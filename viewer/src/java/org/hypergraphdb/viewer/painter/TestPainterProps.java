package org.hypergraphdb.viewer.painter;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.swing.JFrame;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.viewer.*;
import org.hypergraphdb.viewer.HGVComponent;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.HGVNetworkView;
import org.hypergraphdb.viewer.visual.ui.PaintersPanel;
import com.l2fprod.common.beans.editor.DirectoryPropertyEditor;
import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;

public class TestPainterProps extends PropertySheetPanel
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		PaintersPanel p = new PaintersPanel();
		//p.init(new DefaultNodePainter());
		//p.init(new DefaultEdgePainter());
		f.getContentPane().add(p);
		HyperGraph hg = new HyperGraph("F:/temp/xxx2");
		p.setHyperGraph(hg);
		f.getContentPane().add(getView(hg));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setMinimumSize(new Dimension(600, 400));
		f.setVisible(true);
	}
	
	public static Component getView(HyperGraph hg){
		HGHandle h = hg.getTypeSystem().getTypeHandle(HGStats.class);
		HGVComponent c = null;
		try{
		//HGVNetworkView view = HGVKit.getStandaloneView(hg, h, 3, null);
		//view.redrawGraph();
		//c = view.getComponent();
		c = new HGVComponent(hg, h, 0, null);
		//c.setPreferredSize(new java.awt.Dimension(600,400));
		
		//c = new HGVComponent(hg, (Collection<FNode>) new ArrayList<FNode>(), 
		//         (Collection<FEdge>) new ArrayList<FEdge>());
		     //c.setPreferredSize(new java.awt.Dimension(600,400));
		//c.getView().redrawGraph();
		}
		catch(Throwable t){
			t.printStackTrace();
		}
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
