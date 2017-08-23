package Workers;

import java.util.Map;

import Objects.Student;

public class SpecialAssigner {

	public static void assignAll(Map<Integer, Student> db) {
		int IDs[] = { 176, 177, 9, 233,195 };
		for (int id : IDs)
			db.get(Student.createID(id)).setCheerleader(true);
	}

}
