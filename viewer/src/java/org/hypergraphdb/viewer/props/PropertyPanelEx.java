/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.hypergraphdb.viewer.props;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 * Visual Java Bean for editing of properties. It takes the model and represents
 * the property editor for it.
 * 
 * @author Jaroslav Tulach, Petr Hamernik, Jan Jancura, David Strupl
 */
public class PropertyPanelEx extends JComponent
{
	/** Constant for showing properties as a string always. */
	public static final int ALWAYS_AS_STRING = 1;
	/** Constant for preferably showing properties as string. */
	public static final int STRING_PREFERRED = 2;
	/** Constant for preferably painting property values. */
	public static final int PAINTING_PREFERRED = 3;
	/**
	 * Constant defining preferences in displaying of value. Value should be
	 * displayed in read-only mode.
	 */
	public static final int PREF_READ_ONLY = 0x0001;
	/**
	 * Constant defining preferences in displaying of value. Value should be
	 * displayed in custom editor.
	 */
	public static final int PREF_CUSTOM_EDITOR = 0x0002;
	/**
	 * Constant defining preferences in displaying of value. Value should be
	 * displayed in editor only.
	 */
	public static final int PREF_INPUT_STATE = 0x0004;
	/** Name of the 'preferences' property. */
	public static final String PROP_PREFERENCES = "preferences"; // NOI18N
	/** Name of the 'model' property. */
	public static final String PROP_MODEL = "model"; // NOI18N
	/** Name of the read-only property 'propertyEditor'. */
	public static final String PROP_PROPERTY_EDITOR = "propertyEditor"; // NOI18N
	/** Holds value of property preferences. */
	private int preferences;
	/** Current property editor. */
	private PropertyEditor editor;
	/** Holds painting style. */
	private int paintingStyle;
	/** Foreground color of values. */
	private Color foregroundColor;
	/** Foreground color of disabled values. */
	private Color disabledColor;
	/** Is plastic property value. */
	private boolean plastic;
	/** */
	static ThreadLocal current = new ThreadLocal();
	/** Indicates whether the property is writable. */
	private boolean canWrite = true;
	/**
	 * In the (rare) case when the property is not readable value of this
	 * variable is set to false.
	 */
	private boolean canRead = true;
	// private variables for visual controls
	// ...........................................
	/** Component for showing property value is stored here. */
	private SheetButtonEx readComponent;
	/** Component cache. */
	private SheetButtonEx textView;
	/** Component cache. */
	private PropertyShowEx propertyShow;
	/** Component cache. */
	private SheetButtonEx paintView;
	/** Component cache. */
	private SheetButtonEx customizeButton;
	/**
	 * If this is true the read component is visible and we are not performing
	 * any component switch.
	 */
	private boolean isReadState;
	/**
	 * If this is true the write component is visible and we are not performing
	 * any component switch.
	 */
	private boolean isWriteState;
	/** Prevents firing back to ourselves. */
	private boolean ignoreEvents;
	/** Set to <code>true</code> when the custom dialog is showing. */
	private boolean customDialogShown;
	
	/** TextField used for editing the property value as text. */
	private JTextField textField;
	/** Combo used for editing the property value as tags. */
	private JComboBox comboBox;
	/** Listener capable of switching to the writing state. */
	private ReadComponentListener readComponentListener;
	/**
	 * Listener on textField and comboBox - it allows to switch back to the
	 * reading state.
	 */
	private WriteComponentListener writeComponentListener;
	/** Listens on changes in the model. */
	private PropertyChangeListener modelListener;
	
	/**
	 * If this is not <code>null</code> the listener is added to all newly
	 * created Sheetbuttons.
	 */
	private SheetButtonListenerEx sheetButtonListener;
	

	private AbstractProperty bean;

	/**
	 * Creates new PropertyPanel
	 * 
	 * @param bean The AbstractProperty for displaying
	 */
	public PropertyPanelEx(AbstractProperty bean, int preferences)
	{
		this.bean = bean;
		this.preferences = preferences;
		setLayout(new BorderLayout());
		// set defaults without PropertySheetSettings
		paintingStyle = PropertyPanelEx.PAINTING_PREFERRED;
		plastic = true;
		disabledColor = SystemColor.textInactiveText;
		foregroundColor = Color.black;//new Color(0, 0, 128);
		this.setBackground(Color.white);
		bean.addPropertyChangeListener(getModelListener());
		
		updateEditor();
		//System.out.println("PropertyPanelEx");
		reset();
	}

	/**
	 * Getter for property preferences.
	 * 
	 * @return Value of property preferences.
	 */
	public int getPreferences()
	{
		return preferences;
	}

	/**
	 * Setter for visual preferences in displaying of the value of the property.
	 * 
	 * @param pref
	 *            PREF_XXXX constants
	 */
	public void setPreferences(int preferences)
	{
		int oldPreferences = this.preferences;
		this.preferences = preferences;
		readComponent = null;
		reset();
		firePropertyChange(PROP_PREFERENCES, new Integer(oldPreferences),
				new Integer(preferences));
	}

	/**
	 * Getter for BeanProperty.
	 * 
	 * @return Value of AbstractProperty.
	 */
	public AbstractProperty getBeanProperty()
	{
		return bean;
	}

	/**
	 * Setter for AbstractProperty.
	 * 
	 * @param bean  New value of AbstractProperty.
	 */
	public void setModel(AbstractProperty bean)
	{
		AbstractProperty old_bean = this.bean;
		this.bean = bean;
		old_bean.removePropertyChangeListener(getModelListener());
		bean.addPropertyChangeListener(getModelListener());
		updateEditor();
		reset();
		firePropertyChange(PROP_MODEL, old_bean, bean);
	}

	/**
	 * Getter for current property editor depending on the model. It could be
	 * <CODE>null</CODE> if there is not possible to obtain property editor
	 * for the current model.
	 * 
	 * @return the property editor or <CODE>null</CODE>
	 */
	public PropertyEditor getPropertyEditor()
	{
		return editor;
	}

	
	// package private methods ----------------------------------------------
	/** Return a sheet button serving as readComponent if it has been initailized. */
	SheetButtonEx getReadComponent()
	{
		return readComponent;
	}

	/**
	 * Switches from reading component to writing one.
	 */
	void setWriteState()
	{
		if (isWriteState)
			return;
		if ((preferences & PREF_READ_ONLY) != 0)
			return;
		if ((preferences & PREF_CUSTOM_EDITOR) != 0)
			return;
		
		isReadState = false;
		isWriteState = false;
		removeAll();
		JComponent c = getWriterComponent();
		c.setToolTipText(getPanelToolTipText());
		add(c, BorderLayout.CENTER);
		// updateNeighbourPanels();
		revalidate();
		repaint();
		Component focused = getDefaultFocusComponent(c);
		if (focused != null)
		{
			WriteComponentListener l = getWriteComponentListener();
			focused.requestFocus();
			focused.removeFocusListener(l);
			focused.removeKeyListener(l);
			focused.addFocusListener(l);
			focused.addKeyListener(l);
		}
		isReadState = false;
		isWriteState = true;
	}

	/**
	 * Gets default focus component from the JComponent container hierarchy,
	 * i.e. component which calling on container requestDefaultComponent should
	 * get the focus - it differs from SwingUtilities.findFocusOwner.
	 * 
	 * @return <code>Component</code> which should get the focus as default or
	 *         <code>null</code> if there is no such one.
	 */
	private static Component getDefaultFocusComponent(JComponent container)
	{
		Component[] ca = container.getComponents();
		for (int i = 0; i < ca.length; i++)
		{
			if (ca[i].isFocusable())
			{
				return ca[i];
			}
			if (ca[i] instanceof JComponent)
					//&& !((JComponent) ca[i]).isManagingFocus())
			{
				Component res = getDefaultFocusComponent((JComponent) ca[i]);
				if (res != null)
				{
					return res;
				}
			}
		}
		return null;
	}

	/**
	 * Set whether buttons in sheet should be plastic.
	 * 
	 * @param plastic
	 *            true if so
	 */
	void setPlastic(boolean plastic)
	{
		this.plastic = plastic;
		reset();
	}

	/**
	 * Test whether buttons in sheet are plastic.
	 * 
	 * @return <code>true</code> if so
	 */
	boolean getPlastic()
	{
		return plastic;
	}

	/**
	 * Set the foreground color of values.
	 * 
	 * @param color
	 *            the new color
	 */
	void setForegroundColor(Color color)
	{
		this.foregroundColor = color;
		reset();
	}

	/**
	 * Set the foreground color of disabled properties.
	 * 
	 * @param color
	 *            the new color
	 */
	void setDisabledColor(Color color)
	{
		disabledColor = color;
		reset();
	}

	/** Sets painting style. */
	void setPaintingStyle(int style)
	{
		paintingStyle = style;
		reset();
	}

	/** Adds sheet button listener to the <code>readComponent</code>. */
	void addSheetButtonListener(SheetButtonListenerEx list)
	{
		this.sheetButtonListener = list;
		if (readComponent != null)
		{
			readComponent.addSheetButtonListener(list);
		}
	}

	/** Getter for <code>isWriteState</code> property. */
	boolean isWriteState()
	{
		return isWriteState;
	}

	// private methods -------------------------------------------------------
	/**
	 * Update the current property editor depending on the model.
	 */
	private void updateEditor()
	{
		PropertyEditor oldEditor = editor;
				
		// find new editor
		canWrite = bean.canWrite();
		canRead = bean.canRead();
		editor = bean.getPropertyEditor();
		if(editor != null)
		try {
		  editor.setValue(bean.getValue());
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

		// fire the change
		firePropertyChange(PROP_PROPERTY_EDITOR, oldEditor, editor);
	}

	/**
	 * Set the panel to the read state with fresh read component.
	 */
	private void reset()
	{
		if ((preferences & PREF_INPUT_STATE) != 0)
		{
			setWriteState();
			return;
		}
		if (((preferences & PREF_CUSTOM_EDITOR) != 0) && (editor != null))
		{
			Component c = editor.getCustomEditor();
			if ((c != null) && (c.getParent() != this))
			{
				removeAll();
				add(c, BorderLayout.CENTER);
				validate();
				return;
			}
		}
		setReadState();
	}

	/**
	 * Refreshes the view after the property has changed.
	 */
	void refresh()
	{
		if (customDialogShown)
		{
			// if custom dialog is currently shown the
			// refresh will happen immediately after it is closed
			return;
		}
		if (isReadState)
		{
			// [PENDING] anything more reasonable than reset here?
		}
		if (isWriteState && ((preferences & PREF_INPUT_STATE) == 0))
		{
			// When leaving 'write state' setReadState will be called directly,
			// see listeners at the bottom.
			return;
		}
		reset();
	}

	/**
	 * Switches from writing component to reading one.
	 */
	void setReadState()
	{
		if (isReadState)
			return;
		if ((preferences & PREF_INPUT_STATE) != 0)
		   return;
		
		if ((preferences & PREF_CUSTOM_EDITOR) != 0)
			return;
		
		isWriteState = false;
		isReadState = false;
		removeAll();
		readComponent = getReaderComponent();
		readComponent.addSheetButtonListener(getReadComponentListener());
		// bugfix# 10171 setComponentEnabled() call before add()...
		setComponentEnabled(readComponent, isEnabled());
		readComponent.setToolTipText(getPanelToolTipText());
		updateSheetButtonVisually();
		add(readComponent, BorderLayout.CENTER);
		revalidate();
		repaint();
		// Bug fix #13933 needles requesting of focus for cases when only
		// refresh
		// needed. In case of problems some better solution is needed.
		// Sure is only the thing, focus may not be requested when coming from
		// refresh().
		// requestFocus();
		isReadState = true;
		isWriteState = false;
	}

	/** Lazy init of the <code>readComponentListener</code>. */
	private ReadComponentListener getReadComponentListener()
	{
		if (readComponentListener == null)
		{
			readComponentListener = new ReadComponentListener();
		}
		return readComponentListener;
	}

	/** Lazy init of the <code>writeComponentListener</code>. */
	private WriteComponentListener getWriteComponentListener()
	{
		if (writeComponentListener == null)
		{
			writeComponentListener = new WriteComponentListener();
		}
		return writeComponentListener;
	}

	/** Lazy init of the <code>modelListener</code>. */
	private PropertyChangeListener getModelListener()
	{
		if (modelListener == null)
		{
			modelListener = new ModelListener();
		}
		return modelListener;
	}

	public void setBackground(Color bg)
	{
		super.setBackground(bg);
		updateSheetButtonVisually();
	}

	// reader component ........................
	/**
	 * Creates Reader component.
	 */
	private SheetButtonEx getReaderComponent()
	{
		String stringValue = null;
		SheetButtonEx c = null;
		if (editor == null)
		{
			return getTextView("null"); // NOI18N
		}
		
		try
		{
			if ((canRead))
				stringValue = editor.getAsText();
			if (editor.isPaintable()
					&& ((paintingStyle == PropertyPanelEx.PAINTING_PREFERRED) || ((paintingStyle == PropertyPanelEx.STRING_PREFERRED) && (stringValue == null))))
			{
				   c = getPaintView();
			} else
			{
				if (stringValue == null)
				{
					// display info the values are different in place of class
					// name
					c =  getTextView(getTypeString(bean.getPropertyType()));// getTextView("");
				} else
				{
					c = getTextView(stringValue);
				}
			}
		}
		catch (Exception e)
		{
			// exception while getAsText () | isPaintable ()
			e.printStackTrace();
			c = getTextView(getExceptionString(e));
		}
		// we should never return null (dstrupl)
		if (c == null)
		{
			c = getTextView("null"); // NOI18N
		}
		return c;
	}

	/**
	 * Creates SheetButton with text representing current value of property.
	 */
	private SheetButtonEx getTextView(String str)
	{
		textView = new PropertySheetButton(str, plastic, plastic);
		textView.setFocusTraversable(true);
		if (sheetButtonListener != null)
		{
			textView.addSheetButtonListener(sheetButtonListener);
		}
		// XXX Read-only should be handled via enabling/disabling component
		// not via settin 'active' foreground color.
		if (canWrite)
		{
			textView.setActiveForeground(foregroundColor);
		} else
		{
		    if(bean instanceof ReadOnlyProperty)
		        textView.setActiveForeground(Color.BLUE);
		    else
			   textView.setActiveForeground(disabledColor);
		}
		return textView;
	}

	/**
	 * Creates SheetButton with PropertyShow representing current value of
	 * property.
	 */
	private SheetButtonEx getPaintView()
	{
		if (propertyShow == null)
		{
			propertyShow = new PropertyShowEx(editor);
		} else
		{
			propertyShow.setEditor(editor);
		}
		paintView = new PropertySheetButton();
		paintView.setFocusTraversable(true);
		// XXX Read-only should be handled via enabling/disabling component
		// not via settin 'active' foreground color.
		if (canWrite)
		{
			paintView.setActiveForeground(foregroundColor);
			propertyShow.setForeground(foregroundColor);
		} else
		{
			paintView.setActiveForeground(disabledColor);
			propertyShow.setForeground(disabledColor);
		}
		if (sheetButtonListener != null)
		{
			paintView.addSheetButtonListener(sheetButtonListener);
		}
		paintView.add(propertyShow);
		paintView.setPlastic(plastic);
		paintView.setToolTipText(getPanelToolTipText());
		return paintView;
	}

	// writer component ........................
	/**
	 * This method returns property value editor Component like input line (if
	 * property supports setAsText (String string) method) or some others.
	 * 
	 * @return property value editor Component
	 */
	private JComponent getWriterComponent()
	{
		if (editor == null) return getDisabledWriterComponent();
		String stringValue = null;
		//TODO:???
		boolean canEditAsText = true;
		/*
		Object customEditAsText = bean.getValue(PROP_CAN_EDIT_AS_TEXT);
		if (customEditAsText instanceof Boolean)
		{
			canEditAsText = ((Boolean) customEditAsText).booleanValue();
		}
		*/
		try
		{
			if (canRead)
				stringValue = editor.getAsText();
		}
		catch (Exception x)
		{
			processThrowable(x);
		}
		
		if (stringValue == null) stringValue = "";
		if ((stringValue == null)) 
			canEditAsText = false;
		
		getWriteComponentListener().setOldValue(editor.getValue());
		boolean existsCustomEditor = editor.supportsCustomEditor();
		if (!bean.canWrite())
		{
			if (existsCustomEditor)
			{
				return getInput(getDisabledWriterComponent(), true);// read-only
			} else
			{
				return getDisabledWriterComponent();
			}
		}
		
		boolean editable = false;
		String[] tags; // Tags
		if (((tags = editor.getTags()) != null))
		{
			return getInput(getInputTag(tags, stringValue, editable),
					existsCustomEditor);
		}
		
		if (canEditAsText)
		{
			return getInput(getInputLine((stringValue == null) ? "???"
					: stringValue, true), // NOI18N
					existsCustomEditor);
		}
		if (existsCustomEditor)
		{
			return getInput(getDisabledWriterComponent(), true);
		}
		return getDisabledWriterComponent();
	}

	/** */
	private JComponent getDisabledWriterComponent()
	{
		SheetButtonEx c = getReaderComponent();
		if (!bean.canWrite())
		{
			c.setActiveForeground(disabledColor);
		}
		c.addFocusListener(getWriteComponentListener());
		return c;
	}

	/**
	 * This is helper method for method getWriterComponent () which returns
	 * Panel with Choice in the "Center" and enhanced property editor open
	 * button on the "East". This Panel is then returned as property value
	 * editor Component.
	 * 
	 * @param tags
	 *            There are lines for Choice stored.
	 * @param selected
	 *            Line to be selected.
	 * 
	 * @return Choice Component
	 */
	private JComponent getInputTag(String[] tags, final String selected,
			boolean editable)
	{
		comboBox = new PropertyComboBox();
		comboBox.setBackground(Color.white);
		comboBox.setModel(new DefaultComboBoxModel(tags));
		comboBox.setMaximumRowCount(tags.length <= 12 ? tags.length : 8);
		if (selected != null)
		{
			for (int i = 0; i < tags.length; i++)
			{
				if (tags[i].equals(selected))
				{
					comboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		if (editable)
		{
			comboBox.setEditable(true);
			comboBox.setSelectedItem(selected);
			comboBox.getEditor().setItem(selected);
			comboBox.getEditor().getEditorComponent().addFocusListener(
					getWriteComponentListener());
			comboBox.getEditor().getEditorComponent().addKeyListener(
					getWriteComponentListener());
		}
		comboBox.addActionListener(getWriteComponentListener());
		comboBox.setToolTipText(getPanelToolTipText());
		return comboBox;
	}

	/** Attempts to advance to next item in comboBox. */
	void tryToSelectNextTag()
	{
		if (comboBox == null)
		{
			return;
		}
		setWriteState();
		int index = comboBox.getSelectedIndex();
		index++;
		if (index >= comboBox.getItemCount())
		{
			index = 0;
		}
		comboBox.setSelectedIndex(index);
	}

	/**
	 * This is helper method for method getWriterComponent () which returns
	 * Panel with TextField in the "Center" and enhanced property editor open
	 * button on the "East". This Panel is then returned as property value
	 * editor Component.
	 * 
	 * @param String
	 *            propertyStringValue initial property value.
	 * @param boolean
	 *            editable is true if string editing should be allowed.
	 * @param boolean
	 *            existsCustomEditor is true if enhanced property editor open
	 *            button should be showen.
	 * 
	 * @return Panel Component
	 */
	private JComponent getInputLine(final String propertyStringValue,
			boolean editable)
	{
		textField = new PropertyTextField();
		textField.addActionListener(getWriteComponentListener());
		textField.addKeyListener(getWriteComponentListener());
		textField.addFocusListener(getWriteComponentListener());
		textField.setText(propertyStringValue);
		textField.setEditable(editable);
		textField.setToolTipText(getPanelToolTipText());
		if (!isWriteState)
		{
			textField.selectAll();
		}
		return textField;
	}

	/** */
	private String getExceptionString(Throwable exception)
	{
		if (exception instanceof InvocationTargetException)
			exception = ((InvocationTargetException) exception)
					.getTargetException();
		return "<" + exception.getClass().getName() + ">"; // NOI18N
	}

	/** */
	private String getTypeString(Class clazz)
	{
		if (clazz == null)
		{
			return "No Class Property Editor";
		}
		if (clazz.isArray())
		{
			return "[ Array: " + // NOI18N
					getTypeString(clazz.getComponentType()) + "]"; // NOI18N
		}
		return "Class [" + clazz.getName() + "]"; // NOI18N
	}

	/**
	 * This is helper method for method getInput () and getInputTag () which
	 * returns Panel with enhanced property editor open button on the "East".
	 * 
	 * @param Component
	 *            leftComponent this component will be added to the "Center" of
	 *            this panel
	 * @param boolean
	 *            existsCustomEditor is true if enhanced property editor open
	 *            button should be shown.
	 * 
	 * @return <code>JPanel</code> component
	 */
	private JComponent getInput(Component leftComponent,
			boolean existsCustomEditor)
	{
		JPanel panel;
		if ((leftComponent == null) && (editor != null)
				&& (editor.isPaintable())
				&& (paintingStyle != PropertyPanelEx.ALWAYS_AS_STRING))
		{
			panel = new PropertyShowEx(editor);
		} else
		{
			panel = new JPanel();
		}
		panel.setLayout(new BorderLayout());
		if (leftComponent != null)
		{
			panel.add(leftComponent, BorderLayout.CENTER);
		}
		if (existsCustomEditor)
		{
			panel.add(getCustomizeButton(), BorderLayout.EAST);
		}
		panel.setToolTipText(getPanelToolTipText());
		panel.addFocusListener(getWriteComponentListener());
		panel.addKeyListener(getWriteComponentListener());
		return panel;
	}

	/** Gets <code>customizedButton</code>, so called 'three-dot-button'. */
	private SheetButtonEx getCustomizeButton()
	{
		if (customizeButton == null)
		{
			customizeButton = new SheetButtonEx("...", plastic, plastic); // NOI18N
			customizeButton.setFocusTraversable(true);
			Font currentFont = customizeButton.getFont();
			customizeButton.setFont(new Font(currentFont.getName(), currentFont
					.getStyle()
					| Font.BOLD, currentFont.getSize()));
			customizeButton.addFocusListener(getWriteComponentListener());
			customizeButton.addSheetButtonListener(new CustomizeListener());
			customizeButton.setToolTipText("Invokes Customizer");
		}
		// XXX Read-only should be handled via enabling/disabling component
		// not via settin 'active' foreground color.
		if (canWrite)
		{
			customizeButton.setActiveForeground(foregroundColor);
		} else
		{
		    customizeButton.setActiveForeground(disabledColor);
		}
		return customizeButton;
	}

	/**
	 * Processes <code>Exception</code> thrown from <code>setAsText</code>
	 * or <code>setValue</code> call on <code>editor</code>. Helper method.
	 */
	private void notifyExceptionWhileSettingProperty(Exception iae)
	{
		// partly bugfix #10791, notify exception to an user if PREF_INPUT_STATE
		// is set
		if (getPreferences() == 0 || getPreferences() == PREF_INPUT_STATE)
		{
			PropertyPanelEx.notifyUser(iae, bean.getName());
		} else
		{
		    iae.printStackTrace();
		}
	}

	/**
	 * Tries to find a localized message in the exception annotation or directly
	 * in the exception. If the message is found it is notified with user
	 * severity. If the message is not found the exception is notified with
	 * informational severity.
	 * 
	 * @param e
	 *            exception to notify
	 * @param propertyName
	 *            display name of property
	 */
	static void notifyUser(Exception e, String propertyName)
	{
		String userMessage = e.getLocalizedMessage();
		if ((userMessage == null) && (e instanceof InvocationTargetException))
		{
			userMessage = ((InvocationTargetException) e).getTargetException()
					.getLocalizedMessage();
		}
	    e.printStackTrace();
	}

	/**
	 * Processes <code>Throwable</code> thrown from <code>setAsText</code>
	 * or <code>setValue</code> call on <code>editor</code>. Helper method.
	 */
	private void processThrowable(Throwable throwable)
	{
		if (throwable instanceof ThreadDeath)
		{
			throw (ThreadDeath) throwable;
		}
		throwable.printStackTrace();
	}

	/**
	 * Sets whether or not all components within selected component will be
	 * enabled.
	 * 
	 * @param cmp
	 *            component to be enabled/disabled
	 * @param enabled
	 *            flag defining the action.
	 */
	private void setComponentEnabled(Component cmp, boolean enabled)
	{
		cmp.setEnabled(enabled);
		if (Container.class.isAssignableFrom(cmp.getClass()))
		{
			Container cont = (Container) cmp;
			Component[] comp = cont.getComponents();
			for (int i = 0; i < comp.length; i++)
			{
				comp[i].setEnabled(enabled);
				setComponentEnabled(comp[i], enabled);
			}
		}
	}

	/**
	 * Gets tooltip for this <code>PropertyPanel</code>.
	 * 
	 * @return tooltip retrieved from getToolTipText method or property class
	 *         type name if the former is <code>null</code> or
	 *         <code>null</code> if the model returns null as its class type
	 */
	private String getPanelToolTipText()
	{
		String toolTip = getToolTipText();
		if (toolTip != null) return toolTip;
		if (editor == null) return null;
		if (canRead)
			return editor.getAsText();
		else
			return null;
	}

	/**
	 * Paint sheet button with PropertyPanel backround and without border when
	 * used as cell renderer.
	 */
	private void updateSheetButtonVisually()
	{
		boolean flat = false; // true;
		// Object f = getClientProperty("flat"); // NOI18N
		// if (f instanceof Boolean)
		// flat = ((Boolean)f).booleanValue();
		if (readComponent != null)
		{
			readComponent.setFlat(flat);
			readComponent.setBackground(Color.white);
		}
		if (propertyShow != null) propertyShow.setBackground(Color.white);
		// if (customizeButton != null)
		// customizeButton.setFlat(flat);
	}

	/**
	 * Listener on textField(<code>JTextField</code>) or comboBox(<code>JComboBox</code>) -
	 * input line components controlled by this panel.
	 */
	private final class WriteComponentListener extends KeyAdapter implements
			ActionListener, FocusListener
	{
		/** Holds old value. */
		private Object oldValue;
		/** Task which handles action performed from combo box. */
		private Runnable comboActionTask;
		/**
		 * Task which requests default focus for enclosing class when setting to
		 * 'readState'.
		 */
		private Runnable requestFocusTask;

		/** Sets <code>oldValue</code>. */
		private void setOldValue(Object oldValue)
		{
			this.oldValue = oldValue;
		}

		/** Implements <code>ActionListener</code> interface. */
		public void actionPerformed(ActionEvent e)
		{
			if (!isWriteState) return;
			if (e.getSource() == comboBox)
			{
				// XXX #16101 - There is a problem with actions fired
				// from combo, we can't see the diff if it was caused by mouse
				// selection in popup or key navigation in popup, thus we plan
				// to handle this event later so we will know to decide what
				// was the cause of the action depening on it if popup is
				// visible or not (yes->mouse, no->key navigation).
				SwingUtilities.invokeLater(getComboActionTask());
			} else
			{
				if (e.getSource() == textField)
				{
					String val = textField.getText();
					changeValue(val);
				}
				prepareReadState();
			}
			PropertyPanelEx.this.getParent().repaint();
		}

		/** Overrides <code>KeyAdapter</code> superclass method. */
		public void keyPressed(KeyEvent e)
		{
			Object source = e.getSource();
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				if (// XXX ESC during navigation in popup resets the old value.
				comboBox != null
						&& comboBox.isPopupVisible()
						&& source instanceof Component
						&& SwingUtilities.isDescendingFrom((Component) source,
								comboBox))
				{
					resetOldValue();
				} else if (source == textField)
				{
					resetOldValue();
					e.consume();
				}
				prepareReadState();
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER
					&& comboBox != null
					&& source instanceof Component
					&& SwingUtilities.isDescendingFrom((Component) source,
							comboBox) && !comboBox.isEditable()
					&& ((preferences & PREF_INPUT_STATE) == 0))
			{
				changeValue((String) comboBox.getSelectedItem());
				prepareReadState();
			}
		}

		/** Implements <code>FocusListener</code> interface. */
		public void focusLost(final FocusEvent e)
		{
			if (!isWriteState)
			{
				return;
			}
			if ((comboBox != null) && (e.isTemporary()))
			{
				return;
			}
		
			boolean supportsCustom = (editor != null && editor
					.supportsCustomEditor());
			if (supportsCustom || comboBox != null)
			{
				// XXX
				// In this case we need to find out if the focus was
				// moved to component inside this PropetyPanel hierarchy,
				// currently to "..." customize button.
				// To figure out the new focus owner we have to skip the current
				// task.
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						// bugfix #18326 set value to editor w/o "read state" is
						// reset
						if (e.getSource().equals(textField))
						{
								changeValue(textField.getText());
								if (editor != null && editor.getAsText() != null
										&& !editor.getAsText().equals(
												textField.getText()))
								{
									// if a value is invalid then old value is
									// set back
									textField.setText(editor.getAsText());
								}
							
						}
						
						if (isWriteState)
						{
								setReadState();
						}
					}
				});
			} else
			{
				setReadState();
			}
		}

		/** Implements <code>FocusListener</code> interface. */
		public void focusGained(FocusEvent e)
		{
			if (e.getSource() == textField)
			{
				textField.selectAll();
			} else if (comboBox != null && comboBox.isEditable())
			{
				comboBox.getEditor().selectAll();
			}
		}

		/**
		 * Changes value if needed.
		 */
		public void changeValue(String s)
		{
			if (s != null && s.length() > 0 && (editor.getValue() == null // Fix #13339
					|| !(s.equals(editor.getAsText()))))
			{
				if (!setAsText(s))
				{
					resetOldValue();
				}
			}
		}

		/** Gets <code>comboActionTask</code>. */
		private synchronized Runnable getComboActionTask()
		{
			if (comboActionTask == null)
			{
				comboActionTask = new Runnable() {
					public void run()
					{
						// XXX For combos allow key navigation in popup.
						if (comboBox.isPopupVisible() || !isWriteState)
						{
							return;
						}
						changeValue((String) comboBox.getSelectedItem());
						prepareReadState();
					}
				};
			}
			return comboActionTask;
		}

		/** Gets <code>requestFocusTask</code>. */
		private synchronized Runnable getRequestFocusTask()
		{
			if (requestFocusTask == null)
			{
				requestFocusTask = new Runnable() {
					public void run()
					{
						Component focused = getDefaultFocusComponent(PropertyPanelEx.this);
						if (focused != null) focused.requestFocus();
					}
				};
			}
			return requestFocusTask;
		}

		/**
		 * Prepares 'readState'. Sets 'readState' and requests default focus for
		 * changed component tree of enclosing <code>PropertyPanel</code>.
		 */
		private void prepareReadState()
		{
			boolean hasFocus = SwingUtilities
					.findFocusOwner(PropertyPanelEx.this) != null;
			setReadState();
			if (hasFocus)
			{
				// XXX direct call doesn't work - #16052.
				SwingUtilities.invokeLater(getRequestFocusTask());
			}
		}

		/** Resets 'read state' on focus lost change. Helper method. */
		private void resetReadState(FocusEvent e)
		{
			// bugfix #18326 change value if "..." customize button is left
			if (e.getSource() == customizeButton && textField != null)
			{
				changeValue(textField.getText());
			} else if (e.getSource() == textField)
			{
				changeValue(textField.getText());
			} else if ((comboBox != null) && (comboBox.isEditable()))
			{
				changeValue((String) comboBox.getEditor().getItem());
			}
			setReadState();
		}

		/** Resets specified value to <code>editor</code>. */
		private void resetOldValue()
		{
			try
			{
				// don't set old value if there are different values
				//TODO:???
				//if (!differentValues) 
					editor.setValue(oldValue);
			}
			catch (IllegalArgumentException iae)
			{
				notifyExceptionWhileSettingProperty(iae);
			}
			catch (RuntimeException throwable)
			{
				processThrowable(throwable);
			}
		}

		/**
		 * Sets as text.
		 * 
		 * @value <code>String</code> value which is possible to convert to
		 *        the type of property.
		 * @returns <code>true</code> if succesfull, <code>false</code>
		 *          otherwise
		 */
		private boolean setAsText(String value)
		{
			System.out.println("WriteCompList - setAsText: " + value);
			try
			{
				editor.setAsText(value);
				bean.setValue(editor.getValue());
				return true;
			}
			catch (IllegalArgumentException iae)
			{
				notifyExceptionWhileSettingProperty(iae);
			}
			catch (Exception throwable)
			{
				processThrowable(throwable);
			}
			return false;
		}
	} // End of class WriteComponentListener.

	/** Listener on <code>readComponent</code>. */
	private final class ReadComponentListener implements SheetButtonListenerEx
	{
		/**
		 * Invoked when the mouse exits a component.
		 */
		public void sheetButtonExited(ActionEvent e)
		{
		}

		/**
		 * Invoked when the mouse has been clicked on a component.
		 */
		public void sheetButtonClicked(ActionEvent e)
		{
			if (SheetButtonEx.RIGHT_MOUSE_COMMAND.equals(e.getActionCommand())
					|| isWriteState)
			{
				return;
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					setWriteState();
				}
			});
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void sheetButtonEntered(ActionEvent e)
		{
		}
	} // End of class ReadComponentListener.

	/** Listener on <code>customizeButton</code>. */
	private final class CustomizeListener implements SheetButtonListenerEx
	{
		/**
		 * Invoked when the mouse exits a component. Dummy implementation. Does
		 * nothing.
		 */
		public void sheetButtonExited(ActionEvent e)
		{
		}

		/**
		 * Invoked when the mouse has been clicked on a component.
		 */
		public void sheetButtonClicked(ActionEvent e)
		{
			//String title = bean.getName();
			customDialogShown = true;
			// bugfix #18326 editor's value is taken from textField
			if (textField != null)
			{
				try
				{
					if (editor.getValue() == null // Fix #13339
							|| !(textField.getText().equals(editor.getAsText())))
					{
						editor.setAsText(textField.getText());
					}
				}
				catch (Exception ite)
				{
					// old value back will be set back
				}
			}
			PropertyDialogManager pdm = new PropertyDialogManager(
					//"PropertyEditor: " + ((title == null) ? "" : title + " ")+ bean.getPropertyType()
			        bean.getPropertyType().getName(), true, editor, bean);
			pdm.getDialog().setVisible(true); 
			// update editor from the model
			if (canRead)
			{
				try
				{
					ignoreEvents = true;
					Object newValue = bean.getValue();
					Object oldValue = editor.getValue();
					// test if newValue is not equal to oldValue
					if ((newValue != null && !newValue.equals(oldValue))
							|| (newValue == null && oldValue != null))
					{
						editor.setValue(newValue);
					}
				}
				catch (Exception ite)
				{
					processThrowable(ite);
				}
				finally
				{
					ignoreEvents = false;
				}
			}
			//force the update of the read component 
			isReadState = false;
			reset();
			requestFocus();
			customDialogShown = false;
		}

		/**
		 * Invoked when the mouse enters a component. Dummy implementation. Does
		 * nothing.
		 */
		public void sheetButtonEntered(ActionEvent e)
		{
		}
	} // End of class CustomizeListener.

	/**
	 * Property change listener for the model.
	 */
	private class ModelListener implements PropertyChangeListener
	{
		/** Property was changed. */
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (AbstractProperty.PROP_VALUE.equals(evt.getPropertyName()))
			{
				if (editor != null)
				{
					/*try
					{
						ignoreEvents = true;
						Object newValue = evt.getNewValue();//bean.getValue();
						Object oldValue =  evt.getOldValue();//editor.getValue();
						// test if newValue is not equal to oldValue
						if ((newValue != null && !newValue.equals(oldValue))
								|| (newValue == null && oldValue != null))
						{
							editor.setValue(newValue);
						}
						*/
						refresh();
					/*}
					catch (InvocationTargetException e)
					{
						notifyExceptionWhileSettingProperty(e);
					}
					finally
					{
						ignoreEvents = false;
					}
					*/
				}
			}
		}
	} // End of class ModelListener.

	
	private class PropertyTextField extends JTextField
	{
		// XXX workaround of jdkbug #4670767 in jdk1.4
		// JTextField filters the new lines if the property filterNewlines is
		// TRUE
		// which is TRUE as default
		// there is a problem when in PropertyTextFiled is set a multi-line
		// string
		// then single-line string is get back, see issue 22450
		public void setDocument(Document doc)
		{
			super.setDocument(doc);
			if (doc != null)
			{
				doc.putProperty("filterNewlines", null);
			}
		}
	}

	private class PropertyComboBox extends JComboBox
	{
	}

	private class PropertySheetButton extends SheetButtonEx
	{
		PropertySheetButton()
		{
		}

		PropertySheetButton(String aLabel, boolean isPlasticButton,
				boolean plasticActionNotify)
		{
			super(aLabel, isPlasticButton, plasticActionNotify);
			setBackground(Color.white);
		}
	}
}
