package destructible;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.tagkit.AbstractHost;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.LeopardMain;
import ac.constant.Signature;
import ac.util.InheritanceProcess;
import ac.util.Log;

public class DestructibleIdentify {

	public final static Map<SootClass, SootMethod> destructibleClasses = new HashMap<>();
	public final static Set<String> destroyClasses = new HashSet<>();
	public final static Set<String> closeClasses = new HashSet<>();
	public final static Set<String> annotationClasses = new HashSet<>();
	public final static Map<String, SootMethod> emptyDestructibleClasses = new HashMap<>();

	public static void collect() {
		Set<SootClass> classes = new HashSet<SootClass>();
		classes.addAll(Scene.v().getClasses());
		classes.addAll(Scene.v().getApplicationClasses());
		for (SootClass sootClass : classes) {
			if (isDestructibleClass(sootClass, sootClass)) {
			}
		}
	}

	public static boolean isEmptyMethod(SootMethod sootMethod) {
		if (sootMethod.isAbstract()) {
			return false;
		}
		if (sootMethod.isNative()) {
			return false;
		}
		if (sootMethod.isStatic()) {
			return true;
		}
		if (!sootMethod.hasActiveBody()) {
			return true;
		}
		Body body = sootMethod.getActiveBody();
		if (body.getUnits().isEmpty()) {
			return true;
		}
		UnitGraph unitGraph = new BriefUnitGraph(body);
		Local thisLocal = body.getThisLocal();
		SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
		SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);
		for (Unit defsOfThisLocal : localDefs.getDefsOf(thisLocal)) {
			if (!localUses.getUsesOf(defsOfThisLocal).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDestructibleClass(SootClass originClass, SootClass sootClass) {
		if (LeopardMain.isApk()) {
			return InheritanceProcess.isInheritedFromActivity(originClass)
//					|| InheritanceProcess.isInheritedFromFragment(originClass)
					|| InheritanceProcess.isInheritedFromView(originClass);
		}
		if (destructibleClasses.containsKey(sootClass)) {
			destructibleClasses.put(originClass, destructibleClasses.get(sootClass));
			return true;
		}
		if (Signature.CLASS_THREAD.equals(sootClass.getName())) {
			return false;
		}
		Set<SootMethod> sootMethods = new HashSet<SootMethod>(sootClass.getMethods());
		for (SootMethod sootMethod : sootMethods) {
			if (isPreDestroyAnnotationMethod(sootMethod) || isDestroyMethod(sootMethod) || isCloseMethod(sootMethod)) {
				destructibleClasses.put(originClass, sootMethod);
				// empty method and purified method refinement
				if (LeopardMain.refine && (isEmptyMethod(sootMethod) && !sootClass.isLibraryClass())) {
					Log.e("##########refine###########", sootMethod);
					emptyDestructibleClasses.put(originClass.getName(), sootMethod);
					continue;
				}
				return true;
			}
		}
		if (sootClass.hasSuperclass()) {
			if (isDestructibleClass(originClass, sootClass.getSuperclass())) {
				return true;
			}
		}
		if (sootClass.getInterfaces() != null) {
			for (SootClass interfaceClass : sootClass.getInterfaces()) {
				if (isDestructibleClass(originClass, interfaceClass)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isDestructibleMethod(SootMethod sootMethod) {
		boolean isDestroyMethod = isPreDestroyAnnotationMethod(sootMethod) || isCloseMethod(sootMethod)
				|| isDestroyMethod(sootMethod);
		return LeopardMain.refine
				? isDestroyMethod && (isEmptyMethod(sootMethod) && !sootMethod.getDeclaringClass().isLibraryClass())
				: isDestroyMethod;
	}

	private static boolean isPreDestroyAnnotationMethod(SootMethod sootMethod) {
		List<AnnotationTag> annotationTags = getAnnotationtags(sootMethod);
		for (AnnotationTag annotationTag : annotationTags) {
			// Ljavax/annotation/PreDestroy;
			// Ljakarta/annotation/PreDestroy;
			// xxx/PostConstruct;
			if (annotationTag.getType().endsWith("PreDestroy") || annotationTag.getType().endsWith("PostConstruct")) {
				annotationClasses.add(sootMethod.getDeclaringClass().getName());
				return true;
			}
		}
		return false;
	}

	private static boolean isDestroyMethod(SootMethod sootMethod) {
		if (sootMethod.getName().toLowerCase().endsWith("destroy")) {
			destroyClasses.add(sootMethod.getDeclaringClass().getName());
			return true;
		}
		return false;
	}

	private static boolean isCloseMethod(SootMethod sootMethod) {
		if (sootMethod.getName().toLowerCase().equals("close")) {
			closeClasses.add(sootMethod.getDeclaringClass().getName());
			return true;
		}
		return false;
	}

	private static List<AnnotationTag> getAnnotationtags(AbstractHost host) {
		List<AnnotationTag> tags = new ArrayList<>();
		for (Tag tag : host.getTags()) {
			if (tag instanceof VisibilityAnnotationTag) {
				VisibilityAnnotationTag visibilityAnnotationTag = (VisibilityAnnotationTag) tag;
				tags.addAll(visibilityAnnotationTag.getAnnotations());
			}
		}
		return tags;
	}

}
