package phoebe.event;

import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JLabel;

import java.util.ArrayList;

import phoebe.PGraphView;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * This class should probably be put into Client code
 */
public class PContextMenu extends PBasicInputEventHandler {

  PGraphView view;

  public PContextMenu ( PGraphView view ) {
    this.view = view;

    

  }


  public void mousePressed ( PInputEvent event ) {
    if ( event.getPickedNode().getClientProperty( "no_menu" ) == "yes" ) 
      return;

    if ( event.getPickedNode() instanceof PCamera != true ) {
      event.setHandled( true );
      showMenu( event );
    }
  }

 //  public void mouseReleased( PInputEvent event ) { 
//     if ( event.isPopupTrigger() && event.getPickedNode() instanceof PCamera != true ) {
//       event.setHandled( true );
//       showMenu( event );
//     }
//   }


  /**
   * Creates the Appropriate JMenu for the particular node type
   */
  public void showMenu ( PInputEvent event ) {

    PNode thing = event.getPickedNode();
   

    Object[] methods = view.getContextMethods( thing.getClass().toString(), true );
   

    // System.out.println( "Number of methods: "+methods.length );

    // only do menuing if there is a reason to
    if ( methods != null ) {
      JPopupMenu menu = new JPopupMenu();
      menu.setLabel( "TEST" );
      //menu.add( thing.toString() );
      //menu.add( new JSeparator() );
      for ( int i = 0; i < methods.length; ++i ) {

        // System.out.println( ":: "+( ( Object[] )methods[i])[1] );

        if ( ( ( Object[] )methods[i])[1] == "getTitle" ) {
          // System.out.println( "TITLE Found" );
          //menu.insert( new JSeparator() );
          try {
            Object[] method_info = ( Object[] )methods[i];
            String method_class_name = ( String )method_info[0];
            String method_name = ( String )method_info[1];
            Object[] args = ( Object[] )method_info[2];
            ClassLoader loader = ( ClassLoader )method_info[3];

            Class method_class = Class.forName( method_class_name, true, loader );
            Class[] method_arg_classes = new Class[] { Object[].class, PNode.class };
            final Method method = method_class.getMethod( method_name, method_arg_classes );
            Object method_object = method_class.newInstance();
            String title = ( String )method.invoke( method_object, new Object[] { args, thing } );
            
            menu.insert( new JLabel("<HTML><b><big><font color=#333366>"+title+"</font></b></big></HTML>" ), 0 );
          } catch ( Exception e ) {
            System.out.println( "Title Failed!!!!" );
            e.printStackTrace();
          }          
          continue;
        }
        menu.add( createMenuItem( (Object[] )methods[i], thing ) );
      }
      menu.show( (PCanvas)event.getComponent(), 
                (int)event.getCanvasPosition().getX(),
                (int)event.getCanvasPosition().getY());
    }
  }


  private JMenuItem createMenuItem ( Object[] method_info, PNode thing ) {
    try {
      String method_class_name = ( String )method_info[0];
      String method_name = ( String )method_info[1];
      Object[] args = ( Object[] )method_info[2];
      ClassLoader loader = ( ClassLoader )method_info[3];

      Class method_class = Class.forName( method_class_name, true, loader );
      Class[] method_arg_classes = new Class[] { Object[].class, PNode.class };
      final Method method = method_class.getMethod( method_name, method_arg_classes );
      
      Object method_object = method_class.newInstance();
      JMenuItem item = ( JMenuItem )method.invoke( method_object, new Object[] { args, thing } );
      return item;
    } catch  ( Exception ex ) {
      ex.printStackTrace();
      return new JMenuItem( "Null Item" );
      
    }

  } // createMenuItem




}


