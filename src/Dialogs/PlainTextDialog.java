package Dialogs;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.apache.commons.io.FileUtils;

import Utilities.WindowUtils;
import Workers.Logger;

public class PlainTextDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private final String filePath;
	private final String title;
	private String initialText;
	private final boolean editable;
	public boolean isLog;

	public PlainTextDialog(String title, int width, int height, int margin, String text, boolean editable,
			String filePath) {
		this.title = title;
		this.filePath = filePath;
		this.setTitle(title);
		this.setSize(width, height);
		this.editable = editable;
		WindowUtils.setCenter(this);

		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(layout);

		textArea = new JTextArea();
		textArea.setText(initialText = text);
		textArea.setEditable(editable);

		layout.putConstraint(SpringLayout.WEST, textArea, margin, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.EAST, textArea, -margin, SpringLayout.EAST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, textArea, margin, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.SOUTH, textArea, -margin, SpringLayout.SOUTH, getContentPane());

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(width - 5, height));
		getContentPane().add(scroll);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				_windowClosing();
			}
		});
		this.setResizable(true);
	}
	
	public void _windowClosing() {
		if (Logger.currentDialog != null && isLog)
			Logger.currentDialog = null;
		if (editable && filePath != null) {
			String newText = textArea.getText();
			if (!initialText.equals(newText)) {
				int result = JOptionPane.showConfirmDialog(null, "Apply changes?", title,
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					try {
						FileUtils.write(new File(filePath), newText);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Error saving file to Path: " + filePath);
					}
				}
			}
		}
	}

	public PlainTextDialog(String title, int width, int height, int margin, String text, boolean editable) {
		this(title, width, height, margin, text, editable, null);
	}

	public void setText(String text) {
		textArea.setText(text);
	}

	public String getFilePath() {
		return filePath;
	}

}
