package Workers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import Objects.Student;

public class SpecialAssigner {

	public static void assignAll(Map<Integer, Student> db) {
		List<Integer> footballIDs = Arrays.asList(5988105, 5988130, 5988047, 5988259, 5988192, 5988182, 5988116,
				5988223, 5988102, 5988045, 5988119, 5988200, 5988060, 5988018, 5988051, 5988059, 5988131, 5988125);
		for (Integer ID : footballIDs)
			db.get(ID).setFootball(true);
	}

}
