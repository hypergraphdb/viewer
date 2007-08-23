package org.hypergraphdb.viewer.painter.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.hypergraphdb.viewer.dialogs.DialogDescriptor;
import org.hypergraphdb.viewer.dialogs.DialogDisplayer;
import org.hypergraphdb.viewer.dialogs.NotifyDescriptor;
import org.hypergraphdb.viewer.util.GUIUtilities;
import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

public class FontEditor extends AbstractPropertyEditor
{
	static String[] fonts;
	static
	{
		try
		{
			fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	}
	static final Integer[] sizes = new Integer[] { new Integer(3),
			new Integer(5), new Integer(8), new Integer(10), new Integer(12),
			new Integer(14), new Integer(18), new Integer(24), new Integer(36),
			new Integer(48) };
	static final String[] styles = new String[] {
			"Plain", "Bold","Italic", "BoldItalic"};
	private CellRenderer label;
	private JButton button;
	private Font font;
	

	public FontEditor()
	{
		editor = new JPanel(new PercentLayout(0, 0));
		((JPanel) editor).add("*", label = new CellRenderer());
		label.setOpaque(false);
		((JPanel) editor)
				.add(button = com.l2fprod.common.swing.ComponentFactory.Helper
						.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				selectShape();
			}
		});
		((JPanel) editor).setOpaque(false);
	}

	public Object getValue()
	{
		return font;
	}

	public void setValue(Object value)
	{
		font = (Font) value;
		label.setValue(font);
	}

	protected void selectShape()
	{
		//FontChooser chooser = new FontChooser((Font) getValue());
		FontPanel panel = new FontPanel(); 
		DialogDescriptor d = new DialogDescriptor(
				GUIUtilities.getFrame(editor), panel, "Select Font");
		d.setModal(true);
		d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
		if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION)
		{
			Font f = panel.resultingFont;
			if (f != null)
			{
				Font oldColor = font;
				label.setValue(f);
				font = f;
				firePropertyChange(oldColor, f);
			}
		}
	}
	
	public static void paintStaticValue(Font font, Graphics g, Rectangle rectangle)
	{
		Font f = g.getFont();
		if (font == null) return;
		Font paintFont = font;
		FontMetrics fm = g.getFontMetrics(paintFont);
		if (fm.getHeight() > rectangle.height)
		{
			paintFont = font.deriveFont(12.0F);
			fm = g.getFontMetrics(paintFont);
		}
		g.setFont(paintFont);
		g.drawString(makeFontDescr(font), rectangle.x, rectangle.y
				+ (rectangle.height - fm.getHeight()) / 2 + fm.getAscent());
		g.setFont(f);
	}
	
	static String getStyleName(int i)
	{
		if ((i & Font.BOLD) > 0)
			if ((i & Font.ITALIC) > 0)
				return "BoldItalic";
			else
				return "Bold";
		else if ((i & Font.ITALIC) > 0)
			return "Italic";
		else
			return "Plain";
	}
	
	static String makeFontDescr(Font font){
		return font.getName() + " " + font.getSize() + " "
		+ getStyleName(font.getStyle());
	}

	public static class CellRenderer extends DefaultCellRenderer implements
			TableCellRenderer
	{
		protected String convertToString(Object value)
		{
			if (value == null) return null;
			return  makeFontDescr((Font)value);
		}

		protected Icon convertToIcon(Object value)
		{
			return null;
		}

		public Component getTableCellRendererComponent(JTable table,
				final Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			JPanel pp = new JPanel() {
				public Dimension getPreferredSize()
				{
					return new Dimension(150, 60);
				}

				public void paint(Graphics g)
				{
					paintStaticValue((Font) value, g, new Rectangle(0, 0, this
							.getSize().width - 1, this.getSize().height - 1));
				}
			};
			return pp;
		}
	}
	
	class FontPanel extends JPanel
	{
		JTextField tfFont;
		JTextField tfStyle;
		JTextField tfSize;
		JList lFont;
		JList lStyle;
		JList lSize;
		Font resultingFont; 
		
		FontPanel()
		{
			super();
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(12, 12, 0, 11));
			lFont = new JList(fonts);
			lStyle = new JList(styles);
			lSize = new JList(sizes);
			tfSize = new JTextField("" + FontEditor.this.font.getSize());
			GridBagLayout la = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			setLayout(la);
			c.gridwidth = 1;
			c.weightx = 1.0;
			c.insets = new Insets(0, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			JLabel l = new JLabel("Font Face");
			l.setLabelFor(lFont);
			la.setConstraints(l, c);
			add(l);
			c.insets = new Insets(0, 5, 0, 0);
			l = new JLabel("Font Style");
			l.setLabelFor(lStyle);
			la.setConstraints(l, c);
			add(l);
			c.insets = new Insets(0, 5, 0, 0);
			c.gridwidth = GridBagConstraints.REMAINDER;
			l = new JLabel("Size");
			l.setLabelFor(tfSize);
			la.setConstraints(l, c);
			add(l);
			c.insets = new Insets(5, 0, 0, 0);
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			tfFont = new JTextField(FontEditor.this.font.getName());
			tfFont.setEnabled(false);
			la.setConstraints(tfFont, c);
			add(tfFont);
			c.insets = new Insets(5, 5, 0, 0);
			tfStyle = new JTextField(getStyleName(FontEditor.this.font
					.getStyle()));
			tfStyle.setEnabled(false);
			la.setConstraints(tfStyle, c);
			add(tfStyle);
			c.insets = new Insets(5, 5, 0, 0);
			c.gridwidth = GridBagConstraints.REMAINDER;
			tfSize.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER) setValue();
				}
			});
			tfSize.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt)
				{
					setValue();
				}
			});
			la.setConstraints(tfSize, c);
			add(tfSize);
			c.gridwidth = 1;
			c.insets = new Insets(5, 0, 0, 0);
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			c.weighty = 1.0;
			lFont.setVisibleRowCount(5);
			lFont.setSelectedValue(FontEditor.this.font.getName(), true);
			lFont.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e)
				{
					if (!lFont.isSelectionEmpty())
					{
						if (fonts.length > 0)
						{
							int i = lFont.getSelectedIndex();
							tfFont.setText(fonts[i]);
							setValue();
						}
					}
				}
			});
			JScrollPane sp = new JScrollPane(lFont);
			sp
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			la.setConstraints(sp, c);
			add(sp);
			lStyle.setVisibleRowCount(5);
			lStyle.setSelectedValue(getStyleName(FontEditor.this.font
					.getStyle()), true);
			lStyle.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e)
				{
					if (!lStyle.isSelectionEmpty())
					{
						int i = lStyle.getSelectedIndex();
						tfStyle.setText(styles[i]);
						setValue();
					}
				}
			});
			sp = new JScrollPane(lStyle);
			sp
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			c.insets = new Insets(5, 5, 0, 0);
			la.setConstraints(sp, c);
			add(sp);
			c.gridwidth = GridBagConstraints.REMAINDER;
			lSize.getAccessibleContext().setAccessibleName(
					tfSize.getAccessibleContext().getAccessibleName());
			lSize.setVisibleRowCount(5);
			updateSizeList(FontEditor.this.font.getSize());
			lSize.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e)
				{
					if (!lSize.isSelectionEmpty())
					{
						int i = lSize.getSelectedIndex();
						tfSize.setText("" + sizes[i]);
						setValue();
					}
				}
			});
			sp = new JScrollPane(lSize);
			sp
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			c.insets = new Insets(5, 5, 0, 0);
			la.setConstraints(sp, c);
			add(sp);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weighty = 2.0;
			JPanel p = new JPanel(new BorderLayout());
			p.setBorder(new TitledBorder(" Preview "));
			JPanel pp = new JPanel() {
				public Dimension getPreferredSize()
				{
					return new Dimension(150, 60);
				}

				public void paint(Graphics g)
				{
					paintStaticValue(
							resultingFont, g, new Rectangle(0, 0, this
							.getSize().width - 1, this.getSize().height - 1));
				}
			};
			p.add("Center", pp);
			c.insets = new Insets(12, 0, 0, 0);
			la.setConstraints(p, c);
			add(p);
		}

		public Dimension getPreferredSize()
		{
			return new Dimension(400, 250);
		}

		private void updateSizeList(int size)
		{
			if (java.util.Arrays.asList(sizes).contains(new Integer(size)))
				lSize.setSelectedValue(new Integer(size), true);
			else
				lSize.clearSelection();
		}

		void setValue()
		{
			int size = 12;
			try
			{
				size = Integer.parseInt(tfSize.getText());
				updateSizeList(size);
			}
			catch (NumberFormatException e)
			{
				return;
			}
			int i = lStyle.getSelectedIndex();
			int ii = Font.PLAIN;
			switch (i)
			{
			case 0:
				ii = Font.PLAIN;
				break;
			case 1:
				ii = Font.BOLD;
				break;
			case 2:
				ii = Font.ITALIC;
				break;
			case 3:
				ii = Font.BOLD | Font.ITALIC;
				break;
			}
			resultingFont =	new Font(tfFont.getText(), ii, size);
			invalidate();
			java.awt.Component p = getParent();
			if (p != null)
				p.validate();
			repaint();
		}
	}
}