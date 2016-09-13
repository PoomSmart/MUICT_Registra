package Workers;

import java.util.Map;

import Objects.Student;

public class SpecialAssigner {

	public static void assignAll(Map<Integer, Student> db) {
		db.get(5988029).setCheerleader(true);
		db.get(5988057).setCheerleader(true);
	}

}
