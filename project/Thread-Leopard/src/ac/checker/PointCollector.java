package ac.checker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ac.constant.Signature;
import ac.graph.CallGraphManagerGlobal;
import ac.util.AsyncInherit;
import ac.util.InheritanceProcess;
import ac.util.Log;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class PointCollector {

	private Set<InitialPoint> initialPoints = new HashSet<>();
	private Set<StartPoint> startPoints = new HashSet<>();

	private SootMethod entryMethod = null;

	public PointCollector(SootMethod entryMethod) {
		this.entryMethod = entryMethod;
		start();
	}

	private void start() {
		findKeyPoints();
		for (InitialPoint newPoint : initialPoints) {
			Set<SootMethod> visitedMethods = new HashSet<SootMethod>();
			visitedMethods.add(newPoint.sootMethod);
			processValueFlowIntraProcedure(new BriefUnitGraph(newPoint.sootMethod.getActiveBody()),
					newPoint.initialStmt, newPoint.valueSet, visitedMethods, new HashSet<Unit>());
		}

		Set<StartPoint> tpmPoints = new HashSet<>();

		for (StartPoint startPoint : startPoints) {
			Value value = startPoint.instanceInvokeExpr.getBase();
			for (InitialPoint newPoint : initialPoints) {
				if (containsValue(value, newPoint.valueSet)) {
					startPoint.valueSet.addAll(newPoint.valueSet);
				}
			}
			if (startPoint.valueSet.size() > 0) {
				tpmPoints.add(startPoint);
			}
		}
		startPoints = tpmPoints;
	}

	int notTerminateClassIndex = 1;

	private void findKeyPoints() {
		if (entryMethod !=null) {
			findKeyPointFromEntry(entryMethod);
			return;
		}
		Set<SootClass> sootClasses = new HashSet<SootClass>(Scene.v().getApplicationClasses());
		for (SootClass currentClass : sootClasses) {
			Set<SootMethod> methods = new HashSet<SootMethod>(currentClass.getMethods());
			for (SootMethod sootMethod : methods) {
				findKeyPointFromEntry(sootMethod);
			}
		}

	}
	
	

	private void findKeyPointFromEntry(SootMethod entryMethod) {

		if (entryMethod.hasActiveBody()) {
			Body body = entryMethod.getActiveBody();
			for (Unit unit : body.getUnits()) {
				InitialPoint newPoint = getInitialPoint(unit, entryMethod);
				if (newPoint != null) {
					initialPoints.add(newPoint);
				}
				StartPoint startPoint = getStartPoint(unit, entryMethod);
				if (startPoint != null) {
					startPoints.add(startPoint);
				}
			}
		}
	
	}

	private InitialPoint getInitialPoint(Unit unit, SootMethod sootMethod) {
		Stmt stmt = (Stmt) unit;
		SootClass pointClass = null;
		Value variable = null;
		Collection<SootMethod> runnableInitMethods = null;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			// thread.init(...);
			if (invokeExpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
				if (InheritanceProcess.isInheritedFromGivenClass(instanceInvokeExpr.getMethod().getDeclaringClass(),
						Signature.CLASS_THREAD) && instanceInvokeExpr.getMethod().isConstructor()) {
					pointClass = instanceInvokeExpr.getMethod().getDeclaringClass();
					variable = instanceInvokeExpr.getBase();
					for (Value arg : instanceInvokeExpr.getArgs()) {
						if(AsyncInherit.isInheritedFromRunnable(arg.getType())) {
							BriefUnitGraph unitGraph = null;
							Value runnableArg = stmt.getInvokeExpr().getArg(0);
							Body body = sootMethod.getActiveBody();
							unitGraph = new BriefUnitGraph(body);
							runnableInitMethods = getRunnableArgSootMethod(runnableArg, unit, unitGraph,
									new HashSet<>(), new HashSet<>());
							break;
						}
						
					}
				}

			}

			// thread = .getThread(...);
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt definitionStmt = (DefinitionStmt) stmt;
				Type type = invokeExpr.getMethod().getReturnType();
				if (type instanceof RefType) {
					RefType returnRefType = (RefType) type;
					if (InheritanceProcess.isInheritedFromGivenClass(returnRefType.getSootClass(),
							Signature.CLASS_THREAD) && invokeExpr.getMethod().getDeclaringClass().isLibraryClass()) {
						pointClass = returnRefType.getSootClass();
						variable = definitionStmt.getLeftOp();
					}
				}
			}
		}
		if (variable != null) {
			InitialPoint newPoint = new InitialPoint();
			newPoint.sootMethod = sootMethod;
			newPoint.initialStmt = (Stmt) unit;
			newPoint.pointClass = pointClass;
			if (runnableInitMethods != null) {
				newPoint.taskSootMethods.addAll(runnableInitMethods);
			}
			ValuePoint valuePoint = new ValuePoint();
			valuePoint.value = variable;
			valuePoint.sootMethod = sootMethod;
			newPoint.valueSet.add(valuePoint);

			return newPoint;
		}
		return null;
	}

	private StartPoint getStartPoint(Unit unit, SootMethod sootMethod) {
		if (unit instanceof Stmt) {
			Stmt stmt = (Stmt) unit;
			if (stmt.containsInvokeExpr()) {
				String methodSubSig = stmt.getInvokeExpr().getMethod().getSubSignature();
				if (!AsyncInherit
						.isInheritedFromThread(stmt.getInvokeExpr().getMethod().getDeclaringClass())) {
					return null;
				}
				switch (methodSubSig) {
				case Signature.METHOD_SUBSIG_START:
					StartPoint startPoint = new StartPoint();
					startPoint.instanceInvokeExpr = (InstanceInvokeExpr) stmt.getInvokeExpr();
					startPoint.sootMethod = sootMethod;
					startPoint.unit = unit;
					return startPoint;
				default:
					break;
				}
			}
		}
		return null;
	}

	private Collection<SootMethod> getRunnableArgSootMethod(Value value, Unit unit, UnitGraph unitGraph,
			HashSet<Unit> visited, HashSet<SootMethod> visitedMethods) {
		if (visited.contains(unit)) {
			return null;
		}
		visited.add(unit);
		visitedMethods.add(unitGraph.getBody().getMethod());
		if (unit instanceof Stmt) {
			Stmt stmt = (Stmt) unit;
			if (stmt.containsInvokeExpr()) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				if (invokeExpr instanceof InstanceInvokeExpr) {
					InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
					if (value.equals(instanceInvokeExpr.getBase()) && instanceInvokeExpr.getMethod().isConstructor()) {
						Set<SootMethod> sootMethods = new HashSet<SootMethod>();
						sootMethods.add(instanceInvokeExpr.getMethod());
						return sootMethods;
					}
				}
			}
			if (unit instanceof DefinitionStmt) {
				DefinitionStmt assignStmt = (DefinitionStmt) unit;
				Value leftValue = assignStmt.getLeftOp();
				if (leftValue.equals(value)) {
					Value rightValue = assignStmt.getRightOp();
					if (rightValue instanceof ParameterRef) {
						ParameterRef parameterRef = (ParameterRef) rightValue;
						int index = parameterRef.getIndex();
						CallGraph callGraph = CallGraphManagerGlobal.CALL_GRAPH;
						SootMethod currentMethod = unitGraph.getBody().getMethod();
						visitedMethods.add(currentMethod);
						Iterator<Edge> iterator = callGraph.edgesInto(currentMethod);
						Collection<SootMethod> sootMethods = null;
						while (iterator.hasNext()) {
							Edge edge = (Edge) iterator.next();
							SootMethod src = edge.src();
							Value arg = ((Stmt) edge.srcUnit()).getInvokeExpr().getArg(index);
							if (src.hasActiveBody() && !visitedMethods.contains(edge.src())) {
								UnitGraph preUnitGraph = new BriefUnitGraph(src.getActiveBody());
								Collection<SootMethod> subSootMethods = getRunnableArgSootMethod(arg, edge.srcUnit(),
										preUnitGraph, new HashSet<>(), visitedMethods);
								if (subSootMethods != null) {
									if (sootMethods == null) {
										sootMethods = subSootMethods;
									} else {
										sootMethods.addAll(subSootMethods);
									}
								}
							}
						}
						if (sootMethods != null) {
							return sootMethods;
						}
					} else if (rightValue instanceof InvokeExpr) {
						InvokeExpr invokeExpr = (InvokeExpr) rightValue;
						Collection<SootMethod> sootMethods = getRunnableArgSootMethodFromReturnValue(invokeExpr);
						if (sootMethods != null) {
							return sootMethods;
						}
					}
					if (!(rightValue instanceof NewExpr)) {
						visited.remove(unit);
						Collection<SootMethod> sootMethods = getRunnableArgSootMethod(rightValue, unit, unitGraph,
								visited, visitedMethods);
						if (sootMethods != null) {
							return sootMethods;
						}
					}
				}
			}
		}
		Collection<SootMethod> sootMethods = null;
		for (Unit pred : unitGraph.getPredsOf(unit)) {
			Collection<SootMethod> subSootMethods = getRunnableArgSootMethod(value, pred, unitGraph, visited,
					visitedMethods);
			if (subSootMethods != null) {
				if (sootMethods == null) {
					sootMethods = subSootMethods;
				} else {
					sootMethods.addAll(subSootMethods);
				}
			}
		}
		return sootMethods;
	}

	private Collection<SootMethod> getRunnableArgSootMethodFromReturnValue(InvokeExpr invokeExpr) {
		if (!invokeExpr.getMethod().hasActiveBody()) {
			return null;
		}
		Body body = invokeExpr.getMethod().getActiveBody();
		UnitGraph unitGraph = new BriefUnitGraph(body);
		Collection<SootMethod> sootMethods = null;
		HashSet<Unit> visitedUnits = new HashSet<>();
		HashSet<SootMethod> visitedMethods = new HashSet<>();
		for (Unit tail : unitGraph.getTails()) {
			if (tail instanceof ReturnStmt) {
				ReturnStmt returnStmt = (ReturnStmt) tail;
				Collection<SootMethod> subSootMethods = getRunnableArgSootMethod(returnStmt.getOp(), tail, unitGraph,
						visitedUnits, visitedMethods);
				if (subSootMethods != null) {
					if (sootMethods == null) {
						sootMethods = subSootMethods;
					} else {
						sootMethods.addAll(subSootMethods);
					}
				}
			}
		}
		return sootMethods;
	}

	private void processValueFlowIntraProcedure(UnitGraph unitGraph, Unit start, Set<ValuePoint> valueSet,
			Set<SootMethod> visitedMethods, Set<Unit> visitedUnits) {
		SootMethod sootMethod = unitGraph.getBody().getMethod();
		if (visitedUnits.contains(start)) {
			return;
		}
		visitedUnits.add(start);

		if (start instanceof DefinitionStmt) {
			DefinitionStmt definitionStmt = (DefinitionStmt) start;
			if (containsValue(definitionStmt.getRightOp(), valueSet)) {
				ValuePoint valuePoint = new ValuePoint();
				valuePoint.value = definitionStmt.getLeftOp();
				valuePoint.sootMethod = sootMethod;
				valueSet.add(valuePoint);
			}
		}
		if (start instanceof Stmt) {
			Stmt stmt = (Stmt) start;
			if (stmt.containsInvokeExpr()) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				List<Value> args = invokeExpr.getArgs();
				// Thread thread = new Thread(runnable);
//				if ("java.lang.Thread".equals(invokeExpr.getMethod().getDeclaringClass().getName())
//						&& invokeExpr.getMethod().isConstructor() && invokeExpr instanceof InstanceInvokeExpr) {
//					for (Value arg : args) {
//						if (containsValue(arg, valueSet)) {
//							ValuePoint valuePoint = new ValuePoint();
//							valuePoint.value = ((InstanceInvokeExpr) invokeExpr).getBase();
//							valuePoint.sootMethod = sootMethod;
//							valueSet.add(valuePoint);
//							break;
//						}
//					}
//
//				}
				if (invokeExpr.getMethod().hasActiveBody()) {
					Body invokedMethodBody = invokeExpr.getMethod().getActiveBody();
					// base.invoke(runnable);
					for (int i = 0; i < args.size(); i++) {
						Value arg = args.get(i);
						if (containsValue(arg, valueSet)) {
							Local parameterLocal = invokedMethodBody.getParameterLocal(i);
							ValuePoint valuePoint = new ValuePoint();
							valuePoint.value = parameterLocal;
							valuePoint.sootMethod = invokeExpr.getMethod();
							valueSet.add(valuePoint);
						}
					}
					if (!visitedMethods.contains(invokeExpr.getMethod())) {
						visitedMethods.add(invokeExpr.getMethod());
						UnitGraph invokedMethodUnitGraph = new BriefUnitGraph(invokedMethodBody);
						for (Unit head : invokedMethodUnitGraph.getHeads()) {
							processValueFlowIntraProcedure(invokedMethodUnitGraph, head, valueSet, visitedMethods,
									new HashSet<Unit>());
						}
					}

				}
			}
		}
		for (Unit sucss : unitGraph.getSuccsOf(start)) {
			processValueFlowIntraProcedure(unitGraph, sucss, valueSet, visitedMethods, visitedUnits);
		}

	}

	public boolean containsValue(Value value, Set<ValuePoint> valueSet) {
		for (ValuePoint valuePoint : valueSet) {
			if (valuePoint.value == value) {
				return true;
			}
		}
		return false;
	}
//
//	public Set<InitialPoint> getInitialPoints() {
//		return initialPoints;
//	}

	public Set<InitialPoint> getInitialPoints() {
		Set<InitialPoint> points = new HashSet<>();
		Log.e("initialPoints.size() = ", initialPoints.size());
		Log.e("startPoints.size() = ", startPoints.size());
		for (InitialPoint initialPoint : initialPoints) {
			for (StartPoint startPoint : startPoints) {
				Set<ValuePoint> all = new HashSet<>(initialPoint.valueSet);
				all.addAll(startPoint.valueSet);
				// initial point starts
				if (all.size() < (initialPoint.valueSet.size() + startPoint.valueSet.size())) {
					points.add(initialPoint);
					break;
				}
			}
		}
		return points;
	}

	public Set<StartPoint> getStartPoints() {
		return startPoints;
	}

	public static class InitialPoint {
		public SootMethod sootMethod = null;
		public Stmt initialStmt = null;
		public SootClass pointClass = null;
		public Set<ValuePoint> valueSet = new HashSet<>();
		boolean isCancelled = false;

		public Set<SootMethod> taskSootMethods = new HashSet<>();

		@Override
		public String toString() {
			return "{" + sootMethod + "," + initialStmt + ", valueSet=" + valueSet + "}";
		}

		@Override
		public int hashCode() {
			return initialStmt.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof InitialPoint) {
				InitialPoint another = (InitialPoint) obj;
				return initialStmt.equals(another.initialStmt);
			}
			return super.equals(obj);
		}

		public boolean isAliasedToField() {
			for (ValuePoint valuePoint : valueSet) {
				if (valuePoint.value instanceof FieldRef) {
					return true;
				}
			}
			return false;
		}
	}

	public static class StartPoint {
		SootMethod sootMethod = null;
		InstanceInvokeExpr instanceInvokeExpr = null;
		Set<ValuePoint> valueSet = new HashSet<>();
		Unit unit = null;
		boolean isCancelled = false;
		boolean repeatStart = false;
		boolean earlyCancel = false;

		boolean directedSuccOfInit = false;

		public StartPoint() {

		}

		@Override
		public String toString() {
			return "{" + sootMethod + "," + instanceInvokeExpr + ", valueSet=" + valueSet + "}";
		}

		@Override
		public int hashCode() {
			return instanceInvokeExpr.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof StartPoint) {
				StartPoint another = (StartPoint) obj;
				return instanceInvokeExpr.equals(another.instanceInvokeExpr);
			}
			return super.equals(obj);
		}
	}

	public static class ValuePoint {
		SootMethod sootMethod = null;
		Value value = null;

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ValuePoint) {
				ValuePoint another = (ValuePoint) obj;
				return value == another.value;
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return "sootMethod=" + sootMethod + "," + value;
		}
	}

}
