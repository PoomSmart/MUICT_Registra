package Objects;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import Utilities.DateUtils;

public class Student implements Cloneable {

	private Integer ID;
	private String name;
	private String firstname;
	private String lastname;
	private String nickname;
	private String gender;
	private Map<String, Status> statuses;
	private Position<Integer, Integer> position;

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
		this.position = new Position<Integer, Integer>(-1, -1);
		this.statuses = new TreeMap<String, Status>();
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
	
	public Position<Integer, Integer> getPosition() {
		return position;
	}

	public void setPosition(Position<Integer, Integer> position) {
		this.position = position;
	}
	
	/***
	 * Get current status of a student in the specific date
	 * 
	 * @return
	 */
	
	public Status getStatus(String date) {
		for (Entry<String, Status> entry : statuses.entrySet()) {
			String idate = entry.getKey();
			if (idate.equals(date))
				return entry.getValue();
		}
		return null;
	}

	/***
	 * Get current status of a student in the current date
	 * 
	 * @return
	 */

	public Status getCurrentStatus() {
		return getStatus(DateUtils.getCurrentFormattedDate());
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
	
	/***
	 * This student has left with reason
	 * 
	 * @return
	 */
	
	public boolean isLeft() {
		return getCurrentStatus().getType() == Status.Type.LEAVE;
	}
	
	/***
	 * This student is absent
	 * 
	 * @return
	 */
	
	public boolean isAbsent() {
		return getCurrentStatus().getType() == Status.Type.ABSENT;
	}
	
	public void addStatus(Date date, Status status) {
		String key = DateUtils.formattedDate(date);
		// We shall overwrite status if same date (same key)
		if (statuses.containsKey(key))
			statuses.remove(key);
		statuses.put(key, status);
	}

	public void addStatus(Status status) {
		addStatus(DateUtils.getCurrentDate(), status);
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
		sb.append(" ID: " + ID + "\n");
		sb.append(" Full Name: " + name + "\n");
		sb.append(" Nickname: " + nickname + "\n");
		sb.append(" Gender: " + getNormalizedGender() + "\n");
		if (mode == 0) {
			Status status = getCurrentStatus();
			sb.append(" Current Status: " + status + "\n");
			if (status.getType() == Status.Type.LEAVE)
				sb.append(" Reason: " + status.getReason() + "\n");
			sb.append(" Current Position: " + position + "\n");
		}
		sb.append(String.format(" Present | Leave | Absent: %d, %d, %d\n", getPresentCount(), getLeaveCount(), getAbsenceCount()));
		sb.append(" Overall Status:\n");
		for (Entry<String, Status> entry : statuses.entrySet()) {
			String dateKey = entry.getKey();
			Date date = null;
			try {
				date = DateUtils.s_fmt.parse(dateKey);
			} catch (ParseException e) {}
			String realDateKey = DateUtils.n_fmt.format(date);
			Status status = entry.getValue();
			sb.append(String.format("  %s: %s\n", realDateKey, status));
		}
		return sb.toString();
	}
	
	public String toString() {
		return toString(0);
	}

	public Student clone() {
		Student student = new Student(ID, name, nickname, gender);
		student.statuses = new TreeMap<String, Status>();
		student.position = position.clone();
		for (Map.Entry<String, Status> entry : statuses.entrySet()) {
			String date = entry.getKey();
			Status status = entry.getValue().clone();
			student.statuses.put(date, status);
		}
		return student;
	}

}
