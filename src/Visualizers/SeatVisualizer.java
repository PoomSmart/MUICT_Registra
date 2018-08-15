package Visualizers;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import MainApp.MainApp;
import Objects.Cell;
import Objects.Position;
import Objects.Student;
import Utilities.*;


class SeatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;
	private String date;

	private static final int textGap = 10;
	private static final int infoGap = 3;

	private int blue, yellow, gray;

	private Cell<String, Integer> selectedCell;

	public SeatPanel(Map<Integer, Student> students, String date) {
		this.students = students;
		this.date = date;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (e.getClickCount() == 1) {
					selectedCell = new Cell<String, Integer>(null, -1);
					selectedCell.setName(x, y);
					updateSelection();
				}
			}
		});
	}

	public void setStudents(Map<Integer, Student> students) {
		this.students = students;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private Position<Integer, Integer> studentPositionFromSelection() {
		return Position.positionByCellPosition(selectedCell);
	}

	private Student studentFromSelection() {
		for (Student student : students.values()) {
			if (student.getPosition().equals(studentPositionFromSelection()))
				return student;
		}
		return null;
	}

	public void updateSelection() {
		Student selectedStudent = studentFromSelection();
		if (selectedStudent == null)
			return;
		JOptionPane.showMessageDialog(this, selectedStudent.toString(0, date, false));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		blue = yellow = gray = 0;
		int tileWidth = SeatVisualizer.tileSize.width;
		int tileHeight = SeatVisualizer.tileSize.height;
		int shiftLeft = SeatVisualizer.shiftLeft;
		int shiftTop = SeatVisualizer.shiftTop;
		int width = SeatVisualizer.bounds.width;
		int height = SeatVisualizer.bounds.height;
		FontMetrics metrics = g.getFontMetrics();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				g.setColor(Color.white);
				Position<Integer, Integer> findPos = new Position<Integer, Integer>(width - x,height - y-1);
				
				Student student = MainApp.studentsByPositions.get(findPos);
				
				Student student2 = null;
				Integer ID = -1;
				if (student != null) {
					ID = student.getID();
					student2 = students.get(ID);
				}
				if (student2 != null) {
					Color infoColor = Color.WHITE;
					if (student2.isNormal(date)) {
						int presentCount = student2.getPresentCount();
						int absenceCount = student2.getAbsenceCount();
						float degrade = (float)presentCount / (presentCount + absenceCount);
						g.setColor(Color.getHSBColor((240.0f - (1 - degrade) * 46.0f) / 360.0f, 1.0f, 0.9f));
						blue++;
					} else if (student2.isLeft(date)) {
						g.setColor(Color.yellow);
						infoColor = Color.BLACK;
						yellow++;
					} else if (student2.isAbsent(date)) {
						g.setColor(Color.getHSBColor(0.0f, 0.0f, 0.9f));
						infoColor = Color.BLACK;
						gray++;
					}
					g.fillRect(shiftLeft + x * tileWidth, shiftTop + y * tileHeight, tileWidth, tileHeight);
					String nickname = student.getNickname();
					int labelWidth = metrics.stringWidth(nickname);
					int labelHeight = metrics.getHeight();
					int startY = tileHeight - infoGap - labelHeight * 2;
					g.setColor(infoColor);
					g.drawString(nickname, shiftLeft + x * tileWidth + (tileWidth - labelWidth) / 2,
							shiftTop + y * tileHeight + startY);
					startY += infoGap + labelHeight;
					String sID = ID.toString().substring(4, ID.toString().length());
					labelWidth = metrics.stringWidth(sID);
					g.drawString(sID, shiftLeft + x * tileWidth + (tileWidth - labelWidth) / 2,
							shiftTop + y * tileHeight + startY);
				} else {
					// Senior
					g.setColor(Color.getHSBColor(0.4f, 0.8f, 0.62f));
					g.fillRect(shiftLeft + x * tileWidth, shiftTop + y * tileHeight, tileWidth, tileHeight);
					String senior = "Senior";
					int labelWidth = metrics.stringWidth(senior);
					int labelHeight = metrics.getHeight();
					int startY = tileHeight - labelHeight;
					g.setColor(Color.WHITE);
					g.drawString(senior, shiftLeft + x * tileWidth + (tileWidth - labelWidth) / 2,
							shiftTop + y * tileHeight + startY);
				}
				findPos = null;
			}
			g.setColor(Color.gray);
			g.drawLine(shiftLeft, shiftTop + y * tileHeight, SeatVisualizer.absoluteSize.width,
					shiftTop + y * tileHeight);
			g.setColor(Color.black);
			String yLabel = CommonUtils.alphabet(height - y - 1);
			int labelWidth = metrics.stringWidth(yLabel);
			int labelHeight = metrics.getHeight();
			g.drawString(yLabel, shiftLeft - textGap - (int) (0.5 * labelWidth),
					shiftTop + (int) (0.5 * labelHeight) + y * tileHeight + (int) (0.5 * tileHeight));
		}
		for (int x = 0; x < width; x++) {
			g.setColor(Color.black);
			String xLabel = String.valueOf(width - x);
			int labelWidth = metrics.stringWidth(xLabel);
			g.drawString(xLabel, shiftLeft + x * tileWidth + (int) (0.5 * tileWidth) - (int) (0.5 * labelWidth),
					shiftTop - textGap);
			g.setColor(Color.gray);
			g.drawLine(shiftLeft + x * tileWidth, shiftTop, shiftLeft + x * tileWidth,
					SeatVisualizer.absoluteSize.height);
		}
		g.setColor(Color.gray);
		g.drawLine(SeatVisualizer.absoluteSize.width, shiftTop, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
		g.drawLine(shiftLeft, SeatVisualizer.absoluteSize.height, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
		g.drawString(
				String.format("Present: %d, Leave: %d, Absent: %d, Completeness: %.2f%%", blue, yellow, gray,
						(100.0 * blue / (blue + yellow + gray))),
				shiftLeft, SeatVisualizer.absoluteSize.height + textGap * 2);
	}

}

public class SeatVisualizer extends JFrame {

	private static final long serialVersionUID = 1L;

	public static SeatVisualizer activeVisualizer = null;

	public static final int shiftLeft = 30;
	public static final int shiftTop = 30;
	public static final Dimension bounds = new Dimension(9, 7); // width and height of seat visualizer
	public static final Dimension tileSize = new Dimension(55, 55); // width and height of each seat in seat visualizer
	public static final Dimension absoluteSize = new Dimension(bounds.width * tileSize.width + shiftLeft,
			bounds.height * tileSize.height + shiftTop);

	private SeatPanel panel;
	private Map<Integer, Student> currentStudents;
	private JComboBox<String> dateSelector;
	private String date;

	public SeatVisualizer() {
		this.setTitle(WindowUtils.realTitle("Stand Cheer Seats"));
		this.setSize(absoluteSize.width + shiftLeft, absoluteSize.height + (int) (tileSize.height * 1.8) + shiftTop);
		Container self = getContentPane();
		self.setLayout(new BoxLayout(self, BoxLayout.Y_AXIS));
		WindowUtils.setRelativeCenter(this, -this.getWidth() + bounds.width / 2 - 30, 0);
		date = DateUtils.getCurrentFormattedDate();
		reloadStudents();
		panel = new SeatPanel(currentStudents, date);
		panel.setPreferredSize(this.getSize());
		this.setPreferredSize(this.getSize());
		this.setMinimumSize(this.getSize());
		this.setMaximumSize(this.getSize());
		self.add(panel);
		List<String> dates = DateUtils.s_availableDates();
		dateSelector = new JComboBox<String>(dates.toArray(new String[0]));
		dateSelector.setSelectedIndex(dates.size() - 1);
		dateSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDate((String) dateSelector.getSelectedItem());
				updateIfPossible(true);
			}
		});
		JPanel form = new JPanel(new SpringLayout());
		form.add(new JLabel("Select Date:", SwingConstants.TRAILING));
		form.add(dateSelector);
		self.add(form);
		SpringUtilities.makeCompactGrid(form, 1, 2, 4, 4, 4, 4);
		this.setResizable(false);
		activeVisualizer = this;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});
	}

	private void reloadStudents() {
		currentStudents = DBUtils.getStudentsAllTime();
	}

	public static void updateIfPossible(boolean manual) {
		System.out.println("Update SeatVisualizer");
		if (activeVisualizer != null) {
			activeVisualizer.panel.setDate(manual ? activeVisualizer.date : DateUtils.getCurrentFormattedDate());
			activeVisualizer.reloadStudents();
			activeVisualizer.panel.setStudents(activeVisualizer.currentStudents);
			activeVisualizer.repaint();
		}
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
