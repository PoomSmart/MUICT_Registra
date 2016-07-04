import java.io.File;

public class CommonUtils {
	
	public static final String pMUICTID = "\\d\\d88\\d\\d\\d";
	
	public static boolean fileExistsAtPath(String filePath) {
		return new File(filePath).exists();
	}
}
