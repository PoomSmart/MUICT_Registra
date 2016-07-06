package Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;

import Database.StudentDatabase;
import Dialogs.ControlCenterDialog;
import Dialogs.LeaveDialog;
import Dialogs.ScannerDialog;
import Dialogs.ScannerListDialog;
import Objects.Position;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DateUtils;
import Visualizers.SeatVisualizer;

public class Main {

	public static final boolean test = true;

	private static void runFrame(JFrame frame) throws InterruptedException {
		CommonUtils.setDontClose(frame);
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
		Integer width = SeatVisualizer.bounds.width;
		Integer height = SeatVisualizer.bounds.height;
		if (width * height < students.size()) {
			System.out.println("Couldn't random (Columns x Rows < Map Size)");
			return;
		}
		Random r = new Random();
		Vector<Position<Integer, Integer>> positions = new Vector<Position<Integer, Integer>>();
		for (Student student : students.values()) {
			if (student.getPosition().equals(Position.nullPosition)) {
				Position<Integer, Integer> position;
				do {
					position = new Position<>(r.nextInt(width), r.nextInt(height));
				} while (positions.contains(position));
				positions.add(position);
				student.setPosition(position);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Map<Integer, Student> students = new StudentDatabase("batch-14.csv").getStudents();
		randomPosition(students);
		
		String datePath = CommonUtils.datePath(DateUtils.getCurrentDate());
		File file = new File(datePath);
		if (!file.exists())
			System.out.println("Create directory " + Files.createDirectory(file.toPath()));
		
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
		
		SeatVisualizer vis = new SeatVisualizer(students);
		vis.setVisible(true);
		runFrame(vis);

		scannerDialog.toFront();
		scannerDialog.getField().requestFocus();
	}

}
