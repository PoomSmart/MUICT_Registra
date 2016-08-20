package Tables;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
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

import MainApp.MainApp;
import Objects.Status;
import Objects.Student;
import Utilities.DBUtils;
import Utilities.DateUtils;
import Utilities.SpringUtilities;
import Utilities.WindowUtils;

public class StudentTable extends JFrame {

	// TODO: Disallow table windows duplication

	private static final long serialVersionUID = 1L;

	public static StudentTable activeTable = null;

	private Map<Integer, Student> internalStudents;

	private JTextField filterText;
	private JTextArea studentText;
	private JLabel statText;
	private JLabel bannText;
	private JComboBox<String> dateSelector;
	
	private Object[][] data;

	public static int totalPresent;
	public static int totalAbsent;
	public static int totalLeft;
	public static int totalPresentIslamic;
	public static int totalIslamic;
	public static int totalPresentFootball;
	public static int totalFootball;

	private JTable table;
	private final int mode;
	private String date;

	private int bannCount[];
	private int bannPresentCount[];

	private static final String[] names = { "ID", "First Name", "Last Name", "Nickname", "Gender", "Status",
			"Acceptance", "Position", "Bann" };
	private static final String[] names_global = { "ID", "First Name", "Last Name", "Nickname", "Gender", "#Present",
			"#Leave", "#Absence", "Bann" };

	private TableRowSorter<? extends AbstractTableModel> sorter;

	public static void updateIfPossible() {
		if (activeTable != null) {
			System.out.println("Update StudentTable");
			activeTable.updateInternalStudents();
			((AbstractTableModel) activeTable.table.getModel()).fireTableDataChanged();
		} else
			System.out.println("Null StudentTable");
	}

	private Object[][] toData(Map<Integer, Student> students, int mode) {
		Object[][] arr = new Object[students.size()][mode == 0 ? names.length : names_global.length];
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
				arr[i][5] = student.getStatus(date);
				arr[i][6] = student.getAcceptanceStatus();
				arr[i][7] = student.getCellPosition();
				arr[i][8] = student.getBann();
			} else {
				arr[i][5] = student.getPresentCount();
				arr[i][6] = student.getLeaveCount();
				arr[i][7] = student.getAbsenceCount();
				arr[i][8] = student.getBann();
			}
			i++;
		}
		return arr;
	}

	private String[] columnNamesForMode(int mode) {
		return mode == 0 ? names : names_global;
	}

	private void updateStatText() {
		if (statText != null)
			statText.setText(String.format(
					"Total Present: %d (Islamic: %d/%d), Total Absent: %d, Total Left: %d, Football: (%d/%d), Total Attended: At most %d",
					totalPresent, totalPresentIslamic, totalIslamic, totalAbsent, totalLeft, totalPresentFootball,
					totalFootball, totalPresent + totalLeft));
	}

	private void updateBannText() {
		if (bannText != null) {
			StringBuilder sb = new StringBuilder();
			for (int bann = 1; bann <= 10; bann++)
				sb.append(String.format("[Bann %d: %d/%d]  ", bann, bannPresentCount[bann - 1], bannCount[bann - 1]));
			bannText.setText(sb.toString());
			sb = null;
		}
	}

	public void updateInternalStudents() {
		if (mode == 0) {
			bannCount = null;
			bannCount = new int[10];
			bannPresentCount = null;
			bannPresentCount = new int[10];
			totalIslamic = totalPresentIslamic = 0;
			totalPresent = totalAbsent = totalLeft = 0;
			totalPresentFootball = totalFootball = 0;
			internalStudents = null;
			internalStudents = DBUtils.getStudents(DateUtils.dateFromString(date));
			for (Entry<Integer, Student> entry : internalStudents.entrySet()) {
				Student student = entry.getValue();
				bannCount[student.getBann() - 1]++;
				if (student.isNormal(date)) {
					bannPresentCount[student.getBann() - 1]++;
					totalPresent++;
				} else if (student.isAbsent(date))
					totalAbsent++;
				else if (student.isLeft(date))
					totalLeft++;
				if (student.isFootball()) {
					if (student.isNormal(date))
						totalPresentFootball++;
					totalFootball++;
				}
				if (student.isIslamic()) {
					if (student.isNormal(date))
						totalPresentIslamic++;
					totalIslamic++;
				}
			}
			updateStatText();
			updateBannText();
		} else {
			internalStudents = new TreeMap<Integer, Student>();
			for (Entry<Integer, Student> entry : MainApp.db.entrySet())
				internalStudents.put(entry.getKey(), entry.getValue().clone());
			for (Date d : DateUtils.availableDates()) {
				// Assigning present and absent students
				Map<Integer, Student> presentStudents = DBUtils.getPresentStudents(d);
				for (Integer ID : MainApp.db.keySet()) {
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
					Status leaveStatus = student.getStatus(DateUtils.getFormattedDate(d));
					if (leaveStatus != null)
						internalStudents.get(ID).addStatus(d, leaveStatus.clone());
				}
			}
		}
		data = toData(internalStudents, mode);
	}

	public StudentTable(int mode) {
		this(mode, DateUtils.getCurrentFormattedDate());
	}

	public StudentTable(int mode, String date) {
		this.mode = mode;
		this.date = date;

		updateInternalStudents();

		String title = "Attendance";
		if (mode == 0)
			title += " for " + DateUtils.getNormalFormattedDate(DateUtils.dateFromString(date));
		if (mode > 1)
			return;
		JPanel self = new JPanel();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		this.setTitle(WindowUtils.realTitle(title));
		this.setSize(1100, 850);

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
				return data[row][col];
			}

			public Class<? extends Object> getColumnClass(int c) {
				Object value = getValueAt(0, c);
				return value != null ? value.getClass() : Object.class;
			}

		}

		StudentTableModel model = new StudentTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sorter = new TableRowSorter<StudentTableModel>(model);
		table.setRowSorter(sorter);
		table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), this.getHeight() - 250));

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = table.getSelectedRow();
				if (viewRow < 0)
					studentText.setText("");
				else {
					int modelRow = table.convertRowIndexToModel(viewRow);
					studentText.setText(
							internalStudents.get(table.getModel().getValueAt(modelRow, 0)).toString(mode, date));
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		self.add(scrollPane);
		JPanel form = new JPanel(new SpringLayout());

		if (mode == 0) {
			form.add(new JLabel("Select Date:", SwingConstants.TRAILING));
			List<String> dates = DateUtils.s_availableDates();
			dateSelector = new JComboBox<String>(dates.toArray(new String[0]));
			dateSelector.setSelectedIndex(dates.size() - 1);
			dateSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setDate((String) dateSelector.getSelectedItem());
					updateIfPossible();
				}
			});
			form.add(dateSelector);
		}

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
		studentText.setPreferredSize(new Dimension(studentText.getWidth(), 200));
		studentText.setBorder(BorderFactory.createLineBorder(Color.gray));
		studentText.setEditable(false);
		JScrollPane scroll = new JScrollPane(studentText);
		form.add(scroll);

		if (mode == 0) {
			form.add(new JLabel());
			statText = new JLabel();
			form.add(statText);
			updateStatText();
			form.add(new JLabel());
			bannText = new JLabel();
			form.add(bannText);
			updateBannText();
		}

		SpringUtilities.makeCompactGrid(form, mode == 0 ? 5 : 2, 2, 6, 6, 6, 6);
		self.add(form);
		this.setContentPane(self);
		this.pack();
		WindowUtils.setCenter(this);
		activeTable = this;
		if (dateSelector != null)
			dateSelector.setSelectedIndex(dateSelector.getItemCount() - 1);
		filterText.requestFocus();
	}

	private void newFilter() {
		RowFilter<? super AbstractTableModel, Object> rf = null;
		try {
			if (mode == 0)
				rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0, 1, 2, 3, 5, 6, 7, 8);
			else
				rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0, 1, 2, 3, 4);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
