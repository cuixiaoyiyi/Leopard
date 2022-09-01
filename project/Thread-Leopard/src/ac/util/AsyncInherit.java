package ac.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ac.constant.Signature;
import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.jimple.InvokeExpr;

public class AsyncInherit {

	public static final String[] startMethods = { "<java.util.concurrent.Executor: void execute(java.lang.Runnable)>",
			"<java.lang.Thread: void start()>" };

//	public static final String[] startMethods = { "<java.util.concurrent.Executor: void execute(java.lang.Runnable)>",
//			"<android.app.Activity: void runOnUiThread(java.lang.Runnable)>",
//			"<android.os.Handler: boolean post(java.lang.Runnable)>",
//			"<android.os.Handler: boolean postDelayed(java.lang.Runnable,long)>", "<java.lang.Thread: void start()>"};

	public static final Set<String> startMethodSet = new HashSet<>(Arrays.asList(startMethods));

	public static boolean isInheritedFromThread(SootClass theClass) {
		return InheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_THREAD);
	}
	
//	public static boolean isInheritedFromExecutor(SootClass theClass) {
//		return InheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_EXECUTOR_SERVICE);
//	}

	public static boolean isInheritedFromThread(Type type) {
		if (type instanceof RefType) {
			return isInheritedFromThread(((RefType) type).getSootClass());
		}
		return false;
	}
	
//	public static boolean isInheritedFromExecutor(Type type) {
//		if (type instanceof RefType) {
//			return isInheritedFromExecutor(((RefType) type).getSootClass());
//		}
//		return false;
//	}

	public static boolean isInheritedFromRunnable(SootClass theClass) {
		return InheritanceProcess.isInheritedFromGivenClass(theClass, Signature.CLASS_RUNNABLE);
	}

	public static boolean isInheritedFromRunnable(Type type) {
		if (type instanceof RefType) {
			return isInheritedFromRunnable(((RefType) type).getSootClass());
		}
		return false;
	}

	public static boolean isStartInvokeExpr(InvokeExpr invokeExpr) {
		return startMethodSet.contains(invokeExpr.getMethod().getSignature());
	}

}
