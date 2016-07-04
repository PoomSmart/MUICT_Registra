import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;

public class StudentTable extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Map<Integer, Student> students;
	
	public Map<Integer, Student> presentStudentMapForDate(Date date) {
		String mapPath = CommonUtils.filePath(CommonUtils.FileType.REGULAR, date);
		if (!CommonUtils.fileExistsAtPath(mapPath)) {
			System.out.println("Map for date " + DateUtils.normalFormattedDate(date) + " not found");
			return null;
		}
		Map<Integer, Student> map = new TreeMap<Integer, Student>();
		List<String> lines;
		try {
			lines = FileUtils.readLines(new File(mapPath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		for (String line : lines) {
			Integer ID = CommonUtils.getID(line);
			if (ID == -1)
				continue;
			Student student = students.get(ID);
			student.addStatus(new Status());
			map.put(ID, student);
		}
		return map;
	}

	private Object[][] toData(Map<Integer, Student> students, int mode) {
		Object[][] arr = new Object[students.size()][mode == 0 ? 6 : 5];
		Set<Map.Entry<Integer, Student>> entries = students.entrySet();
		Iterator<Map.Entry<Integer, Student>> entriesIterator = entries.iterator();
		int i = 0;
		while (entriesIterator.hasNext()) {
			Map.Entry<Integer, Student> mapping = (Map.Entry<Integer, Student>) entriesIterator.next();
			Student student = mapping.getValue();
			arr[i][0] = student.getFirstname();
			arr[i][1] = student.getLastname();
			arr[i][2] = student.getNickname();
			arr[i][3] = student.getGender();
			if (mode == 0) {
				arr[i][4] = "Test Available";
				arr[i][5] = "Test Position";
			} else
				arr[i][4] = student.getAbsenceCount();
			i++;
		}
		return arr;
	}
	
	private String[] columnNamesForMode(int mode) {
		String[] names = { "First Name", "Last Name", "Nickname", "Gender", "Status", "Position" };
		String[] names_global = { "First Name", "Last Name", "Nickname", "Gender", "Total #Absence" };
		return mode == 0 ? names : names_global;
	}

	public StudentTable(Map<Integer, Student> students, int mode) {
		this.students = students;
		String title = "Attendance";
		if (mode == 0)
			title += " for " + DateUtils.getCurrentNormalFormattedDate();
		if (mode > 1)
			return;
		this.setTitle(CommonUtils.realTitle(title));
		this.setSize(700, 900);
		CommonUtils.setCenter(this);
		Map<Integer, Student> internalStudents = presentStudentMapForDate(DateUtils.getCurrentDate());

		class StudentTableModel extends AbstractTableModel {

			private static final long serialVersionUID = 1L;

			private String[] columnNames = columnNamesForMode(mode);
			private Object data[][] = toData(internalStudents, mode);

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
				return data[row][col];
			}

			public Class<? extends Object> getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

		}

		JTable table = new JTable(new StudentTableModel());
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane);
	}
}
