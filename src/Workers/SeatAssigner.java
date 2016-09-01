package Workers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import MainApp.MainApp;
import Objects.Position;
import Objects.Student;
import Visualizers.SeatVisualizer;

public class SeatAssigner {
	
	public static final boolean used = true;
	
	public static void assignAll(Map<Integer, Student> db) {
		if (!used)
			return;
		try {
			List<String> seats = FileUtils.readLines(new File("seats.csv"));
			for (String seat : seats) {
				String[] tuples = seat.split(",");
				if (tuples.length == 2) {
					Integer ID = Integer.parseInt(tuples[0]);
					Student student = db.get(ID);
					if (student == null) {
						System.out.println("ID: " + ID + " not found");
						continue;
					}
					Position<Integer, Integer> position = Position.positionFromCellString(tuples[1]);
					student.setPosition(position);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void preheat() {
		Map<Integer, Student> students = MainApp.db;
		Integer width = SeatVisualizer.bounds.width;
		Integer height = SeatVisualizer.bounds.height;
		boolean small = false;
		if (width * height < students.size())
			small = true;
		Random r = new Random();
		Vector<Position<Integer, Integer>> positions = new Vector<Position<Integer, Integer>>();
		StringBuilder sb = new StringBuilder();
		for (Student student : students.values()) {
			if (student.getPosition().equals(Position.nullPosition)) {
				Position<Integer, Integer> position;
				int numRandom = 0;
				do {
					position = new Position<>(r.nextInt(width), r.nextInt(height));
				} while (positions.contains(position) && (numRandom++ < 20 || !small));
				positions.add(position);
				sb.append(String.format("%d,%s\n", student.getID(), position.toCellString()));
			}
		}
		positions = null;
		r = null;
		try {
			FileUtils.write(new File("seats.csv"), sb.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
