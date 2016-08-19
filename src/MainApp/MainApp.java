package MainApp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

import Database.StudentDatabase;
import Dialogs.ControlCenterDialog;
import Dialogs.LeaveDialog;
import Dialogs.ScannerDialog;
import Dialogs.ScannerListDialog;
import Objects.Constants;
import Objects.Position;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DateUtils;
import Utilities.WindowUtils;
import Visualizers.SeatVisualizer;
import Workers.AcceptanceAssigner;
import Workers.AllergiesAssigner;
import Workers.SpecialAssigner;

public class MainApp {

	public static final boolean test = false;

	public static Map<Integer, Student> db;

	private static void runFrame(JFrame frame) throws InterruptedException {
		WindowUtils.setDontClose(frame);
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
		boolean small = false;
		if (width * height < students.size()) {
			System.out.println("Warning: ID duplication occurs (Columns x Rows < Map Size)");
			small = true;
		}
		Random r = new Random();
		Vector<Position<Integer, Integer>> positions = new Vector<Position<Integer, Integer>>();
		for (Student student : students.values()) {
			if (student.getPosition().equals(Position.nullPosition)) {
				Position<Integer, Integer> position;
				int numRandom = 0;
				do {
					position = new Position<>(r.nextInt(width), r.nextInt(height));
				} while (positions.contains(position) && (numRandom++ < 20 || !small));
				positions.add(position);
				student.setPosition(position);
			}
		}
	}

	public static void createPathIfNecessary(String path) {
		File output = new File(path);
		if (!output.exists()) {
			try {
				System.out.println("Create directory " + Files.createDirectory(output.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// License agreement
		if (!CommonUtils.fileExistsAtPath("agreed")) {
			JPanel panel = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(530, 800);
				}
			};

			// We can use JTextArea or JLabel to display messages
			JTextArea textArea = new JTextArea();
			textArea.setText(FileUtils.readFileToString(new File("LICENSE"), Charset.defaultCharset()));
			textArea.setEditable(false);
			panel.setLayout(new BorderLayout());
			panel.add(new JScrollPane(textArea));
			Object buttonLabels[] = { "Agree", "Disagree " };
			int answer = JOptionPane.showOptionDialog(null, panel, "License Agreement", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, buttonLabels, buttonLabels[0]);
			if (answer != JOptionPane.YES_OPTION)
				System.exit(0);
			FileUtils.write(new File("agreed"), "");
		}
		Map<Integer, Student> students = new StudentDatabase("batch-14-new2.csv").getStudents();
		randomPosition(db = students);
		AllergiesAssigner.assignAll(db);
		AcceptanceAssigner.assignAll(db);
		SpecialAssigner.assignAll(db);

		// Create our working directory
		createPathIfNecessary(Constants.FILE_ROOT);

		// Create a directory for current date, if necessary
		String datePath = CommonUtils.datePath(DateUtils.getCurrentDate());
		createPathIfNecessary(datePath);

		ScannerDialog scannerDialog = new ScannerDialog();
		scannerDialog.setVisible(true);
		runFrame(scannerDialog);

		ScannerListDialog scannerListDialog = new ScannerListDialog();
		scannerListDialog.setVisible(true);
		scannerDialog.setList(scannerListDialog);
		runFrame(scannerListDialog);

		LeaveDialog leaveDialog = new LeaveDialog();
		leaveDialog.setVisible(true);
		runFrame(leaveDialog);

		ControlCenterDialog ccDialog = new ControlCenterDialog();
		ccDialog.setVisible(true);
		runFrame(ccDialog);

		SeatVisualizer vis = new SeatVisualizer();
		vis.setVisible(true);
		runFrame(vis);

		// Initially focus scanner window
		scannerDialog.toFront();
		scannerDialog.getField().requestFocus();
	}

}
