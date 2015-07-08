package org.hypergraphdb.viewer;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hypergraphdb.HGSystemFlags;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;

/**
 * Singleton class used for persisting various configuration options
 */
public class AppConfig
{
	private static final String APP_CONFIG_HG_NAME = "hgviewer_config";
	public static final String VIEW_THRESHOLD = "viewThreshold"; 
	public static final String BIG_ICONS = "bigIcons"; 
		
	private static AppConfig instance;
	private static HyperGraph graph;
	// Most-Recently-Used-Dir
	private String mrud = "";
	// Most-Recently-Used-Files
	private HashSet<String> mrufs = new HashSet<String>(10);
	private Map<String, Object> properties = new HashMap<String, Object>();

	public AppConfig()
	{
		if (instance != null)
			throw new RuntimeException(
					"Can't construct AppConfig twice...it's a singleton.");
		setProperty(BIG_ICONS, false);
	}

	public static AppConfig getInstance()
	{
		if (instance == null)
		{
			String path = new File(getConfigDirectory(),
			APP_CONFIG_HG_NAME).getAbsolutePath();
			System.out.println("Trying to get or create new AppConfig in: " + path);
			graph = new HyperGraph(path);
			//System.out.println("l: " + Thread.currentThread().getContextClassLoader());
			// System.out.println("2: " + AppConfig.class.getClassLoader());
			//graph.getTypeSystem().setClassLoader(AppConfig.class.getClassLoader());
           
			instance = (AppConfig) hg.getOne(graph, hg.type(AppConfig.class));
			if (instance == null)
			{
				instance = new AppConfig();
				graph.add(instance, HGSystemFlags.MUTABLE);
			}
		}
		return instance;
	}

	/**
	 * Returns most-recently-used-directory 
	 */
	public String getMRUD()
	{
		return mrud;
	}

	/**
     * Sets most-recently-used-directory 
     */
	public void setMRUD(String f)
	{
		mrud = f;
	}

	/**
     * Returns most-recently-used-files 
     */
	public HashSet<String> getMRUF()
	{
		return mrufs;
	}

	/**
     * Returns most-recently-used-files 
     */
	public void setMRUF(HashSet<String> m)
	{
		mrufs = m;
	}

	/**
	 * Returns a property given its name
	 */
	public Object getProperty(String key)
	{
		return properties.get(key);
	}
	
	/**
     * Removes a property given its name
     */
	public void removeProperty(String key)
	{
		properties.remove(key);
	}

	/**
     * Returns a property given its name. If property is not found, returns the 
     * passed in default value
     */
	public Object getProperty(String key, Object def)
	{
		if (properties.containsKey(key)) 
			 return properties.get(key);
		properties.put(key, def);
		return def;
	}

	/**
     * Sets a property
     */
	public void setProperty(String key, Object def)
	{
		properties.put(key, def);
	}

	/**
	 * @return the directory of the HGViewer.jar.
	 */
	public static File getConfigDirectory()
	{
		try
		{
			CodeSource cs = AppConfig.class.getProtectionDomain()
					.getCodeSource();
			URL url = null;
			if (cs != null)
			{
				url = cs.getLocation();
				if (url == null)
				{
					// Try to find 'cls' definition as a resource; this is not
					// documented to be legal, but Sun's implementations seem to
					// allow this:
					final ClassLoader clsLoader = AppConfig.class
							.getClassLoader();
					final String clsAsResource = AppConfig.class.getName()
							.replace('.', '/').concat(".class");
					url = clsLoader != null ? clsLoader
							.getResource(clsAsResource) : ClassLoader
							.getSystemResource(clsAsResource);
				}
			}
			if (url != null) return (new File(url.getPath())).getParentFile();
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(
					"Unable to find installation directory:", ex);
		}
		return null;
	}

	/**
	 * Returns the maximum number of nodes in a GraphView 
	 */
	public int getViewThreshold()
	{
		return ((Integer)getProperty(VIEW_THRESHOLD, 500)).intValue();
	}

	/**
     * Sets the maximum number of nodes in a GraphView 
     */
	public void setViewThreshold(int viewThreshold)
	{
		setProperty(VIEW_THRESHOLD, viewThreshold);
	}

	/**
	 * Returns the map with all properties
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	/**
     * Sets the map with all properties
     */
	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}

	/**
	 * Returns the HyperGraph instance in which this AppConfig is stored
	 */
	public HyperGraph getGraph()
	{
		return graph;
	}
}
