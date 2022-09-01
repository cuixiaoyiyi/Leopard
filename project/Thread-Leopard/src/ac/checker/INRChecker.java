package ac.checker;

import java.util.HashSet;
import java.util.Set;

import ac.constant.Signature;
import ac.util.InheritanceProcess;
import soot.SootClass;
import soot.SootMethod;

public class INRChecker {

	public static boolean hasInterruptCheck(SootMethod sootMethod) {
		return new RunMethod(sootMethod).isAllLoopCancelled();
	}

	public static boolean hasInterruptCheck(SootClass sootClass) {
		if (InheritanceProcess.isInheritedFromThread(sootClass)) {
			Set<SootMethod> methods = new HashSet<>(sootClass.getMethods());
			for (SootMethod sootMethod : methods) {
				if (Signature.METHOD_SUBSIG_RUN.equals(sootMethod.getSubSignature())) {
					return hasInterruptCheck(sootMethod);
				}
			}

		}
		return true;
	}
}
