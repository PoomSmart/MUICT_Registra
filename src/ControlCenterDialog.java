import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class ControlCenterDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public ControlCenterDialog(Map<Integer, Student> students) {
		this.setTitle(CommonUtils.realTitle("Control Center"));
		this.setSize(450, 100);
		CommonUtils.setRelativeCenter(this, 0, -250);
		this.setLayout(new FlowLayout());

		JButton showDBButton = new JButton("Attendance");
		Object[] dbOptions = { "Today", "All" };
		showDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showOptionDialog(null, "Select type:", "Attendance Type", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, dbOptions, dbOptions[0]);
				if (n != JOptionPane.CLOSED_OPTION) {
					StudentTable stuTable = new StudentTable(students, n);
					stuTable.setVisible(true);
				}
			}
		});
		this.getContentPane().add(showDBButton);

		JButton showLogButton = new JButton("Log");
		Object[] logOptions = { "Current", "All" };
		showLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showOptionDialog(null, "Select type:", "Log Type", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, logOptions, logOptions[0]);
				PlainTextDialog log = null;
				try {
					switch (n) {
					case 0:
						log = new PlainTextDialog("Log for " + DateUtils.getCurrentDate(), 500, 500, 5,
								FileUtils.readFileToString(CommonUtils.fileFromType(CommonUtils.FileType.LOG)), false);
						break;
					case 1:
						log = new PlainTextDialog("All Log", 500, 500, 5, "foo", false);
					}
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, "No log created");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				if (log != null)
					log.setVisible(true);
			}
		});
		this.getContentPane().add(showLogButton);

		this.setResizable(false);
	}
}
