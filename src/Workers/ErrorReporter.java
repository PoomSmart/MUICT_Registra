package Workers;
import javax.swing.JOptionPane;

public class ErrorReporter {
	
	public static void report(Object message) {
		JOptionPane.showMessageDialog(null, message);
	}

	public static void report(Exception e) {
		e.printStackTrace();
		report(e.getStackTrace());
	}
}
