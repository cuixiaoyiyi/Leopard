package ac;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ac.checker.HTRChecker;
import ac.checker.INRChecker;
import ac.checker.NTTChecker;
import ac.checker.PointCollector;
import ac.checker.PointCollector.InitPoint;
import ac.checker.PointCollector.KeyPoint;
import ac.checker.PointCollector.StartPoint;
import ac.constant.Signature;
import ac.record.ThreadErrorRecord;
import ac.util.AsyncInherit;
import ac.util.Log;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.Stmt;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm;
import soot.jimple.infoflow.InfoflowConfiguration.CodeEliminationMode;
import soot.jimple.infoflow.InfoflowConfiguration.SootIntegrationMode;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

public class LeopardMain {

	public static long startTime = 0;

	public static long preprocessStartTime = 0;

	public static String Android_Platforms = "./android-platforms";

	static String inputPath = null;

	public static final String Output = "./LeopardOutput/";

	public static boolean refine = false;

	private static boolean isApk = false;

	private static List<String> jars = new ArrayList<String>();

	private static long maxUsedMemory = 0;

	public static int misuseNum = 0;

	static class MemoryThread extends Thread {
		@Override
		public void run() {
			while (true && !isInterrupted()) {
				try {
					long maxUsedMemory_t = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					maxUsedMemory = Math.max(maxUsedMemory, maxUsedMemory_t);
					sleep(2 * 100);
				} catch (InterruptedException e) {
					long maxUsedMemory_t = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					maxUsedMemory = Math.max(maxUsedMemory, maxUsedMemory_t);
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		Thread thread = new MemoryThread();
		thread.start();
		startTime = System.currentTimeMillis();
		inputPath = args[0];
		
		if(args.length >1 ) {
			Android_Platforms = args[1];
		}

		if (inputPath.toLowerCase().endsWith(".apk")) {
			isApk = true;
			startApk();
		} else if (inputPath.toLowerCase().endsWith(".jar")) {
			jars.add(inputPath);
			startJars();
		} else {
			jars.addAll(getJars(inputPath));
			startJars();
		}
		thread.interrupt();
		try {
			thread.join();
			Log.i("maxUsedMemory = ", maxUsedMemory / (1024 * 1024));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void startJars() {
		G.reset();
		Options.v().set_src_prec(Options.src_prec_c);
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

		List<SootMethod> entryMethods = new ArrayList<>();

		Transform t1 = new Transform(phaseName, new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phase, Map<String, String> options) {
				b.getMethod().setActiveBody(b);
				if (b.getMethod().isPrivate() || b.getMethod().isProtected()) {
					return;
				}
				synchronized (entryMethods) {
					entryMethods.add(b.getMethod());
				}
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

		Infoflow infoflow = new Infoflow(inputPath, false);
		infoflow.getConfig().setSootIntegrationMode(SootIntegrationMode.UseExistingInstance);
		infoflow.getConfig().setCallgraphAlgorithm(CallgraphAlgorithm.SPARK);
		infoflow.getConfig().setTaintAnalysisEnabled(false);
		infoflow.getConfig().setCodeEliminationMode(CodeEliminationMode.NoCodeElimination);
		Scene.v().setEntryPoints(entryMethods);
		try {
			Method constructCG = Infoflow.class.getDeclaredMethod("runAnalysis", ISourceSinkManager.class);
			constructCG.setAccessible(true);
			constructCG.invoke(infoflow, (ISourceSinkManager) null);
			detectMisuse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startApk() {
		Log.i("## start apk ", inputPath);

		SetupApplication application = new SetupApplication(Android_Platforms, inputPath);
		application.getConfig().setMergeDexFiles(true);
		application.getConfig().getCallbackConfig().setCallbackAnalyzer(CallbackAnalyzer.Fast);
		application.getConfig().getCallbackConfig().setCallbackAnalysisTimeout(5 * 60);
		application.constructCallgraph();
		detectMisuse();
	}

	private static void detectMisuse() {
		preprocessStartTime = System.currentTimeMillis();
		Log.i("----------detector.detectThread() starts ");
		final Set<String> runnableClasses = new HashSet<String>();
		Set<String> iNRClasses = new HashSet<>();
		Set<SootClass> sootClasses = new HashSet<SootClass>();
		sootClasses.addAll(Scene.v().getApplicationClasses());
		for (SootClass currentClass : sootClasses) {
			if (AsyncInherit.isInheritedFromRunnable(currentClass)) {
				runnableClasses.add(currentClass.getName());
				if (!INRChecker.hasInterruptCheck(currentClass)) {
					iNRClasses.add(currentClass.getName());
				}
			}
		}

		Log.i("iNRClasses.size() = ", iNRClasses.size());
		HTRChecker.init();
		PointCollector pointCollector = new PointCollector();
		pointCollector.start(sootClasses);
		int eventSize = runnableClasses.size();
		eventSize += pointCollector.getInitialPoints().size();
		eventSize += pointCollector.getStartPoints().size();
		eventSize += pointCollector.getInterruptPoints().size();
		ThreadErrorRecord.recordWorkingData(eventSize);

		// cg
		complementCGFromStartToRun(pointCollector);

		if (pointCollector.getStartPoints().isEmpty()) {
			Log.i(" end: StartPoint set is empty..  ");
			return;
		}
		Log.i(" Start HTR..  ");
		for (InitPoint point : pointCollector.getInitialPoints()) {
			for (RefType refType : point.getPossibleTypes()) {
				if (HTRChecker.hasHTRMisuse(refType)) {
					ThreadErrorRecord.recordHTR(point, refType.getSootClass());
					ThreadErrorRecord.recordWorkingData(++misuseNum);
					break;
				}
			}
		}
		Log.i(" Start INR..  ");
		for (InitPoint point : pointCollector.getInitialPoints()) {
			for (RefType refType : point.getPossibleTypes()) {
				if (iNRClasses.contains(refType.toString())
						&& INRChecker.hasINRMisuse(point, pointCollector.getInterruptPoints())) {
					ThreadErrorRecord.recordINR(point, refType.getSootClass());
					ThreadErrorRecord.recordWorkingData(++misuseNum);
					break;
				}
			}
		}
		Log.i(" Start NTT..  ");
		Log.i("## StartPoints Size ", pointCollector.getStartPoints().size());
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
		for (StartPoint startPoint : pointCollector.getStartPoints()) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (NTTChecker.checkNTTMisuse(startPoint, pointCollector.getInterruptPoints(),
								pointCollector)) {
							ThreadErrorRecord.recordNTT(startPoint);
							synchronized (runnableClasses) {
								ThreadErrorRecord.recordWorkingData(++misuseNum);
							}
						}
					} catch (Throwable e) {
						Log.i("## Exception During NTT ##", e.getClass());
					}

				}
			});

		}
		executor.shutdown();
		long timeStart = System.currentTimeMillis();
		while (executor.isTerminated() || (System.currentTimeMillis() - timeStart > 900000)) {
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				break;
			}
		}

		ThreadErrorRecord.recordTime("", preprocessStartTime, startTime);
		Log.i("## end ", inputPath);
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
		if (isApk || inputPath.toLowerCase().endsWith(".jar")) {
			path = inputPath.substring(0, inputPath.lastIndexOf("."));
		} else {
			path = inputPath;
		}
		while (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}
		int start = path.lastIndexOf(File.separator);
		if (start == -1) {
			start = 0;
		}
		path = Output + path.substring(start) + File.separator + sub + File.separator;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	static void complementCGFromStartToRun(PointCollector pointCollector) {
		for (KeyPoint startPoint : pointCollector.getStartPoints()) {
			for (InitPoint point : pointCollector.getInitialPoints()) {
				if (point.alias(startPoint)) {
					for (RefType type : point.getPossibleTypes()) {
						addEdgeFromStartToRunMethod(startPoint, type.getSootClass());
					}
				}
			}
		}
	}

	static void addEdgeFromStartToRunMethod(KeyPoint startPoint, SootClass sootClass) {
		try {
			SootMethod runMethod = sootClass.getMethod(Signature.METHOD_SUBSIG_RUN);
			Edge edge = new Edge(startPoint.getSootMethod(), (Stmt) startPoint.getUnit(), runMethod);
			Scene.v().getCallGraph().addEdge(edge);
		} catch (Exception e) {

		}

	}

	public static String getApkFullPath() {
		return inputPath;
	}

	public static boolean isApk() {
		return isApk;
	}

}
