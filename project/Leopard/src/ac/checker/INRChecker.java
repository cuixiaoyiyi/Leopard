package ac.checker;

import java.util.ArrayList;
import java.util.Set;

import ac.checker.PointCollector.KeyPoint;
import ac.constant.Signature;
import ac.util.AsyncInherit;
import soot.SootClass;
import soot.SootMethod;

public class INRChecker {

	public static boolean hasInterruptCheck(SootMethod sootMethod) {
		return new RunMethod(sootMethod).isAllLoopCancelled();
	}

	public static boolean hasInterruptCheck(SootClass sootClass) {
		boolean hasInterruptCheck = true;
		if (AsyncInherit.isInheritedFromRunnable(sootClass)) {
			ArrayList<SootMethod> methods = new ArrayList<>(sootClass.getMethods());
			for (SootMethod sootMethod : methods) {
				if (Signature.METHOD_SUBSIG_RUN.equals(sootMethod.getSubSignature())
						&& sootMethod.getParameterCount() == 0) {
					hasInterruptCheck = hasInterruptCheck(sootMethod);
					if (!hasInterruptCheck) {
						return hasInterruptCheck;
					}
				}
			}
		}
		return hasInterruptCheck;
	}
	
	public static boolean hasINRMisuse(KeyPoint initialPoint, Set<KeyPoint> interruptPoints) {
		for (KeyPoint point : interruptPoints) {
			if (initialPoint.alias(point)) {
				return true;
			}
		}
		return false;
	}
}
