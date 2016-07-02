import java.io.IOException;
import java.util.Map;

public class Main {
	
	public static final boolean test = true;

	public static void main(String[] args) throws InterruptedException, IOException {
		
		Map<Integer, Student> students = new StudentDatabase("students-test.txt").getStudents();
		
		ScannerDialog scannerDialog = new ScannerDialog();
		scannerDialog.setVisible(true);
		Thread t1 = new Thread() {
			public void run() {
				synchronized (scannerDialog) {
					while (scannerDialog.isVisible()) {
						try {
							scannerDialog.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		t1.join();
		ScannerListDialog scannerListDialog = new ScannerListDialog(students);
		scannerListDialog.setVisible(true);
		scannerDialog.setList(scannerListDialog);
		Thread t2 = new Thread() {
			public void run() {
				synchronized (scannerListDialog) {
					while (scannerListDialog.isVisible()) {
						try {
							scannerListDialog.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		t2.join();
	}

}
