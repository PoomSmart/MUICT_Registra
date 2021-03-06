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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

import org.apache.commons.io.FileUtils;

import MainApp.MainApp;
import Objects.Constants;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DBUtils;
import Utilities.DateUtils;
import Utilities.WindowUtils;
import Workers.Logger;
import Workers.ScannerSaver;

public class ScannerDialog extends JFrame {

	private static final long serialVersionUID = 2561998L;

	public static ScannerDialog activeScanner = null;

	private Vector<Integer> IDs;
	private static final Pattern pDelAtIndex = Pattern.compile("(\\d+)del");

	private ScannerListDialog list;

	private JLabel desLabel;
	private JLabel statusLabel;
	private JTextField field;
	private JButton confirmRegularBtn;
	private JButton appendRegularBtn;

	private boolean shouldCleanup;
	private boolean removeLabel;

	private Map<Integer, Student> currentPresentStudents;

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

	public void reloadCurrentPresentStudents() {
		currentPresentStudents = DBUtils.getCurrentPresentStudents();
	}

	public static void random(int maxCount) {
		if (activeScanner != null) {
			Random r = new Random();
			Vector<Integer> presentIDs = new Vector<Integer>(DBUtils.getCurrentPresentStudents().keySet());
			Vector<Integer> oldIDs = new Vector<Integer>();
			for (Integer ID : presentIDs)
				oldIDs.add(ID);
			Set<Integer> randomIDs = new HashSet<Integer>();
			Integer rID;
			while (maxCount-- != 0) {
				do {
					rID = 88001 + r.nextInt(300) + DateUtils.studentYearInt() * 100000;
				} while ((randomIDs.contains(rID) || oldIDs.contains(rID)) && oldIDs.size() != MainApp.db.size()
						&& !MainApp.db.containsKey(rID));
				randomIDs.add(rID);
			}
			try {
				// Clean up leave.csv before adding randomized list
				FileUtils.write(CommonUtils.fileFromType(FileType.NOTHERE), "");
				addingCode(activeScanner, new Vector<Integer>(randomIDs), false, FileType.REGULAR, true, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addingCode(ScannerDialog dialog, Vector<Integer> IDs, boolean append, FileType type,
			boolean force, boolean reload) throws IOException {
		dialog.resolveIDDuplicates();
		ScannerSaver.doneAddingCodes(IDs, append, type, force);
		if (reload) {
			dialog.cleanup();
			dialog.reloadCurrentPresentStudents();
		}
	}

	private void updateDialog() {
		if (removeLabel)
			setStatus(" ");
		if (shouldCleanup)
			cleanupTextField();
	}

	private void fixUpLeaveStudents() {
		// We would update leave.csv if we also found any ID there, like they leave and then come back in reality
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				List<String> presentIDs = null;
				List<String> leaveStudents = null;
				try {
					presentIDs = FileUtils.readLines(CommonUtils.fileFromType(CommonUtils.FileType.REGULAR));
					leaveStudents = FileUtils.readLines(CommonUtils.fileFromType(CommonUtils.FileType.NOTHERE));
				} catch (IOException e) {
					presentIDs = new Vector<String>();
					leaveStudents = new Vector<String>();
				}
				Vector<Integer> leaveIDs = new Vector<Integer>();
				Vector<String> reasons = new Vector<String>();
				for (String tuple : leaveStudents) {
					String[] line = tuple.split(",");
					String spresentID = line[0];
					Integer presentID = Integer.parseInt(spresentID);
					if (!presentIDs.contains(spresentID)) {
						leaveIDs.add(presentID);
						reasons.add(line[2]);
					} else {
						int reasonIdx = leaveStudents.indexOf(tuple);
						if (reasonIdx < reasons.size())
							reasons.remove(reasonIdx);
						leaveIDs.remove(presentID);
					}
				}
				System.out.println("PresentIDs: " + presentIDs);
				System.out.println("LeaveIDs: " + leaveIDs);
				System.out.println("Reasons: " + reasons);
				try {
					ScannerSaver.doneAddingCodes(leaveIDs, reasons, false, CommonUtils.FileType.NOTHERE, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				presentIDs = null;
				leaveIDs = null;
				reasons = null;
			}
		});
		cleanup();
		reloadCurrentPresentStudents();
	}

	private void actionPerformWriteForType(String title, String confirmString, String fileExistedString,
			CommonUtils.FileType type) {
		try {
			int result = JOptionPane.showConfirmDialog(null, confirmString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				addingCode(this, IDs, false, type, false, false);
				fixUpLeaveStudents();
			}
		} catch (FileAlreadyExistsException ex) {
			int result = JOptionPane.showConfirmDialog(null, fileExistedString, title, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				try {
					addingCode(this, IDs, false, type, true, false);
					fixUpLeaveStudents();
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
				addingCode(this, IDs, true, type, false, false);
				fixUpLeaveStudents();
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

	public ScannerDialog() {
		this.setTitle(WindowUtils.realTitle(Constants.SCANNER_DIALOG_TITLE));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.setSize(400, 150);
		field = new JTextField();
		field.setHorizontalAlignment(JTextField.CENTER);
		AbstractDocument document = (AbstractDocument) (field.getDocument());
		document.setDocumentFilter(new DocumentFilter());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (Logger.currentDialog != null)
					Logger.currentDialog._windowClosing();
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

			public boolean performSpecialCode(String code) {
				switch (code) {
				case "append":
				case "20822200000011":
					// Append data
					if (appendRegularBtn.isEnabled())
						appendRegularBtn.doClick();
					else
						confirmRegularBtn.doClick();
					return true;
				case "currenttable":
				case "20822200000021":
					// Show student table (current date)
					ControlCenterDialog.currentDialog.showStudentTable(0);
					return true;
				case "table":
				case "20822200000031":
					// Show student table (all-time)
					ControlCenterDialog.currentDialog.showStudentTable(1);
					return true;
				case "20822200000041":
					// Clear scanned codes
					parseText("clean");
					return true;
				case "20822200000051":
					// Delete last scanned code
					parseText("dellast");
					return true;
				}
				return false;
			}

			public void parseText(String text) {
				// Shortcut section
				if (!"208222".contains(text)) {
					Integer ID;
					if (text.length() == 3) {
						ID = CommonUtils.getID(DateUtils.studentYear() + "88" + text);
						if (ID / 1000 == (DateUtils.studentYearInt() * 100 + 88)) {
							addID(ID);
							return;
						}
					} else if (text.replaceAll("p", "p5888").matches("p" + CommonUtils.pMUICTID)) {
						ID = CommonUtils.getID("5888" + text.substring(1, text.length()));
						if (ID != -1) {
							addID(ID);
							return;
						}
					}
				}
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
					removeLabel = idx == (IDs.size() - 1);
					if (idx < IDs.size())
						IDs.remove(idx);
					shouldCleanup = true;
				} else {
					Integer ID = -1;
					int stringLength = text.length();
					if (!MainApp.test) {
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
					addID(ID);
				}
			}

			public void addID(Integer ID) {
				if (ID == -1)
					return;
				System.out.println("-> " + ID);
				if (!MainApp.db.containsKey(ID)) {
					if (ID == 5888220)
						setStatus("Add yourself?: " + ID); // the founder
					else {
						System.out.println("ID does not exist in database: " + ID);
						setStatus("Not Added (1): " + ID);
					}
				} else {
					if (IDs.contains(ID)) {
						System.out.println("ID already existed: " + ID);
						setStatus("Not Added (2): " + ID);
					} else {
						IDs.add(ID);
						list.addID(ID);
						setStatus("Added: " + ID);
					}
				}
				shouldCleanup = true;
			}

			public void validate() {
				updateButtonsState();
				String text = field.getText();
				shouldCleanup = removeLabel = false;
				if (!performSpecialCode(text))
					parseText(text);
				else
					removeLabel = shouldCleanup = true;
				updateDialog();
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
		JLabel regularLabel = new JLabel("Save Present:");
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

		WindowUtils.setRelativeCenter(this, 0, -50);
		this.setResizable(false);
		IDs = new Vector<Integer>();

		reloadCurrentPresentStudents();
		activeScanner = this;
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
