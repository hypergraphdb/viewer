package org.hypergraphdb.viewer.util;

import java.awt.FileDialog;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.Iterator;

import org.hypergraphdb.viewer.*;

/**
 * Provides a platform-dependent way to open files. Mainly because Mac would
 * prefer that you use java.awt.FileDialog instead of the Swing FileChooser.
 */
public abstract class FileUtil {

	public static int LOAD = FileDialog.LOAD;

	public static int SAVE = FileDialog.SAVE;

	public static int CUSTOM = LOAD + SAVE;

	/**
	 * Returns a File object, this method should be used instead of rolling your
	 * own JFileChooser.
	 * 
	 * @return the location of the selcted file
	 * @param title
	 *            the title of the dialog box
	 * @param load_save_custom
	 *            a flag for the type of file dialog
	 */
	public static File getFile(String title, int load_save_custom) {
		return getFile(title, load_save_custom, new ExtFileFilter[] {}, null,
				null, false);
	}

	/**
	 * Returns a File object, this method should be used instead of rolling your
	 * own JFileChooser.
	 * 
	 * @return the location of the selcted file
	 * @param title
	 *            the title of the dialog box
	 * @param load_save_custom
	 *            a flag for the type of file dialog
	 * @param filters
	 *            an array of CyFileFilters that let you filter based on
	 *            extension
	 */
	public static File getFile(String title, int load_save_custom,
			ExtFileFilter[] filters) {
		return getFile(title, load_save_custom, filters, null, null, false);
	}

	/**
	 * Returns a File object, this method should be used instead of rolling your
	 * own JFileChooser.
	 * 
	 * @return the location of the selcted file
	 * @param title
	 *            the title of the dialog box
	 * @param load_save_custom
	 *            a flag for the type of file dialog
	 * @param filters
	 *            an array of CyFileFilters that let you filter based on
	 *            extension
	 * @param start_dir
	 *            an alternate start dir, if null the default MUD will  be used
	 * @param custom_approve_text
	 *            if this is a custom dialog, then custom text should be on the
	 *            approve button.
	 */
	public static File getFile(String title, int load_save_custom,
			ExtFileFilter[] filters, String start_dir,
			String custom_approve_text, boolean dir_only) {

		File start = null;
		if (start_dir == null) {
			String mrud = AppConfig.getInstance().getMRUD();
			start = mrud == null ? AppConfig.getConfigDirectory():
				new File(mrud);
		} else {
			start = new File(start_dir);
		}

		String osName = System.getProperty("os.name");
		System.out.println("Os name: " + osName);
		if (osName.startsWith("Mac")) {

			// this is a Macintosh, use the AWT style file dialog
         	FileDialog chooser = new FileDialog(GUIUtilities.getFrame(), title,
					load_save_custom);

			// we can only set the one filter; therefore, create a special
			// version of ExtFileFilter that contains all extensions
			if (!dir_only) {
				ExtFileFilter fileFilter = new ExtFileFilter();
				for (int i = 0; i < filters.length; i++) {
					Iterator iter;
					for (iter = filters[i].getExtensionSet().iterator(); iter
							.hasNext();) {
						fileFilter.addExtension((String) iter.next());
					}
				}

				fileFilter.setDescription("All network files");
				chooser.setFilenameFilter(fileFilter);
			}
			else
			{
				chooser.setFilenameFilter( new CustomFileFilter());
			}
			chooser.setVisible(true);

			if (chooser.getFile() != null) {
				File result = new File(chooser.getDirectory() + "/"
						+ chooser.getFile());
				if (chooser.getDirectory() != null) {
					AppConfig.getInstance().setMRUD(chooser.getDirectory());
				}
				return result;
			}
			return null;

		} else {
			// this is not a mac, use the Swing based file dialog
			JFileChooser chooser = new JFileChooser(start);

			// set the dialog title
			chooser.setDialogTitle(title);
            
			// add filters
			if (!dir_only) {
			for (int i = 0; i < filters.length; ++i) {
				chooser.addChoosableFileFilter(filters[i]);
			}}else
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			File result = null;
			// set the dialog type
			if (load_save_custom == LOAD) {
				if (chooser.showOpenDialog(HGVKit.getDesktop()) == JFileChooser.APPROVE_OPTION) {
					result = chooser.getSelectedFile();
				}
			} else if (load_save_custom == SAVE) {
				if (chooser.showSaveDialog(HGVKit.getDesktop()) == JFileChooser.APPROVE_OPTION) {
					result = chooser.getSelectedFile();
				}
			} else {
				if (chooser.showDialog(HGVKit.getDesktop(),
						custom_approve_text) == JFileChooser.APPROVE_OPTION) {
					result = chooser.getSelectedFile();
				}
			}

			if (result != null && start_dir == null)
				AppConfig.getInstance().setMRUD(
						chooser.getCurrentDirectory().getAbsolutePath());

			return result;
		}

	}
	
	private static class CustomFileFilter implements java.io.FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			File f = new File(dir, name);
			if(f.isDirectory())
			{
				if(new File(f, "je.lck").exists() &&
					new File(f, "00000000.jdb").exists())
					return true;
				}
			return false;
		}
	}

}