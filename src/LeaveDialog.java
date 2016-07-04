import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
		reasonField.setPreferredSize(new Dimension(reasonField.getWidth(), 90));
		panel.add(reasonField);
		reasonLabel.setLabelFor(reasonField);
		
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
		JButton nullBtn = new JButton();
		nullBtn.setVisible(false);
		panel.add(nullBtn);
		panel.add(saveBtn);
		SpringUtilities.makeCompactGrid(panel, 3, 2, 6, 6, 6, 6);
		this.add(panel);
		this.setResizable(false);
	}
}
