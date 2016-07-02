import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
	
	public static String formattedDate(Date date) {
		return fmt.format(date);
	}
	
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		return currentDate;
	}
	
	public static String getCurrentFormattedDate() {
		return formattedDate(getCurrentDate());
	}
}
