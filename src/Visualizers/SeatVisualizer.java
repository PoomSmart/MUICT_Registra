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

import Objects.Cell;
import Objects.Position;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DBUtils;
import Utilities.WindowUtils;

class SeatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;

	private static final int textGap = 10;

	private Cell<String, Integer> selectedCell;

	public SeatPanel(Map<Integer, Student> students) {
		this.students = students;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (e.getClickCount() == 2) {
					selectedCell = new Cell<String, Integer>(null, -1);
					selectedCell.setName(x, y);
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

	private Position<Integer, Integer> studentPositionFromSelection() {
		return CommonUtils.positionByCellPosition(selectedCell);
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
		int shiftLeft = SeatVisualizer.shiftLeft;
		int shiftTop = SeatVisualizer.shiftTop;
		int width = SeatVisualizer.bounds.width;
		int height = SeatVisualizer.bounds.height;
		for (int x = 0; x < width; x++) {
			g.setColor(Color.black);
			FontMetrics metrics = g.getFontMetrics();
			String xLabel = String.valueOf(width - x);
			int labelWidth = metrics.stringWidth(xLabel);
			g.drawString(xLabel, shiftLeft + x * tileWidth + (int) (0.5 * tileWidth) - (int) (0.5 * labelWidth),
					shiftTop - textGap);
			for (int y = 0; y < height; y++) {
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
		for (int y = 0; y < height; y++) {
			g.setColor(Color.gray);
			g.drawLine(shiftLeft, shiftTop + y * tileHeight, SeatVisualizer.absoluteSize.width,
					shiftTop + y * tileHeight);
			g.setColor(Color.black);
			FontMetrics metrics = g.getFontMetrics();
			String yLabel = CommonUtils.alphabet(height - y - 1);
			int labelWidth = metrics.stringWidth(yLabel);
			int labelHeight = metrics.getHeight();
			g.drawString(yLabel, shiftLeft - textGap - (int) (0.5 * labelWidth),
					shiftTop + (int) (0.5 * labelHeight) + y * tileHeight + (int) (0.5 * tileHeight));
		}
		g.setColor(Color.gray);
		g.drawLine(SeatVisualizer.absoluteSize.width, shiftTop, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
		g.drawLine(shiftLeft, SeatVisualizer.absoluteSize.height, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
	}

}

public class SeatVisualizer extends JFrame {

	private static final long serialVersionUID = 1L;

	public static SeatVisualizer activeVisualizer = null;
	
	public static final int shiftLeft = 30;
	public static final int shiftTop = 30;
	public static final Dimension bounds = new Dimension(10, 10);
	public static final Dimension tileSize = new Dimension(40, 40);
	public static final Dimension absoluteSize = new Dimension(bounds.width * tileSize.width + shiftLeft,
			bounds.height * tileSize.height + shiftTop);

	private SeatPanel panel;
	private Map<Integer, Student> currentStudents;

	public SeatVisualizer() {
		this.setTitle(WindowUtils.realTitle("Seat Visualizer"));
		this.setSize(absoluteSize.width + shiftLeft, absoluteSize.height + (int) (tileSize.height * 1.5) + shiftTop);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 1));
		WindowUtils.setRelativeCenter(this, -this.getWidth() + bounds.width / 2, 0);
		reloadStudents();
		panel = new SeatPanel(currentStudents);
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
	}

	private void reloadStudents() {
		currentStudents = DBUtils.getCurrentStudents();
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
