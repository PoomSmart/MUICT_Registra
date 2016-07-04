import java.io.File;

public class CommonUtils {
	
	public static boolean fileExistsAtPath(String filePath) {
		return new File(filePath).exists();
	}
}
