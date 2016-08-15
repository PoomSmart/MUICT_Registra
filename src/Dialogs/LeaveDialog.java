package Dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.io.FileUtils;

import MainApp.MainApp;
import Objects.Constants;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DBUtils;
import Utilities.SpringUtilities;
import Utilities.WindowUtils;
import Workers.ScannerSaver;

public class LeaveDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField inputField;
	private JTextArea reasonField;
	private JComboBox<String> reasonSelector;
	
	private int defaultIndex = 2;
	private String[] commonReasons = { "Sick", "ACM-ICPC", "Urgent Business", "Limited Allowances", "Others" };
	
	private boolean othersSelected() {
		return reasonSelector.getSelectedIndex() == commonReasons.length - 1;
	}
	
	private void updateReasonField() {
		boolean others = othersSelected();
		reasonField.setEnabled(others);
		reasonField.setBackground(others ? Color.white : Color.getHSBColor(0.0f, 0.0f, 0.85f));
		if (!others)
			reasonField.setText("");
	}

	public LeaveDialog() {
		this.setTitle(WindowUtils.realTitle("Leave Form"));
		this.setSize(400, 210);
		WindowUtils.setRelativeCenter(this, 0, 180);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		
		JLabel IDLabel = new JLabel("ID: ", JLabel.TRAILING);
		panel.add(IDLabel);
		inputField = new JTextField();
		inputField.setHorizontalAlignment(JTextField.LEFT);
		inputField.setBorder(null);
		panel.add(inputField);
		IDLabel.setLabelFor(inputField);

		JLabel reasonLabel = new JLabel("Reason: ", JLabel.TRAILING);
		panel.add(reasonLabel);
		reasonField = new JTextArea();
		reasonField.setPreferredSize(new Dimension(reasonField.getWidth(), 50));
		panel.add(reasonField);
		reasonLabel.setLabelFor(reasonField);

		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean shouldCleanup = false;
				try {
					Map<Integer, Student> currentStudentMap = DBUtils.getCurrentStudents();
					String oIDs = inputField.getText();
					String[] sIDs = oIDs.split(",");
					Vector<Integer> IDs = new Vector<Integer>();
					for (String sID : sIDs) {
						Integer ID = CommonUtils.getID(sID);
						if (ID == -1)
							throw new Exception();
						if (!MainApp.db.containsKey(ID)) {
							JOptionPane.showMessageDialog(null, ID + " or more student ID not found");
							return;
						}
						Student realStudent = currentStudentMap.get(ID);
						if (realStudent.isLeft()) {
							JOptionPane.showMessageDialog(null, ID + " or more student has already left");
							return;
						}
						IDs.add(ID);
					}
					boolean others = othersSelected();
					String reason = !others ? getReason() : reasonField.getText();
					String reasonCheck = reason.trim().replaceAll("[\n\t\r]", "");
					if (reasonCheck.length() == 0 && others) {
						JOptionPane.showMessageDialog(null, "Couldn't save leave form without reason");
						System.out.println("Null reason field");
						return;
					}
					reason = reason.replaceAll("\n", "\\\\n");
					int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, "Leave Form",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						ScannerSaver.doneAddingCodes(IDs, CommonUtils.sameReason(reason, IDs.size()), true,
								CommonUtils.FileType.NOTHERE);
						// We are supposed to update present.csv since ID there may also exist in leave.csv
						Map<Integer, Student> leaveStudents = DBUtils.getCurrentLeaveStudents();
						List<String> presentIDs = FileUtils.readLines(CommonUtils.fileFromType(CommonUtils.FileType.REGULAR));
						for (Integer ID : leaveStudents.keySet())
							presentIDs.remove(String.valueOf(ID));
						Vector<Integer> spresentIDs = new Vector<Integer>();
						for (String sID : presentIDs)
							spresentIDs.add(Integer.parseInt(sID));
						ScannerSaver.doneAddingCodes(spresentIDs, false, CommonUtils.FileType.REGULAR, true);
						shouldCleanup = true;
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Not saving due to malformed input");
					System.out.println("Malformed input");
				} finally {
					if (shouldCleanup) {
						inputField.setText("");
						reasonField.setText("");
					}
				}
			}
		});
		
		JButton nullBtn = new JButton();
		nullBtn.setVisible(false);
		panel.add(nullBtn);
		
		reasonSelector = new JComboBox<String>(commonReasons);
		reasonSelector.setSelectedIndex(defaultIndex);
		reasonSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateReasonField();
			}
		});
		panel.add(reasonSelector);
		
		nullBtn = new JButton();
		nullBtn.setVisible(false);
		panel.add(nullBtn);
		
		panel.add(saveBtn);
		
		updateReasonField();
		
		SpringUtilities.makeCompactGrid(panel, 4, 2, 6, 6, 6, 6);
		this.add(panel);
		this.setResizable(false);
	}
	
	public String getReason() {
		return (String)reasonSelector.getSelectedItem();
	}
}
