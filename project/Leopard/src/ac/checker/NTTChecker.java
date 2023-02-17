package ac.checker;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import ac.checker.PointCollector.DestroyPoint;
import ac.checker.PointCollector.InitPoint;
import ac.checker.PointCollector.KeyPoint;
import ac.checker.PointCollector.StartPoint;
import ac.constant.Signature;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class NTTChecker {

	private static CallGraph cg = Scene.v().getCallGraph();

	private static PointCollector pointCollector;

	private static Map<SootMethod, WeakReference<UnitGraph>> methodCFGMap = new ConcurrentHashMap<>();

	public static boolean checkNTTMisuse(StartPoint startPoint, Set<KeyPoint> interruptPoints,
			PointCollector pointCollector) {
		NTTChecker.pointCollector = pointCollector;
		boolean hasInterruptPoint = false;
		for (KeyPoint interruptEvent : interruptPoints) {
			if (startPoint.alias(interruptEvent)) {
				hasInterruptPoint = true;
				for (DestroyPoint destroyEvent : startPoint.getDestroyPoints()) {
					if (HB(destroyEvent.unit, interruptEvent.unit, interruptEvent.sootMethod)) {
						return true;
					}
				}
			}
		}
		return !hasInterruptPoint;
	}

	// destroy happens before unit
	private static boolean HB(Unit destroyUnit, Unit unit, SootMethod sootMethod) {
		ConcurrentLinkedQueue<Unit> unitQueue = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedQueue<SootMethod> unitToMethodQueue = new ConcurrentLinkedQueue<>();
		HashSet<Unit> visitedUnits = new HashSet<>();
		HashSet<Local> visitedJoinCaller = new HashSet<>();
		visitedUnits.add(unit);
		unitToMethodQueue.add(sootMethod);
		while (!unitQueue.isEmpty()) {
			Unit currentUnit = unitQueue.poll();
			SootMethod currentMethod = unitToMethodQueue.poll();
			visitedUnits.add(currentUnit);
			// destroyEvent --> interruptEvent
			if (currentUnit.equals(destroyUnit)) {
				return true;
			}
			// invoke Expr
			if (currentUnit instanceof Stmt) {
				Stmt stmt = (Stmt) currentUnit;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invokeExpr = stmt.getInvokeExpr();
					SootMethod invokeMethod = invokeExpr.getMethod();
					// join method
					if (invokeMethod.getSignature().startsWith(Signature.METHOD_JOIN_PREFIX)) {
						pushMethodUnitsViaJoin(invokeExpr, visitedUnits, visitedJoinCaller, unitQueue,
								unitToMethodQueue);
					}
					// start method
					else if (invokeMethod.getSignature().equals(Signature.METHOD_SIG_START)) {
						pushMethodUnitsViaStart(invokeExpr, visitedUnits, visitedJoinCaller, unitQueue,
								unitToMethodQueue);
					} else {
						if (!invokeMethod.getDeclaringClass().isLibraryClass()) {
							pushMethodUnitsToConcurrentLinkedQueue(invokeMethod, visitedUnits, unitQueue,
									unitToMethodQueue);
						}
					}
				}
			}
			UnitGraph currentCFG = getUnitGraph(currentMethod);
			List<Unit> preds = currentCFG.getPredsOf(currentUnit);
			if (preds.isEmpty()) {
				// traverse on CG
				Iterator<Edge> edgesInto = cg.edgesInto(currentMethod);
				while (edgesInto.hasNext()) {
					Edge edge = edgesInto.next();
					MethodOrMethodContext edgeSrc = edge.getSrc();
					SootMethod srcMethod = edgeSrc.method();
					if (edge.srcStmt() != null) {
						pushMethodUnitsToConcurrentLinkedQueue(srcMethod, visitedUnits, unitQueue, unitToMethodQueue,
								edge.srcStmt());
					} else {
						pushMethodUnitsToConcurrentLinkedQueue(srcMethod, visitedUnits, unitQueue, unitToMethodQueue);
					}
				}
			} else {
				pushMethodUnitsToConcurrentLinkedQueue(currentMethod, visitedUnits, unitQueue, unitToMethodQueue,
						currentUnit);
			}

		}
		return false;
	}

	private static void pushMethodUnitsToConcurrentLinkedQueue(SootMethod sootMethod, HashSet<Unit> visitedUnits,
			ConcurrentLinkedQueue<Unit> unitQueue, ConcurrentLinkedQueue<SootMethod> unitToMethodQueue) {
		if (sootMethod.hasActiveBody()) {
			UnitGraph srcUnitGraph = getUnitGraph(sootMethod);
			List<Unit> tails = srcUnitGraph.getTails();
			for (Unit tail : tails) {
				unitQueue.add(tail);
				unitToMethodQueue.add(sootMethod);
			}
		}
	}

	private static void pushMethodUnitsToConcurrentLinkedQueue(SootMethod sootMethod, HashSet<Unit> visitedUnits,
			ConcurrentLinkedQueue<Unit> unitQueue, ConcurrentLinkedQueue<SootMethod> unitToMethodQueue, Unit start) {
		if (sootMethod.hasActiveBody()) {
			UnitGraph srcUnitGraph = getUnitGraph(sootMethod);
			List<Unit> preds = srcUnitGraph.getPredsOf(start);
			for (Unit pred : preds) {
				if (!visitedUnits.contains(pred) && !unitQueue.contains(pred)) {
					unitQueue.add(pred);
					unitToMethodQueue.add(sootMethod);
				}
			}

		}
	}

	private static void pushMethodUnitsViaStart(InvokeExpr invokeExpr, HashSet<Unit> visitedUnits,
			HashSet<Local> visitedJoinCaller, ConcurrentLinkedQueue<Unit> unitQueue,
			ConcurrentLinkedQueue<SootMethod> unitToMethodQueue) {
		if (invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
			Value caller = instanceInvokeExpr.getBase();
			if (caller instanceof Local) {
				Local localCaller = (Local) caller;
				PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
				PointsToSet localCallerPointsToSet = pta.reachingObjects(localCaller);
				for (Local joinCaller : visitedJoinCaller) {
					PointsToSet joinCallerPointsToSet = pta.reachingObjects(joinCaller);
					boolean alais = localCallerPointsToSet.hasNonEmptyIntersection(joinCallerPointsToSet);
					if (alais) {
						return;
					}
				}
				pushFindRunMethodUnitsToConcurrentLinkedQueue(localCaller, visitedUnits, unitQueue, unitToMethodQueue);
			}
		}
		return;
	}

	private static void pushMethodUnitsViaJoin(InvokeExpr invokeExpr, HashSet<Unit> visitedUnits,
			HashSet<Local> visitedJoinCaller, ConcurrentLinkedQueue<Unit> unitQueue,
			ConcurrentLinkedQueue<SootMethod> unitToMethodQueue) {
		if (invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
			Value caller = instanceInvokeExpr.getBase();
			if (caller instanceof Local) {
				Local localCaller = (Local) caller;
				visitedJoinCaller.add(localCaller);
				pushFindRunMethodUnitsToConcurrentLinkedQueue(localCaller, visitedUnits, unitQueue, unitToMethodQueue);
			}
		}

	}

	private static void pushFindRunMethodUnitsToConcurrentLinkedQueue(Local localCaller, HashSet<Unit> visitedUnits,
			ConcurrentLinkedQueue<Unit> unitQueue, ConcurrentLinkedQueue<SootMethod> unitToMethodQueue) {
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		PointsToSet localCallerPointsToSet = pta.reachingObjects(localCaller);
		for (InitPoint initPoint : pointCollector.getInitialPoints()) {
			boolean alais = localCallerPointsToSet.hasNonEmptyIntersection(initPoint.getCallerPointsToSet());
			if (alais) {
				for (RefType type : initPoint.getPossibleTypes()) {
					pushRunMethodUnitsToConcurrentLinkedQueue(type.getSootClass(), visitedUnits, unitQueue,
							unitToMethodQueue);

				}
			}

		}
	}

	private static void pushRunMethodUnitsToConcurrentLinkedQueue(SootClass sootClass, HashSet<Unit> visitedUnits,
			ConcurrentLinkedQueue<Unit> unitQueue, ConcurrentLinkedQueue<SootMethod> unitToMethodQueue) {
		try {
			SootMethod runMethod = sootClass.getMethod(Signature.METHOD_SUBSIG_RUN);
			pushMethodUnitsToConcurrentLinkedQueue(runMethod, visitedUnits, unitQueue, unitToMethodQueue);

		} catch (Exception e) {

		}
	}

	private static UnitGraph getUnitGraph(SootMethod sootMethod) {
		UnitGraph unitGraph = null;

		if (methodCFGMap.containsKey(sootMethod)) {
			unitGraph = methodCFGMap.get(sootMethod).get();
		}
		if (unitGraph == null) {
			if (sootMethod.hasActiveBody()) {
				unitGraph = new BriefUnitGraph(sootMethod.getActiveBody());
				methodCFGMap.put(sootMethod, new WeakReference<UnitGraph>(unitGraph));
			}
		}
		return unitGraph;
	}

}
