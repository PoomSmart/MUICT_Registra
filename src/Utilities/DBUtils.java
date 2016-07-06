package Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import Main.Main;
import Objects.Status;
import Objects.Student;
import Objects.Status.Type;
import Utilities.CommonUtils.FileType;

public class DBUtils {

	// Pattern: ID,type,reason
	public static final Pattern pLeave = Pattern.compile("(\\d+),leave,(.*)");
	
	
	/**
	 * Get leave-with reason students from specific date
	 * 
	 * @param date
	 * @return
	 */
	public static Map<Integer, Student> getLeaveStudents(Date date) {
		Map<Integer, Student> leaveStudents = new TreeMap<Integer, Student>();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(CommonUtils.fileFromType(FileType.NOTHERE, date)));
			String line;
			Matcher m;
			while ((line = reader.readLine()) != null) {
				if ((m = pLeave.matcher(line)).find()) {
					Integer ID = CommonUtils.getID(m.group(1));
					String reason = m.group(2);
					if (!Main.db.containsKey(ID)) {
						System.out.println("ID not found: " + ID);
						System.out.println("I don't think we would face this problem");
						continue;
					}
					Student student = Main.db.get(ID);
					if (student == null)
						continue;
					student = student.clone();
					student.addStatus(date, new Status(Status.Type.LEAVE, reason));
					leaveStudents.put(ID, student);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("leave.csv for " + DateUtils.normalFormattedDate(date) + " not found");
		}
		return leaveStudents;
	}
	
	public static Map<Integer, Student> getCurrentLeaveStudents() {
		return getLeaveStudents(DateUtils.getCurrentDate());
	}
	
	/**
	 * Get present students from specific date
	 * 
	 * @param date
	 * @return
	 */
	public static Map<Integer, Student> getPresentStudents(Date date) {
		Map<Integer, Student> presentStudents = new TreeMap<Integer, Student>();
		File file = CommonUtils.fileFromType(FileType.REGULAR, date);
		try {
			List<String> lines = FileUtils.readLines(file);
			for (String line : lines) {
				Integer ID = CommonUtils.getID(line);
				if (ID == -1)
					continue;
				if (!Main.db.containsKey(ID))
					continue;
				Student student = Main.db.get(ID);
				if (student == null)
					continue;
				student = student.clone();
				student.addStatus(date, new Status());
				presentStudents.put(ID, student);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("present.csv for " + DateUtils.normalFormattedDate(date) + " not found");
		}
		return presentStudents;
	}
	
	public static Map<Integer, Student> getCurrentPresentStudents() {
		return getPresentStudents(DateUtils.getCurrentDate());
	}
	
	/**
	 * Get absent students from specific date
	 * 
	 * @param date
	 * @return
	 */
	public static Map<Integer, Student> getAbsentStudents(Date date) {
		Map<Integer, Student> absentStudents = new TreeMap<Integer, Student>();
		Map<Integer, Student> presentStudents = getPresentStudents(date);
		for (Student student : Main.db.values()) {
			Integer ID = student.getID();
			if (!presentStudents.containsKey(ID)) {
				Student absentStudent = student.clone();
				absentStudent.addStatus(date, new Status(Type.ABSENT));
				absentStudents.put(ID, absentStudent);
			}
		}
		return absentStudents;
	}
	
	public static Map<Integer, Student> getCurrentAbsentStudents() {
		return getAbsentStudents(DateUtils.getCurrentDate());
	}
	
	/**
	 * Get students (with statuses processed) from specific date
	 * 
	 * @param date
	 * @return
	 */
	public static Map<Integer, Student> getStudents(Date date) {
		// Assuming that there's no duplication among three kinds of students
		Map<Integer, Student> presentStudents = getPresentStudents(date);
		Map<Integer, Student> absentStudents = getAbsentStudents(date);
		Map<Integer, Student> leaveStudents = getLeaveStudents(date);
		// Should I do shallow copy ?
		Map<Integer, Student> students = new TreeMap<Integer, Student>();
		students.putAll(presentStudents);
		students.putAll(absentStudents);
		students.putAll(leaveStudents);
		return students;
	}
	
	public static Map<Integer, Student> getCurrentStudents() {
		return getStudents(DateUtils.getCurrentDate());
	}
}
