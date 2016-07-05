package Dialogs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.apache.commons.io.FileUtils;

import Utilities.CommonUtils;

public class PlainTextDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private String filePath;
	private String initialText;

	public PlainTextDialog(String title, int width, int height, int margin, String text, boolean editable,
			String filePath) {
		this.filePath = filePath;
		this.setTitle(title);
		this.setSize(width, height);
		CommonUtils.setCenter(this);

		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(layout);

		textArea = new JTextArea();
		textArea.setText(initialText = text);
		textArea.setEditable(editable);

		layout.putConstraint(SpringLayout.WEST, textArea, margin, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.EAST, textArea, -margin, SpringLayout.EAST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, textArea, margin, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.SOUTH, textArea, -margin, SpringLayout.SOUTH, getContentPane());

		getContentPane().add(textArea);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
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
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
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
