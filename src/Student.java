import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Student {
	
	private Integer ID;
	private String name;
	private String nickname;
	private Map<Date, Status> statuses;
	
	public Student(Integer ID, String name, String nickname) {
		this.ID = ID;
		this.name = name;
		this.nickname = nickname;
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
	
	/***
	 * Get current status of a student in the current date
	 * 
	 * @return
	 */
	
	public Status getCurrentStatus() {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		for (Entry<Date, Status> entry : statuses.entrySet()) {
			Date date = entry.getKey();
			if (fmt.format(currentDate).equals(fmt.format(date)))
				return entry.getValue();
		}
		return null;
	}
	
	public Integer getAbsenceCount() {
		Integer count = 0;
		for (Status status : statuses.values()) {
			if (status.getType() == Status.Type.ABSENT)
				count++;
		}
		return count;
	}
	
	/***
	 * A student is considered normal if he or she is present in that day
	 * 
	 * @return
	 */
	
	public boolean isNormal() {
		return getCurrentStatus().getType() == Status.Type.PRESENT;
	}
	
}
