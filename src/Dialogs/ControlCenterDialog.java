package Dialogs;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import MainApp.MainApp;
import Objects.Constants;
import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DBUtils;
import Utilities.DateUtils;
import Utilities.WindowUtils;
import Visualizers.GraphPanel;
import Visualizers.SeatVisualizer;
import Workers.Logger;

public class ControlCenterDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public static ControlCenterDialog currentDialog = null;

	public JButton showDBButton;
	public JButton showLogButton;
	public JButton showGraphButton;
	public JButton clearPresentButton;
	public JButton randomPresentButton;
	public JButton clearLeaveButton;

	private static Object[] dbOptions = { "Single", "All" };

	public void showStudentTable(int result) {
		result = result == -1
				? JOptionPane.showOptionDialog(null, "Select type:", "Attendance Type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, dbOptions, dbOptions[0])
				: result;
		if (result != JOptionPane.CLOSED_OPTION) {
			StudentTable stuTable = new StudentTable(result);
			stuTable.setVisible(true);
		}
	}

	public ControlCenterDialog() {
		this.setTitle(WindowUtils.realTitle("Control Center"));
		this.setSize(450, MainApp.test ? 110 : 75);
		WindowUtils.setRelativeCenter(this, 0, -200);
		this.setLayout(new FlowLayout());

		showDBButton = new JButton("Attendance");
		showDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStudentTable(-1);
			}
		});
		getContentPane().add(showDBButton);

		showLogButton = new JButton("Log");
		Object[] logOptions = { "Current", "All" };
		showLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showOptionDialog(null, "Select type:", "Log Type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, logOptions, logOptions[0]);
				switch (result) {
				case 0:
					Logger.showLog(DateUtils.getCurrentDate(), true, true);
					break;
				case 1:
					Logger.showLogs();
				}
			}
		});
		getContentPane().add(showLogButton);

		showGraphButton = new JButton("Visualize");
		showGraphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "All", "Per Section", "Gender" };
				int result = JOptionPane.showOptionDialog(null, "Select type:", "Graph Type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (result > 2)
					return;
				Map<Integer, Student> students;
				Vector<Integer> presentPerDay = result == 0 ? new Vector<Integer>() : null;
				Vector<Integer> presentAndLeavePerDay = result == 0 ? new Vector<Integer>() : null;
				Vector<Integer> leavePerDay = result == 0 ? new Vector<Integer>() : null;
				//Vector<Integer> absentPerDay = result == 0 ? new Vector<Integer>() : null;
				Vector<Integer> section1 = result == 1 ? new Vector<Integer>() : null;
				Vector<Integer> section2 = result == 1 ? new Vector<Integer>() : null;
				Vector<Integer> section3 = result == 1 ? new Vector<Integer>() : null;
				Vector<Integer> manPerDay = result == 2 ? new Vector<Integer>() : null;
				Vector<Integer> womanPerDay = result == 2 ? new Vector<Integer>() : null;
				List<List<Integer>> graphs = new Vector<List<Integer>>();
				int presentStudents, leaveStudents/*, absentStudents*/;
				int manCount, womanCount;
				int[] perSection;
				for (Date date : DateUtils.availableDates()) {
					String fdate = DateUtils.getFormattedDate(date);
					students = DBUtils.getStudents(date);
					presentStudents = leaveStudents /*= absentStudents*/ = 0;
					manCount = womanCount = 0;
					perSection = new int[3];
					for (Student student : students.values()) {
						// We probably skip those unable to join at all
						if (student.unableToJoin())
							continue;
						if (student.isNormal(fdate)) {
							presentStudents++;
							if (result == 1) {
								int section = student.getSection();
								perSection[section - 1]++;
							} else if (result == 2) {
								if (student.isMan())
									manCount++;
								else if (student.isWoman())
									womanCount++;
							}
						} else {
							if (student.isLeft(fdate))
								leaveStudents++;
							/*else if (student.isAbsent(fdate))
								absentStudents++;*/
						}
					}
					if (presentStudents > 0) {
						if (result == 0) {
							presentPerDay.add(presentStudents);
							presentAndLeavePerDay.add(presentStudents + leaveStudents);
							leavePerDay.add(leaveStudents);
							//absentPerDay.add(absentStudents);
						} else if (result == 1) {
							section1.add(perSection[0]);
							section2.add(perSection[1]);
							section3.add(perSection[2]);
						} else if (result == 2) {
							manPerDay.add(manCount);
							womanPerDay.add(womanCount);
						}
					}
				}
				if (result == 0) {
					graphs.add(presentPerDay);
					graphs.add(presentAndLeavePerDay);
					graphs.add(leavePerDay);
					//graphs.add(absentPerDay);
				} else if (result == 1) {
					graphs.add(section1);
					graphs.add(section2);
					graphs.add(section3);
				} else if (result == 2) {
					graphs.add(manPerDay);
					graphs.add(womanPerDay);
				}
				GraphPanel grapher;
				if (result == 0) {
					grapher = new GraphPanel("Attendance", graphs);
					grapher.addGraphColor(0, Color.BLUE);
					grapher.addGraphColor(1, Color.ORANGE);
					grapher.addGraphColor(2, Color.RED);
					grapher.writeToFile("attendance-all", Arrays.asList("Present", "Present + Leave", "Leave"));
				} else if (result == 1) {
					grapher = new GraphPanel("Attendance by Section", graphs);
					grapher.addGraphColor(0, Color.BLUE);
					grapher.addGraphColor(1, Color.GREEN);
					grapher.addGraphColor(2, Color.MAGENTA);
					grapher.writeToFile("attendance-section", Arrays.asList("Section 1", "Section 2", "Section 3"));
				} else if (result == 2) {
					grapher = new GraphPanel("Attendance by Gender", graphs);
					grapher.addGraphColor(0, Color.BLUE);
					grapher.addGraphColor(1, Color.PINK);
					grapher.writeToFile("attendance-gender", Arrays.asList("Man", "Woman"));
				}
				presentPerDay = null;
				presentAndLeavePerDay = null;
				//absentPerDay = null;
				section1 = null;
				section2 = null;
				section3 = null;
				manPerDay = null;
				womanPerDay = null;
				students = null;
				perSection = null;
			}
		});
		getContentPane().add(showGraphButton);

		if (MainApp.test) {
			clearPresentButton = new JButton("Clear Present");
			clearPresentButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, getTitle(),
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						System.out.println("Clear current present.csv");
						try {
							FileUtils.write(CommonUtils.fileFromType(FileType.REGULAR), "");
							// empty present.csv = empty leave.csv
							FileUtils.write(CommonUtils.fileFromType(FileType.NOTHERE), "");
							SeatVisualizer.updateIfPossible(false);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			getContentPane().add(clearPresentButton);

			clearLeaveButton = new JButton("Clear Leave");
			clearLeaveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(null, Constants.COMMON_CONFIRM, getTitle(),
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						System.out.println("Clear current leave.csv");
						try {
							FileUtils.write(CommonUtils.fileFromType(FileType.NOTHERE), "");
							SeatVisualizer.updateIfPossible(false);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			getContentPane().add(clearLeaveButton);

			randomPresentButton = new JButton("Random Present");
			randomPresentButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ScannerDialog.random(60);
				}
			});
			getContentPane().add(randomPresentButton);
		}
		
		JButton aboutButton = new JButton("About");
		aboutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Founder: Thatchapon Unprasert (Batch #13)\n\n");
				sb.append("MUICT_Registra is a convenient Java program for Stand Cheer registration.\n");
				sb.append("We can check total students in real-time. We can know how often each attend the activity.\n");
				sb.append("We can assign status to any student just by clicking. We can log things that happen each day.\n");
				sb.append("We can generate table showing various information about students and sort them by any filter.\n");
				sb.append("We definitely can analyze the activity in terms of statistics.\n\n");
				sb.append("Founder's message\n");
				sb.append("Redistribution and publication are strictly not allowed.\n");
				sb.append("And the founder does not really like Stand Cheer.");
				JOptionPane.showMessageDialog(null, sb.toString(), "MUICT_Registra", JOptionPane.INFORMATION_MESSAGE);
				sb = null;
			}
		});
		getContentPane().add(aboutButton);

		this.setResizable(false);
		currentDialog = this;
	}
}
