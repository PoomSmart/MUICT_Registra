package Visualizers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Objects.Position;
import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;

class SeatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;

	public SeatPanel(Map<Integer, Student> students) {
		this.students = students;
	}

	public void setStudents(Map<Integer, Student> students) {
		this.students = students;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int tileWidth = SeatVisualizer.tileSize.width;
		int tileHeight = SeatVisualizer.tileSize.height;
		for (int x = 0; x < SeatVisualizer.bounds.width; x++) {
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
						found = true;
						break;
					}
				}
				if (!found)
					g.setColor(Color.white);
				g.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
				g.setColor(Color.gray);
				g.drawLine(0, y * tileHeight, SeatVisualizer.absoluteSize.width, y * tileHeight);
			}
			g.setColor(Color.gray);
			g.drawLine(x * tileWidth, 0, x * tileWidth, SeatVisualizer.absoluteSize.height);
		}
		g.setColor(Color.gray);
		g.drawLine(SeatVisualizer.absoluteSize.width, 0, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
		g.drawLine(0, SeatVisualizer.absoluteSize.height, SeatVisualizer.absoluteSize.width,
				SeatVisualizer.absoluteSize.height);
	}

}

public class SeatVisualizer extends JFrame {

	// FIXME: Fix bug with leave-with-reason students calculation
	// TODO: Fix up coloring

	private static final long serialVersionUID = 1L;

	public static SeatVisualizer activeVisualizer = null;

	public static final Dimension bounds = new Dimension(14, 20);
	public static final Dimension tileSize = new Dimension(40, 25);
	public static final Dimension absoluteSize = new Dimension(bounds.width * tileSize.width,
			bounds.height * tileSize.height);

	private SeatPanel panel;
	private Map<Integer, Student> currentStudents;
	private Map<Integer, Student> students;

	public SeatVisualizer(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Seat Visualizer"));
		this.setSize(absoluteSize.width, absoluteSize.height + (int) (tileSize.height * 1.5));
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 1));
		CommonUtils.setRelativeCenter(this, -this.getWidth() + 45, 0);
		this.students = students;
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
		
		// TODO: Panel for Visualizer
	}

	private void reloadStudents() {
		currentStudents = StudentTable.currentStudentMap(students);
	}

	public static void updateIfPossible() {
		System.out.println("Updating SeatVisualizer");
		if (activeVisualizer != null) {
			activeVisualizer.reloadStudents();
			activeVisualizer.panel.setStudents(activeVisualizer.currentStudents);
			activeVisualizer.repaint();
		}
	}

}
