import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

public class Dialog extends JFrame {

	private static final long serialVersionUID = 2561998L;

	private static final boolean test = true;

	private Set<Integer> IDs;
	private static final String pMUICTID = "\\d\\d88\\d\\d\\d";

	private JLabel statusLabel;

	public Dialog(String title, int width, int height) {
		this.setTitle("Scanner");
		this.setLayout(new GridLayout(3, 1));
		this.setSize(width, height);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.out.println("Exit");
				System.exit(0);
			}
		});
		TextField field = new TextField(1);
		field.setHorizontalAlignment(JTextField.CENTER);
		AbstractDocument document = (AbstractDocument)(field.getDocument());
		document.setDocumentFilter(new DocumentFilter());
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				validate();
			}

			public void removeUpdate(DocumentEvent e) {
				validate();
			}

			public void insertUpdate(DocumentEvent e) {
				validate();
			}

			public void validate() {
				String text = field.getText();
				String ID = null;
				if (!test) {
					if (text.length() != 14)
						return;
					ID = text.substring(6, 6 + 7);
					if (!ID.matches(pMUICTID))
						return;
				} else {
					if (!text.matches(pMUICTID))
						return;
					ID = text;
				}
				if (ID != null) {
					System.out.println("-> " + ID);
					IDs.add(Integer.parseInt(ID));
					((Dialog)field.getFrame()).setStatus("Added " + ID);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
				        public void run() {
							field.setText("");
						}
					});
				}
			}

		});

		this.getContentPane().add(field);
		field.setFrame(this);

		JLabel des = new JLabel("Detecting scanned code", JLabel.CENTER);
		this.getContentPane().add(des);
		this.setLocationRelativeTo(null);
		this.IDs = new HashSet<Integer>();
		this.statusLabel = new JLabel("", JLabel.CENTER);
		this.statusLabel.setForeground(Color.gray);
		this.getContentPane().add(this.statusLabel);
	}

	public Dialog(String title) {
		this(title, 400, 140);
	}

	public Set<Integer> getIDs() {
		return IDs;
	}

	public void setStatus(String status) {
		this.statusLabel.setText(status);
	}

}
