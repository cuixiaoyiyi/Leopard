package ac.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import ac.constant.Signature;
import ac.util.ThreadInherit;
import ac.util.PolymorphismProcess;
import jymbolic.constant.MethodSignature;
import jymbolic.entity.ContextMessage;
import jymbolic.entity.GlobalMessage;
import jymbolic.execution.processor.interprocedure.ICFGProcessor;
import jymbolic.execution.processor.interprocedure.IMethodInterProceduralJudge;
import jymbolic.util.Log;
import soot.ArrayType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticInvokeExpr;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class ThreadInterProceduralJudge implements IMethodInterProceduralJudge {

	private static ThreadInterProceduralJudge processor;

	private ThreadInterProceduralJudge() {
	}

	public static ThreadInterProceduralJudge v() {
		if (processor == null) {
			processor = new ThreadInterProceduralJudge();
		}
		return processor;
	}

	private Map<SootMethod, Boolean> methodToAsyncRelated = new HashMap<SootMethod, Boolean>();

	private Set<SootMethod> visitedMethod = new HashSet<SootMethod>();

	@Override
	public boolean isNeedInterProceduralAnalysis(InvokeExpr ie, ContextMessage context, GlobalMessage globalMessage) {
//		return true;
		return isContainThreadOperation(ICFGProcessor.getInvokedMethod(ie, context));
	}

	public boolean isContainThreadOperation(SootMethod sm) {
		if (!methodToAsyncRelated.containsKey(sm))
			methodToAsyncRelated.put(sm,
					isMethodThreadRelated(sm, Scene.v().getRefTypeUnsafe(Signature.CLASS_THREAD)));
		return methodToAsyncRelated.get(sm);
	}

	/**
	 * Traverse parameter and unit of current method to determine whether they are
	 * AsyncTask-related. If there are method invocation expressions, then
	 * recursively traverse the invoked methods
	 * 
	 * @param sm method under process
	 * @return whether current method is AsyncTask-related or not
	 */
	private boolean isMethodThreadRelated(SootMethod sm, Type targetType) {
		this.visitedMethod.add(sm);
		if (!sm.hasActiveBody())
			return false;
		if (sm.getName().equals(MethodSignature.INIT_NAME))
			return true;

		// Firstly, traverse each parameter
		Iterator<Type> typeIt = sm.getParameterTypes().iterator();
		while (typeIt.hasNext()) {
			Type t = getType(typeIt.next());
			if (ThreadInherit.isInheritedFromThread(t)) {
				return true;
			}
		}

		// Secondly, traverse each unit
		UnitGraph graph = new BriefUnitGraph(sm.getActiveBody());
		Iterator<Unit> unitIt = graph.iterator();
		while (unitIt.hasNext()) {
			Unit currentUnit = unitIt.next();
			if (isUnitThreadRelated(currentUnit, targetType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Judge whether current invoke expression contains AsyncTask operation
	 * 
	 * @param ie
	 * @return
	 */
	private boolean isInvokeExprThreadRelated(InvokeExpr ie, Type targetType) {
		if (ie instanceof InstanceInvokeExpr) { // We need to consider polymorphism for instance invoke expression
			Type baseType = getType(((InstanceInvokeExpr) ie).getBase().getType());
			if (ThreadInherit.isInheritedFromThread(baseType)) {
				return true;
			}
			Set<SootMethod> possibleMethodSet = PolymorphismProcess.getPossibleMethodList((InstanceInvokeExpr) ie);
			Iterator<SootMethod> it = possibleMethodSet.iterator();
			while (it.hasNext()) {
				SootMethod currentMethod = it.next();
				if (this.visitedMethod.contains(currentMethod)) {
					continue;
				}
				methodToAsyncRelated.putIfAbsent(currentMethod,
						this.isMethodThreadRelated(currentMethod, targetType));
				if (methodToAsyncRelated.get(currentMethod))
					return true;
			}
			return false;
		} else if (ie instanceof StaticInvokeExpr) { // We don't need to consider polymorphism if it is static invoke
														// expression
			SootMethod invokedMethod = ((StaticInvokeExpr) ie).getMethod();
			if (this.visitedMethod.contains(invokedMethod)) {
				return true;
			}
			methodToAsyncRelated.putIfAbsent(invokedMethod, this.isMethodThreadRelated(invokedMethod, targetType));
			if (methodToAsyncRelated.get(invokedMethod))
				return true;
		}
		return true;
	}

	/**
	 * Judge whether current statement contains AsyncTask operation
	 * 
	 * @param currentUnit
	 * @return
	 */
	private boolean isUnitThreadRelated(Unit currentUnit, Type targetType) {
		if (currentUnit instanceof IdentityStmt) {
			Type leftType = getType(((IdentityStmt) currentUnit).getLeftOp().getType());
			Type rightType = getType(((IdentityStmt) currentUnit).getRightOp().getType());
			if (ThreadInherit.isInheritedFromThread(leftType) || ThreadInherit.isInheritedFromThread(rightType))
				return true;
			else
				return false;
		} else if (currentUnit instanceof AssignStmt) {
			Type leftType = getType(((AssignStmt) currentUnit).getLeftOp().getType());
			if (ThreadInherit.isInheritedFromThread(leftType))
				return true;

			// Consider the rightOp
			Value rightOp = ((AssignStmt) currentUnit).getRightOp();
			if (rightOp instanceof InvokeExpr) {
				return isInvokeExprThreadRelated((InvokeExpr) rightOp, targetType);
			} else {
				Type rightType = getType(rightOp.getType());
				return ThreadInherit.isInheritedFromThread(rightType);
			}
		} else if (currentUnit instanceof InvokeStmt) {
			boolean b = isInvokeExprThreadRelated(((InvokeStmt) currentUnit).getInvokeExpr(), targetType);
			if (((InvokeStmt) currentUnit).getInvokeExpr().getMethod().getName().contains("maybeOpenCamera")) {
				Log.e("aaaaaaaaaaa", b);
			}
			return b;
		} else
			return false;
	}

	private static Type getType(Type t) {
		if (t instanceof ArrayType)
			return ((ArrayType) t).baseType;
		else
			return t;
	}
}
