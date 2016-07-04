import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class StudentDatabase {
	// Pattern: ID,name,nickname
	private static final Pattern pattern = Pattern.compile("(\\d+),(.+),(M|F),(.+)");

	private Map<Integer, Student> students = new TreeMap<Integer, Student>();
	
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
				String name = m.group(2);
				String gender = m.group(3);
				String nickname = m.group(4);
				Student student = new Student(ID, name, nickname, gender);
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
