import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class LeaveDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField inputField;
	private JTextArea reasonField;

	public LeaveDialog(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Leave Form"));
		this.setSize(400, 210);
		CommonUtils.setRelativeCenter(this, 0, 180);

		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(layout);
		
		JLabel IDLabel = new JLabel("ID: ", JLabel.TRAILING);
		this.add(IDLabel);
		inputField = new JTextField(10);
		inputField.setHorizontalAlignment(JTextField.LEFT);
		inputField.setBorder(null);
		this.add(inputField);
		IDLabel.setLabelFor(inputField);
		layout.putConstraint(SpringLayout.WEST, IDLabel, 5, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, IDLabel, 5, SpringLayout.NORTH, getContentPane());
		// FIXME: Better layout
		layout.putConstraint(SpringLayout.WEST, inputField, 37, SpringLayout.EAST, IDLabel);
		layout.putConstraint(SpringLayout.NORTH, inputField, 5, SpringLayout.NORTH, getContentPane());
		
		JLabel reasonLabel = new JLabel("Reason: ", JLabel.TRAILING);
		this.add(reasonLabel);
		reasonField = new JTextArea();
		reasonField.setPreferredSize(new Dimension(305, 90));
		this.add(reasonField);
		reasonLabel.setLabelFor(reasonField);
		layout.putConstraint(SpringLayout.WEST, reasonLabel, 5, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, reasonLabel, 5, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.WEST, reasonField, 5, SpringLayout.EAST, reasonLabel);
		layout.putConstraint(SpringLayout.NORTH, reasonField, 5, SpringLayout.NORTH, getContentPane());
		
		layout.putConstraint(SpringLayout.NORTH, reasonField, 5, SpringLayout.SOUTH, inputField);
		layout.putConstraint(SpringLayout.NORTH, reasonLabel, 9, SpringLayout.SOUTH, IDLabel);
		
		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean shouldCleanup = true;
				try {
					String sID = inputField.getText();
					Integer ID = CommonUtils.getID(sID);
					if (ID == -1)
						throw new Exception();
					if (!students.containsKey(ID)) {
						JOptionPane.showMessageDialog(null, "ID not found: " + sID);
						System.out.println("ID not found: " + sID);
						shouldCleanup = false;
						return;
					}
					String reason = reasonField.getText().replaceAll("\n", "\\\\n");
					if (reason.length() == 0) {
						JOptionPane.showMessageDialog(null, "Couldn't save leave form without reason");
						System.out.println("null reason field");
						shouldCleanup = false;
						return;
					}
					int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, "Leave Form", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						System.out.println(String.format("%d - %s", ID, reason));
						ScannerSaver.doneAddingCode(ID, reason, true, CommonUtils.FileType.NOTHERE);
					} else
						shouldCleanup = false;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Not saving due to malformed input or any error");
					System.out.println("malformed input or any error");
				}
				finally {
					if (shouldCleanup) {
						inputField.setText("");
						reasonField.setText("");
					}
				}
			}
		});
		layout.putConstraint(SpringLayout.NORTH, saveBtn, 9, SpringLayout.SOUTH, reasonField);
		layout.putConstraint(SpringLayout.EAST, saveBtn, -9, SpringLayout.EAST, getContentPane());
		this.add(saveBtn);
		this.setResizable(false);
	}
}
