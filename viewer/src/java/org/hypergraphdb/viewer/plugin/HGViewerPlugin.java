package org.hypergraphdb.viewer.plugin;

import org.hypergraphdb.viewer.*;

/**
 * A HGViewerPlugin is the new "Global" plugin. A HGViewerPlugin constructor
 * does not have any arguments, since it is Network agnostic.  Instead all
 * access to the HGViewer Data Structures is handled throught the static
 * methods provided by org.hypergraphdb.viewer.org.hypergraphdb.viewer.
 *
 * It is encouraged, but not mandatory, for plugins to override the
 * {@link #describe describe} method to state what the plugin does and how it
 * should be used.
 */
public abstract class HGViewerPlugin {

  /**
   * There are no arguments required or allowed in a HGViewerPlugin
   * constructor.
   */
  public HGViewerPlugin () { 
  }


  /**
   * If true, this plugin is capable if accepting scripts, and we 
   * will find out what its script name is
   */
  public boolean isScriptable () {
    return false;
  }

  /**
   * If this plugin is scriptable, then this will return a unique
   * script name, that will come after the colon like:
   * :name
   */
  public String getScriptName () {
    return "default";
  }

  /**
   * Take a HGVNetwork as input along with some arguments, and return a HGVNetwork,
   * which can be the same, or different, it doesn't really matter, and is 
   * up to the individual plugin.
   */
  public HGVNetwork interpretScript ( String[] args, HGVNetwork network ) {
    return null;
  }

  /**
   * If implemented, then this plugin will be activated after being initialized
   */
  public void activate () {
  }

  /**
   * If implemented then this plugin can remove itself from the Menu system, and
   * anything else, when the user decides to deactivate it.
   */
  public void deactivate () {
  }

  /**
   * method returning a String description of the plugin.
   */
  public String describe () { 
    return new String("No description."); 
  }

  /**
   * Attempts to instantiate a plugin of the class argument.
   *
   * @return true if the plugin was successfulyl constructed, false otherwise
   */
  public static boolean loadPlugin ( Class pluginClass ) {

    System.out.println( "Loading: "+pluginClass );

    if ( pluginClass == null ) {
      return false;
    }

    Object object = null;
      try {
        object = pluginClass.newInstance();
      } catch (InstantiationException e) {
        System.out.println( "InstantiationException");
          System.out.println(e);
          e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.out.println( "IllegalAccessException");
          System.out.println(e);
          e.printStackTrace();
      }
      if ( object == null ) {
        System.out.println( "Instantiation seems to have failed" );
      }

      return true;
  }
}


