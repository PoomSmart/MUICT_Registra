package Workers;

import java.util.Map;

import Objects.Student;

public class AllergiesAssigner {

	public static Map<Integer, Student> students;

	public static void assign(Integer ID, String allergies) {
		students.get(ID).setAllergies(allergies);
	}

	public static void assignAll() {
		assign(5988001, "General flu");
	}
}
