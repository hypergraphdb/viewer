package org.hypergraphdb.viewer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HGSystemFlags;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.AtomTypeCondition;
import org.hypergraphdb.HGQuery.hg;

public class AppConfig
{
	private static final String APP_CONFIG_HG_NAME = "hgviewer_config";
	public static final String VIEW_THRESHOLD = "viewThreshold"; 
	public static final String SEC_VIEW_THRESHOLD = "secondaryViewThreshold"; 
	
	private static final String EXT_DIR = "jars";
	private static AppConfig instance;
	private static HyperGraph graph;
	// Most-Recently-Used-Dir
	private String mrud = "";
	// Most-Recently-Used-Files
	private HashSet<String> mrufs = new HashSet<String>(10);
	private HashSet<String> openedFiles = new HashSet<String>(10);
	private Map<String, Object> properties = new HashMap<String, Object>();
	private URLClassLoader classLoader;

	public AppConfig()
	{
		if (instance != null)
			throw new RuntimeException(
					"Can't construct AppConfig twice...it's a singleton.");
	}

	public static AppConfig getInstance()
	{
		if (instance == null)
		{
			graph = new HyperGraph(new File(getConfigDirectory(),
					APP_CONFIG_HG_NAME).getAbsolutePath());
			instance = (AppConfig) hg.getOne(graph, hg.type(AppConfig.class));
			if (instance == null)
			{
				System.out.println("Creating new AppConfig");
				instance = new AppConfig();
				graph.add(instance, HGSystemFlags.MUTABLE);
			}
			instance.classLoader = new URLClassLoader(new URL[] {},
					HGViewer.class.getClassLoader());
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run()
				{
					try
					{
						System.out.println("Saving AppConfig");
						graph.close();
					}
					catch (Throwable t)
					{
						t.printStackTrace(System.err);
					}
				}
			}));
		}
		return instance;
	}

	public String getMRUD()
	{
		return mrud;
	}

	public void setMRUD(String f)
	{
		mrud = f;
	}

	public HashSet<String> getMRUF()
	{
		return mrufs;
	}

	public void setMRUF(HashSet<String> m)
	{
		mrufs = m;
	}

	public HashSet<String> getOpenedFiles()
	{
		return openedFiles;
	}

	public void setOpenedFiles(HashSet<String> openedFiles)
	{
		this.openedFiles = openedFiles;
	}

	public Object getProperty(String key)
	{
		return properties.get(key);
	}
	
	public void removeProperty(String key)
	{
		properties.remove(key);
	}

	public Object getProperty(String key, Object def)
	{
		if (properties.containsKey(key)) 
			 return properties.get(key);
		properties.put(key, def);
		return def;
	}

	public void setProperty(String key, Object def)
	{
		properties.put(key, def);
	}

	public URLClassLoader getClassLoader()
	{
		return classLoader;
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

	public int getViewThreshold()
	{
		return ((Integer)getProperty(VIEW_THRESHOLD, 500)).intValue();
	}

	public void setViewThreshold(int viewThreshold)
	{
		setProperty(VIEW_THRESHOLD, viewThreshold);
	}

	public int getSecondaryViewThreshold()
	{
		return ((Integer)getProperty(SEC_VIEW_THRESHOLD, 2000)).intValue();
	}

	public void setSecondaryViewThreshold(int secondaryViewThreshold)
	{
		setProperty(VIEW_THRESHOLD, secondaryViewThreshold);
	}

	public Map<String, Object> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}

	public HyperGraph getGraph()
	{
		return graph;
	}
}
