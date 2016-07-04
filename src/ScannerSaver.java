import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class ScannerSaver {
	
	public enum Type { REGULAR, NOTHERE }
	
	private static String filename(Type type) throws FileNotFoundException {
		switch (type) {
		case REGULAR:
			return "present.csv";
		case NOTHERE:
			return "absent.csv";
		}
		throw new FileNotFoundException("");
	}
	
	public static String attendancePath(Type type) throws FileNotFoundException {
		return "Attendance/" + DateUtils.getCurrentFormattedDate() + "/" + filename(type);
	}
	
	public static void doneAddingCode(Vector<Integer> IDs, boolean append, Type type, boolean force) throws IOException {
		String filePath = attendancePath(type);
		File file = new File(filePath);
		if (file.exists() && !append && !force)
			throw new FileAlreadyExistsException("File already existed: " + filePath);
		/*if (!file.exists() && append)
			System.out.println("Note: appending file where it does not exist");*/
		FileUtils.writeLines(file, IDs, append);
		System.out.println(String.format("(%s mode) %s data to %s", type, (append ? "Append" : "Write"), filePath));
	}
	
	public static void doneAddingCode(Vector<Integer> IDs, boolean append, Type type) throws IOException {
		doneAddingCode(IDs, append, type, false);
	}
}
