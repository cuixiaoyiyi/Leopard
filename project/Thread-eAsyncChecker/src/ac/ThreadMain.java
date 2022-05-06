package ac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import ac.entity.ThreadRefObject;
import ac.processor.ThreadLibraryInvocationProcessor;
import ac.processor.ThreadNewArrayExprProcessor;
import ac.processor.ThreadNewExprProcessor;
import ac.processor.ThreadSymbolicInstrumenter;
import ac.processor.ThreadInterProceduralJudge;
import ac.record.ThreadErrorRecord;
import jymbolic.TimeOutDetectionThread;
import jymbolic.android.config.AndroidSootConfig;
import jymbolic.android.config.AndrolicConfigurationManager;
import jymbolic.config.ISootConfig;
import jymbolic.config.SymbolicConfiguration;
import jymbolic.android.entrypoint.activity.ActivityEntryPointWithoutFragment;
import jymbolic.android.entrypoint.activity.AndroidCallBacks;
import jymbolic.android.entrypoint.fragment.FragmentEntryPoint;
import jymbolic.execution.SymbolicEngine;
import jymbolic.record.ExceptionRecord;
import jymbolic.util.ClassInheritanceProcess;
import jymbolic.util.Log;

public class ThreadMain {
	
	private static ExecutorService sEXECUTOR = Executors.newFixedThreadPool(40);
	
	private static TimeOutDetectionThread timeThread;
	
	private static volatile long SOOT_START_TIME = 0;
	
	private static volatile long ANDROLIC_START_TIME = 0;
	
	private static volatile boolean EXIT = false;
	
	private static volatile boolean isRecord = false;
	
	public static Map<String, ThreadRefObject> HTRMap = new HashMap<String, ThreadRefObject>();
	public static Map<String, ThreadRefObject> NTTMap = new HashMap<String, ThreadRefObject>();
	public static Map<String, ThreadRefObject> INRMap = new HashMap<String, ThreadRefObject>();
	
	public static Map<String, ThreadRefObject> errorInstanceMap = new HashMap<String, ThreadRefObject>();
	public static Map<String, ThreadRefObject> rightInstanceMap = new HashMap<String, ThreadRefObject>();
	
	static class AsyncClass {
		public String className = "";
		public String startTaskMethod = "";
		public String cancelTaskMethod = "";
		public String backgroundMethod = "";
	}
	
	public static void main(String[] args) {
		
		AndrolicConfigurationManager.init(args);
		
		final Object lock = new Object();
		timeThread = new TimeOutDetectionThread(lock);
		timeThread.setOnKillSelfListener(new TimeOutDetectionThread.OnKillSelfListener() {
			
			@Override
			public void onPreKillSelf() {
				EXIT = true;
				sEXECUTOR.shutdownNow();
				ThreadErrorRecord.recordTime("", ANDROLIC_START_TIME, SOOT_START_TIME);
				if (!isRecord) {
					ThreadErrorRecord.recordRightInstance();
					if (Options.v().output_format() != Options.output_format_none)
						PackManager.v().writeOutput();
					isRecord = true;
				}
			}
		});
		timeThread.setPriority(10);
		timeThread.start();
		
		ISootConfig mSootConfig = new AndroidSootConfig();
		SymbolicConfiguration.setNewArrayExprProcessor(ThreadNewArrayExprProcessor.v());
		
		SymbolicConfiguration.setInterProceduralJudge(ThreadInterProceduralJudge.v());
		SymbolicConfiguration.setNewExprProcessor(ThreadNewExprProcessor.v());
		SymbolicConfiguration.setLibraryInvocationProcessor(ThreadLibraryInvocationProcessor.v());
		SymbolicConfiguration.setSymbolicInstrumenter(ThreadSymbolicInstrumenter.v());
		
		SOOT_START_TIME = System.currentTimeMillis();
		mSootConfig.sootInitialization();
		ANDROLIC_START_TIME = System.currentTimeMillis();
		
		try {
			Log.i("----------analysis start------------");
//			test("de.tap.easy_xkcd.fragments.NestedPreferenceFragment");
			analyzeActivity(timeThread);
			
			Log.i("----------analysis finish-AA-----------");
		} catch (Exception  | Error e) {
			Log.i("----------analysis finish-----Exception-------");
			e.printStackTrace();
			ExceptionRecord.saveException(e);
		} finally {
			if (!isRecord) {
				ThreadErrorRecord.recordRightInstance();
				if (Options.v().output_format() != Options.output_format_none)
					PackManager.v().writeOutput();
				isRecord = true;
			}
			timeThread.killSelf();
		}
	}
	
	public static void test(String className) {
		SootClass targetClass = Scene.v().getSootClassUnsafe(className);
		SootMethod dummyMethod = new FragmentEntryPoint(targetClass).getMainMethod();
		SymbolicEngine cs = new SymbolicEngine(targetClass);
		System.out.println(cs.solve(dummyMethod));
	}
	
	public static void analyzeActivity(TimeOutDetectionThread timeThread) throws IOException {
		Iterator<SootClass> it = Scene.v().getApplicationClasses().iterator();
		while (it.hasNext() && !EXIT) {
			SootMethod dummyMethod = null;

			SootClass sc = it.next();
			if(sc.getName().startsWith("androidx.")) {
				continue;
			}
			boolean hasAsyncField = true;
//			for (SootField sootField : sc.getFields()) {
//				if(ThreadInherit.isInheritedFromAsyncTask(sootField.getType())) {
//					hasAsyncField = true;
//					break;
//				}
//			}
			if (hasAsyncField && !sc.isAbstract() && ClassInheritanceProcess.isInheritedFromActivity(sc)) {
				dummyMethod = new ActivityEntryPointWithoutFragment(sc).getMainMethod();
			} else {
				continue;
			}
			if (!ThreadInterProceduralJudge.v().isContainThreadOperation(dummyMethod)) {
				continue;
			}
			if (EXIT) {
				break;
			}
			try {
				start(sc, dummyMethod);
			} catch (Exception e) {
				ExceptionRecord.saveException(e);
			} catch (Error e) {
				ExceptionRecord.saveException(e);
			}
		}
		sEXECUTOR.shutdown();
		while (true) {
			if (sEXECUTOR.isTerminated()) {
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void start(final SootClass sc, final SootMethod sootMethod) { 
		AndroidCallBacks.getCallBackSootClasses(); 
		Thread thread = new Thread() {
			@Override
			public void run() {
				Log.i("start ## ",sc);
				int time = -1;
				try {
					SymbolicEngine cs = new SymbolicEngine(sc);
//					Log.e("before analysis");
					time = cs.solve(sootMethod);
				}
				catch (Exception e) {
					ExceptionRecord.saveException(e);
				} catch (Error e) {
					ExceptionRecord.saveException(e);
				}finally {
//					Log.e("after analysis");
					record(sc, time);
				}
				Log.i("end ## ",sc);
			}
		};
		sEXECUTOR.execute(thread);
	}
	
	private synchronized static void record(SootClass sc, int time) {
		String content = sc.getName() + " " + time + "\n";
		ThreadErrorRecord.recordTime(content, ANDROLIC_START_TIME, SOOT_START_TIME);
	}

}