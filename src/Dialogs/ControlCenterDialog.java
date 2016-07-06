package Dialogs;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import Main.Main;
import Objects.Constants;
import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DateUtils;
import Visualizers.SeatVisualizer;
import Workers.Logger;

public class ControlCenterDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public ControlCenterDialog() {
		this.setTitle(CommonUtils.realTitle("Control Center"));
		this.setSize(450, 110);
		CommonUtils.setRelativeCenter(this, 0, -200);
		this.setLayout(new FlowLayout());

		JButton showDBButton = new JButton("Attendance");
		Object[] dbOptions = { "Today", "All" };
		showDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showOptionDialog(null, "Select type:", "Attendance Type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, dbOptions, dbOptions[0]);
				if (result != JOptionPane.CLOSED_OPTION) {
					StudentTable stuTable = new StudentTable(result);
					stuTable.setVisible(true);
				}
			}
		});
		getContentPane().add(showDBButton);

		JButton showLogButton = new JButton("Log");
		Object[] logOptions = { "Current", "All" };
		showLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showOptionDialog(null, "Select type:", "Log Type", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, logOptions, logOptions[0]);
				switch (result) {
				case 0:
					Logger.showLog(DateUtils.getCurrentDate(), true, true, CommonUtils.filePath(CommonUtils.FileType.LOG, DateUtils.getCurrentDate()));
					break;
				case 1:
					Logger.showLogs();
				}
			}
		});
		getContentPane().add(showLogButton);
		
		JButton clearPresentButton = new JButton("Clear Present");
		clearPresentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, getTitle(), JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					System.out.println("Clear current present.csv");
					try {
						FileUtils.write(CommonUtils.fileFromType(FileType.REGULAR), "");
						// empty present.csv = empty leave.csv
						FileUtils.write(CommonUtils.fileFromType(FileType.NOTHERE), "");
						SeatVisualizer.updateIfPossible();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		getContentPane().add(clearPresentButton);
		
		JButton clearLeaveButton = new JButton("Clear Leave");
		clearLeaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, getTitle(), JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					System.out.println("Clear current leave.csv");
					try {
						FileUtils.write(CommonUtils.fileFromType(FileType.NOTHERE), "");
						SeatVisualizer.updateIfPossible();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		getContentPane().add(clearLeaveButton);
		
		if (Main.test) {
			JButton randomPresentButton = new JButton("Random Present");
			randomPresentButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ScannerDialog.random(40);
				}
			});
			getContentPane().add(randomPresentButton);
		}

		this.setResizable(false);
	}
}
