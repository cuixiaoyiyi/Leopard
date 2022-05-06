package ac.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ac.util.Log;
import ac.util.PolymorphismProcess;
import soot.MethodOrMethodContext;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CallGraphManagerGlobal {

	public static CallGraph CALL_GRAPH = null;
	private static final Set<SootMethod> sourceMethods = new HashSet<>();
	static {
		Set<SootClass> sootClasses = new HashSet<>(Scene.v().getApplicationClasses());
		Set<SootMethod> visitedMethods = new HashSet<>();
	
		try {
			CALL_GRAPH = Scene.v().getCallGraph();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(CALL_GRAPH == null) {
			CALL_GRAPH = new CallGraph();
		}
		for (SootClass sootClass : sootClasses) {
			Set<SootMethod> methods = new HashSet<>(sootClass.getMethods());
			for (SootMethod sootMethod : methods) {
				callGraphConstruction(CALL_GRAPH, sootMethod, visitedMethods);
			}
		}
		Iterator<MethodOrMethodContext> iterator = CALL_GRAPH.sourceMethods();
		while (iterator.hasNext()) {
			MethodOrMethodContext next = iterator.next();
			sourceMethods.add(next.method());
		}
	}

	/**
	 * Analyze the invoked method in InvokeExpr
	 * 
	 * @param expr
	 * @param currentUnit
	 * @param currentMethod
	 * @return
	 */
	public static SootMethod getPossibleInvokedMethod(InvokeExpr expr, Unit currentUnit, SootMethod currentMethod) {
		if (expr instanceof StaticInvokeExpr) {
			return expr.getMethod();
		} else if (expr instanceof InstanceInvokeExpr) {
			Type baseType = ((InstanceInvokeExpr) expr).getBase().getType();
			if (baseType instanceof RefType) {
				SootMethod invokedMethod = PolymorphismProcess.getInterfaceOrVirtualInvokedMethod(
						((RefType) baseType).getSootClass(), expr.getMethod().getSubSignature());
				return invokedMethod != null ? invokedMethod : expr.getMethod();
			} else {
				return expr.getMethod();
			}
		} else {
			return expr.getMethod();
		}
	}

	public static Set<SootMethod> getEntryMethods() {
		return sourceMethods;
	}

	private static void callGraphConstruction(CallGraph callGraph, SootMethod currentMethod,
			Set<SootMethod> visitedMethod) {
		if (visitedMethod.contains(currentMethod)) {
			return;
		}
		visitedMethod.add(currentMethod);
		if (!currentMethod.hasActiveBody()) {
			return;
		}

		Iterator<Unit> it = currentMethod.getActiveBody().getUnits().iterator();
		while (it.hasNext()) {
			Stmt currentStmt = (Stmt) it.next();
			if (currentStmt.containsInvokeExpr()) {
				SootMethod invokedMethod = getPossibleInvokedMethod(currentStmt.getInvokeExpr(), currentStmt,
						currentMethod);
				try {
					callGraph.addEdge(new Edge(currentMethod, currentStmt, invokedMethod));
				} catch (Exception e) {
					Log.e(currentStmt.getInvokeExpr());
					Log.e(currentStmt.getInvokeExpr().getClass());
//					e.printStackTrace();
				}
				if (invokedMethod.hasActiveBody()) {
					callGraphConstruction(callGraph, invokedMethod, visitedMethod);
				}
			}
		}

	}

}
