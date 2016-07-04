import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class PlainTextDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public PlainTextDialog(String title, int width, int height, int margin, String text, boolean editable) {
		this.setTitle(title);
		this.setSize(width, height);
		CommonUtils.setCenter(this);
		
		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(layout);
		
		JTextArea textArea = new JTextArea();
		textArea.setText(text);
		textArea.setEditable(editable);
		
		layout.putConstraint(SpringLayout.WEST, textArea, margin, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.EAST, textArea, -margin, SpringLayout.EAST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, textArea, margin, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.SOUTH, textArea, -margin, SpringLayout.SOUTH, getContentPane());
		
		getContentPane().add(textArea);
	}

}
