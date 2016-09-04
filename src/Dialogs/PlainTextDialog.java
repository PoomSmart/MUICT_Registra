package Dialogs;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.apache.commons.io.FileUtils;

import Utilities.SpringUtilities;
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

		JPanel self = new JPanel();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		JPanel form = new JPanel(new SpringLayout());

		textArea = new JTextArea();
		textArea.setText(initialText = text);
		textArea.setEditable(editable);

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(textArea.getWidth(), height));
		scroll.setWheelScrollingEnabled(true);
		form.add(scroll);
		SpringUtilities.makeCompactGrid(form, 1, 1, margin, margin, margin, margin);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				_windowClosing();
			}
		});
		self.add(form);
		this.setContentPane(self);
		this.setResizable(true);
	}

	public void _windowClosing() {
		if (Logger.currentDialog != null && isLog)
			Logger.currentDialog = null;
		if (editable && filePath != null) {
			String newText = textArea.getText();
			if (!initialText.equals(newText)) {
				int result = JOptionPane.showConfirmDialog(null, "Apply changes?", title, JOptionPane.YES_NO_OPTION);
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
