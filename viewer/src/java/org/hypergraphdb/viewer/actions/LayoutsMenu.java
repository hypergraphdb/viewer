package org.hypergraphdb.viewer.actions;


import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.layout.Layout;
import org.hypergraphdb.viewer.layout.LayoutAction;
import org.hypergraphdb.viewer.util.GUIUtilities;
import org.hypergraphdb.viewer.util.HGVAction;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;


/**
 *
 **/
public class LayoutsMenu extends JMenu
{

  public LayoutsMenu()
  {
    super("Apply Layout");
    int i = 1;
    for(Layout l : HGViewer.getLayouts())
    {
    	LayoutAction action = new LayoutAction(l, i);
    	JMenuItem menu_item = new JMenuItem(action);
    	menu_item.setAccelerator(  KeyStroke.getKeyStroke( action.getKeyCode(),
                action.getKeyModifiers() ) );
    	add(menu_item);
    	i++;
    }
    add(new JSeparator());
    add(new SelectPrefLayoutAction());
    
  }

  private static class SelectPrefLayoutAction extends AbstractAction
  {

    public SelectPrefLayoutAction()
    {
      super("Select Prefered Layout");
    }

    public void actionPerformed(ActionEvent e)
    {
    	DialogDescriptor d = new DialogDescriptor(GUIUtilities.getFrame(
    			HGViewer.getCurrentView().getComponent()), new SelectLayoutPanel(), 
    			"Select Prefered Layout");
        d.setModal(true);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        DialogDisplayer.getDefault().notify(d);
    }
  }
  
  public static class SelectLayoutPanel extends JPanel
  {
  	ButtonGroup group  = new ButtonGroup();
  	
  	/**
  	 * This is the default constructor
  	 */
  	public SelectLayoutPanel()
  	{
  		super();
  		initialize();
  	}

  	/**
  	 * This method initializes this
  	 * 
  	 * @return void
  	 */
  	private void initialize()
  	{
  		
  		Set<Layout> layouts = HGViewer.getLayouts(); 
  		this.setLayout(new GridBagLayout());
  		int i = 0;
  		final Map<String,Layout> layoutMap = new HashMap<String,Layout>(); 
  		for(Layout l : layouts)
  		{
  			GridBagConstraints gridBagConstraints = new GridBagConstraints();
  			gridBagConstraints.gridy = i++;
  			gridBagConstraints.gridx = 0;
  			JRadioButton butt = new JRadioButton(l.getName());
  		    this.add(butt, gridBagConstraints);
  		    butt.setSelected(l.equals(HGViewer.getPreferedLayout()));
  		    butt.setActionCommand(l.getName());
  		    layoutMap.put(l.getName(), l);
  		    butt.addActionListener(new ActionListener(){
  		    	public void actionPerformed(ActionEvent e) 
  		    	{
  		    		HGViewer.setPreferedLayout(
  		    				layoutMap.get(e.getActionCommand()));
  		    	}
  		    });
  		    group.add(butt);
  		}
  	}
  }  

}
