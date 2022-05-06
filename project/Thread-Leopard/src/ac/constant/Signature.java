package ac.constant;

public class Signature {

	// for thread
	public final static String METHOD_SUBSIG_START = "void start()";
	public final static String METHOD_SUBSIG_INTERRUPT = "boolean stop()";
	public final static String METHOD_SUBSIG_INTERRUPT_SAFELY = "void interrupt()";
	public final static String METHOD_SUBSIG_IS_INTERRUPTED = "boolean isInterrupted()";
	public final static String METHOD_SUBSIG_RUN = "void run()";

	// for ExecutorService
//	public final static String METHOD_SUBSIG_SHUT_DOWN_NOW = "void shutdownNow()";
//	public final static String METHOD_SUBSIG_SHUT_DOWN = "void shutdown()";
//	public final static String METHOD_SUBSIG_IS_SHUT_DOWN = "boolean isShutdown()";
//	public final static String METHOD_SUBSIG_SUBMIT_CALLABLE = "java.util.concurrent.Future submit(java.util.concurrent.Callable)";
//	public final static String METHOD_SUBSIG_SUBMIT_RUNNABLE = "java.util.concurrent.Future submit(java.lang.Runnable)";
//	public final static String METHOD_SUBSIG_SUBMIT_RUNNABLE_T = "java.util.concurrent.Future submit(java.lang.Runnable,java.lang.Object)";
//	public final static String METHOD_SUBSIG_SUBMIT_EXECUTE = "void execute(java.lang.Runnable)";

	// for ExecutorService create
//	public static Value getNewThreadPoolValue(Stmt stmt) {
//		if (stmt.containsInvokeExpr()) {
//			InvokeExpr invokeExpr = stmt.getInvokeExpr();
//			if (invokeExpr instanceof InstanceInvokeExpr) {
//				InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
//				if (InheritanceProcess.isInheritedFromGivenClass(instanceInvokeExpr.getMethod().getDeclaringClass(),
//						CLASS_EXECUTOR_SERVICE)
//						&& instanceInvokeExpr.getMethod().isConstructor()) {
//					return instanceInvokeExpr.getBase();
//				}
//			} 
//			if (stmt instanceof DefinitionStmt) {
//				DefinitionStmt definitionStmt = (DefinitionStmt) stmt;
//				Type type = invokeExpr.getMethod().getReturnType();
//				if (type instanceof RefType) {
//					RefType returnRefType = (RefType) type;
//					if (InheritanceProcess.isInheritedFromGivenClass(returnRefType.getSootClass(),
//							CLASS_EXECUTOR_SERVICE)) {
//						return definitionStmt.getLeftOp();
//					}
//				}
//			}
//		}
//		return null;
//	}

	// Java class
	public static final String CLASS_OBJECT = "java.lang.Object";
	public static final String CLASS_THREAD = "java.lang.Thread";
//	public static final String CLASS_EXECUTOR = "java.util.concurrent.Executor";
	public static final String CLASS_RUNNABLE = "java.lang.Runnable";
//	public static final String CLASS_EXECUTOR_SERVICE = "java.util.concurrent.ExecutorService";
//	public static final String CLASS_EXECUTORS = "java.util.concurrent.Executors";

	// Android platform class
	public static final String CLASS_VIEW = "android.view.View";
	public static final String CLASS_ACTIVITY = "android.app.Activity";
	public static final String CLASS_FRAGMENT = "android.app.Fragment";
	public static final String CLASS_SUPPORT_FRAGMENT = "android.support.v4.app.Fragment";
	public static final String CLASS_SUPPORT_FRAGMENT_V7 = "android.support.v7.app.Fragment";

}
