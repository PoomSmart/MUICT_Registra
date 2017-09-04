package Tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import Objects.Position;
import Objects.Status;
import Objects.Student;
import Utilities.DBUtils;
import Utilities.DateUtils;
import Utilities.SpringUtilities;
import Utilities.WindowUtils;

public class StudentTable extends JFrame {

	// TODO: Disallow table windows duplication

	private static final long serialVersionUID = 1L;

	public static Vector<StudentTable> activeTables = new Vector<StudentTable>();

	private Map<Integer, Student> internalStudents;

	private JTextField filterText;
	private JTextArea studentText;
	private JLabel statText;
	private JLabel bannText;
	private JLabel attendanceText;
	private JLabel perSectionText;
	private JLabel perSectionLeaveText;
	private JLabel totalPerSectionText;
	private JLabel PresentMenWomenText;
	private JLabel LeaveMenWomenText;
	private JComboBox<String> dateSelector;

	private Object[][] data;

	public static int totalPresent;
	public static int totalAbsent;
	public static int totalLeft;
	public static int totalPresentIslamic;
	public static int totalIslamic;
	
	public static int totalMen;
	public static int totalWomen;

	public static int MenLeft;
	public static int WomenLeft;

	public static int totalEverydayAttend;
	public static int totalSomeAttend;
	public static int totalOnceAttend;

	private JTable table;
	private final int mode;
	private String date;
	
	
	private int bannCount[];
	private int bannPresentCount[];
	private int perSectionCount[];
	private int perSectionLeave[];

	private static final String[] names = { "ID", "First Name", "Last Name", "Nickname", "Gender", "Status",
			"Acceptance", "Position", "Bann", "Section" };
	private static final String[] names_global = { "ID", "First Name", "Last Name", "Nickname", "Gender", "#Present",
			"#Here", "#Leave", "#Absence", "Bann", "Section", "Position" };
	@SuppressWarnings("rawtypes")
	private static final Class[] columns = new Class[] { Integer.class, String.class, String.class, String.class,
			String.class, Status.class, String.class, Position.class, Integer.class, Integer.class };
	@SuppressWarnings("rawtypes")
	private static final Class[] columns_global = new Class[] { Integer.class, String.class, String.class, String.class,
			String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,
			Position.class };

	private TableRowSorter<? extends AbstractTableModel> sorter;
	
	public static void updateIfPossible() {
		for (StudentTable activeTable : activeTables) {
			//System.out.println("Update StudentTable");
			activeTable.updateInternalStudents();
			((AbstractTableModel) activeTable.table.getModel()).fireTableDataChanged();
		}
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
				arr[i][9] = student.getSection();
			} else {
				int presentCount = student.getPresentCount();
				int leaveCount = student.getLeaveCount();
				arr[i][5] = presentCount;
				arr[i][6] = presentCount + leaveCount;
				arr[i][7] = leaveCount;
				arr[i][8] = student.getAbsenceCount();
				arr[i][9] = student.getBann();
				arr[i][10] = student.getSection();
				arr[i][11] = student.getCellPosition();
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
					"Total Present: %d (Islamic: %d/%d), Total Absent: %d, Total Left: %d, Total Ever Here: %d",
					totalPresent, totalPresentIslamic, totalIslamic, totalAbsent, totalLeft, totalPresent + totalLeft));
	} 

	private void updatePresentMenWomenText()
	{
		if (PresentMenWomenText != null)
			PresentMenWomenText.setText("Total men: " + totalMen + ", Total women: " + totalWomen);
	}
	
	private void updateLeaveMenWomenText()
	{
		if(LeaveMenWomenText != null)
			LeaveMenWomenText.setText("Total leave men: " + MenLeft + ", Total leave women: " + WomenLeft);
	}
	
	private void updateTotalPerSectionText()
	{
		if(totalPerSectionText != null)
		{
			totalPerSectionText.setText("Total sec1: " + (perSectionCount[0] + perSectionLeave[0]) +
										", Total sec2: " + (perSectionCount[1] + perSectionLeave[1]) +
										", Total sec3: " + (perSectionCount[2] + perSectionLeave[2]));
		}
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

	private void updateAttendanceText() {
		if (attendanceText != null)
			attendanceText.setText(
					String.format("Attend every day: %d, Attend more than one: %d, Attend once: %d, Never attend: %d",
							totalEverydayAttend, totalSomeAttend, totalOnceAttend,
							internalStudents.size() - totalEverydayAttend - totalSomeAttend - totalOnceAttend));
	}

	private void updatePerSectionText() {
		if (perSectionText != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= 3; i++) {
				sb.append(String.format("Section %d present: %d", i, perSectionCount[i - 1]));
				if (i != 3)
					sb.append(", ");
			}
			perSectionText.setText(sb.toString());
			sb = null;
		}
	}
	
	private void updatePerSectionLeaveText()
	{
		if(perSectionLeaveText != null)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= 3; i++) {
				sb.append(String.format("Section %d leave: %d", i, perSectionLeave[i - 1]));
				if (i != 3)
					sb.append(", ");
			}
			perSectionLeaveText.setText(sb.toString());
			sb = null;
		}
	}

	public void updateInternalStudents() {
		perSectionCount = new int[3];
		perSectionLeave = new int[3];
		if (mode == 0) {
			bannCount = new int[10];
			bannPresentCount = new int[10];
			totalIslamic = totalPresentIslamic = 0;
			totalPresent = totalAbsent = totalLeft = MenLeft = WomenLeft = 0;
			totalMen = totalWomen = 0;
			internalStudents = DBUtils.getStudents(DateUtils.dateFromString(date));
			for (Entry<Integer, Student> entry : internalStudents.entrySet()) {
				Student student = entry.getValue();
				bannCount[student.getBann() - 1]++;
				if (student.isNormal(date)) {
					bannPresentCount[student.getBann() - 1]++;
					perSectionCount[student.getSection() - 1]++;
					totalPresent++;
					if(student.isMan())
						totalMen++;
					else if(student.isWoman())
						totalWomen++;
				} else if (student.isAbsent(date))
					totalAbsent++;
				else if (student.isLeft(date))
				{
					perSectionLeave[student.getSection()-1]++;
					if(student.isMan())
						MenLeft++;
					else if(student.isWoman())
						WomenLeft++;
					totalLeft++;
				}
					
				if (student.isIslamic()) {
					if (student.isNormal(date))
						totalPresentIslamic++;
					totalIslamic++;
				}
			}
			updateStatText();
			updateBannText();
			updatePresentMenWomenText();
			updateLeaveMenWomenText();
			updatePerSectionLeaveText();
			updateTotalPerSectionText();
		} else {
			totalEverydayAttend = totalSomeAttend = totalOnceAttend = 0;
			internalStudents = DBUtils.getStudentsAllTime();
			int totalActivityDays = DateUtils.availableDates().size();
			for (Entry<Integer, Student> entry : internalStudents.entrySet()) {
				Student student = entry.getValue();
				int presentCount = student.getPresentCount();
				int leaveCount = student.getLeaveCount();
				if (presentCount > 0) {
					if (presentCount+leaveCount == totalActivityDays)
						totalEverydayAttend++;
					else if (presentCount >= 1) {
						if (presentCount > 1)
							totalSomeAttend++;
						else
							totalOnceAttend++;
					}
					perSectionCount[student.getSection() - 1]++;
				}
			}
			updateAttendanceText();
		}
		updatePerSectionText();
		data = toData(internalStudents, mode);
	}

	public void updateTitle() {
		String title = "Attendance";
		if (mode == 0)
			title += " for " + DateUtils.getNormalFormattedDate(DateUtils.dateFromString(date));
		if (mode > 1)
			return;
		this.setTitle(WindowUtils.realTitle(title));
	}

	public StudentTable(int mode) {
		this(mode, DateUtils.getFormattedDate(DateUtils.availableDates().lastElement()));
	}

	public StudentTable(int mode, String date) {
		this.mode = mode;
		this.date = date;
		updateInternalStudents();
		updateTitle();

		JPanel self = new JPanel();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		this.setSize(1100, 980);

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

			public Class<?> getColumnClass(int col) {
				Class<?> clazz = mode == 0 ? columns[col] : columns_global[col];
				return clazz;
			}

		}

		StudentTableModel model = new StudentTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		sorter = new TableRowSorter<StudentTableModel>(model);
		table.setRowSorter(sorter);
		table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), 600));
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				int selectedRows = 0;
				int minIndex = -1;
				int maxIndex = -1;
				if (!lsm.isSelectionEmpty()) {
					minIndex = lsm.getMinSelectionIndex();
					maxIndex = lsm.getMaxSelectionIndex();
					for (int i = minIndex; i <= maxIndex; i++) {
						if (lsm.isSelectedIndex(i))
							selectedRows++;
					}
				}
				if (selectedRows > 1) {
					StringBuilder sb = new StringBuilder();
					sb.append("Selected items: " + selectedRows + "\n");
					sb.append("Min index: " + minIndex + "\n");
					sb.append("Max index: " + maxIndex);
					studentText.setText(sb.toString());
					sb = null;
					return;
				}
				int viewRow = minIndex;
				if (viewRow < 0)
					studentText.setText("");
				else {
					int modelRow = table.convertRowIndexToModel(viewRow);
					studentText.setText(
							internalStudents.get(table.getModel().getValueAt(modelRow, 0)).toString(mode, date, false));
					studentText.setCaretPosition(0);
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
					updateTitle();
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
		studentText.setBorder(BorderFactory.createLineBorder(Color.gray));
		studentText.setEditable(false);
		studentText.setFont(studentText.getFont().deriveFont(14f));
		JScrollPane scroll = new JScrollPane(studentText);
		scroll.setPreferredSize(new Dimension(studentText.getWidth(), 100));
		scroll.setWheelScrollingEnabled(true);
		scroll.setFocusable(false);
		form.add(scroll);

		if (mode == 0) {
			form.add(new JLabel());
			form.add(statText = new JLabel());
			updateStatText();
			form.add(new JLabel());
			form.add(bannText = new JLabel());
			updateBannText();
			form.add(new JLabel());
			form.add(PresentMenWomenText = new JLabel());
			updatePresentMenWomenText();
			form.add(new JLabel());
			form.add(LeaveMenWomenText = new JLabel());
			updateLeaveMenWomenText();
			form.add(new JLabel());
			form.add(perSectionText = new JLabel());
			updatePerSectionText();
			form.add(new JLabel());
			form.add(perSectionLeaveText = new JLabel());
			updatePerSectionLeaveText();
			form.add(new JLabel());
			form.add(totalPerSectionText = new JLabel());
			updateTotalPerSectionText();
					
		} else {
			form.add(new JLabel());
			form.add(attendanceText = new JLabel());
			updateAttendanceText();
			form.add(new JLabel());
			form.add(perSectionText = new JLabel());
			updatePerSectionText();
		}

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {

				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
				if (isSelected)
					return c;
				int modelRow = sorter.convertRowIndexToModel(row);
				Integer ID = (Integer) table.getModel().getValueAt(modelRow, 0);
				Student student = internalStudents.get(ID);
				if (student.unableToJoin()) {
					c.setBackground(student.isCheerleader() ? Color.BLUE : Color.GRAY);
					c.setForeground(Color.WHITE);
				} else {
					c.setBackground(table.getBackground());
					c.setForeground(table.getForeground());
				}
				return c;
			}
		});
		table.setDefaultRenderer(Integer.class, table.getDefaultRenderer(Object.class));

		SpringUtilities.makeCompactGrid(form, mode == 0 ? 10 : 4, 2, 6, 6, 6, 6); //set size of window (change 10 to others)
		self.add(form);
		this.setContentPane(self);
		this.pack();
		WindowUtils.setCenter(this);
		activeTables.add(this);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				activeTables.remove(this);
			}
		});
		if (dateSelector != null)
			dateSelector.setSelectedIndex(dateSelector.getItemCount() - 1);
		filterText.requestFocus();
	}

	private void newFilter() {
		RowFilter<? super AbstractTableModel, Object> rf = null;
		try {
			if (mode == 0)
				rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0, 1, 2, 3, 5, 6, 7, 8, 9);
			else
				rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0, 1, 2, 3, 4);
		} catch (PatternSyntaxException e) {
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
