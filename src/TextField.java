import javax.swing.JFrame;
import javax.swing.JTextField;

public class TextField extends JTextField {

	private static final long serialVersionUID = 1L;
	
	private JFrame frame;

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	
	public TextField(int alignment) {
		super(alignment);
	}
}
