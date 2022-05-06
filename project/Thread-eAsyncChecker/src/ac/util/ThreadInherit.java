package ac.util;

import ac.constant.Signature;
import jymbolic.util.ClassInheritanceProcess;
import jymbolic.util.ClassInheritanceProcess.MatchType;
import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.jimple.InvokeExpr;

public class ThreadInherit {

	public static boolean isInheritedFromThread(SootClass theClass) {
		return ClassInheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_THREAD, MatchType.equal);
	}

	public static boolean isInheritedFromThread(Type type) {
		if (type instanceof RefType) {
			return isInheritedFromThread(((RefType) type).getSootClass());
		}
		return false;
	}

	public static boolean isInheritedFromRunnable(Type type) {
		if (type instanceof RefType) {
			return ClassInheritanceProcess.isInheritedFromGivenClass(((RefType) type).getSootClass(),
					Signature.CLASS_RUNNABLE, MatchType.equal);
		}
		return false;
	}

	public static boolean isStartInvokeExpr(InvokeExpr invokeExpr) {
		return Signature.METHOD_SIG_START.equals(invokeExpr.getMethod().getSignature());
	}

}
