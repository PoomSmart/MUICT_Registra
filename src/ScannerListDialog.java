import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScannerListDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Integer defaultMaxListCount = 30;

	private Vector<Integer> IDs;
	private Vector<JLabel> labels;
	private Map<Integer, Student> students;

	public ScannerListDialog(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Scanned Codes"));
		this.setLayout(new GridLayout(defaultMaxListCount, 1));
		this.setSize(550, 600);
		CommonUtils.setRelativeCenter(this, 500, 0);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {}
		});
		this.IDs = new Vector<Integer>();
		this.labels = new Vector<JLabel>();
		this.students = students;
	}
	
	public void sort() {
		Collections.sort(IDs);
		update();
	}

	public Vector<Integer> getIDs() {
		return IDs;
	}

	private void update() {
		labels.removeAllElements();
		getContentPane().removeAll();
		int i = 0;
		for (Integer ID : IDs) {
			JLabel label = labelForStudentID(i++, ID);
			getContentPane().add(label);
			labels.addElement(label);
		}
		this.revalidate();
		this.repaint();
	}
	
	public void removeAtIndex(int index) {
		if (index == -1)
			return;
		int count = getContentPane().getComponentCount();
		if (count > index) {
			getContentPane().remove(index);
			IDs.remove(index);
			update();
		}
	}

	public void removeLast() {
		removeAtIndex(getContentPane().getComponentCount() - 1);
	}

	public void removeAll() {
		this.getContentPane().removeAll();
		IDs.removeAllElements();
		update();
	}
	
	private String labelString(int i, Integer ID, Student student) {
		String slabel = String.format(" (%d) %d", i, ID);
		if (student == null)
			slabel += ": Unknown";
		else
			slabel += String.format(": %s (%s)", student.getName(), student.getNickname());
		return slabel;
	}
	
	private JLabel labelForStudentID(int i, Integer ID) {
		Student student = students.get(ID);
		return new JLabel(labelString(i, ID, student), JLabel.LEFT);
	}

	public void addID(Integer ID) {
		labels.add(labelForStudentID(IDs.size() - 1, ID));
		IDs.add(ID);
		update();
	}

}
