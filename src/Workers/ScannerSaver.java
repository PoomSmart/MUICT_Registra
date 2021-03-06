package Workers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import Tables.StudentTable;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Visualizers.SeatVisualizer;

public class ScannerSaver {
	
	public static Vector<Integer> ComeToday = new Vector<Integer>();

	public static void doneAddingCodes(Vector<Integer> IDs, Vector<String> reasons, boolean append, FileType type,
			boolean force) throws IOException {
		if (type == FileType.LOG)
			return;
		String filePath = CommonUtils.filePath(type);
		File file = new File(filePath);
		if (file.exists() && !append && !force)
			throw new FileAlreadyExistsException("File already existed: " + filePath);
		if (!file.exists() && append)
			System.out.println("Note: appending file where it does not exist");
		if (type == CommonUtils.FileType.REGULAR) {
			System.out.println(String.format("Write code data to %s (type: %s, append: %s, force: %s)", filePath, type,
					append, force));
			ComeToday.addAll(CommonUtils.resolveDuplicates(IDs)); // add id of students who come today
			FileUtils.writeLines(file, CommonUtils.resolveDuplicates(IDs), append);
			//System.out.println("writing data to file: " + IDs);	
		} else if (type == CommonUtils.FileType.NOTHERE) {
			if (reasons == null) {
				System.out.println("null reasons");
				return;
			}
			StringBuilder lists = new StringBuilder();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
			for (int i = 0; i < IDs.size(); i++) {
				Integer ID = IDs.elementAt(i);
				System.out.println(ID);
				lists.append(ID + ",leave," + reasons.elementAt(i) + "\n");
			}
			writer.append(lists.toString());
			writer.close();
			lists = null;
			writer = null;

		}
		file = null;
		System.out.println(String.format("(%s mode) %s data to %s", type, (append ? "Append" : "Write"), filePath));
		SeatVisualizer.updateIfPossible(false);
		StudentTable.updateIfPossible();
	}

	public static void doneAddingCodes(Vector<Integer> IDs, boolean append, FileType type, boolean force)
			throws IOException {
		doneAddingCodes(IDs, null, append, type, force);
	}

	public static void doneAddingCodes(Vector<Integer> IDs, Vector<String> reasons, boolean append, FileType type)
			throws IOException {
		doneAddingCodes(IDs, reasons, append, type, false);
	}

	public static void doneAddingCodes(Vector<Integer> IDs, boolean append, FileType type) throws IOException {
		doneAddingCodes(IDs, append, type, false);
	}

	public static void doneAddingCode(Integer ID, String reason, boolean append, FileType type) throws IOException {
		Vector<Integer> IDs = new Vector<Integer>();
		IDs.addElement(ID);
		Vector<String> reasons = new Vector<String>();
		reasons.addElement(reason);
		doneAddingCodes(IDs, reasons, append, type);
		IDs = null;
		reasons = null;
	}
}
