package Visualizers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Objects.Position;
import Objects.Student;
import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DateUtils;

class SeatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;

	public SeatPanel(Map<Integer, Student> students) {
		this.students = students;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int tileWidth = SeatVisualizer.tileSize.width;
		int tileHeight = SeatVisualizer.tileSize.height;
		for (int x = 0; x < SeatVisualizer.bounds.width; x++) {
			for (int y = 0; y < SeatVisualizer.bounds.height; y++) {
				boolean found = false;
				for (Student student : students.values()) {
					Position<Integer, Integer> position = student.getPosition();
					if (position.equals(new Position<Integer, Integer>(x, y))) {
						g.setColor(Color.blue);
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
		g.drawLine(SeatVisualizer.absoluteSize.width, 0, SeatVisualizer.absoluteSize.width, SeatVisualizer.absoluteSize.height);
		g.drawLine(0, SeatVisualizer.absoluteSize.height, SeatVisualizer.absoluteSize.width, SeatVisualizer.absoluteSize.height);
	}

}

public class SeatVisualizer extends JFrame {
	private static final long serialVersionUID = 1L;

	private Map<Integer, Student> students;
	public static final Dimension bounds = new Dimension(12, 20);
	public static final Dimension tileSize = new Dimension(40, 25);
	public static final Dimension absoluteSize = new Dimension(bounds.width * tileSize.width,
			bounds.height * tileSize.height);

	public SeatVisualizer(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Seat Visualizer"));
		this.setSize(absoluteSize.width, absoluteSize.height + (int)(tileSize.height * 1.5));
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 1));
		CommonUtils.setCenter(this);
		StudentTable table = new StudentTable(students, 0);
		Map<Integer, Student> currentStudents = table.studentMapForDate(DateUtils.getCurrentDate(), FileType.REGULAR);
		if (currentStudents != null) {
			SeatPanel panel = new SeatPanel(students = currentStudents);
			panel.setPreferredSize(this.getSize());
			this.setPreferredSize(this.getSize());
			this.setMinimumSize(this.getSize());
			this.setMaximumSize(this.getSize());
			this.add(panel);
			this.setResizable(false);
		}
	}

}
