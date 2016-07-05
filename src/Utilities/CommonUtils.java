package Utilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFrame;

import Main.Main;
import Objects.Constants;
import Objects.Status;

public class CommonUtils {
	
public enum FileType { REGULAR, NOTHERE, LOG }
	
	public static String filename(FileType type) {
		switch (type) {
		case REGULAR:
			return "present.csv";
		case NOTHERE:
			return "leave.csv";
		case LOG:
			return "log.txt";
		}
		return null;
	}
	
	public static Status.Type typeFromFileType(FileType type) {
		switch (type) {
		case REGULAR:
			return Status.Type.PRESENT;
		case NOTHERE:
			return Status.Type.LEAVE;
		case LOG:
			return null;
		}
		return null;
	}

	public static final String pMUICTID = "\\d\\d88\\d\\d\\d";
	
	public static Integer getID(String sID) {
		if (!sID.matches(pMUICTID))
			return -1;
		return Integer.parseInt(sID);
	}
	
	public static File fileFromType(FileType type) {
		return new File(filePath(type));
	}
	
	public static File fileFromType(FileType type, Date date) {
		return new File(filePath(type, date));
	}
	
	public static File fileFromPath(String filePath) {
		if (filePath == null)
			return null;
		return new File(filePath);
	}
	
	public static String filePath(FileType type, Date date) {
		return Constants.FILE_ROOT + DateUtils.formattedDate(date) + "/" + filename(type);
	}
	
	public static String filePath(FileType type) {
		return filePath(type, DateUtils.getCurrentDate());
	}

	public static boolean fileExistsAtPath(String filePath) {
		File file = null;
		if ((file = fileFromPath(filePath)) == null)
			return false;
		return file.exists();
	}

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
	
	public static Vector<String> sameReason(String reason, int count) {
		Vector<String> reasons = new Vector<String>();
		while (count-- != 0)
			reasons.add(reason);
		return reasons;
	}
	
	public static <T> Vector<T> resolveDuplicates(Vector<T> list) {
		return new Vector<T>(new TreeSet<T>(list));
	}
	
	public static void setDontClose(JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {}
		});
	}
}
