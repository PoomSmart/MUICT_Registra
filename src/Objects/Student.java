package Objects;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import Utilities.DateUtils;

public class Student implements Cloneable {

	private final Integer ID;
	private final String firstname;
	private final String lastname;
	private final String nickname;
	private final String gender;
	private final int section;
	private boolean medicalExclusive;
	private Map<String, Status> statuses;
	private Position<Integer, Integer> position;
	private String healthCondition;
	private String medicalAllergies;
	private String foodAllergies;
	private String foodPreference;

	public Student(Integer ID, String firstname, String lastname, String nickname, int section, String gender, String healthCondition, String medicalAllergies, String foodAllergies, String foodPreference) {
		this.ID = ID;
		this.firstname = firstname;
		this.lastname = lastname;
		this.nickname = nickname;
		this.gender = gender;
		this.section = section;
		this.position = new Position<Integer, Integer>(-1, -1);
		this.statuses = new TreeMap<String, Status>();
		this.medicalExclusive = healthCondition.contains("*");
		if (this.medicalExclusive)
			System.out.println("Exclusively take care: " + ID);
		this.healthCondition = healthCondition.replaceAll("\\*", "");
		this.medicalAllergies = medicalAllergies;
		this.foodAllergies = foodAllergies;
		this.foodPreference = foodPreference;
	}

	public Integer getID() {
		return ID;
	}

	public String getName() {
		return firstname + " " + lastname;
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
	
	public int getSection() {
		return section;
	}

	public Position<Integer, Integer> getPosition() {
		return position;
	}

	public void setPosition(Position<Integer, Integer> position) {
		this.position = position;
	}
	
	//public 

	/**
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

	/**
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

	/**
	 * A student is considered normal if he or she is present in that day
	 * 
	 * @return
	 */
	public boolean isNormal() {
		return getCurrentStatus().getType() == Status.Type.PRESENT;
	}

	/**
	 * This student has left with reason
	 * 
	 * @return
	 */
	public boolean isLeft() {
		return getCurrentStatus().getType() == Status.Type.LEAVE;
	}

	/**
	 * This student is absent
	 * 
	 * @return
	 */
	public boolean isAbsent() {
		return getCurrentStatus().getType() == Status.Type.ABSENT;
	}

	public void addStatus(Date date, Status status) {
		String key = DateUtils.getFormattedDate(date);
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
		sb.append(" Full Name: " + getName() + "\n");
		sb.append(" Nickname: " + nickname + "\n");
		sb.append(" Gender: " + getNormalizedGender() + "\n");
		sb.append(" Section: " + section + "\n");
		if (mode == 0) {
			Status status = getCurrentStatus();
			sb.append(" Current Status: " + status + "\n");
			if (status.getType() == Status.Type.LEAVE)
				sb.append(" Reason: " + status.getReason() + "\n");
			sb.append(" Current Position: " + position.toCellString() + "\n");
			if (!healthCondition.isEmpty())
				sb.append(" HEALTH condition: " + healthCondition + "\n");
			if (!medicalAllergies.isEmpty())
				sb.append(" MEDICAL Allergies: " + medicalAllergies + "\n");
			if (!foodAllergies.isEmpty())
				sb.append(" FOOD Allergies: " + foodAllergies + "\n");
			if (!foodPreference.isEmpty())
				sb.append(" FOOD Preference: " + foodPreference + "\n");
			if (medicalExclusive)
				sb.append(" TAKE CARE\n");
		}
		if (mode != 0) {
			sb.append(String.format(" Present | Leave | Absent: %d, %d, %d\n", getPresentCount(), getLeaveCount(),
					getAbsenceCount()));
			sb.append(" Overall Status:\n");
			for (Entry<String, Status> entry : statuses.entrySet()) {
				String dateKey = entry.getKey();
				Date date = null;
				try {
					date = DateUtils.s_fmt.parse(dateKey);
				} catch (ParseException e) {}
				String realDateKey = DateUtils.n_fmt.format(date);
				Status status = entry.getValue();
				sb.append(String.format("  %s: %s\n", realDateKey, status.getDetailedStatus()));
			}
		}
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}

	public Student clone() {
		Student student = new Student(ID, firstname, lastname, nickname, section, gender, healthCondition, medicalAllergies, foodAllergies, foodPreference);
		student.medicalExclusive = medicalExclusive;
		student.statuses = new TreeMap<String, Status>();
		student.position = position.clone();
		for (Map.Entry<String, Status> entry : statuses.entrySet()) {
			String date = entry.getKey();
			Status status = entry.getValue().clone();
			student.statuses.put(date, status);
		}
		return student;
	}
	
	public String getHealthCondition() {
		return healthCondition;
	}
	
	public void addHealthCondition(String condition) {
		healthCondition += " + " + condition;
	}

	public String getMedicalAllergies() {
		return medicalAllergies;
	}
	
	public void addMedicalAllergy(String allergy) {
		medicalAllergies += " + " + allergy;
	}

	public String getFoodAllergies() {
		return foodAllergies;
	}
	
	public void addFoodAllergy(String allergy) {
		foodAllergies += " + " + allergy;
	}

	public String getFoodPreference() {
		return foodPreference;
	}
	
	public void addFoodPreference(String food) {
		foodPreference += " + " + food;
	}

}
