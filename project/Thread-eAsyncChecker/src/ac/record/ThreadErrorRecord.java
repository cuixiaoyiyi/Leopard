package ac.record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import soot.RefType;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import ac.ThreadMain;
import ac.constant.Signature;
import ac.entity.ThreadRefObject;
import ac.entity.ThreadTypeState;
import jymbolic.config.BaseConfiguration;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.IBasicValue;
import jymbolic.record.ExceptionRecord;
import jymbolic.util.MethodUtil;

public class ThreadErrorRecord {

	private static final String ERROR_FOLDER = BaseConfiguration.getErrorFolder() + File.separator;

	private static int misuseNum = 0;

	public synchronized static void recordHTR(ThreadRefObject threadObj, Map<SootField, IBasicValue> taintedField,
			Unit unit, GlobalMessage globalMessage) {
		if (!needRecord(ThreadMain.HTRMap, threadObj)) {
			return;
		}

		String filePath = ERROR_FOLDER + "HTR-sum.txt";
		recordSum(threadObj, globalMessage, filePath);

		filePath = ERROR_FOLDER + "HTR.txt";
		String content = "Tainted field: ";
		int index = 0;
		for (Map.Entry<SootField, IBasicValue> entry : taintedField.entrySet()) {
			content += entry.getKey().getSignature() + "";
			index++;
			if (index != taintedField.size() - 1)
				content += ", ";
		}
		content += "\n";
		content += ThreadErrorRecord.recordPathInfo(threadObj, globalMessage);

		record(content, filePath);
	}

	public synchronized static void recordNTT(ThreadRefObject threadObj, GlobalMessage globalMessage) {
		if (!ThreadMain.HTRMap.containsKey(threadObj.getObjectKey())) {
			return;
		}
		if (!needRecord(ThreadMain.NTTMap, threadObj)) {
			return;
		}

		String filePath = ERROR_FOLDER + "NTT-sum.txt";
		recordSum(threadObj, globalMessage, filePath);

		filePath = ERROR_FOLDER + "NTT.txt";
		String content = threadObj.getInitStatement() + " is not interrupted after start\n";
		content += ThreadErrorRecord.recordPathInfo(threadObj, globalMessage);
		record(content, filePath);
	}

	public synchronized static void recordINR(ThreadRefObject threadObj, GlobalMessage globalMessage) {
		if (!needRecord(ThreadMain.INRMap, threadObj))
			return;

		String filePath = ERROR_FOLDER + "INR-sum.txt";
		recordSum(threadObj, globalMessage, filePath);

		filePath = ERROR_FOLDER + "INR.txt";
		String content = threadObj.getInitStatement() + " did not check interrupted status\n";
		content += ThreadErrorRecord.recordPathInfo(threadObj, globalMessage);
		SootMethod doInBackgroundMethod = MethodUtil.getMethod(((RefType) threadObj.getType()).getSootClass(),
				Signature.METHOD_SUBSIG_RUN);
		content += doInBackgroundMethod.getActiveBody().toString();
		record(content, filePath);
	}

	/**
	 * If processedMap doesn't contain threadObj, return true. Otherwise, return
	 * false
	 * 
	 * @param processedMap
	 * @param threadObj
	 * @return
	 */
	private synchronized static boolean needRecord(Map<String, ThreadRefObject> processedMap,
			ThreadRefObject threadObj) {
		ThreadMain.rightInstanceMap.remove(threadObj.getObjectKey());
//		if (!threadObj.isAliasedToField()) { // only record AsyncTask field
//			return false;
//		}
		if (!ThreadMain.errorInstanceMap.containsKey(threadObj.getObjectKey())) {
			ThreadMain.errorInstanceMap.put(threadObj.getObjectKey(), threadObj);
			String errorInstanceFilePath = BaseConfiguration.getSymbolicPathFolder() + File.separator
					+ "error-async.txt";
			record(threadObj.getObjectKey() + "\n", errorInstanceFilePath);
		}

		if (!processedMap.containsKey(threadObj.getObjectKey())) {
			processedMap.put(threadObj.getObjectKey(), threadObj);
			return true;
		} else {
			return false;
		}
	}

	private synchronized static String recordPathInfo(ThreadRefObject threadObj, GlobalMessage globalMessage) {
		String content = "";
		ThreadTypeState state = threadObj.getTypeState(globalMessage);
		content += "AsyncTask path: \n";
		for (int i = 0; i < state.getExecutionUnitList().size(); i++) {
			content += state.getExecutionUnitList().get(i) + "\r\n";
		}
		content += "\n";

		content += "execution path: \n";
		for (int i = 0; i < globalMessage.getStmtList().size(); i++) {
			content += globalMessage.getStmtList().get(i) + "\r\n";
		}
		content += "\n";
		return content;
	}

	public synchronized static void recordTime(String source, long androlicStartTime, long sootStartTime) {
		String logFilePath = BaseConfiguration.getSymbolicPathFolder() + File.separator + "log.txt";
		String content = source + "sootTime: " + (androlicStartTime - sootStartTime) + "\n" + "androlic time: "
				+ (System.currentTimeMillis() - androlicStartTime) + "\n";
//		String content = source + Thread.currentThread().getName() + " sootTime: " + (androlicStartTime - sootStartTime) + "\n" + "androlic time: "
//				+ (System.currentTimeMillis() - androlicStartTime) + "\n";
		record(content, logFilePath);
	}

	public synchronized static void recordRightInstance() {
		String rightInstanceFilePath = BaseConfiguration.getSymbolicPathFolder() + File.separator + "right-async.txt";
		FileWriter errorWriter = null;
		try {
			errorWriter = new FileWriter(rightInstanceFilePath, true);
			for (String key : ThreadMain.rightInstanceMap.keySet()) {
				errorWriter.write(key + "\n");
			}
		} catch (Exception e) {
			ExceptionRecord.saveException(e);
		} finally {
			try {
				errorWriter.close();
			} catch (IOException e) {
				ExceptionRecord.saveException(e);
			}
		}
	}

	private synchronized static void recordSum(ThreadRefObject threadObj, GlobalMessage globalMessage,
			String filePath) {
		recordWorkingData(++misuseNum);
		String content = threadObj.getType().toString() + ";" + globalMessage.getCurrentPathInfo().getLength() + ";"
				+ threadObj.getInitStatement().toString() + ";" + threadObj.getObjectKey() + "\n";
		record(content, filePath);
	}

	public synchronized static void recordWorkingData(Object arg) {
		String filePath = ERROR_FOLDER + "workingData.txt";
		String content = (System.currentTimeMillis() - ThreadMain.ANDROLIC_START_TIME) + ";" + arg + "\n";
		record(content, filePath);
	}

	public static void record(String content, String filePath) {
		FileWriter errorWriter = null;
		try {
			errorWriter = new FileWriter(filePath, true);
			errorWriter.write(content);
		} catch (Exception e) {
			ExceptionRecord.saveException(e);
		} finally {
			try {
				errorWriter.close();
			} catch (IOException e) {
				ExceptionRecord.saveException(e);
			}
		}
	}

}
