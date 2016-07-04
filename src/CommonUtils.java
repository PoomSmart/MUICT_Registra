import java.io.File;
import java.util.Date;

import javax.swing.JFrame;

public class CommonUtils {
	
public enum FileType { REGULAR, NOTHERE, LOG }
	
	public static String filename(FileType type) {
		switch (type) {
		case REGULAR:
			return "present.csv";
		case NOTHERE:
			return "absent.csv";
		case LOG:
			return "log.txt";
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
	
	public static File fileFromPath(String filePath) {
		if (filePath == null)
			return null;
		return new File(filePath);
	}
	
	public static String filePath(FileType type, Date date) {
		return "Attendance/" + DateUtils.formattedDate(date) + "/" + filename(type);
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
}
