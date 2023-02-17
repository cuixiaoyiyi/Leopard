package ac.util;

import java.util.Map;

import soot.RefType;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import ac.constant.Signature;
import ac.entity.ThreadRefObject;
import ac.record.ThreadErrorRecord;
import ac.summary.RunMethod;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.IBasicValue;

public class ThreadOperationChecker {

	public static void checkHTR(ThreadRefObject asyncObject, Unit executeUnit,
			GlobalMessage globalMessage) {
		Map<SootField, IBasicValue> taintedField = asyncObject.getHoldingStrongReferenceField(globalMessage);
		if (taintedField.size() > 0) {
//			if (!AsyncMain.strongReferenceSet.contains(executeUnit)) {
			ThreadErrorRecord.recordHTR(asyncObject, taintedField, executeUnit, globalMessage);
//			AsyncMain.errorInstanceMap.put(asyncObject.getObjectKey(), asyncObject);
//			AsyncMain.rightInstanceMap.remove(asyncObject.getObjectKey());
//			AsyncMain.errorInstanceSet.add(asyncObject);
//			AsyncMain.rightInstanceSet.remove(asyncObject);
		}
		if (asyncObject.getTargetRunnableField() != null) {
			taintedField = asyncObject.getTargetRunnableField().getHoldingStrongReferenceField(globalMessage);
			if (taintedField.size() > 0) {
//				if (!AsyncMain.strongReferenceSet.contains(executeUnit)) {
				ThreadErrorRecord.recordHTR(asyncObject, taintedField, executeUnit, globalMessage);
			}
		}
	}

	public static void checkINR(ThreadRefObject asyncObject, Unit executeUnit, GlobalMessage globalMessage) {
		SootMethod doInBackgroundMethod = MethodUtil.getMethod(((RefType) asyncObject.getType()).getSootClass(),
				Signature.METHOD_SUBSIG_RUN);

		if (asyncObject.getTargetRunnableField() != null) {
			MethodUtil.getMethod(((RefType) asyncObject.getTargetRunnableField().getType()).getSootClass(),
					Signature.METHOD_SUBSIG_RUN);
		}
		ThreadErrorRecord.recordWorkingData("Event;" + doInBackgroundMethod.getSignature());
		RunMethod summary = new RunMethod(doInBackgroundMethod);

		if (!summary.isAllLoopCancelled()) {
//			if (!AsyncMain.notTerminateSet.contains(executeUnit)) {
			ThreadErrorRecord.recordINR(asyncObject, globalMessage);
//			AsyncMain.errorInstanceMap.put(asyncObject.getObjectKey(), asyncObject);
//			AsyncMain.rightInstanceMap.remove(asyncObject.getObjectKey());
//			AsyncMain.errorInstanceSet.add(asyncObject);
//			AsyncMain.rightInstanceSet.remove(asyncObject);
		}
	}
}
