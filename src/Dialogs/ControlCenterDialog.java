package Dialogs;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import Workers.ScannerSaver;

public class ControlCenterDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public static ControlCenterDialog currentDialog = null;

	public JButton showDBButton;
	public JButton showLogButton;
	public JButton showGraphButton;
	public JButton clearPresentButton;
	public JButton randomPresentButton;
	public JButton clearLeaveButton;
	public JButton ComeNDay;
	public JButton dontcome;
	public JButton ComeFirstDay;
	public JButton Come80PercentOrMore;
	public JButton MostAsked;
	public JButton comeWed;
	public JButton comeThurs;

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
		this.setSize(500, 150);
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
		
		/*dontcome = new JButton("Who don't come today??");
		dontcome.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try 
				{
					String path = CommonUtils.filePath(CommonUtils.FileType.REGULAR);
					String destination = "Attendance/"+ DateUtils.getCurrentFormattedDate() +"/not_come.txt";
					List<String> yList = FileUtils.readLines(new File("acceptance-y.csv"));
					List<String> ComeToday = FileUtils.readLines(new File(path));
					List<Integer> leave = LeaveDialog.IDs;
					StringBuilder notsee = new StringBuilder();

					for(String s: yList)
					{
						int id = 6088000 + Integer.parseInt(s);
						if(!ComeToday.contains(Integer.toString(id)) && !leave.contains(id))
						{
							notsee.append(s+"   ");
						}	
					}
					FileUtils.write(new File(destination), notsee.toString());
					JOptionPane.showMessageDialog(null, notsee.toString(), "Who don't come today",0);
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		
		getContentPane().add(dontcome);*/
		
		/*Come80PercentOrMore = new JButton("Who come equal or more than 80%");
		Come80PercentOrMore.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				int count = 0;
				for(Student s: std.values())
				{
					double come = s.getPresentCount();
					double total = s.getStatuses().size();
					//System.out.println(total);

					if((come/total)*100 >= 80)
					{
						count++;
						//System.out.println(s.getID());
					}
						
				}
				JOptionPane.showMessageDialog(null,"There are " + count + " students who come equal or more than 80%", "Come more than 80%", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		getContentPane().add(Come80PercentOrMore);*/
		
		ComeNDay = new JButton("Come in N day from 1st day to now");
		ComeNDay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				int arr[] = new int[21];
				int a; // for loop
				Arrays.fill(arr, 0);
				StringBuilder sb = new StringBuilder();
				
				for(Student s: std.values())
				{
					arr[s.getPresentCount()]++;
				}
				
				sb.append("Come 1 day: " + arr[1] + " \n");
				for(a=2; a<arr.length; a++)
				{
					sb.append("Come " + a + "days: " + arr[a] + " \n");
				}
				JOptionPane.showMessageDialog(null, sb.toString(), "Come in N day from 1st day to now", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		getContentPane().add(ComeNDay);
		
		comeWed = new JButton("Who comes on Wed");
		comeWed.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				String date[] = {"20170823", "20170830", "20170906", "20170913"};
				Set<Integer> comeOnWed = new HashSet<Integer>();
				int NumOfWeds = 1;
				if(DateUtils.getCurrentFormattedDate().equals(date[1]))
					NumOfWeds = 2;
				else if(DateUtils.getCurrentFormattedDate().equals(date[2]))
					NumOfWeds = 3;
				else if(DateUtils.getCurrentFormattedDate().equals(date[3]))
					NumOfWeds = 4;
				//System.out.println(NumOfWeds);
				
				for(Student s: std.values())
				{
					for(int a = 0; a<NumOfWeds; a++)
					{
						if( (s.isNormal(date[a]) || s.isLeft(date[a])) && s.getAbsenceCount() == DateUtils.availableDates().size()-1)
							comeOnWed.add(s.getID());
					}
				}		
				JOptionPane.showMessageDialog(null, comeOnWed.size() + " students.", "Come on Wednesday", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		getContentPane().add(comeWed);
		
		comeThurs = new JButton("Who comes on Thurs");
		comeThurs.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				String date[] = {"20170824", "20170831", "20170907", "20170914"};
				Set<Integer> comeOnWed = new HashSet<Integer>();
				int NumOfWeds = 1;
				if(DateUtils.getCurrentFormattedDate().equals(date[1]))
					NumOfWeds = 2;
				else if(DateUtils.getCurrentFormattedDate().equals(date[2]))
					NumOfWeds = 3;
				else if(DateUtils.getCurrentFormattedDate().equals(date[3]))
					NumOfWeds = 4;
				
				for(Student s: std.values())
				{
					for(int a = 0; a<NumOfWeds; a++)
					{
						if((s.isNormal(date[a]) || s.isLeft(date[a])) && s.getAbsenceCount() == DateUtils.availableDates().size()-1)
							comeOnWed.add(s.getID());
					}
				}		
				JOptionPane.showMessageDialog(null, comeOnWed.size() + " students.", "Come on Thursday", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		getContentPane().add(comeThurs);
		
		/*ComeFirstDay = new JButton("Who come first day?");
		ComeFirstDay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				StringBuilder sb = new StringBuilder();
				String date = DateUtils.getCurrentFormattedDate();
				int count = 0;
				for(Student s : std.values())
				{
					int period = s.getStatuses().size();
					if(s.getAbsenceCount() == period-1 && (s.isNormal(date) || s.isLeft(date)))
					{
						sb.append(s.getID() + " " + s.getName() + "\n");
						count++;
					}
				}
				sb.append("\nTotal new students for today is " + count);
				
				if(sb.length() > 0)
					JOptionPane.showMessageDialog(null, sb.toString(), "Who come first day?", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "No new comer today", "Who come first day?", JOptionPane.INFORMATION_MESSAGE);
				//System.out.println(DateUtils.getCurrentFormattedDate());
			}
		});
		getContentPane().add(ComeFirstDay);*/
		
		MostAsked = new JButton("Summary of most asked question");
		MostAsked.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Map<Integer,Student> std = DBUtils.getStudentsAllTime();
				StringBuilder sb = new StringBuilder();
				StringBuilder ft = new StringBuilder(); // who come first day?
				StringBuilder allBann = new StringBuilder();
				String date = "20170825";//DateUtils.getCurrentFormattedDate();
				
				int present = 0, men = 0, women = 0, everyday = 0;
				int comeFirstTime = 0;
				int MoreOrEqual80Percent = 0;
				int bann[] = new int[10];	//who comes for each bann
				int section[] = new int[3];	//who comes for each section
				Arrays.fill(bann, 0);
				Arrays.fill(section, 0);
				
				for(Student s: std.values())
				{
					double period = s.getStatuses().size();
					double come = s.getPresentCount() + s.getLeaveCount();

					if(s.isNormal(date))
					{
						section[s.getSection()-1]++;
						if(s.isMan())
							men++;
						else if(s.isWoman())
							women++;
						present++;
						bann[s.getBann()-1]++;
					}
					if(s.getPresentCount() == s.getStatuses().size())
						everyday++;
					
					/*if(s.isLeft(DateUtils.getCurrentFormattedDate()))
						come++;*/
					if((come/period)*100 >= 80)
						MoreOrEqual80Percent++;
					if(s.getAbsenceCount() == period-1 && (s.isNormal(date) || s.isLeft(date)))
					{
						ft.append(s.getID() + " " + s.getName() + "\n");
						comeFirstTime++;
					}
				}
				sb.append("Total present student is " + present + "\n");
				sb.append("Total men: " + men + ", Total women: " + women + "\n\n");
				sb.append("Freshmen for each Bann\n");
				for(int i = 0; i<bann.length; i++)
				{
					sb.append("Bann" + (i+1) + ": " + bann[i] );
					if(i<bann.length-1)
						sb.append(", ");
				}	
				sb.append("\n\n");
				
				for(int i =0; i<section.length; i++)
				{
					sb.append("Sec" + (i+1) + " present: " + section[i]);
					if(i<section.length-1)
						sb.append(", ");
				}	
		
				if(everyday > 0) 
					sb.append("\nThere are " + everyday + " freshmen who come everyday\n");
				else
					sb.append("\nNo one comes everyday T-T");
				sb.append("There are " + MoreOrEqual80Percent + " students who come equal or more than 80%\n\n");
				
				if(comeFirstTime > 0)
				{
					sb.append("Freshmen come here first time \n");
					sb.append(ft.toString());
					sb.append("Total: " + comeFirstTime);
				}
				else
					sb.append("No new freshman comes here");
				JOptionPane.showMessageDialog(null, sb.toString(), "Summary", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		getContentPane().add(MostAsked);
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
