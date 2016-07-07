package Utilities;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	public static final SimpleDateFormat s_fmt = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat n_fmt = new SimpleDateFormat("dd/MM/yyyy");
	
	public static String getFormattedDate(Date date) {
		return s_fmt.format(date);
	}
	
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		return currentDate;
	}
	
	public static String getCurrentFormattedDate() {
		return getFormattedDate(getCurrentDate());
	}
	
	public static String getNormalFormattedDate(Date date) {
		return n_fmt.format(date);
	}
	
	public static String getCurrentNormalFormattedDate() {
		return getNormalFormattedDate(getCurrentDate());
	}
}
