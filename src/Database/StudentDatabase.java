package Database;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import Objects.Student;
import Utilities.CommonUtils;

public class StudentDatabase {
	// Pattern 1: ID,name,gender,nickname
	// private static final Pattern pattern = Pattern.compile("(\\d+),(.+),(M|F),(.+)");
	// Pattern 2: ID,title,name,lastname,nickname
	private static final Pattern pattern = Pattern.compile("(\\d+),(.+),(.+),(.+),(.*)");

	private Map<Integer, Student> students = new TreeMap<Integer, Student>();
	
	private static String getGender(String str) {
		if (str.equals("MR."))
			return "M";
		if (str.equals("MISS"))
			return "F";
		return "Unknown";
	}
	
	public StudentDatabase(List<String> lines) {
		Matcher m;
		for (String line : lines) {
			if ((m = pattern.matcher(line)).find()) {
				Integer ID = CommonUtils.getID(m.group(1));
				if (ID == -1) {
					System.out.println("Malformed ID: " + ID + ", ignoring");
					continue;
				}
				if (students.containsKey(ID)) {
					System.out.println("Duplicate ID: " + ID + ", ignoring");
					continue;
				}
				String firstname = m.group(3);
				String lastname = m.group(4);
				String gender = getGender(m.group(2));
				String nickname = m.group(5);
				Student student = new Student(ID, firstname, lastname, nickname, gender);
				students.put(ID, student);
			}
		}
	}

	public StudentDatabase(String dbPath) throws IOException {
		this(FileUtils.readLines(new File(dbPath)));
	}

	public Map<Integer, Student> getStudents() {
		return students;
	}
}
