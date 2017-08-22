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
	private final int bann;
	private final boolean islamic;
	private final boolean freshman;
	private boolean medicalExclusive;
	private boolean cheerleader;
	private Map<String, Status> statuses;
	private Position<Integer, Integer> position;
	private String cellPosition;
	private String healthCondition;
	private String medicalAllergies;
	private String foodAllergies;
	private String foodPreference;
	private AcceptanceType acceptanceStatus;

	public enum AcceptanceType {
		Y, N, DontSee, Unknown
	}

	public Student(Integer ID, String firstname, String lastname, String nickname, int section, String gender,
			String healthCondition, String medicalAllergies, String foodAllergies, String foodPreference,
			AcceptanceType acceptanceStatus) {
		this.ID = ID;
		this.freshman = ID / 100000 == DateUtils.studentYearInt();
		this.bann = (ID % 100 == 0) ? 10 : (((ID % 100) - 1) / 10) + 1;
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
		this.islamic = foodPreference.contains("Islam");
		this.acceptanceStatus = acceptanceStatus;
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
	
	public boolean isMan() {
		return gender.equals("M");
	}
	
	public boolean isWoman() {
		return gender.equals("F");
	}

	public int getSection() {
		return section;
	}

	public Position<Integer, Integer> getPosition() {
		return position;
	}

	public void setPosition(Position<Integer, Integer> position) {
		this.position = position;
		updateCellPosition();
	}

	private void updateCellPosition() {
		cellPosition = position.toCellString();
	}

	public String getCellPosition() {
		return cellPosition;
	}

	public Status getStatus(String date) {
		for (Entry<String, Status> entry : statuses.entrySet()) {
			String idate = entry.getKey();
			if (idate.equals(date))
				return entry.getValue();
		}
		return null;
	}

	public Status getCurrentStatus() {
		return getStatus(DateUtils.getCurrentFormattedDate());
	}

	public Map<String, Status> getStatuses() {
		return statuses;
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

	public boolean isNormal(String date) {
		Status status = getStatus(date);
		if (status == null)
			return true;
		return status.getType() == Status.Type.PRESENT;
	}

	public boolean isNormal() {
		return isNormal(DateUtils.getCurrentFormattedDate());
	}

	public boolean isLeft(String date) {
		return getStatus(date).getType() == Status.Type.LEAVE;
	}

	public boolean isLeft() {
		return isLeft(DateUtils.getCurrentFormattedDate());
	}

	public boolean isAbsent(String date) {
		return getStatus(date).getType() == Status.Type.ABSENT;
	}

	public boolean isAbsent() {
		return isAbsent(DateUtils.getCurrentFormattedDate());
	}

	public void addStatus(Date date, Status status) {
		String key = DateUtils.getFormattedDate(date);
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

	public String toString(int mode, String sdate, boolean debug) {
		StringBuilder sb = new StringBuilder();
		if (debug)
			sb.append(String.format("[%d] %s [Section %d] [Position %s]", ID, getName(), section, cellPosition));
		else {
			if (cheerleader)
				sb.append(" [Cheerleader]\n");
			sb.append(" ID: " + ID + "\n");
			sb.append(" Full Name: " + getName() + "\n");
			sb.append(" Nickname: " + nickname + "\n");
			sb.append(" Gender: " + getNormalizedGender() + "\n");
			sb.append(" Section: " + section + "\n");
		}
		if (mode == 0 && !debug) {
			Status status = getStatus(sdate);
			// Show current status only if today
			if (status != null) {
				sb.append(" Current Status: " + status + "\n");
				if (status.getType() == Status.Type.LEAVE)
					sb.append(" Reason: " + status.getReason() + "\n");
			}
			sb.append(" Current Position: " + cellPosition + "\n");
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
			if (!debug) {
				sb.append(String.format(" Present | Leave | Absent: %d, %d, %d\n", getPresentCount(), getLeaveCount(),
						getAbsenceCount()));
				sb.append(" Overall Status:\n");
			} else
				sb.append(" Statuses: |");
			for (Entry<String, Status> entry : statuses.entrySet()) {
				String dateKey = entry.getKey();
				Date date = null;
				try {
					date = DateUtils.s_fmt.parse(dateKey);
				} catch (ParseException e) {
				}
				String realDateKey = DateUtils.n_fmt.format(date);
				Status status = entry.getValue();
				if (debug)
					sb.append(status.getType() + "|");
				else
					sb.append(String.format("  %s: %s\n", realDateKey, status.getDetailedStatus()));
			}
		}
		return sb.toString();
	}

	public String toString(int mode, boolean debug) {
		return toString(mode, DateUtils.getCurrentFormattedDate(), debug);
	}
	
	public String toString(int mode) {
		return toString(mode, false);
	}

	public String toString() {
		return toString(0, true);
	}

	public Student clone() {
		Student student = new Student(ID, firstname, lastname, nickname, section, gender, healthCondition,
				medicalAllergies, foodAllergies, foodPreference, acceptanceStatus);
		student.medicalExclusive = medicalExclusive;
		student.statuses = new TreeMap<String, Status>();
		student.position = position.clone();
		student.cellPosition = cellPosition;
		student.cheerleader = cheerleader;
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

	public int getBann() {
		return bann;
	}

	public String getAcceptanceStatus() {
		if (acceptanceStatus == null)
			return "-";
		switch (acceptanceStatus) {
		case Y:
			return "Y";
		case N:
			return "N";
		case DontSee:
			return "No attendance";
		case Unknown:
			return "";
		}
		return "-";
	}

	public void setAcceptanceStatus(AcceptanceType acceptanceStatus) {
		this.acceptanceStatus = acceptanceStatus;
	}

	public boolean unableToJoin() {
		return acceptanceStatus == AcceptanceType.N || isCheerleader();
	}

	public boolean isIslamic() {
		return islamic;
	}

	public boolean isFreshman() {
		return freshman;
	}

	public boolean isCheerleader() {
		return cheerleader;
	}

	public void setCheerleader(boolean cheerleader) {
		this.cheerleader = cheerleader;
	}
	
	public static int createID(int num) {
		return num + (DateUtils.studentYearInt() * 100000) + 88000;
	}

}
