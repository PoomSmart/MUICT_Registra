package Dialogs;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.DateUtils;
import Visualizers.SeatVisualizer;
import Workers.Logger;

public class ControlCenterDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public ControlCenterDialog(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Control Center"));
		this.setSize(450, 75);
		CommonUtils.setRelativeCenter(this, 0, -200);
		this.setLayout(new FlowLayout());

		JButton showDBButton = new JButton("Attendance");
		Object[] dbOptions = { "Today", "All" };
		showDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showOptionDialog(null, "Select type:", "Attendance Type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, dbOptions, dbOptions[0]);
				if (n != JOptionPane.CLOSED_OPTION) {
					StudentTable stuTable = new StudentTable(students, n);
					stuTable.setVisible(true);
				}
			}
		});
		getContentPane().add(showDBButton);

		JButton showLogButton = new JButton("Log");
		Object[] logOptions = { "Current", "All" };
		showLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showOptionDialog(null, "Select type:", "Log Type", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, logOptions, logOptions[0]);
				switch (n) {
				case 0:
					Logger.showLog(DateUtils.getCurrentDate(), false);
					break;
				case 1:
					Logger.showLogs();
				}
			}
		});
		getContentPane().add(showLogButton);
		
		JButton editLogButton = new JButton("Current Log");
		editLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logger.showLog(DateUtils.getCurrentDate(), true, true, CommonUtils.filePath(CommonUtils.FileType.LOG, DateUtils.getCurrentDate()));
			}
		});
		getContentPane().add(editLogButton);
		
		JButton showSeatButton = new JButton("Visualize");
		showSeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SeatVisualizer.activeVisualizer == null) {
					SeatVisualizer vis = new SeatVisualizer(students);
					vis.setVisible(true);
				}
			}
		});
		getContentPane().add(showSeatButton);

		this.setResizable(false);
	}
}
