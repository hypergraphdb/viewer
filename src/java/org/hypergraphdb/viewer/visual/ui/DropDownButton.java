package org.hypergraphdb.viewer.visual.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.hypergraphdb.viewer.util.GUIUtilities;

/**
 * A button with a drop-down menu and default action.
 * @author santhosh kumar - santhosh@in.fiorano.com 
 * @author Konstantin Vandev
 */
public abstract class DropDownButton extends JButton 
                     implements ChangeListener, PopupMenuListener, ActionListener, PropertyChangeListener{ 
    private final JButton mainButton = this; 
    private final JButton arrowButton = new JButton(
    		new ImageIcon(getClass().getResource(
    				"/org/hypergraphdb/viewer/images/dropdown.gif"))); 
 
    private boolean popupVisible = false; 
 
    public DropDownButton(){ 
        mainButton.getModel().addChangeListener(this); 
        arrowButton.getModel().addChangeListener(this); 
        arrowButton.addActionListener(this);
        arrowButton.setMargin(new Insets(3, 0, 3, 0)); 
        mainButton.addPropertyChangeListener("enabled", this); //NOI18N 
    } 
 
    protected abstract JPopupMenu getPopupMenu(); 
    
    /*------------------------------[ PropertyChangeListener ]---------------------------------------------------*/ 
 
    public void propertyChange(PropertyChangeEvent evt){ 
        arrowButton.setEnabled(mainButton.isEnabled()); 
    } 
 
    /*------------------------------[ ChangeListener ]---------------------------------------------------*/ 
 
    public void stateChanged(ChangeEvent e){ 
        if(e.getSource()==mainButton.getModel()){ 
            if(popupVisible && !mainButton.getModel().isRollover()){ 
                mainButton.getModel().setRollover(true); 
                return; 
            } 
            arrowButton.getModel().setRollover(mainButton.getModel().isRollover()); 
            arrowButton.setSelected(mainButton.getModel().isArmed() && mainButton.getModel().isPressed()); 
        }else{ 
            if(popupVisible && !arrowButton.getModel().isSelected()){ 
                arrowButton.getModel().setSelected(true); 
                return; 
            } 
            mainButton.getModel().setRollover(arrowButton.getModel().isRollover()); 
        } 
    } 
 
    /*------------------------------[ ActionListener ]---------------------------------------------------*/ 
 
    public void actionPerformed(ActionEvent ae){ 
        showDropDown(ae); 
     } 
    
    public void showDropDown(ActionEvent ae)
    {
        JPopupMenu popup = getPopupMenu(); 
        popup.addPopupMenuListener(this);
        Point pt = new Point(0, mainButton.getY() + mainButton.getHeight());
        Frame f = GUIUtilities.getFrame(mainButton);
        //TODO: fix this
        pt = SwingUtilities.convertPoint(mainButton, pt.x, pt.y, f);
        pt = GUIUtilities.adjustPointInPicollo(mainButton, pt);
        popup.show(f, pt.x, pt.y); 
    }
 
    /*------------------------------[ PopupMenuListener ]---------------------------------------------------*/ 
 
    public void popupMenuWillBecomeVisible(PopupMenuEvent e){ 
        popupVisible = true; 
        mainButton.getModel().setRollover(true); 
        arrowButton.getModel().setSelected(true); 
    } 
 
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
        popupVisible = false; 
 
        mainButton.getModel().setRollover(false); 
        arrowButton.getModel().setSelected(false); 
        ((JPopupMenu)e.getSource()).removePopupMenuListener(this); // act as good programmer :)
    } 
 
    public void popupMenuCanceled(PopupMenuEvent e){ 
        popupVisible = false; 
    } 
 
    /*------------------------------[ Other Methods ]---------------------------------------------------*/ 
 
    public JButton addToToolBar(JComponent toolbar){ 
    	   JToolBar tempBar = new JToolBar();
           tempBar.setAlignmentX(0.5f);
           tempBar.setRollover(true);
           tempBar.add(mainButton);
           tempBar.add(arrowButton);
           tempBar.setFloatable(false);
           toolbar.add(tempBar);
        return mainButton; 
    } 
    
    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e){
            e.printStackTrace();
        }
        JToolBar toolbar = new JToolBar();
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); //NOI18N

        DropDownButton dropdown = new DropDownButton(){
            protected JPopupMenu getPopupMenu(){
                JPopupMenu popup = new JPopupMenu();
                popup.add(getAction());
                popup.add(createAction("copy"));
                popup.add(createAction("delete"));
                return popup;
            }
        };
        
        DropDownButton dropdown1 = new DropDownButton(){
            protected JPopupMenu getPopupMenu(){
                JPopupMenu popup = new JPopupMenu();
                //popup.add(getAction());
                popup.add(createAction("copy"));
                popup.add(createAction("delete"));
                return popup;
            }
        };
        dropdown.putClientProperty("hideActionText", Boolean.TRUE);
        dropdown.setAction(createAction("cut"));

        dropdown.addToToolBar(toolbar);
        JFrame frame = new JFrame("DropDownButton - santhosh@in.fiorano.com");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);
        JPanel panel = new JPanel();
        dropdown1.addToToolBar(panel);
        frame.getContentPane().add(panel);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    
    public static Action createAction(String name){
        return new AbstractAction(name){
            {
                putValue(Action.SMALL_ICON, new ImageIcon(
                		getClass().getResource(
                				"/org/hypergraphdb/viewer/images/Zoom24.gif")));
            }
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog((Component)e.getSource(), getValue(Action.NAME)+" invoked.");
            }
        };
    }
} 


