import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class ScannerSaver {

	public static void doneAddingCode(Vector<Integer> IDs, boolean append) throws Exception {
		String filePath = "Attendance/" + DateUtils.getCurrentFormattedDate() + "/present.csv";
		File file = new File(filePath);
		if (file.exists() && !append)
			throw new Exception("File already existed: " + filePath);
		FileUtils.writeLines(file, IDs, append);
	}
}
