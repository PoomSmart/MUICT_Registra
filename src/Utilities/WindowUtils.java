package Utilities;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import Main.Main;

public class WindowUtils {

	public static void setRelativeCenter(JFrame frame, int x, int y) {
		frame.setLocationRelativeTo(null);
		frame.setLocation(frame.getX() + x, frame.getY() + y);
	}

	public static void setCenter(JFrame frame) {
		setRelativeCenter(frame, 0, 0);
	}

	public static String realTitle(String title) {
		return title + (Main.test ? " (Test Mode)" : "");
	}
	
	public static void setDontClose(JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {}
		});
	}
}
