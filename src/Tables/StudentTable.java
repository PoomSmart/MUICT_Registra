package Tables;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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

import Main.Main;
import Objects.Constants;
import Objects.Status;
import Objects.Student;
import Utilities.DBUtils;
import Utilities.DateUtils;
import Utilities.SpringUtilities;
import Utilities.WindowUtils;

public class StudentTable extends JFrame {

	private static final long serialVersionUID = 1L;

	public static StudentTable activeTable = null;

	private Map<Integer, Student> internalStudents;

	private JTextField filterText;
	private JTextArea studentText;

	private JTable table;
	private int mode;
	
	private static final String[] names = { "ID", "First Name", "Last Name", "Nickname", "Gender", "Status", "Position" };
	private static final String[] names_global = { "ID", "First Name", "Last Name", "Nickname", "Gender", "#Present", "#Leave", "#Absence" };

	private TableRowSorter<? extends AbstractTableModel> sorter;

	public static void updateIfPossible() {
		if (activeTable != null) {
			System.out.println("Update StudentTable");
			activeTable.updateInternalStudents();
			((AbstractTableModel)activeTable.table.getModel()).fireTableDataChanged();
		} else
			System.out.println("Null StudentTable");
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

	public void updateInternalStudents() {
		if (mode == 0)
			internalStudents = DBUtils.getCurrentStudents();
		else {
			internalStudents = new TreeMap<Integer, Student>();
			for (Entry<Integer, Student> entry : Main.db.entrySet())
				internalStudents.put(entry.getKey(), entry.getValue().clone());
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
				// Assigning present and absent students
				Map<Integer, Student> presentStudents = DBUtils.getPresentStudents(d);
				for (Integer ID : Main.db.keySet()) {
					if (presentStudents.containsKey(ID))
						internalStudents.get(ID).addStatus(d, new Status());
					else
						internalStudents.get(ID).addStatus(d, new Status(Status.Type.ABSENT));
				}
				// Assigning leave-with-reason students
				Map<Integer, Student> leaveStudents = DBUtils.getLeaveStudents(d);
				for (Entry<Integer, Student> entry : leaveStudents.entrySet()) {
					Integer ID = entry.getKey();
					Student student = entry.getValue();
					Status leaveStatus = student.getStatus(DateUtils.formattedDate(d));
					if (leaveStatus != null)
						internalStudents.get(ID).addStatus(d, leaveStatus.clone());
				}
			}
		}
	}

	public StudentTable(int mode) {
		this.mode = mode;

		updateInternalStudents();
		
		String title = "Attendance";
		if (mode == 0)
			title += " for " + DateUtils.getCurrentNormalFormattedDate();
		if (mode > 1)
			return;
		JPanel self = new JPanel();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		this.setTitle(WindowUtils.realTitle(title));
		this.setSize(700, 900);
		WindowUtils.setCenter(this);

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

}
