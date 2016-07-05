package Main;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import Database.StudentDatabase;
import Dialogs.ControlCenterDialog;
import Dialogs.LeaveDialog;
import Dialogs.ScannerDialog;
import Dialogs.ScannerListDialog;
import Objects.Position;
import Objects.Student;
import Visualizers.SeatVisualizer;

public class Main {

	public static final boolean test = true;

	private static void runFrame(JFrame frame) throws InterruptedException {
		Thread t = new Thread() {
			public void run() {
				synchronized (frame) {
					while (frame.isVisible()) {
						try {
							frame.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		t.join();
	}

	// FIXME: Will be eventually removed
	private static void randomPosition(Map<Integer, Student> students) {
		Random r = new Random();
		Integer width = SeatVisualizer.bounds.width;
		Integer height = SeatVisualizer.bounds.height;
		for (Student student : students.values()) {
			if (student.getPosition().equals(Position.nullPosition)) {
				Position<Integer, Integer> position = new Position<>(r.nextInt(width), r.nextInt(height));
				for (Student student2 : students.values()) {
					if (!student.equals(student2)) {
						if (!position.equals(student2.getPosition()))
							student.setPosition(position);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Map<Integer, Student> students = new StudentDatabase("batch-14.csv").getStudents();
		randomPosition(students);

		ScannerDialog scannerDialog = new ScannerDialog(students);
		scannerDialog.setVisible(true);
		runFrame(scannerDialog);

		ScannerListDialog scannerListDialog = new ScannerListDialog(students);
		scannerListDialog.setVisible(true);
		scannerDialog.setList(scannerListDialog);
		runFrame(scannerListDialog);

		LeaveDialog leaveDialog = new LeaveDialog(students);
		leaveDialog.setVisible(true);
		runFrame(leaveDialog);

		ControlCenterDialog ccDialog = new ControlCenterDialog(students);
		ccDialog.setVisible(true);
		runFrame(ccDialog);

		scannerDialog.toFront();
		scannerDialog.getField().requestFocus();
	}

}
