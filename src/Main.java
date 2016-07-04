import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;

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

	public static void main(String[] args) throws InterruptedException, IOException {
		Map<Integer, Student> students = new StudentDatabase("batch-14.csv").getStudents();
		
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
