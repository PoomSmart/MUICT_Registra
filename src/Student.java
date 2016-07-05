import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Student implements Cloneable {

	private Integer ID;
	private String name;
	private String firstname;
	private String lastname;
	private String nickname;
	private String gender;
	private Map<Date, Status> statuses;

	public Student(Integer ID, String name, String nickname, String gender) {
		this.ID = ID;
		this.name = name;
		String[] x = name.split("\\s+");
		if (x.length == 2) {
			this.firstname = x[0];
			this.lastname = x[1];
		} else {
			this.firstname = name;
			this.lastname = "";
		}
		this.nickname = nickname;
		this.gender = gender;
		this.statuses = new TreeMap<Date, Status>();
	}

	public Integer getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public String getNickname() {
		return nickname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getGender() {
		return gender;
	}

	/***
	 * Get current status of a student in the current date
	 * 
	 * @return
	 */

	public Status getCurrentStatus() {
		for (Entry<Date, Status> entry : statuses.entrySet()) {
			Date date = entry.getKey();
			if (DateUtils.getCurrentFormattedDate().equals(DateUtils.formattedDate(date)))
				return entry.getValue();
		}
		return null;
	}
	
	public Integer getTypeCount(Status.Type type) {
		Integer count = 0;
		for (Status status : statuses.values()) {
			if (status.getType() == type)
				count++;
		}
		return count;
	}

	public Integer getAbsenceCount() {
		return getTypeCount(Status.Type.ABSENT);
	}
	
	public Integer getPresentCount() {
		return getTypeCount(Status.Type.PRESENT);
	}
	
	public Integer getLeaveCount() {
		return getTypeCount(Status.Type.LEAVE);
	}

	/***
	 * A student is considered normal if he or she is present in that day
	 * 
	 * @return
	 */

	public boolean isNormal() {
		return getCurrentStatus().getType() == Status.Type.PRESENT;
	}

	public void addStatus(Status status) {
		statuses.put(DateUtils.getCurrentDate(), status);
	}

	private String getNormalizedGender() {
		switch (gender) {
		case "F":
			return "Female";
		case "M":
			return "Male";
		}
		// Huh?
		return "Unknown";
	}

	public String toString(int mode) {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + ID + "\n");
		sb.append("Full Name: " + name + " (" + nickname + ")\n");
		sb.append("Gender: " + getNormalizedGender() + "\n");
		if (mode == 0) {
			Status status = getCurrentStatus();
			sb.append("Current Status: " + status + "\n");
			if (status.getType() == Status.Type.LEAVE)
				sb.append("Reason: " + status.getReason() + "\n");
		}
		sb.append("Total #Absence: " + getAbsenceCount() + "\n");
		return sb.toString();
	}
	
	public String toString() {
		return toString(0);
	}

	public Student clone() {
		Student student = new Student(ID, name, nickname, gender);
		student.statuses = new TreeMap<Date, Status>();
		for (Map.Entry<Date, Status> entry : statuses.entrySet()) {
			Date date = (Date)entry.getKey().clone();
			Status status = entry.getValue().clone();
			student.statuses.put(date, status);
		}
		return student;
	}

}
