package Workers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import Objects.Student;
import Utilities.CommonUtils;

public class AcceptanceAssigner {
	
	public static Map<Integer, Student> students;
	
	public static void assignAll() {
		try {
			List<String> ncList = FileUtils.readLines(new File("acceptance-nc.csv"));
			for (String fID : ncList) {
				Integer ID = CommonUtils.getID("5988" + fID);
				if (ID != -1)
					students.get(ID).setAcceptanceStatus(Student.AcceptanceType.DS); 
			}
			List<String> yList = FileUtils.readLines(new File("acceptance-y.csv"));
			for (String fID : yList) {
				Integer ID = CommonUtils.getID("5988" + fID);
				if (ID != -1)
					students.get(ID).setAcceptanceStatus(Student.AcceptanceType.Y);
			}
			for (Integer ID : students.keySet()) {
				Student student = students.get(ID);
				if (student.getAcceptanceStatus().equals("-"))
					student.setAcceptanceStatus(Student.AcceptanceType.Null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
