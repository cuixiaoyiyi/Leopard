package ac.constant;

public class Signature {

	// for thread
	public final static String METHOD_SIG_START = "<java.lang.Thread: void start()>";
	public final static String METHOD_SUBSIG_START = "void start()";
	public final static String METHOD_SUBSIG_INTERRUPT = "boolean stop()";
	public final static String METHOD_SUBSIG_INTERRUPT_SAFELY = "void interrupt()";
	public final static String METHOD_SUBSIG_IS_INTERRUPTED = "boolean isInterrupted()";
	public final static String METHOD_SUBSIG_RUN = "void run()";


	// Java class
	public static final String CLASS_OBJECT = "java.lang.Object";
	public static final String CLASS_THREAD = "java.lang.Thread";
	public static final String CLASS_RUNNABLE = "java.lang.Runnable";

	// Android platform class
	public static final String CLASS_VIEW = "android.view.View";
	public static final String CLASS_ACTIVITY = "android.app.Activity";
	public static final String CLASS_FRAGMENT = "android.app.Fragment";
	public static final String CLASS_SUPPORT_FRAGMENT = "android.support.v4.app.Fragment";
	public static final String CLASS_SUPPORT_FRAGMENT_V7 = "android.support.v7.app.Fragment";

}
