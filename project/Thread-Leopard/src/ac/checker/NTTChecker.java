package ac.checker;

import java.util.HashSet;
import java.util.Set;

import ac.checker.PointCollector.InitialPoint;
import ac.checker.PointCollector.StartPoint;
import ac.checker.PointCollector.ValuePoint;
import ac.constant.Signature;
import ac.record.ThreadErrorRecord;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class NTTChecker {

	private PointCollector collector = null;

	public NTTChecker(PointCollector collector) {
		this.collector = collector;
		for (StartPoint startPoint : collector.getStartPoints()) {
			findInterrupt(startPoint);
		}
	}

	private void findInterrupt(StartPoint startPoint) {
		for (ValuePoint valuePoint : startPoint.valueSet) {
			if (findInterrupt(valuePoint)) {
				startPoint.isCancelled = true;
				break;
			}
		}

	}

	private boolean findInterrupt(ValuePoint valuePoint) {
		SootMethod sootMethod = valuePoint.sootMethod;
		UnitGraph unitGraph = new BriefUnitGraph(sootMethod.getActiveBody());
		for (Unit head : unitGraph.getHeads()) {
			if (findInterrupt(valuePoint, head, unitGraph, new HashSet<Unit>())) {
				return true;
			}
		}
		return false;
	}

	private boolean findInterrupt(ValuePoint valuePoint, Unit head, UnitGraph unitGraph, HashSet<Unit> visitedUnits) {
		if (visitedUnits.contains(head)) {
			return false;
		}
		visitedUnits.add(head);
		if (isInterruptInvokeExpr(head, valuePoint)) {
			return true;
		}
		for (Unit sucss : unitGraph.getSuccsOf(head)) {
			if (findInterrupt(valuePoint, sucss, unitGraph, visitedUnits)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInterruptInvokeExpr(Unit head, ValuePoint valuePoint) {
		if (head instanceof Stmt) {
			Stmt stmt = (Stmt) head;
			if (stmt.containsInvokeExpr()) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				if (isInterruptMethod(invokeExpr.getMethod())) {
					if (invokeExpr instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
						if (valuePoint.hashCode() == instanceInvokeExpr.getBase().hashCode()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean isInterruptMethod(SootMethod sootMethod) {
		return Signature.METHOD_SUBSIG_INTERRUPT.equals(sootMethod.getSubSignature())
				|| Signature.METHOD_SUBSIG_INTERRUPT_SAFELY.equals(sootMethod.getSubSignature());
	}

	public Set<InitialPoint> getUnInterruptedSet() {
		Set<InitialPoint> uncancelledSet = new HashSet<>();
		for (StartPoint startPoint : collector.getStartPoints()) {
			if (!startPoint.isCancelled) {
				for (InitialPoint newPoint : collector.getInitialPoints()) {
					for (ValuePoint valuePoint : newPoint.valueSet) {
						if (startPoint.valueSet.contains(valuePoint)) {
							uncancelledSet.add(newPoint);
							break;
						}
					}
				}
			}
		}
		return uncancelledSet;

	}

	public void record() {
		for (InitialPoint newPoint : getUnInterruptedSet()) {
			ThreadErrorRecord.recordNTT(newPoint);
		}
	}

}
