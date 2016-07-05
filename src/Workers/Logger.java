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
import Utilities.DateUtils;

public class Logger {

	private static final Dimension logSize = new Dimension(500, 500);

	public static String logContentForDate(Date date) throws IOException {
		return FileUtils.readFileToString(CommonUtils.fileFromType(CommonUtils.FileType.LOG, date));
	}

	public static void showLog(Date date, boolean editable) {
		try {
			PlainTextDialog dialog = new PlainTextDialog("Log for " + date, logSize.width, logSize.height, 5,
					logContentForDate(date), editable);
			dialog.setVisible(true);
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, "No log has been created for " + DateUtils.normalFormattedDate(date));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void showLog(Date date, boolean editable, boolean force, String filePath) {
		PlainTextDialog dialog = null;
		String title = "Log for " + DateUtils.normalFormattedDate(date);
		try {
			dialog = new PlainTextDialog(title, logSize.width, logSize.height, 5, logContentForDate(date),
					editable);
		} catch (FileNotFoundException ex) {
			if (force)
				dialog = new PlainTextDialog(title, logSize.width, logSize.height, 5, "", editable, filePath);
			else
				JOptionPane.showMessageDialog(null,
						"No log has been created for " + DateUtils.normalFormattedDate(date));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (dialog != null)
				dialog.setVisible(true);
		}
	}
	
	public static void showLog(Date date, boolean editable, boolean force) {
		showLog(date, editable, force, null);
	}

	public static void showLogs() {
		PlainTextDialog dialog = new PlainTextDialog("All Logs", logSize.width, logSize.height, 5, "", false);
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
			content.append(DateUtils.normalFormattedDate(d) + "\n");
			try {
				content.append(logContentForDate(d));
			} catch (IOException e) {
				content.append("No content.\n\n");
			}
		}
		dialog.setText(content.toString());
		dialog.setVisible(true);
	}
}
