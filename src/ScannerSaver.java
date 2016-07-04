import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class ScannerSaver {
	
	public static void doneAddingCodes(Vector<Integer> IDs, Vector<String> reasons, boolean append, CommonUtils.FileType type, boolean force) throws IOException {
		String filePath = CommonUtils.filePath(type);
		File file = new File(filePath);
		if (file.exists() && !append && !force)
			throw new FileAlreadyExistsException("File already existed: " + filePath);
		if (!file.exists() && append)
			System.out.println("Note: appending file where it does not exist");
		if (type == CommonUtils.FileType.REGULAR)
			FileUtils.writeLines(file, IDs, append);
		else {
			if (reasons == null)
				return;
			StringBuilder lists = new StringBuilder();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
			for (int i = 0; i < IDs.size(); i++) {
				Integer ID = IDs.elementAt(i);
				lists.append(ID + ",leave," + reasons.elementAt(i) + "\n");
			}
			writer.append(lists.toString());
			writer.close();
		}
		System.out.println(String.format("(%s mode) %s data to %s", type, (append ? "Append" : "Write"), filePath));
	}
	
	public static void doneAddingCodes(Vector<Integer> IDs, boolean append, CommonUtils.FileType type, boolean force) throws IOException {
		doneAddingCodes(IDs, null, append, type, force);
	}
	
	public static void doneAddingCodes(Vector<Integer> IDs, Vector<String> reasons, boolean append, CommonUtils.FileType type) throws IOException {
		doneAddingCodes(IDs, reasons, append, type, false);
	}
	
	public static void doneAddingCodes(Vector<Integer> IDs, boolean append, CommonUtils.FileType type) throws IOException {
		doneAddingCodes(IDs, append, type, false);
	}
	
	public static void doneAddingCode(Integer ID, String reason, boolean append, CommonUtils.FileType type) throws IOException {
		Vector<Integer> IDs = new Vector<Integer>();
		IDs.addElement(ID);
		Vector<String> reasons = new Vector<String>();
		reasons.addElement(reason);
		doneAddingCodes(IDs, reasons, append, type);
	}
}
