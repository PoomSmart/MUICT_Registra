package Workers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Objects.Status;
import Objects.Student;
import Utilities.CommonUtils;
import Utilities.DateUtils;

public class LeaveParser {

	// Pattern: ID,type,reason
	public static final Pattern pDB = Pattern.compile("(\\d+),leave,(.*)");
	
	private Map<Integer, Student> leaveStudents = new TreeMap<Integer, Student>();

	public LeaveParser(String dbPath, Map<Integer, Student> students, Date date) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(dbPath)));
			String line;
			Matcher m;
			while ((line = reader.readLine()) != null) {
				if ((m = pDB.matcher(line)).find()) {
					Integer ID = CommonUtils.getID(m.group(1));
					String reason = m.group(2);
					if (!students.containsKey(ID)) {
						System.out.println("ID not found: " + ID);
						System.out.println("I don't think we would face this problem");
						continue;
					}
					students.get(ID).addStatus(date, new Status(Status.Type.LEAVE, reason));
					leaveStudents.put(ID, students.get(ID));
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("leave.csv for " + DateUtils.normalFormattedDate(date) + " not found");
		}
	}
	
	public LeaveParser(String dbPath, Map<Integer, Student> students) {
		this(dbPath, students, DateUtils.getCurrentDate());
	}

	public Map<Integer, Student> getLeaveStudents() {
		return leaveStudents;
	}

}
