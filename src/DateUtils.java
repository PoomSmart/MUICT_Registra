import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	private static final SimpleDateFormat s_fmt = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat n_fmt = new SimpleDateFormat("dd/MM/yyyy");
	
	public static String formattedDate(Date date) {
		return s_fmt.format(date);
	}
	
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		return currentDate;
	}
	
	public static String getCurrentFormattedDate() {
		return formattedDate(getCurrentDate());
	}
	
	public static String normalFormattedDate(Date date) {
		return n_fmt.format(date);
	}
	
	public static String getCurrentNormalFormattedDate() {
		return normalFormattedDate(getCurrentDate());
	}
}
