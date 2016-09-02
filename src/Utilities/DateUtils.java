package Utilities;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import Objects.Constants;

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

	public static Date dateFromString(String date) {
		Date d;
		try {
			d = DateUtils.s_fmt.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return d;
	}

	public static Vector<Date> availableDates() {
		Vector<Date> list = new Vector<Date>();
		File[] dates = new File(Constants.FILE_ROOT).listFiles();
		for (File date : dates) {
			if (!date.isDirectory()) {
				System.out.println("Skip directory: " + date.getName());
				continue;
			}
			Date d = dateFromString(date.getName());
			if (d != null) {
				if (isBusinessDay(DateToCalendar(d)))
					list.add(d);
				else
					System.out.println("Skip holiday: " + date.getName());
			}
			else
				System.out.println("Invalid folder: " + date.getName());
		}
		return list;
	}

	public static List<String> s_availableDates() {
		List<String> list = new Vector<String>();
		for (Date date : availableDates())
			list.add(getFormattedDate(date));
		return list;
	}

	public static Calendar DateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static boolean isBusinessDay(Calendar cal) {
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return false;
		if (cal.get(Calendar.DAY_OF_MONTH) == 31 && cal.get(Calendar.MONTH) == Calendar.AUGUST)
			return false;
		if (cal.get(Calendar.DAY_OF_MONTH) == 2 && cal.get(Calendar.MONTH) == Calendar.SEPTEMBER)
			return false;
		return true;
	}
}
