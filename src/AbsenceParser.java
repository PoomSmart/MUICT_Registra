import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbsenceParser {

	// Pattern: ID,type,reason
	private static final Pattern pDB = Pattern.compile("(\\d+),(.*),(.*)");
	
	private Map<Integer, Student> absentStudents = new TreeMap<Integer, Student>();

	public AbsenceParser(String dbPath, Map<Integer, Student> students) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(dbPath)));
			String line;
			Matcher m;
			while ((line = reader.readLine()) != null) {
				if ((m = pDB.matcher(line)).find()) {
					Integer ID = Integer.parseInt(m.group(1));
					Status.Type type = Status.getType(m.group(1));
					String reason = m.group(2);
					if (!students.containsKey(ID)) {
						System.out.println("ID not found: " + ID);
						System.out.println("I don't think we would face this problem");
						continue;
					}
					students.get(ID).addStatus(new Status(type, reason));
					absentStudents.put(ID, students.get(ID));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
