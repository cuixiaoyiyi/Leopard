package ac.entrypoint.activity;

import java.util.ArrayList;
import java.util.List;

import ac.util.InheritanceProcess;
import soot.SootClass;
import soot.SootMethod;

public class MethodSynchronize {
private static Object mLock = new Object();
	
	public static List<SootMethod> getSootMethods(SootClass sootClass){
		if(InheritanceProcess.isInheritedFromActivity(sootClass) || InheritanceProcess.isInheritedFromFragment(sootClass)) {
			try {
				synchronized(mLock) {
					List<SootMethod> sootMethods = sootClass.getMethods();
					return sootMethods;
				}
			} catch (Exception e) {
				mLock.notifyAll();
				e.printStackTrace();
				return new ArrayList<>();
			}finally {
				
			}
		}else {
			return sootClass.getMethods();
		}
	}
	
	public static void addMethod(SootClass sootClass,SootMethod sootMethod) {
		try {
			synchronized(mLock) {
				sootClass.addMethod(sootMethod);
			}
		} catch (Exception e) {
			mLock.notifyAll();
		}
	}
}
