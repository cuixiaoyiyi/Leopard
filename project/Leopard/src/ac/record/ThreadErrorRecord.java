package ac.record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ac.LeopardMain;
import ac.checker.PointCollector.KeyPoint;
import soot.SootClass;

public class ThreadErrorRecord {

	private static final String Path_FOLDER = LeopardMain.getOutputPath("path") + File.separator;
	private static final String ERROR_FOLDER = LeopardMain.getOutputPath("error") + File.separator;
	private static final String EXCEPTION_FOLDER = LeopardMain.getOutputPath("") + File.separator;

//	public static Set<KeyPoint> HTRPointSet = new HashSet<>();
//	public static Set<String> HTRSet = new HashSet<>();
//	public static Set<String> NTTSet = new HashSet<>();
//	public static Set<String> INRSet = new HashSet<>();

	public static Set<KeyPoint> errorInstanceSet = new HashSet<>();
	public static Set<KeyPoint> rightInstanceSet = new HashSet<>();

	public synchronized static void recordHTR(KeyPoint newPoint, SootClass sootClass) {
//		HTRPointSet.add(newPoint);
//		if (!needRecord(HTRSet, newPoint, sootClass)) {
//			return;
//		}

		String filePath = ERROR_FOLDER + "HTR-sum.txt";
		recordSum(newPoint, sootClass, filePath);
	}

	public synchronized static void recordNTT(KeyPoint newPoint) {
//		if (!needRecord(NTTSet, newPoint, null))
//			return;
		String filePath = ERROR_FOLDER + "NTT-sum.txt";
		recordSum(newPoint, null, filePath);
		return;

	}

	public synchronized static void recordINR(KeyPoint newPoint, SootClass sootClass) {
//		if (!needRecord(INRSet, newPoint, null))
//			return;

		String filePath = ERROR_FOLDER + "INR-sum.txt";
		recordSum(newPoint, sootClass, filePath);

	}

	public static String getObjectKey(KeyPoint newPoint, SootClass sootClass) {
		return newPoint.getSootMethod() + ";" + newPoint.getUnit() + ";" + sootClass;
	}

	public synchronized static void recordTime(String source, long androlicStartTime, long sootStartTime) {
		String logFilePath = Path_FOLDER + "log.txt";

		String content = source + "\n" + "sootTime: " + (androlicStartTime - sootStartTime) + "\n" + "androlic time: "
				+ (System.currentTimeMillis() - androlicStartTime) + "\n";
//		String content = source + Thread.currentThread().getName() + " sootTime: " + (androlicStartTime - sootStartTime) + "\n" + "androlic time: "
//				+ (System.currentTimeMillis() - androlicStartTime) + "\n";
		record(content, logFilePath);
	}

	public synchronized static void recordRightInstance() {
		String rightInstanceFilePath = Path_FOLDER + "right-async.txt";
		FileWriter errorWriter = null;
		try {
			errorWriter = new FileWriter(rightInstanceFilePath, true);
			for (KeyPoint newPoint : rightInstanceSet) {
				errorWriter.write(newPoint + "\n");
			}
		} catch (Exception e) {
			saveException(e);
		} finally {
			try {
				errorWriter.close();
			} catch (IOException e) {
				saveException(e);
			}
		}
	}

	private static void recordSum(KeyPoint newPoint, SootClass sootClass, String filePath) {
		String content = getObjectKey(newPoint, sootClass) + "\n";
		errorInstanceSet.add(newPoint);
		rightInstanceSet.remove(newPoint);
		record(content, filePath);

		String errorInstanceFilePath = ERROR_FOLDER + "error-async.txt";
		String key = getObjectKey(newPoint, sootClass);
		record(key + "\n", errorInstanceFilePath);
	}

	static long currentTime = LeopardMain.preprocessStartTime;

	public synchronized static void recordWorkingData(int arg) {
		String filePath = ERROR_FOLDER + "workingData.txt";
		String content = (System.currentTimeMillis() - LeopardMain.startTime) + ";" + arg + "\n";
		record(content, filePath);
	}

	public static void record(String content, String filePath) {
		FileWriter errorWriter = null;
		try {
			errorWriter = new FileWriter(filePath, true);
			errorWriter.write(content);
		} catch (Exception e) {
			e.printStackTrace();
//			saveException(e);
		} finally {
			try {
				errorWriter.close();
			} catch (Exception | Error e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static void saveException(Throwable exception) {
		String filePath = EXCEPTION_FOLDER + File.separator + "exception.txt";

		exception.printStackTrace();

		StringBuffer out = new StringBuffer();
		out.append(LeopardMain.getApkFullPath() + "\r\n");
		out.append(exception + "\r\n");
		for (StackTraceElement element : exception.getStackTrace()) {
			out.append("	" + element + "\r\n");
		}
		out.append("------------------------------\r\n\n");
		record(out.toString(), filePath);
	}

}
