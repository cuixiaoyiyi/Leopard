package ac.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ac.LeopardMain;
import ac.constant.Signature;
import ac.util.AsyncInherit;
import ac.util.InheritanceProcess;
import destructible.DestructibleIdentify;
import soot.AnySubType;
import soot.Body;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class PointCollector {

	private Set<InitPoint> initialPoints = new HashSet<>();

	private Set<StartPoint> startPoints = new HashSet<>();

	private Set<KeyPoint> interruptPoints = new HashSet<>();

	private Set<DestroyPoint> destroyPoints = new HashSet<>();

	public PointCollector() {
	}

	public void start(Collection<SootClass> classes) {
		for (SootClass sootClass : classes) {
			ArrayList<SootMethod> methods = new ArrayList<>(sootClass.getMethods());
			for (SootMethod method : methods) {
				if (LeopardMain.isApk() && !Scene.v().getReachableMethods().contains(method)) {
					continue;
				}
				if (method.hasActiveBody()) {
					Body body = method.getActiveBody();
					for (Unit unit : body.getUnits()) {
						findKeyPoint((Stmt) unit, method);
					}
				}
			}
		}
		for (DestroyPoint destroyPoint : destroyPoints) {
			for (InitPoint initPoint : initialPoints) {
				for (RefType refType : initPoint.getPossibleTypes()) {
					if (refType.getSootClass().equals(destroyPoint.getHTRField().getDeclaringClass())) {
						initPoint.getDestroyPoints().add(destroyPoint);
						break;
					}
				}
				for (StartPoint startPoint : startPoints) {
					if (startPoint.alias(initPoint)) {
						startPoint.getDestroyPoints().addAll(initPoint.getDestroyPoints());
					}
				}
			}
		}
	}

	private void findKeyPoint(Stmt stmt, SootMethod method) {
		if (stmt.containsInvokeExpr()) {

			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokeMethod = stmt.getInvokeExpr().getMethod();
			if (!AsyncInherit.isInheritedFromThread(invokeMethod.getDeclaringClass())
					&& !DestructibleIdentify.isDestructibleMethod(invokeMethod)) {
				return;
			}
			if (invokeExpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) stmt.getInvokeExpr();
				Value caller = instanceInvokeExpr.getBase();
				if (invokeMethod.isConstructor()) {
					// without super.<init>(); Thread.<init>();
					if (!method.isStatic()) {
						Local thisLocal = method.getActiveBody().getThisLocal();
						if (caller.equals(thisLocal)) {
							return;
						}
					}
					InitPoint initPoint = newInitPoint(stmt, method, instanceInvokeExpr);
					initialPoints.add(initPoint);
				} else if (Signature.METHOD_SUBSIG_INTERRUPT.equals(invokeMethod.getSubSignature())
						|| Signature.METHOD_SUBSIG_INTERRUPT_SAFELY.equals(invokeMethod.getSubSignature())) {
					KeyPoint point = newPoint(stmt, method, caller);
					interruptPoints.add(point);
				} else if (Signature.METHOD_SUBSIG_START.equals(invokeMethod.getSubSignature())) {
					KeyPoint point = newPoint(stmt, method, caller);
					StartPoint startPoint = new StartPoint();
					startPoint.callerPointsToSet = point.callerPointsToSet;
					startPoint.sootMethod = point.sootMethod;
					startPoint.unit = point.unit;
					startPoints.add(startPoint);
				} else {
					if (DestructibleIdentify.isDestructibleMethod(invokeMethod)) {
						if (caller instanceof Local) {
							PointsToAnalysis pointsToAnalysis = Scene.v().getPointsToAnalysis();
							for (SootField sootField : HTRChecker.destructibleSootFields) {
								if (InheritanceProcess.isInheritedFromGivenClass(sootField.getType(), caller.getType())
										|| InheritanceProcess.isInheritedFromGivenClass(caller.getType(),
												sootField.getType())) {
									PointsToSet aliaSet = pointsToAnalysis.reachingObjects((Local) caller, sootField);
									if (!aliaSet.isEmpty()) {
										DestroyPoint point = newDestroyPoint(stmt, method, caller, sootField);
										destroyPoints.add(point);
									}
								}
							}
						}
					}

				}
			}
		}
	}

	private KeyPoint newPoint(Stmt stmt, SootMethod sootMethod, Value value) {
		KeyPoint point = new KeyPoint();
		point.sootMethod = sootMethod;
		point.unit = stmt;
		PointsToAnalysis pointsToAnalysis = Scene.v().getPointsToAnalysis();
		if (value instanceof Local) {
			point.callerPointsToSet = pointsToAnalysis.reachingObjects((Local) value);
		} else if (value instanceof FieldRef) {
			FieldRef fieldRef = (FieldRef) value;
			if (fieldRef.getField() != null && fieldRef.getField().isStatic()) {
				point.callerPointsToSet = pointsToAnalysis.reachingObjects(fieldRef.getField());
			}
		}
		return point;
	}

	private DestroyPoint newDestroyPoint(Stmt stmt, SootMethod sootMethod, Value value, SootField sootField) {
		DestroyPoint point = new DestroyPoint();
		point.sootMethod = sootMethod;
		point.unit = stmt;
		point.htrField = sootField;
		PointsToAnalysis pointsToAnalysis = Scene.v().getPointsToAnalysis();
		if (value instanceof Local) {
			point.callerPointsToSet = pointsToAnalysis.reachingObjects((Local) value);
		} else if (value instanceof FieldRef) {
			FieldRef fieldRef = (FieldRef) value;
			if (fieldRef.getField() != null && fieldRef.getField().isStatic()) {
				point.callerPointsToSet = pointsToAnalysis.reachingObjects(fieldRef.getField());
			}
		}
		return point;
	}

	private InitPoint newInitPoint(Stmt stmt, SootMethod sootMethod, InstanceInvokeExpr instanceInvokeExpr) {
		InitPoint point = new InitPoint();
		point.sootMethod = sootMethod;
		point.unit = stmt;
		PointsToAnalysis pointsToAnalysis = Scene.v().getPointsToAnalysis();
		point.callerPointsToSet = pointsToAnalysis.reachingObjects((Local) instanceInvokeExpr.getBase());
		for (Value para : instanceInvokeExpr.getArgs()) {
			if (AsyncInherit.isInheritedFromRunnable(para.getType())) {
				PointsToSet taskParaPointsToSet = pointsToAnalysis.reachingObjects((Local) para);
				if (taskParaPointsToSet != null && taskParaPointsToSet.possibleTypes() != null) {
					for (Type type : taskParaPointsToSet.possibleTypes()) {
						RefType refType = null;
						if (type instanceof RefType) {
							refType = (RefType) type;
						} else if (type instanceof AnySubType) {
							refType = ((AnySubType) type).getBase();
						} else {
							continue;
						}
						point.taskPointTos.add(refType.getSootClass());
					}
				}
			}
		}

		return point;
	}

	public Set<StartPoint> getStartPoints() {
		return startPoints;
	}

	public Set<InitPoint> getInitialPoints() {
		return initialPoints;
	}

	public Set<KeyPoint> getInterruptPoints() {
		return interruptPoints;
	}

	public static class KeyPoint {
		SootMethod sootMethod = null;
		Unit unit = null;
		PointsToSet callerPointsToSet = null;

		public SootMethod getSootMethod() {
			return sootMethod;
		}

		public Unit getUnit() {
			return unit;
		}

		public PointsToSet getCallerPointsToSet() {
			return callerPointsToSet;
		}

		public boolean alias(KeyPoint other) {
			PointsToSet otherPointsToSet = other.getCallerPointsToSet();
			if (callerPointsToSet != null && otherPointsToSet != null) {
				if (callerPointsToSet.hasNonEmptyIntersection(otherPointsToSet)) {
					return true;
				}
			}
			return false;
		}

	}

	public static class InitPoint extends KeyPoint {
		Set<SootClass> taskPointTos = new HashSet<>();

		Set<DestroyPoint> destroyPoints = new HashSet<>();

		HashSet<RefType> possibleTypes = null;

		public Set<SootClass> getTaskPointTos() {
			return taskPointTos;
		}

		public Set<DestroyPoint> getDestroyPoints() {
			return destroyPoints;
		}

		public HashSet<RefType> getPossibleTypes() {
			if (possibleTypes == null) {
				possibleTypes = new HashSet<>();
				for (Type type : callerPointsToSet.possibleTypes()) {
					if (type instanceof RefType) {
						possibleTypes.add((RefType) type);
					} else if (type instanceof AnySubType) {
						possibleTypes.add(((AnySubType) type).getBase());
					}

				}
				for (SootClass sootClass : taskPointTos) {
					possibleTypes.add(sootClass.getType());
				}
			}
			return possibleTypes;
		}

	}

	public static class StartPoint extends KeyPoint {
		Set<DestroyPoint> destroyPoints = new HashSet<>();

		public Set<DestroyPoint> getDestroyPoints() {
			return destroyPoints;
		}
	}

	public static class DestroyPoint extends KeyPoint {
		SootField htrField = null;

		public SootField getHTRField() {
			return htrField;
		}
	}

}
