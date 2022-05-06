package ac.record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ac.LeopardMain;
import ac.checker.PointCollector.InitialPoint;

public class ThreadErrorRecord {

	private static final String Path_FOLDER = LeopardMain.getOutputPath("path") + File.separator;
	private static final String ERROR_FOLDER = LeopardMain.getOutputPath("error")+ File.separator;
	private static final String EXCEPTION_FOLDER = LeopardMain.getOutputPath("");
	
	public static Set<InitialPoint> HTRSet = new HashSet<>();
	public static Set<InitialPoint> NTTSet = new HashSet<>();
	public static Set<InitialPoint> INRSet = new HashSet<>();

	public static Set<InitialPoint> errorInstanceSet = new HashSet<>();
	public static Set<InitialPoint> rightInstanceSet = new HashSet<>();

	public synchronized static void recordHTR(InitialPoint newPoint) {
		if (!needRecord(HTRSet, newPoint)) {
			return;
		}

		String filePath = ERROR_FOLDER + "HTR-sum.txt";
		recordSum(newPoint, filePath);
	}

	public synchronized static void recordNTT(InitialPoint newPoint) {
		if (!needRecord(NTTSet, newPoint))
			return;
		if (!HTRSet.contains(newPoint)) {
			return;
		}
		String filePath = ERROR_FOLDER + "NTT-sum.txt";
		recordSum(newPoint, filePath);

	}

	public synchronized static void recordINR(InitialPoint newPoint) {
		if (!needRecord(INRSet, newPoint))
			return;

		String filePath = ERROR_FOLDER + "NIR-sum.txt";
		recordSum(newPoint, filePath);

	}

	/**
	 * If processedMap doesn't contain newPoint, return true. Otherwise, return
	 * false
	 * 
	 * @param processedMap
	 * @param newPoint
	 * @return
	 */
	private synchronized static boolean needRecord(Set<InitialPoint> processedSet, InitialPoint newPoint) {
		rightInstanceSet.remove(newPoint);
//		if (!newPoint.isAliasedToField()) { // only record AsyncTask field
//			return false;
//		}
		if (!errorInstanceSet.contains(newPoint)) {
			errorInstanceSet.add(newPoint);
			String errorInstanceFilePath = ERROR_FOLDER + "error-async.txt";
			record(getObjectKey(newPoint) + "\n", errorInstanceFilePath);
		}

		if (!processedSet.contains(newPoint)) {
			processedSet.add(newPoint);
			return true;
		} else {
			return false;
		}
	}

	public static String getObjectKey(InitialPoint newPoint) {
		return newPoint.pointClass + ";" + newPoint.initialStmt + ";"
				+ newPoint.sootMethod.getSignature();
	}

	public synchronized static void recordTime(String source, long androlicStartTime, long sootStartTime) {
		String logFilePath = Path_FOLDER + "log.txt";

		String content = source +"\n" + "sootTime: " + (androlicStartTime - sootStartTime) + "\n" + "androlic time: "
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
			for (InitialPoint newPoint : rightInstanceSet) {
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

	private static void recordSum(InitialPoint newPoint, String filePath) {
		String content = getObjectKey(newPoint) + "\n";
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
		for(StackTraceElement element: exception.getStackTrace()){
			out.append("	"+ element + "\r\n");
		}
		out.append( "------------------------------\r\n\n");
		record(out.toString(), filePath);
	}

}
