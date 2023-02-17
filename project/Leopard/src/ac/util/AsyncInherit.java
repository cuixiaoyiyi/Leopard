package ac.util;

import ac.constant.Signature;
import soot.RefType;
import soot.SootClass;
import soot.Type;

public class AsyncInherit {

	public static boolean isInheritedFromThread(SootClass theClass) {
		return InheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_THREAD);
	}

	public static boolean isInheritedFromThread(Type type) {
		if (type instanceof RefType) {
			return isInheritedFromThread(((RefType) type).getSootClass());
		}
		return false;
	}

	public static boolean isInheritedFromRunnable(SootClass theClass) {
		return InheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_RUNNABLE);
	}

	public static boolean isInheritedFromRunnable(Type type) {
		if (type instanceof RefType) {
			return isInheritedFromRunnable(((RefType) type).getSootClass());
		}
		return false;
	}

}
