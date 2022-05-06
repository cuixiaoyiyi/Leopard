package ac;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;
import ac.checker.NTTChecker;
import ac.checker.PointCollector;
import ac.checker.HTRChecker;
import ac.checker.INRChecker;
import ac.checker.PointCollector.InitialPoint;
import ac.entrypoint.activity.ActivityEntryPointWithoutFragment;
import ac.record.ThreadErrorRecord;
import ac.util.AsyncInherit;
import ac.util.InheritanceProcess;
import ac.util.Log;

public class LeopardMain {
	
	public static final String Android_Platforms = "C:\\Users\\C\\eclipseworkspace\\soot-path\\android-platforms-master";
	public static final String Output = "." + File.separator + "result0506" + File.separator;
	private static String inputPath = null;
//	public static final String Android_Platforms = "/HostServer/home/**/android-platforms";
	
//	public static Map<SootMethod, Boolean> methodToIsContainAsyncOperation = new HashMap<SootMethod, Boolean>();

	public static long sootStartTime = 0;

	public static long preprocessStartTime = 0;
	
	private static boolean isApk = false;
	private static List<String> jars = new ArrayList<String>();

//	public static final String apkBasePath = "/HostServer/home/**/hongwj/scripts/fdroid/2021";

	public static boolean isApk() {
		return isApk; 
	}

	public static void main(String[] args) {

		sootStartTime = System.currentTimeMillis();
//		inputPath = args[0];
		inputPath = "G:\\code\\ThreadBenchmark\\apache-tomcat-10.0.20";
		Log.i(inputPath);
		if (inputPath.toLowerCase().endsWith(".apk")) {
			isApk = true;
			jars.add(inputPath);
		}else if (inputPath.toLowerCase().endsWith(".jar")) {
			jars.add(inputPath);
		} else {
			jars.addAll(getJars(inputPath));
		}
		sootInitialization(jars);
		preprocessStartTime = System.currentTimeMillis();
		detect();
		ThreadErrorRecord.recordTime("", preprocessStartTime, sootStartTime);
	}
	
	private static void sootInitialization(List<String> jars) {
		G.reset();
		if(isApk) {
			Options.v().set_android_jars(Android_Platforms);
			Options.v().set_process_multiple_dex(true);
			Options.v().set_src_prec(Options.src_prec_apk);
		}else {
			Options.v().set_src_prec(Options.src_prec_c);
		}
		Options.v().set_process_dir(jars);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_no_writeout_body_releasing(true); // must be set to true if we want to access method bodies
															// after writing output to jimple
		Options.v().set_output_format(Options.output_format_none);
		Options.v().allow_phantom_refs();
		Options.v().set_whole_program(true);
		Options.v().set_exclude(getExcludeList());

		Pack p1 = PackManager.v().getPack("jtp");
		String phaseName = "jtp.bt";

		Transform t1 = new Transform(phaseName, new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phase, Map<String, String> options) {
				b.getMethod().setActiveBody(b);
			}
		});

		p1.add(t1);

		soot.Main.v().autoSetOptions();

		try {
			Scene.v().loadNecessaryClasses();
			PackManager.v().runPacks();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void detect() {
		Log.i("----------detector.detectThread() starts ");
		final Set<String> runnableClasses = new HashSet<String>();
		Set<String> iNRClasses = new HashSet<>();
		Set<SootClass> activityClasses = null;
		Set<SootClass> sootClasses = new HashSet<SootClass>(Scene.v().getApplicationClasses());
		for (SootClass currentClass : sootClasses) {
			if (InheritanceProcess.isInheritedFromThread(currentClass)
					|| AsyncInherit.isInheritedFromRunnable(currentClass.getType())) {
				runnableClasses.add(currentClass.getName());
				if (!INRChecker.hasInterruptCheck(currentClass)) {
					iNRClasses.add(currentClass.getName());
				}
			}
			if(isApk) {
				if(activityClasses == null) {
					activityClasses = new HashSet<>();
				}
				if (InheritanceProcess.isInheritedFromActivity(currentClass) && !currentClass.isAbstract()) {
					activityClasses.add(currentClass);
				}
			}

		}
		Log.i("iNRClasses.size() = ", iNRClasses.size());

		if(isApk) {
			detectAPK(iNRClasses,activityClasses);
		}else {
			detectJars(iNRClasses);
		}
		ThreadErrorRecord.recordTime("", preprocessStartTime, sootStartTime);

	}
	private static void detectJars(Set<String> iNRClasses) {
		try {
			long activityStartTime = System.currentTimeMillis();
			PointCollector pointCollector = new PointCollector(null);
			Log.i("pointCollector.getInitialPoints().size() = ", pointCollector.getInitialPoints().size());
			check(pointCollector, iNRClasses);
			ThreadErrorRecord.recordTime("timeaa", activityStartTime, sootStartTime);
		} catch (Exception | Error e) {
			ThreadErrorRecord.saveException(e);
		}
	
	}
	
	private static void check(PointCollector collector, Set<String> iNRClasses) {
		for (InitialPoint newPoint : collector.getInitialPoints()) {
			if (iNRClasses.contains(newPoint.pointClass.getName())) {
				ThreadErrorRecord.recordINR(newPoint);
			} else {
				for (SootMethod sootMethod : newPoint.taskSootMethods) {
					if (iNRClasses.contains(sootMethod.getDeclaringClass().getName())) {
						ThreadErrorRecord.recordINR(newPoint);
					}
				}
			}
			for (SootMethod sootMethod : newPoint.taskSootMethods) {
				if (HTRChecker.hasHRTMisuse(sootMethod)) {
					ThreadErrorRecord.recordHTR(newPoint);
				}
			}

		}
		NTTChecker notCancelChecker = new NTTChecker(collector);
		notCancelChecker.record();
	}
	private static void detectAPK(Set<String> iNRClasses, Set<SootClass> activityClasses) {
		ExecutorService sEXECUTOR = Executors.newFixedThreadPool(40);
		Log.i("----------detector.detectAsyncTask() starts ");
		Log.e("activityClasses.size()=", activityClasses.size());
		for (final SootClass currentClass : activityClasses) {
//			if(!"eu.siacs.conversations.ui.ScanActivity".equals(currentClass.getName())) {
//				continue;
//			}
			// check misused  in all Activities
			final SootMethod dummyMainMethod = new ActivityEntryPointWithoutFragment(currentClass).getMainMethod();
			sEXECUTOR.execute(new Runnable() {
				@Override
				public void run() { 
					try {
						long activityStartTime = System.currentTimeMillis(); 
						System.out.println("start Activity  " + currentClass);
						PointCollector pointCollector = new PointCollector(dummyMainMethod);
						if (pointCollector.getStartPoints().isEmpty()) {
							Log.i(" end empty ..  ", currentClass);
							return;
						}
						check(pointCollector, iNRClasses);
						ThreadErrorRecord.recordTime(currentClass.getName(), activityStartTime, sootStartTime);
						Log.i("end ..  ", currentClass);
					} catch (Exception | Error e) {
						ThreadErrorRecord.saveException(e);
						Log.e("Exception activity ", inputPath, "  ", currentClass);
					}

				}
			});
		}
		sEXECUTOR.shutdown();
		while (true) {
			if (sEXECUTOR.isTerminated()) {
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}

	public static Set<String> getJars(String dic) {
		File file = new File(dic);
		Set<String> jarList = new HashSet<String>();
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			if (fileList != null) {
				for (File subFile : fileList) {
					String string = subFile.getAbsolutePath();
					if (subFile.isDirectory()) {
						jarList.addAll(getJars(string));
					} else {
						if (string.toLowerCase().endsWith(".jar")) {
							jarList.add(string);
						}
					}

				}
			}
		}
		return jarList;
	}

	private static List<String> getExcludeList() {
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add("android.*");
		excludeList.add("androidx.*");
//		excludeList.add("org.*");
//		excludeList.add("soot.*");


		excludeList.add("java.*");
		excludeList.add("sun.*");
		excludeList.add("javax.*");
		excludeList.add("com.sun.*");
//		excludeList.add("com.ibm.*");
		excludeList.add("org.xml.*");
		excludeList.add("org.w3c.*");
//		excludeList.add("apple.awt.*");
//		excludeList.add("com.apple.*");
		return excludeList;
	}

	public static String getOutputPath(String sub) {
		String path = null;
		if(isApk || inputPath.toLowerCase().endsWith(".jar")) {
			path = inputPath.substring(0, inputPath.lastIndexOf("."));
		}else {
			path = inputPath;
		}
		path = Output + path.substring(path.lastIndexOf(File.separator)) + File.separator +sub + File.separator;
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getApkFullPath() {
		return inputPath;
	}

}
