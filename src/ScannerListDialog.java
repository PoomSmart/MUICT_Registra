import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScannerListDialog extends JFrame {

	private static final long serialVersionUID = 212016L;
	
	private static final Integer maxListCount = 25;

	private List<Integer> IDs;
	private Map<Integer, Student> students;

	public ScannerListDialog(Map<Integer, Student> students) {
		this.setTitle("Scanned codes" + (Main.test ? " (Test Mode)" : ""));
		this.setLayout(new GridLayout(1 + maxListCount, 1));
		this.setSize(400, 600);
		//this.setLocationRelativeTo(null);
		this.IDs = new Vector<Integer>();
		this.students = students;
	}

	public List<Integer> getIDs() {
		return IDs;
	}
	
	public void addID(Integer ID) {
		Student student = students.get(ID);
		String slabel = " " + ID;
		if (student == null)
			slabel += ": Unknown";
		else
			slabel += ": " + student.getName() + " (" + student.getNickname() + ")";
		JLabel label = new JLabel(slabel, JLabel.LEFT);
		IDs.add(ID);
		this.getContentPane().add(label);
		this.revalidate();
		this.repaint();
	}

}
