package Workers;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import Dialogs.PlainTextDialog;
import Objects.Constants;
import Utilities.CommonUtils;
import Utilities.CommonUtils.FileType;
import Utilities.DateUtils;

public class Logger {

	private static final Dimension logSize = new Dimension(500, 500);
	
	public static PlainTextDialog currentDialog = null;

	public static String logContentForDate(Date date) throws IOException {
		return FileUtils.readFileToString(CommonUtils.fileFromType(FileType.LOG, date));
	}

	public static void showLog(Date date, boolean editable, boolean force, String filePath) {
		String title = "Log for " + DateUtils.getNormalFormattedDate(date);
		try {
			currentDialog = new PlainTextDialog(title, logSize.width, logSize.height, 5, logContentForDate(date),
					editable, filePath);
		} catch (FileNotFoundException ex) {
			if (force)
				currentDialog = new PlainTextDialog(title, logSize.width, logSize.height, 5, "", editable, filePath);
			else
				JOptionPane.showMessageDialog(null,
						"No log has been created for " + DateUtils.getNormalFormattedDate(date));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (currentDialog != null) {
				currentDialog.setVisible(true);
				currentDialog.isLog = true;
			}
		}
	}
	
	public static void showLog(Date date, boolean editable) {
		showLog(date, editable, false, null);
	}
	
	public static void showLog(Date date, boolean editable, boolean force) {
		showLog(date, editable, force, null);
	}

	public static void showLogs() {
		currentDialog = new PlainTextDialog("All Logs", logSize.width, logSize.height, 5, "", false);
		StringBuilder content = new StringBuilder();
		File[] dates = new File(Constants.FILE_ROOT).listFiles();
		for (File date : dates) {
			if (!date.isDirectory()) {
				System.out.println("Skip directory: " + date.getName());
				continue;
			}
			Date d;
			try {
				d = DateUtils.s_fmt.parse(date.getName());
			} catch (ParseException e) {
				System.out.println("Invalid folder: " + date.getName());
				continue;
			}
			content.append(DateUtils.getNormalFormattedDate(d) + "\n");
			try {
				content.append(logContentForDate(d) + "\n----------\n");
			} catch (IOException e) {
				content.append("No content.\n----------\n");
			}
		}
		content.append("\n===========================");
		currentDialog.setText(content.toString());
		currentDialog.setVisible(true);
		currentDialog.isLog = false;
	}
}
