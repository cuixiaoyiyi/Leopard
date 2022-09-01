package ac.checker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ac.LeopardMain;
import ac.util.InheritanceProcess;
import destructible.DestructibleIdentify;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class HTRChecker {

	//@Deprecated
	 static boolean hasHTRMisuse2(CallGraph cg, SootClass classUnderChecked, SootMethod startMethod) {
		Set<SootMethod> sootMethods = getStrongRefAssignMethods(classUnderChecked);
		if (sootMethods.isEmpty()) {
			return false;
		}

		for (SootMethod sootMethod : sootMethods) {
			if (isPathExist(startMethod, sootMethod, cg, new HashSet<>())) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasHRTMisuse(SootMethod sootMethod) {
		Set<SootField> fields = getStrongRefFields(sootMethod);
		if (fields.isEmpty()) {
			return false;
		}
		if (sootMethod.hasActiveBody()) {
			for (Unit unit : sootMethod.getActiveBody().getUnits()) {
				if (unit instanceof DefinitionStmt) {
					DefinitionStmt definitionStmt = (DefinitionStmt) unit;
					if (definitionStmt.getLeftOp() instanceof FieldRef) {
						FieldRef fieldRef = (FieldRef) definitionStmt.getLeftOp();
						if (fields.contains(fieldRef.getField())) {
							Value rightOp = definitionStmt.getRightOp();
							if (LeopardMain.refinement) {
								if(DestructibleIdentify.emptyDestructibleClasses.containsKey(rightOp.getType().toString())) {
//									Log.e("xx00000xx#rightOp.getType()", " ", rightOp.getType(), " ", sootMethod.getDeclaringClass());
									continue;
								}
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static Set<SootField> getStrongRefFields(SootMethod sootMethod) {
		return getStrongRefFields(sootMethod.getDeclaringClass());
	}

	private static Set<SootField> getStrongRefFields(SootClass sootClass) {
		Set<SootField> fields = new HashSet<SootField>();
		for (SootField sootField : sootClass.getFields()) {
			if (sootField.getType() instanceof RefType) {
				RefType refType = (RefType) sootField.getType();
				SootClass fieldClass = refType.getSootClass();
				if (LeopardMain.isApk()) {
					if (InheritanceProcess.isInheritedFromActivity(fieldClass)
							|| InheritanceProcess.isInheritedFromFragment(fieldClass)
							|| InheritanceProcess.isInheritedFromView(fieldClass)) {
						fields.add(sootField);
					}
				} else {
					if (DestructibleIdentify.isDestructibleClass(fieldClass, fieldClass)) {
						fields.add(sootField);
					}
				}
			}
		}
		return fields;
	}

	public static Set<SootMethod> getStrongRefAssignMethods(SootClass classUnderChecked) {
		Set<SootMethod> sootMethods = new HashSet<SootMethod>();
		Set<SootMethod> methods = new HashSet<>(classUnderChecked.getMethods());
		for (SootMethod sootMethod : methods) {
			if (hasHRTMisuse(sootMethod)) {
				sootMethods.add(sootMethod);
			}
		}
		return sootMethods;
	}

	private static boolean isPathExist(SootMethod start, SootMethod end, CallGraph cg, Set<SootMethod> visitedMethods) {
		if (start == null)
			return false;
		if (visitedMethods.contains(start)) {
			return false;
		}
		visitedMethods.add(start);
		if (start == end) {
			return true;
		}

		Iterator<Edge> edgesOutOfStartIterator = cg.edgesOutOf(start);
		while (edgesOutOfStartIterator.hasNext()) {
			Edge edgeOutOfStart = edgesOutOfStartIterator.next();
			if (isPathExist(edgeOutOfStart.getTgt().method(), end, cg, visitedMethods)) {
				return true;
			}
		}
		return false;
	}
}
