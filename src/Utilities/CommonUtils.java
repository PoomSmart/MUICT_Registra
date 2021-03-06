package Utilities;

import java.io.File;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

import Objects.Constants;
import Objects.Status;

public class CommonUtils {

	public enum FileType {
		REGULAR, NOTHERE, LOG
	}

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

	public static String datePath(Date date) {
		return Constants.FILE_ROOT + DateUtils.getFormattedDate(date);
	}

	public static String filePath(FileType type, Date date) {
		return datePath(date) + "/" + filename(type);
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

	public static Vector<String> sameReason(String reason, int count) {
		Vector<String> reasons = new Vector<String>();
		while (count-- != 0)
			reasons.add(reason);
		return reasons;
	}

	public static <T> Vector<T> resolveDuplicates(Vector<T> list) {
		TreeSet<T> treeSet = new TreeSet<T>(list);
		Vector<T> nlist = new Vector<T>(treeSet);
		treeSet = null;
		return nlist;
	}

	public static String alphabet(int x) {
		return Character.toString((char) (x + 'A'));
	}

}
