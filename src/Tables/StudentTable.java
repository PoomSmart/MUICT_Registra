package Tables;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;

import Objects.Constants;
import Objects.Status;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DateUtils;
import Utilities.SpringUtilities;
import Workers.LeaveParser;

public class StudentTable extends JFrame {

	// FIXME: Huge algorithm

	private static final long serialVersionUID = 1L;

	private static StudentTable activeTable = null;

	private Map<Integer, Student> students;
	private Map<Integer, Student> internalStudents;

	private JTextField filterText;
	private JTextArea studentText;

	private JTable table;
	private int mode;
	
	private static String[] names = { "ID", "First Name", "Last Name", "Nickname", "Gender", "Status", "Position" };
	private static String[] names_global = { "ID", "First Name", "Last Name", "Nickname", "Gender", "#Present", "#Leave", "#Absence" };

	private TableRowSorter<? extends AbstractTableModel> sorter;

	public static Map<Integer, Student> currentStudentMap(Map<Integer, Student> students) {
		StudentTable table = new StudentTable(students, 0, false);
		return table.getInternalStudents();
	}

	public static void updateIfPossible() {
		if (activeTable != null) {
			activeTable.updateInternalStudents();
			((AbstractTableModel)activeTable.table.getModel()).fireTableDataChanged();
		}
	}

	public Map<Integer, Student> studentMapForDate(Date date, CommonUtils.FileType type) {
		String mapPath = CommonUtils.filePath(type, date);
		if (!CommonUtils.fileExistsAtPath(mapPath)) {
			System.out.println("Map (" + type + ") for date " + DateUtils.normalFormattedDate(date) + " not found");
			return new TreeMap<Integer, Student>();
		}
		Map<Integer, Student> map = new TreeMap<Integer, Student>();
		List<String> lines;
		try {
			lines = FileUtils.readLines(new File(mapPath));
		} catch (IOException e) {
			e.printStackTrace();
			return new TreeMap<Integer, Student>();
		}
		for (String line : lines) {
			Integer ID;
			Matcher m = null;
			if (type == CommonUtils.FileType.REGULAR)
				ID = CommonUtils.getID(line);
			else {
				m = LeaveParser.pDB.matcher(line);
				if (!m.find())
					continue;
				ID = CommonUtils.getID(m.group(1));
			}
			if (ID == -1)
				continue;
			Student student = students.get(ID).clone();
			if (type == CommonUtils.FileType.REGULAR)
				student.addStatus(new Status());
			else {
				String reason = m.group(2);
				student.addStatus(new Status(Status.Type.LEAVE, reason));
			}
			map.put(ID, student);
		}
		return map;
	}

	public Map<Integer, Student> presentStudentMapForDate(Date date) {
		return studentMapForDate(date, CommonUtils.FileType.REGULAR);
	}

	public Map<Integer, Student> leaveStudentMapForDate(Date date) {
		return studentMapForDate(date, CommonUtils.FileType.NOTHERE);
	}

	private Object[][] toData(Map<Integer, Student> students, int mode) {
		Object[][] arr = new Object[students.size()][mode == 0 ? names.length: names_global.length];
		Set<Map.Entry<Integer, Student>> entries = students.entrySet();
		Iterator<Map.Entry<Integer, Student>> entriesIterator = entries.iterator();
		int i = 0;
		while (entriesIterator.hasNext()) {
			Map.Entry<Integer, Student> mapping = (Map.Entry<Integer, Student>) entriesIterator.next();
			Student student = mapping.getValue();
			arr[i][0] = student.getID();
			arr[i][1] = student.getFirstname();
			arr[i][2] = student.getLastname();
			arr[i][3] = student.getNickname();
			arr[i][4] = student.getGender();
			if (mode == 0) {
				arr[i][5] = student.getCurrentStatus();
				arr[i][6] = student.getPosition();
			} else {
				arr[i][5] = student.getPresentCount();
				arr[i][6] = student.getLeaveCount();
				arr[i][7] = student.getAbsenceCount();
			}
			i++;
		}
		return arr;
	}

	private String[] columnNamesForMode(int mode) {
		return mode == 0 ? names : names_global;
	}

	private void updateInternalStudents() {
		if (mode == 0) {
			internalStudents = presentStudentMapForDate(DateUtils.getCurrentDate());
			
			// Add leave-with-reason students
			String leaveDBPath = CommonUtils.filePath(CommonUtils.FileType.NOTHERE);
			LeaveParser leaveParser = new LeaveParser(leaveDBPath, students);
			internalStudents.putAll(leaveParser.getLeaveStudents());

			// Add absent students
			// FIXME: Possibly slow algorithm
			for (Student student : students.values()) {
				if (!internalStudents.containsKey(student.getID())) {
					Student absentStudent = student.clone();
					absentStudent.addStatus(new Status(Status.Type.ABSENT));
					internalStudents.put(absentStudent.getID(), absentStudent);
				}
			}
		} else {
			internalStudents = new TreeMap<Integer, Student>();
			for (Map.Entry<Integer, Student> entry : students.entrySet()) {
				internalStudents.put(entry.getKey(), entry.getValue().clone());
			}
			File[] dates = new File(Constants.FILE_ROOT).listFiles();
			for (File date : dates) {
				if (!date.isDirectory()) {
					System.out.println("Skip directory: " + date.getName());
					continue;
				}
				Date d;
				try {
					d = DateUtils.s_fmt.parse(date.getName());
				} catch (ParseException e) {
					System.out.println("Invalid folder: " + date.getName());
					continue;
				}
				Map<Integer, Student> presentStudents = presentStudentMapForDate(d);
				// Assign present students
				for (Student presentStudent : presentStudents.values()) {
					Integer ID = presentStudent.getID();
					internalStudents.get(ID).addStatus(d, new Status());
				}
				// Assign leave students
				// Fist adding leave-with-reason students
				String leaveDBPath = CommonUtils.filePath(CommonUtils.FileType.NOTHERE, d);
				LeaveParser leaveParser = new LeaveParser(leaveDBPath, students, d);
				Map<Integer, Student> leaveStudents = leaveParser.getLeaveStudents();
				for (Student leaveStudent : leaveStudents.values()) {
					Integer ID = leaveStudent.getID();
					Status leaveStatus = leaveStudent.getStatus(DateUtils.formattedDate(d));
					if (leaveStatus != null)
						internalStudents.get(ID).addStatus(d, leaveStatus.clone());
				}
				// Assign absent students
				for (Student student : internalStudents.values()) {
					Integer ID = student.getID();
					if (!presentStudents.containsKey(ID))
						internalStudents.get(ID).addStatus(d, new Status(Status.Type.ABSENT));
				}
			}
		}
	}

	public StudentTable(Map<Integer, Student> students, int mode, boolean UI) {
		this.students = students;
		this.mode = mode;

		updateInternalStudents();
		
		String title = "Attendance";
		if (mode == 0)
			title += " for " + DateUtils.getCurrentNormalFormattedDate();
		if (mode > 1)
			return;
		JPanel self = new JPanel();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		this.setTitle(CommonUtils.realTitle(title));
		this.setSize(700, 900);
		CommonUtils.setCenter(this);
		
		if (!UI)
			return;

		class StudentTableModel extends AbstractTableModel {

			private static final long serialVersionUID = 1L;

			private String[] columnNames = columnNamesForMode(mode);

			@Override
			public int getColumnCount() {
				return columnNames.length;
			}

			@Override
			public int getRowCount() {
				return internalStudents.size();
			}

			public String getColumnName(int col) {
				return columnNames[col];
			}

			@Override
			public Object getValueAt(int row, int col) {
				return toData(internalStudents, mode)[row][col];
			}

			public Class<? extends Object> getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

		}

		StudentTableModel model = new StudentTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sorter = new TableRowSorter<StudentTableModel>(model);
		table.setRowSorter(sorter);
		table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), this.getHeight() - 200));

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = table.getSelectedRow();
				if (viewRow < 0)
					studentText.setText("");
				else {
					int modelRow = table.convertRowIndexToModel(viewRow);
					studentText.setText(internalStudents.get(table.getModel().getValueAt(modelRow, 0)).toString(mode));
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		self.add(scrollPane);

		JPanel form = new JPanel(new SpringLayout());
		JLabel filterLabel = new JLabel("Filter Text:", SwingConstants.TRAILING);
		form.add(filterLabel);

		filterText = new JTextField(10);
		filterText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		filterLabel.setLabelFor(filterText);
		filterText.setBorder(BorderFactory.createLineBorder(Color.gray));
		form.add(filterText);

		JLabel statusLabel = new JLabel("Student Info:", SwingConstants.TRAILING);
		form.add(statusLabel);
		studentText = new JTextArea();
		statusLabel.setLabelFor(studentText);
		studentText.setPreferredSize(new Dimension(studentText.getWidth(), 150));
		studentText.setBorder(BorderFactory.createLineBorder(Color.gray));
		studentText.setEditable(false);
		form.add(studentText);

		SpringUtilities.makeCompactGrid(form, 2, 2, 6, 6, 6, 6);
		self.add(form);
		this.setContentPane(self);
		this.pack();
		activeTable = this;
	}
	
	public StudentTable(Map<Integer, Student> students, int mode) {
		this(students, mode, true);
	}

	private void newFilter() {
		RowFilter<? super AbstractTableModel, Object> rf = null;
		try {
			if (mode == 0)
				rf = RowFilter.regexFilter(filterText.getText(), 0, 1, 2, 3, 6);
			else
				rf = RowFilter.regexFilter(filterText.getText(), 0, 1, 2, 3);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	public Map<Integer, Student> getInternalStudents() {
		return internalStudents;
	}

}
