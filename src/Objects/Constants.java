package Objects;
import java.util.Arrays;
import java.util.List;

public class Constants {
	private static final String[] _flushStrings = { "flush", "remall", "null", "clean" };
	public static final List<String> flushStrings = Arrays.asList(_flushStrings);
	private static final String[] _delLastStrings = { "dellast", "pop" };
	public static final List<String> delLastStrings = Arrays.asList(_delLastStrings);
	
	public static final String CONFIRM_WRITE_REGULAR = "Confirm writing regular data?";
	public static final String CONFIRM_APPEND_REGULAR = "Confirm append regular data?";
	
	public static final String CONFIRM_OVERWRITE = "File already existed, continue?";
	
	public static final String SCANNER_DIALOG_TITLE = "ID Scanner";
	public static final String SCANNER_DETECTING_MESSAGE = "Detecting scanned code";
	
	public static final String COMMON_CONFIRM = "Confirm?";
	
	public static final String FILE_ROOT = "Attendance/";
}
