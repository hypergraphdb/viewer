/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.hypergraphdb.viewer.props;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.*;
import java.beans.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import org.hypergraphdb.viewer.HGVLogger;
import org.hypergraphdb.viewer.HGViewer;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;

/**
 * Helper dialog box manager for showing custom property editors.
 * 
 * @author Jan Jancura, Dafe Simonek, David Strupl
 */
final class PropertyDialogManager
{
	/* JST: Made package private because PropertyPanel should be used instead. */
	/** Listener to editor property changes. */
	private PropertyChangeListener listener;
	/** Cache for reverting on cancel. */
	private Object oldValue;
	/** Custom property editor. */
	private PropertyEditor editor;
	/** this is extracted from the model if possible, can be null */
	private AbstractProperty bean;
	/** Set true when property is changed. */
	private boolean changed = false;
	/** Given component stored for test on Enhance property ed. */
	private Component component;
	/** Dialog instance. */
	private Window dialog;
	/** Ok button can be enabled/disabled */
	private JButton okButton;
	/** */
	private Runnable errorPerformer;
	private boolean okButtonState = true;
	private boolean isModal = true;
	private String title = null;
	private Object defaultOption;
	private Object[] options;
	private static ThreadLocal caller = new ThreadLocal();
	private ActionListener actionListener;
	private Object lastValueFromEditor;
    private static Frame frame;
	// init
	
	/**
	 * Create a dialog.
	 * 
	 * @param title
	 *            title of the dialog
	 * @param component
	 *            component to show
	 * @param isModal
	 *            <code>true</code> if the dialog should be modal
	 * @param editor
	 *            custom property editor. May be <code>null</code>.
	 */
	public PropertyDialogManager(final String title, final boolean isModal,
			final PropertyEditor editor, AbstractProperty bean)
	{
		this.editor = editor;
		this.component = editor.getCustomEditor();
		this.bean = bean;
		this.title = title;
		this.isModal = isModal;
		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				doButtonPressed(evt);
			}
		};
		// create dialog instance and initialize listeners
		createDialog();
		initializeListeners();
	}

	// public methods
	// ............................................................
	/**
	 * Get the created dialog instance.
	 * 
	 * @return the dialog instance managed by this class.
	 */
	public Window getDialog()
	{
		return dialog;
	}

	// other methods
	// ............................................................
	/**
	 * Creates proper DialogDescriptor and obtain dialog instance via
	 * DialogDisplayer.createDialog() call.
	 */
	private void createDialog()
	{
		if (component instanceof Window)
		{
			// custom component is already a window --> just return it
			// from getDialog
			dialog = (Window) component;
			dialog.pack();
			return;
		}
		// prepare our options (buttons)
		boolean cannotWrite  = !bean.canWrite();
		
		if ((editor == null) || (cannotWrite))
		{
			JButton closeButton = new JButton("Close");
			options = new Object[] { closeButton };
			defaultOption = closeButton;
		} else
		{
			okButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setVerifyInputWhenFocusTarget(false);
			cancelButton.setDefaultCapable(false);
			cancelButton.addActionListener(actionListener);
			options = new Object[] { okButton, cancelButton };
			defaultOption = okButton;
		}
		try
		{
			caller.set(this);
			Class c = Class
					.forName("org.hypergraphdb.viewer.props.PropertyDialogManager$CreateDialogInvoker"); // NOI18N
			Runnable r = (Runnable) c.newInstance();
			r.run();
			return;
		}
		catch (Exception e)
		{
			// if something went wrong just
			// resort to swing (IDE probably not present)
		}
		catch (LinkageError e)
		{
		}
		if (dialog == null)
		{
			JOptionPane jop = new JOptionPane(component,
					JOptionPane.PLAIN_MESSAGE, JOptionPane.NO_OPTION, null,
					options, defaultOption);
			if (okButton != null)
			{
				okButton.addActionListener(actionListener);
			}
							
			dialog = jop.createDialog(frame, title);
		}
	}

	/**
	 * Initializes dialog listeners. Must be called after createDialog method
	 * call. (dialog variable must not be null)
	 */
	private void initializeListeners()
	{
		// dialog closing reactions
		dialog.addWindowListener(new WindowAdapter() {
			/**
			 * Ensure that values are reverted when user cancelles dialog by
			 * clicking on x image
			 */
			public void windowClosing(WindowEvent e)
			{
				if ((editor != null) && !(component instanceof Window))
				{
					// if someone provides a window (s)he has to handle
					// the cancel him/herself
					cancelValue();
				}
				dialog.dispose();
			}

			/** Remove property listener on window close */
			public void windowClosed(WindowEvent e)
			{
				if (component instanceof Window)
				{
					// in this case we have to do similar thing as we do
					// directly after the Ok button is pressed. The difference
					// is in the fact that here we do not decide whether the
					// dialog will be closed or not - it is simply being closed
					// But we have to take care of propagating the value to
					// the model
					try
					{
						bean.setValue(lastValueFromEditor);
					}
					catch (java.lang.reflect.InvocationTargetException ite)
					{
						PropertyPanelEx.notifyUser(ite, bean == null ? ""
								: bean.getDisplayName()); // NOI18N
					}
					catch (IllegalStateException ise)
					{
						notifyUser(ise);
					}
				}
				if (listener != null)
					editor.removePropertyChangeListener(listener);
				dialog.removeWindowListener(this);
			}
		});
		// reactions to editor property changes
		if (editor != null)
		{
			try
			{
				oldValue = bean.getValue();
			}
			catch (Exception e)
			{
				// Ignored, there can be number of exceptions
				// when asking for old values...
			}
			lastValueFromEditor = editor.getValue();
			editor
					.addPropertyChangeListener(listener = new PropertyChangeListener() {
						/** Notify displayer about property change in editor */
						public void propertyChange(PropertyChangeEvent e)
						{
							changed = true;
							lastValueFromEditor = editor.getValue();
						}
					});
		}
	}

	/**
	 * Reverts to old values.
	 */
	private void cancelValue()
	{
		if (!changed) return;
		try
		{
			bean.setValue(oldValue);
		}
		catch (Exception e)
		{
			// Ignored, there can be number of exceptions
			// when asking for old values...
		}
	}

	/**
	 * Called when user presses a button on some option (button) in the dialog.
	 * 
	 * @param evt
	 *            The button press event.
	 */
	private void doButtonPressed(ActionEvent evt)
	{
		String label = evt.getActionCommand();
		if (label.equals("Cancel"))
		{
			cancelValue();
		}
		if (label.equals("OK"))
		{
			try
			{
				lastValueFromEditor = editor.getValue();
				bean.setValue(lastValueFromEditor);
			}
			catch (java.lang.reflect.InvocationTargetException ite)
			{
				PropertyPanelEx.notifyUser(ite, bean == null ? "" : bean
						.getDisplayName()); // NOI18N
				return;
			}
			catch (IllegalStateException ise)
			{
				notifyUser(ise);
				return;
			}
			// this is an old hack allowing to notify a cached value
			// obtained via propertyChangeEvent from the editor
			if (!okButtonState)
			{
				if (errorPerformer != null)
				{
					errorPerformer.run();
				}
				return;
			}
		}
		// close the dialog
		changed = false;
		dialog.dispose();
	}

	/**
	 * For testing purposes we need to _not_ notify some exceptions. That is why
	 * here is a package private method to register an exception that should not
	 * be fired.
	 */
	static void doNotNotify(Throwable ex)
	{
		doNotNotify = ex;
	}
	private static Throwable doNotNotify;

	/**
	 * Notifies an exception to error manager or prints its it to stderr.
	 * 
	 * @param ex
	 *            exception to notify
	 */
	static void notify(Throwable ex)
	{
		Throwable d = doNotNotify;
		doNotNotify = null;
		if (d == ex) return;
		HGVLogger.getInstance().exception(ex);
	}

	/**
	 * Notifies an exception to error manager or prints its it to stderr.
	 * 
	 * @param ex
	 *            exception to notify
	 */
	static void notify(int severity, Throwable ex)
	{
		Throwable d = doNotNotify;
		doNotNotify = null;
		if (d == ex) return;
		HGVLogger.getInstance().exception(ex);
	}

	/**
	 * Tries to find a localized message in the exception annotation or directly
	 * in the exception. If the message is found it is notified with user
	 * severity. If the message is not found the exception is notified with
	 * informational severity.
	 * 
	 * @param ex
	 *            exception to notify
	 */
	private static void notifyUser(Exception e)
	{
		HGVLogger.getInstance().exception(e);
	}

	static class CreateDialogInvoker implements Runnable
	{
		public void run()
		{
			final PropertyDialogManager pdm = (PropertyDialogManager) caller
					.get();
			caller.set(null);
			if (pdm == null)
			{
				throw new IllegalStateException("Parameter caller not passed."); // NOI18N
			}
			// create dialog descriptor, create & return the dialog
			// bugfix #24998, set helpCtx obtain from
			// PropertyEnv.getFeatureDescriptor()
			DialogDescriptor descriptor = new DialogDescriptor(getFrame(), pdm.component,
					pdm.title, pdm.isModal, pdm.options, pdm.defaultOption,
					DialogDescriptor.DEFAULT_ALIGN, pdm.actionListener);
			pdm.dialog = DialogDisplayer.getDefault().createDialog(descriptor);
			// System.out.println("PropertyDialogManager - dialog should go
			// here!!!!!!!!!!!!!!!!!!!1:");
		}
	}

	static Frame getFrame()
	{
		return frame;
	}

	static void setFrame(Frame frame)
	{
		PropertyDialogManager.frame = frame;
	}
}
