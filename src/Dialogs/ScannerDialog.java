package Dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
import java.util.TreeMap;
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

import Main.Main;
import Objects.Constants;
import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DateUtils;
import Workers.ScannerSaver;

public class ScannerDialog extends JFrame {

	private static final long serialVersionUID = 2561998L;

	private Vector<Integer> IDs;
	private static final Pattern pDelAtIndex = Pattern.compile("(\\d+)del");

	private ScannerListDialog list;

	private JLabel desLabel;
	private JLabel statusLabel;
	private JTextField field;
	private JButton confirmRegularBtn;
	private JButton appendRegularBtn;

	private Map<Integer, Student> students;
	private Map<Integer, Student> currentPresentStudents;

	private void destroyEverything() {
		this.setVisible(false);
		this.dispose();
		list.setVisible(false);
		list.dispose();
	}

	private void cleanup() {
		IDs.removeAllElements();
		list.removeAll();
		setStatus(" ");
		updateButtonsState();
	}

	private void cleanupTextField() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				field.setText("");
			}
		});
	}

	private void resolveIDDuplicates() {
		for (Integer ID : currentPresentStudents.keySet())
			IDs.remove(ID);
	}

	private void reloadCurrentPresentStudents() {
		StudentTable table = new StudentTable(students, 0);
		currentPresentStudents = table.presentStudentMapForDate(DateUtils.getCurrentDate());
		if (currentPresentStudents == null)
			currentPresentStudents = new TreeMap<Integer, Student>();
	}

	private void actionPerformWriteForType(String title, String confirmString, String fileExistedString,
			CommonUtils.FileType type) {
		try {
			int result = JOptionPane.showConfirmDialog(null, confirmString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				resolveIDDuplicates();
				ScannerSaver.doneAddingCodes(IDs, false, type);
				cleanup();
				reloadCurrentPresentStudents();
			}
		} catch (FileAlreadyExistsException ex) {
			int result = JOptionPane.showConfirmDialog(null, fileExistedString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					resolveIDDuplicates();
					ScannerSaver.doneAddingCodes(IDs, false, type, true);
					cleanup();
					reloadCurrentPresentStudents();
				} catch (IOException ex2) {
					ex2.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void actionPerformAppendForType(String title, String confirmString, CommonUtils.FileType type) {
		try {
			int result = JOptionPane.showConfirmDialog(null, confirmString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				resolveIDDuplicates();
				ScannerSaver.doneAddingCodes(IDs, true, type);
				cleanup();
				reloadCurrentPresentStudents();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void updateButtonsState() {
		boolean isIDNotEmpty = !IDs.isEmpty();
		confirmRegularBtn.setEnabled(isIDNotEmpty);
		appendRegularBtn
				.setEnabled(isIDNotEmpty && CommonUtils.fileExistsAtPath(CommonUtils.filePath(FileType.REGULAR)));
	}

	public ScannerDialog(Map<Integer, Student> students) {
		this.students = students;
		this.setTitle(CommonUtils.realTitle(Constants.SCANNER_DIALOG_TITLE));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.setSize(400, 150);
		field = new JTextField();
		field.setHorizontalAlignment(JTextField.CENTER);
		AbstractDocument document = (AbstractDocument) (field.getDocument());
		document.setDocumentFilter(new DocumentFilter());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
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
				updateButtonsState();
				String text = field.getText();
				boolean shouldCleanup = false;
				boolean removeLabel = false;
				Matcher m;
				if (text.equals("sort")) {
					list.sort();
					shouldCleanup = true;
				} else if (Constants.flushStrings.contains(text)) {
					cleanup();
					shouldCleanup = true;
				} else if (Constants.delLastStrings.contains(text)) {
					list.removeLast();
					if (!IDs.isEmpty())
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
					Integer ID = -1;
					int stringLength = text.length();
					if (!Main.test) {
						if (stringLength == 14) {
							ID = CommonUtils.getID(text.substring(6, 6 + 7));
							if (ID == -1) {
								cleanupTextField();
								return;
							}
						}
					} else {
						if (stringLength == 7) {
							ID = CommonUtils.getID(text);
							if (ID == -1) {
								cleanupTextField();
								return;
							}
						}
					}
					if (ID != -1) {
						System.out.println("-> " + ID);
						if (!students.containsKey(ID)) {
							System.out.println("ID does not exist in database: " + ID);
							setStatus("Not Added: " + ID);
						} else {
							if (IDs.contains(ID)) {
								System.out.println("ID already existed: " + ID);
								setStatus("Not Added: " + ID);
							} else {
								IDs.add(ID);
								list.addID(ID);
								setStatus("Added: " + ID);
							}
						}
						shouldCleanup = true;
					}
				}
				if (removeLabel)
					setStatus(" ");
				if (shouldCleanup)
					cleanupTextField();
			}

		});
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Enter pressed");
				destroyEverything();
			}
		});
		field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				desLabel.setText(Constants.SCANNER_DETECTING_MESSAGE);
			}

			public void focusLost(FocusEvent e) {
				desLabel.setText(" ");
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = c.gridy = 0;
		c.gridwidth = 5;
		c.ipady = 5;
		getContentPane().add(field, c);

		desLabel = new JLabel(Constants.SCANNER_DETECTING_MESSAGE, JLabel.CENTER);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 5;
		c.ipady = 10;
		getContentPane().add(desLabel, c);

		statusLabel = new JLabel(" ", JLabel.CENTER);
		statusLabel.setForeground(Color.gray);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 5;
		c.ipady = 10;
		getContentPane().add(statusLabel, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.ipady = 0;
		JLabel regularLabel = new JLabel("Present:");
		regularLabel.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(regularLabel, c);

		confirmRegularBtn = new JButton("Confirm");
		confirmRegularBtn.setEnabled(false);
		confirmRegularBtn.setForeground(Color.blue);
		confirmRegularBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformWriteForType(Constants.SCANNER_DIALOG_TITLE, Constants.CONFIRM_WRITE_REGULAR,
						Constants.CONFIRM_OVERWRITE, CommonUtils.FileType.REGULAR);
			}
		});
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(3, 5, 3, 5);
		getContentPane().add(confirmRegularBtn, c);

		appendRegularBtn = new JButton("Append");
		appendRegularBtn.setEnabled(false);
		appendRegularBtn.setForeground(Color.red);
		appendRegularBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformAppendForType(Constants.SCANNER_DIALOG_TITLE, Constants.CONFIRM_APPEND_REGULAR,
						CommonUtils.FileType.REGULAR);
			}
		});
		c.gridx = 3;
		c.gridy = 3;
		c.gridwidth = 2;
		getContentPane().add(appendRegularBtn, c);

		CommonUtils.setRelativeCenter(this, 0, -50);
		this.setResizable(false);
		this.IDs = new Vector<Integer>();

		reloadCurrentPresentStudents();
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