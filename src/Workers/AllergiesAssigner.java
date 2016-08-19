package Workers;

import java.util.Map;

import Objects.Student;

/**
 * Runtime modification of freshmen's allergies
 * 
 * @author PoomSmart
 *
 */

public class AllergiesAssigner {
	
	// Serious allergies : stress
	// ÀÕ∫À◊¥ - they must bring their own cure
	// Hyperventilation - Fix: Immediately put them into safe and sound environment + use any cover to prevent O2 when breathe
	// ≈¡∫È“À¡Ÿ - Fix: ,, but don't need cover
	// Allergic to Aspirin
	
	// Ponstan (Fix Menstruation), 1 ID; + Generic type
	
	// Reminder: put no stress
	
	private enum Type {
		Medical, Food
	}

	public static void assign(Map<Integer, Student> db, Integer ID, String allergy, Type type) {
		switch (type) {
		case Medical:
			db.get(ID).addMedicalAllergy(allergy);
			break;
		case Food:
			db.get(ID).addFoodAllergy(allergy);
			break;
		}
	}

	public static void assignAll(Map<Integer, Student> db) {
		//assign(db, 5988001, "General flu", Type.Medical);
	}
}
