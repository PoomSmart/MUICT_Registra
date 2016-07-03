import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

public class ScannerDialog extends JFrame {

	private static final long serialVersionUID = 2561998L;

	private Vector<Integer> IDs;
	private static final String pMUICTID = "\\d\\d88\\d\\d\\d";
	private static final Pattern pDelAtIndex = Pattern.compile("del(\\d+)");

	private ScannerListDialog list;
	private Map<Integer, Student> students;

	private JLabel statusLabel;
	private JTextField field;
	private JButton confirmRegularBtn;
	private JButton appendRegularBtn;
	private JButton confirmNotHereBtn;
	private JButton appendNotHereBtn;

	private void destroyEverything() {
		this.setVisible(false);
		this.dispose();
		list.setVisible(false);
		list.dispose();
	}

	private void actionPerformWriteForType(String title, String confirmString, String fileExistedString,
			ScannerSaver.Type type) {
		try {
			int result = JOptionPane.showConfirmDialog(null, confirmString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				ScannerSaver.doneAddingCode(IDs, false, type);
			}
		} catch (FileAlreadyExistsException ex) {
			int result = JOptionPane.showConfirmDialog(null, fileExistedString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					ScannerSaver.doneAddingCode(IDs, false, type, true);
				} catch (IOException ex2) {
					ex2.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void actionPerformAppendForType(String title, String confirmString, ScannerSaver.Type type) {
		try {
			int result = JOptionPane.showConfirmDialog(null, confirmString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				ScannerSaver.doneAddingCode(IDs, true, type);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public ScannerDialog(Map<Integer, Student> students) {
		this.setTitle("Scanner" + (Main.test ? " (Test Mode)" : ""));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.setSize(400, 200);
		field = new JTextField(1);
		field.setHorizontalAlignment(JTextField.CENTER);
		AbstractDocument document = (AbstractDocument) (field.getDocument());
		document.setDocumentFilter(new DocumentFilter());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.out.println("Exit");
				System.exit(0);
			}
		});
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				validate();
			}

			public void removeUpdate(DocumentEvent e) {
				validate();
			}

			public void insertUpdate(DocumentEvent e) {
				validate();
			}

			public void validate() {
				confirmRegularBtn.setEnabled(!IDs.isEmpty());
				appendRegularBtn.setEnabled(!IDs.isEmpty());
				confirmNotHereBtn.setEnabled(!IDs.isEmpty());
				appendNotHereBtn.setEnabled(!IDs.isEmpty());
				String text = field.getText();
				boolean shouldCleanup = false;
				boolean removeLabel = false;
				Matcher m;
				if (text.equals("flush")) {
					list.removeAll();
					IDs.removeAllElements();
					shouldCleanup = removeLabel = true;
				} else if (text.equals("dellast")) {
					list.removeLast();
					IDs.remove(IDs.lastElement());
					shouldCleanup = removeLabel = true;
				} else if ((m = (pDelAtIndex.matcher(text))).find()) {
					int idx = Integer.parseInt(m.group(1));
					list.removeAtIndex(idx);
					removeLabel = idx == IDs.size() - 1;
					if (idx < IDs.size())
						IDs.remove(idx);
					shouldCleanup = true;
				} else {
					String ID = null;
					if (!Main.test) {
						if (text.length() != 14)
							return;
						ID = text.substring(6, 6 + 7);
						if (!ID.matches(pMUICTID))
							return;
					} else {
						if (!text.matches(pMUICTID))
							return;
						ID = text;
					}
					if (ID != null) {
						System.out.println("-> " + ID);
						Integer iID = Integer.parseInt(ID);
						if (!students.containsKey(iID)) {
							System.out.println("ID does not exist in database: " + ID);
							setStatus("Not Added: " + ID);
						} else {
							if (IDs.contains(iID)) {
								System.out.println("ID already existed: " + ID);
								setStatus("Not Added: " + ID);
							} else {
								IDs.add(iID);
								list.addID(iID);
								setStatus("Added " + ID);
							}
						}
						shouldCleanup = true;
					}
				}
				if (removeLabel)
					setStatus("");
				if (shouldCleanup) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							field.setText("");
						}
					});
				}
			}

		});
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Enter pressed");
				destroyEverything();
			}
		});
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.ipady = 5;
		this.getContentPane().add(field, c);

		JLabel des = new JLabel("Detecting scanned code", JLabel.CENTER);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 2;
		c.ipady = 10;
		this.getContentPane().add(des, c);
		
		statusLabel = new JLabel(" ", JLabel.CENTER);
		statusLabel.setForeground(Color.gray);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.ipady = 10;
		this.getContentPane().add(statusLabel, c);

		confirmRegularBtn = new JButton("Confirm regular data");
		confirmRegularBtn.setEnabled(false);
		confirmRegularBtn.setForeground(Color.blue);
		confirmRegularBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformWriteForType("Scanner", "Confirm writing data?", "File already existed, continue?",
						ScannerSaver.Type.REGULAR);
			}
		});
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		this.getContentPane().add(confirmRegularBtn, c);

		appendRegularBtn = new JButton("Append regular data");
		appendRegularBtn.setEnabled(false);
		appendRegularBtn.setForeground(Color.red);
		appendRegularBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformAppendForType("Scanner", "Confirm append data?", ScannerSaver.Type.REGULAR);
			}
		});
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		this.getContentPane().add(appendRegularBtn, c);

		confirmNotHereBtn = new JButton("Confirm absence data");
		confirmNotHereBtn.setEnabled(false);
		confirmNotHereBtn.setForeground(Color.blue);
		confirmNotHereBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformWriteForType("Scanner", "Confirm writing absence data?", "File already existed, continue?",
						ScannerSaver.Type.NOTHERE);
			}
		});
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		this.getContentPane().add(confirmNotHereBtn, c);

		appendNotHereBtn = new JButton("Append absence data");
		appendNotHereBtn.setEnabled(false);
		appendNotHereBtn.setForeground(Color.red);
		appendNotHereBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformAppendForType("Scanner", "Confirm append absence data?", ScannerSaver.Type.NOTHERE);
			}
		});
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		this.getContentPane().add(appendNotHereBtn, c);

		this.setLocationRelativeTo(null);
		this.IDs = new Vector<Integer>();
		this.students = students;
	}

	public Vector<Integer> getIDs() {
		return IDs;
	}

	public void setStatus(String status) {
		this.statusLabel.setText(status);
	}

	public ScannerListDialog getList() {
		return list;
	}

	public void setList(ScannerListDialog list) {
		this.list = list;
	}

	public JTextField getField() {
		return field;
	}

}
