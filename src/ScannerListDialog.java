import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
		this.setTitle("Scanned Codes" + (Main.test ? " (Test Mode)" : ""));
		this.setLayout(new GridLayout(maxListCount, 1));
		this.setLocation(1200, 320);
		this.setSize(450, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {}
		});
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
			slabel += String.format(": %s (%s)", student.getName(), student.getNickname());
		JLabel label = new JLabel(slabel, JLabel.LEFT);
		IDs.add(ID);
		this.getContentPane().add(label);
		this.revalidate();
		this.repaint();
	}

}
