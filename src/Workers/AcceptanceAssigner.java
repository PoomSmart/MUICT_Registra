package Workers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import Objects.Student;
import Utilities.CommonUtils;

public class AcceptanceAssigner {
		
	public static void assignAll(Map<Integer, Student> db) {
		try {
			List<String> ncList = FileUtils.readLines(new File("acceptance-nc.csv"));
			for (String fID : ncList) {
				Integer ID = CommonUtils.getID("5988" + fID);
				if (ID != -1)
					db.get(ID).setAcceptanceStatus(Student.AcceptanceType.DontSee); 
			}
			List<String> yList = FileUtils.readLines(new File("acceptance-y.csv"));
			for (String fID : yList) {
				Integer ID = CommonUtils.getID("5988" + fID);
				if (ID != -1)
					db.get(ID).setAcceptanceStatus(Student.AcceptanceType.Y);
			}
			List<String> nList = FileUtils.readLines(new File("acceptance-n.csv"));
			for (String fID : nList) {
				Integer ID = CommonUtils.getID("5988" + fID);
				if (ID != -1)
					db.get(ID).setAcceptanceStatus(Student.AcceptanceType.N);
			}
			for (Integer ID : db.keySet()) {
				Student student = db.get(ID);
				if (student.getAcceptanceStatus().equals("-"))
					student.setAcceptanceStatus(Student.AcceptanceType.Unknown);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}