package Visualizers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Objects.Position;
import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;

class SeatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;
	private int shiftLeft;
	private int shiftTop;

	private static final int textGap = 10;

	class Cell<X, Y> {
		public String x;
		public Integer y = -1;

		public Cell(String x, Integer y) {
			this.x = x;
			this.y = y;
		}

		public Cell(int wx, int wy) {
			setName(wx, wy);
		}

		public boolean isNull() {
			return x == null || y == -1;
		}

		public void setName(int x, int y) {
			int cx = (x - shiftLeft) / SeatVisualizer.tileSize.width;
			int cy = (y - shiftTop) / SeatVisualizer.tileSize.height;
			if (cx >= SeatVisualizer.bounds.width || x < shiftLeft || cy >= SeatVisualizer.bounds.height
					|| y < shiftTop)
				return;
			this.x = SeatPanel.alphabet(cx);
			this.y = cy;
		}

		public String toString() {
			if (isNull())
				return "Null";
			return String.format("%s-%d", x, y);
		}
	}

	private Cell<String, Integer> selectedCell;

	public SeatPanel(Map<Integer, Student> students, int shiftLeft, int shiftTop) {
		this.shiftLeft = shiftLeft;
		this.shiftTop = shiftTop;
		this.students = students;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (e.getClickCount() == 2) {
					selectedCell = new Cell<String, Integer>(x, y);
					updateSelection();
				}
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
			}
		});
	}

	public void setStudents(Map<Integer, Student> students) {
		this.students = students;
	}

	private static String alphabet(int x) {
		return Character.toString((char) (x + 'A'));
	}

	private Position<Integer, Integer> studentPositionFromSelection() {
		if (selectedCell == null)
			return Position.nullPosition;
		if (selectedCell.isNull())
			return Position.nullPosition;
		Position<Integer, Integer> position = new Position<Integer, Integer>(selectedCell.x.charAt(0) - 'A',
				selectedCell.y);
		return position;
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
		JOptionPane.showMessageDialog(this, selectedStudent);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int tileWidth = SeatVisualizer.tileSize.width;
		int tileHeight = SeatVisualizer.tileSize.height;
		for (int x = 0; x < SeatVisualizer.bounds.width; x++) {
			g.setColor(Color.black);
			FontMetrics metrics = g.getFontMetrics();
			String xLabel = alphabet(x);
			int labelWidth = metrics.stringWidth(xLabel);
			g.drawString(xLabel, shiftLeft + x * tileWidth + (int) (0.5 * tileWidth) - (int) (0.5 * labelWidth),
					shiftTop - textGap);
			for (int y = 0; y < SeatVisualizer.bounds.height; y++) {
				g.setColor(Color.white);
				boolean found = false;
				for (Student student : students.values()) {
					Position<Integer, Integer> position = student.getPosition();
					if (position.equals(new Position<Integer, Integer>(x, y))) {
						if (student.isNormal())
							g.setColor(Color.blue);
						else if (student.isLeft())
							g.setColor(Color.yellow);
						else if (student.isAbsent())
							g.setColor(Color.getHSBColor(0.0f, 0.0f, 0.9f));
						g.fillRect(shiftLeft + x * tileWidth, shiftTop + y * tileHeight, tileWidth, tileHeight);
						found = true;
						break;
					}
				}
				if (!found) {
					g.setColor(Color.white);
					g.fillRect(shiftLeft + x * tileWidth, shiftTop + y * tileHeight, tileWidth, tileHeight);
				}
			}
			g.setColor(Color.gray);
			g.drawLine(shiftLeft + x * tileWidth, shiftTop, shiftLeft + x * tileWidth,
					SeatVisualizer.absoluteSize.height);
		}
		for (int y = 0; y < SeatVisualizer.bounds.height; y++) {
			g.setColor(Color.gray);
			g.drawLine(shiftLeft, shiftTop + y * tileHeight, SeatVisualizer.absoluteSize.width,
					shiftTop + y * tileHeight);
			g.setColor(Color.black);
			String yLabel = String.valueOf(y);
			FontMetrics metrics = g.getFontMetrics();
			int labelWidth = metrics.stringWidth(yLabel);
			int labelHeight = metrics.getHeight();
			g.drawString(yLabel, shiftLeft - textGap - (int) (0.5 * labelWidth),
					shiftTop + labelHeight + y * tileHeight);
		}
		g.setColor(Color.gray);
		g.drawLine(SeatVisualizer.absoluteSize.width, shiftTop, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
		g.drawLine(shiftLeft, SeatVisualizer.absoluteSize.height, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
	}

}

public class SeatVisualizer extends JFrame {

	// FIXME: Fix bug with leave-with-reason students calculation
	// TODO: Fix up coloring

	private static final long serialVersionUID = 1L;

	public static SeatVisualizer activeVisualizer = null;

	private static final int shiftLeft = 30;
	private static final int shiftTop = 30;
	public static final Dimension bounds = new Dimension(14, 20);
	public static final Dimension tileSize = new Dimension(40, 25);
	public static final Dimension absoluteSize = new Dimension(bounds.width * tileSize.width + shiftLeft,
			bounds.height * tileSize.height + shiftTop);

	private SeatPanel panel;
	private Map<Integer, Student> currentStudents;
	private Map<Integer, Student> students;

	public SeatVisualizer(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Seat Visualizer"));
		this.setSize(absoluteSize.width + shiftLeft, absoluteSize.height + (int) (tileSize.height * 1.5) + shiftTop);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 1));
		CommonUtils.setRelativeCenter(this, -this.getWidth() + 45, 0);
		this.students = students;
		reloadStudents();
		panel = new SeatPanel(currentStudents, shiftLeft, shiftTop);
		panel.setPreferredSize(this.getSize());
		this.setPreferredSize(this.getSize());
		this.setMinimumSize(this.getSize());
		this.setMaximumSize(this.getSize());
		this.add(panel);
		this.setResizable(false);
		activeVisualizer = this;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				activeVisualizer = null;
			}
		});

		// TODO: Panel for Visualizer ?
	}

	private void reloadStudents() {
		currentStudents = StudentTable.currentStudentMap(students);
	}

	public static void updateIfPossible() {
		System.out.println("Update SeatVisualizer");
		if (activeVisualizer != null) {
			activeVisualizer.reloadStudents();
			activeVisualizer.panel.setStudents(activeVisualizer.currentStudents);
			activeVisualizer.repaint();
		}
	}

}
