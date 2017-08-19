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
	
	// Pattern: #,ID,titleTH,firstnameTH,lastnameTH,title,firstname,lastname,section,(unused),school,schoolLoc,nickname,bann,healthCondition,medAllergies,foodAllergies,foodPref
	private static final String textSet = "[^\\,]*"; // turns out this simplistically works
	private static final Pattern pattern = Pattern.compile(String.format("\\d+,(\\d+),%s,%s,%s,(.*),(.*),(.*),(\\d),.*,%s,%s,(.*),(\\d*),(%s),(%s),(%s),(%s)", textSet, textSet, textSet, textSet, textSet, textSet, textSet, textSet, textSet), Pattern.UNICODE_CHARACTER_CLASS);

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
				String gender = getGender(m.group(2));
				String firstname = m.group(3);
				String lastname = m.group(4);
				int section = Integer.parseInt(m.group(5));
				String nickname = m.group(6);
				String healthCondition = m.group(8);
				String medicalAllergies = m.group(9);
				String foodAllergies = m.group(10);
				String foodPreference = m.group(11);
				Student student = new Student(ID, firstname, lastname, nickname, section, gender, healthCondition, medicalAllergies, foodAllergies, foodPreference, null);
				students.put(ID, student);
				System.out.println("Added: " + ID);
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
